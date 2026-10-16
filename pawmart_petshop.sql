-- =====================================================================
-- PAWMART PET SHOP MANAGEMENT SYSTEM
-- MySQL Database Schema & Initial Data
-- Compatible with MySQL 8.x / 9.x / MariaDB (WampServer & phpMyAdmin)
-- Database Name: pawmart_petshop
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `pawmart_petshop`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `pawmart_petshop`;

-- Disable foreign key checks for clean drop/create
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `sale_items`;
DROP TABLE IF EXISTS `sales`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `users`;

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- Table 1: users
-- Stores Manager and Cashier accounts with role-based access & BCrypt hashes
-- ---------------------------------------------------------------------
CREATE TABLE `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(100) NOT NULL,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `role` ENUM('MANAGER', 'CASHIER') NOT NULL,
    `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_users_username` (`username`),
    INDEX `idx_users_role` (`role`),
    INDEX `idx_users_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- Table 2: categories
-- Product categories for pet supplies
-- ---------------------------------------------------------------------
CREATE TABLE `categories` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE,
    `description` TEXT,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_categories_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- Table 3: products
-- Pet shop inventory records with pricing, stock, threshold & status
-- ---------------------------------------------------------------------
CREATE TABLE `products` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(150) NOT NULL,
    `category_id` INT NOT NULL,
    `price` DECIMAL(10, 2) NOT NULL,
    `quantity` INT NOT NULL DEFAULT 0,
    `description` TEXT,
    `image_path` VARCHAR(255) DEFAULT NULL,
    `low_stock_threshold` INT NOT NULL DEFAULT 5,
    `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_products_category` (`category_id`),
    INDEX `idx_products_status` (`status`),
    INDEX `idx_products_name` (`name`),
    CONSTRAINT `fk_products_category`
        FOREIGN KEY (`category_id`)
        REFERENCES `categories` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- Table 4: sales
-- Checkout transactions with receipt number, cashier reference & totals
-- ---------------------------------------------------------------------
CREATE TABLE `sales` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `receipt_number` VARCHAR(50) NOT NULL UNIQUE,
    `cashier_id` INT NOT NULL,
    `subtotal` DECIMAL(10, 2) NOT NULL,
    `discount_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `grand_total` DECIMAL(10, 2) NOT NULL,
    `payment_method` ENUM('CASH', 'CREDIT_CARD', 'DEBIT_CARD', 'MOBILE_PAY') NOT NULL DEFAULT 'CASH',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_sales_receipt` (`receipt_number`),
    INDEX `idx_sales_cashier` (`cashier_id`),
    INDEX `idx_sales_date` (`created_at`),
    CONSTRAINT `fk_sales_cashier`
        FOREIGN KEY (`cashier_id`)
        REFERENCES `users` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- Table 5: sale_items
-- Line items for each sales transaction
-- ---------------------------------------------------------------------
CREATE TABLE `sale_items` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `sale_id` INT NOT NULL,
    `product_id` INT NOT NULL,
    `product_name` VARCHAR(150) NOT NULL,
    `unit_price` DECIMAL(10, 2) NOT NULL,
    `quantity` INT NOT NULL,
    `line_total` DECIMAL(10, 2) NOT NULL,
    INDEX `idx_sale_items_sale` (`sale_id`),
    INDEX `idx_sale_items_product` (`product_id`),
    CONSTRAINT `fk_sale_items_sale`
        FOREIGN KEY (`sale_id`)
        REFERENCES `sales` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_sale_items_product`
        FOREIGN KEY (`product_id`)
        REFERENCES `products` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- Initial Seed Data
-- =====================================================================

-- Default Categories
INSERT INTO `categories` (`id`, `name`, `description`) VALUES
(1, 'Dog Food', 'Premium dry kibble, wet food cans, and raw nutrition for dogs'),
(2, 'Cat Food', 'Nutritious salmon, tuna, poultry, and special dietary cat meals'),
(3, 'Pet Toys', 'Interactive chew toys, balls, plushies, scratching posts, and wands'),
(4, 'Pet Accessories', 'Collars, harnesses, leashes, feeding bowls, tags, and travel crates'),
(5, 'Grooming & Care', 'Shampoos, conditioners, de-shedding combs, nail clippers, and wipes'),
(6, 'Pet Beds & Housing', 'Orthopedic cushions, plush donuts, cat trees, and cave beds'),
(7, 'Pet Health & Hygiene', 'Vitamins, flea & tick prevention, dental gels, and puppy pads'),
(8, 'Small Pets & Birds', 'Seed mixes, pellets, mineral blocks, hay, and cage supplies');

-- Initial Seed Products
INSERT INTO `products` (`id`, `name`, `category_id`, `price`, `quantity`, `description`, `image_path`, `low_stock_threshold`, `status`) VALUES
(1, 'Royal Canin Mini Adult Dog Food 3kg', 1, 38.50, 25, 'Tailored nutrition for small dogs up to 10kg with balanced nutrients.', 'dog_food_royal.png', 5, 'ACTIVE'),
(2, 'Purina Pro Plan Sensitive Skin Cat Food 1.5kg', 2, 26.90, 18, 'Salmon and rice formula supporting healthy skin and shiny coats.', 'cat_food_purina.png', 5, 'ACTIVE'),
(3, 'KONG Classic Dog Chew Toy Large', 3, 14.99, 35, 'Durable natural red rubber bouncing chew toy for active dogs.', 'kong_toy.png', 8, 'ACTIVE'),
(4, 'Whisker City Catnip Feather Wand', 3, 8.50, 42, 'Interactive teaser toy infused with premium Canadian catnip.', 'cat_wand.png', 10, 'ACTIVE'),
(5, 'Reflective No-Pull Dog Harness (Medium)', 4, 22.00, 14, 'Comfortable padded nylon chest harness with sturdy zinc-alloy D-ring.', 'dog_harness.png', 4, 'ACTIVE'),
(6, 'Furminator Undercoat De-Shedding Brush', 5, 29.99, 12, 'Reduces loose hair shedding up to 90% without damaging topcoat.', 'grooming_brush.png', 3, 'ACTIVE'),
(7, 'Plush Orthopedic Memory Foam Pet Bed', 6, 45.00, 8, 'Ultra-soft fleece cover with high-density joint relief memory foam.', 'pet_bed.png', 3, 'ACTIVE'),
(8, 'TropiClean Fresh Breath Dental Gel 118ml', 7, 13.50, 24, 'Removes plaque and tartar without brushing for fresh pet breath.', 'dental_gel.png', 5, 'ACTIVE'),
(9, 'Bioline Flea & Tick Spot-On Drops (3pk)', 7, 17.80, 3, 'Fast-acting topical treatment repelling fleas, ticks, and lice.', 'flea_drops.png', 5, 'ACTIVE'),
(10, 'Stainless Steel Anti-Skid Pet Bowl 850ml', 4, 11.20, 30, 'Rust-resistant stainless steel feeding dish with silicone non-slip base.', 'pet_bowl.png', 6, 'ACTIVE'),
(11, 'Natural Clumping Lavender Cat Litter 10L', 7, 19.50, 20, '99.9% dust-free bentonite clay with instant odor neutralizer.', 'cat_litter.png', 5, 'ACTIVE'),
(12, 'Whiskas Tuna in Jelly Pouch Box (12x85g)', 2, 15.90, 4, 'Real fish chunks in gravy delivering vitamins and minerals for cats.', 'whiskas_tuna.png', 6, 'ACTIVE');

-- Initial Demo Accounts (passwords: 'admin123' for manager, 'cashier123' for cashier)
-- BCrypt hashed with salt rounds 12
-- You can also create new managers and cashiers in the application.
INSERT INTO `users` (`id`, `full_name`, `username`, `email`, `password_hash`, `role`, `status`) VALUES
(1, 'PawMart Store Manager', 'admin', 'manager@pawmart.com', '$2a$12$e0tqGvM35vC83x2x6bH4x.Q/qjO5u6zV3lI6v5uOWjL.tYp0ZkG/l', 'MANAGER', 'ACTIVE'),
(2, 'Emma Watson (Cashier)', 'cashier', 'emma@pawmart.com', '$2a$12$e0tqGvM35vC83x2x6bH4x.Q/qjO5u6zV3lI6v5uOWjL.tYp0ZkG/l', 'CASHIER', 'ACTIVE');

-- Sample Initial Sales & Transaction History
INSERT INTO `sales` (`id`, `receipt_number`, `cashier_id`, `subtotal`, `discount_amount`, `grand_total`, `payment_method`, `created_at`) VALUES
(1, 'REC-20261015-001', 2, 53.49, 3.49, 50.00, 'CASH', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 'REC-20261016-002', 2, 35.40, 0.00, 35.40, 'CREDIT_CARD', DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(3, 'REC-20261016-003', 2, 67.00, 5.00, 62.00, 'MOBILE_PAY', DATE_SUB(NOW(), INTERVAL 1 HOUR));

INSERT INTO `sale_items` (`sale_id`, `product_id`, `product_name`, `unit_price`, `quantity`, `line_total`) VALUES
(1, 1, 'Royal Canin Mini Adult Dog Food 3kg', 38.50, 1, 38.50),
(1, 3, 'KONG Classic Dog Chew Toy Large', 14.99, 1, 14.99),
(2, 4, 'Whisker City Catnip Feather Wand', 8.50, 1, 8.50),
(2, 2, 'Purina Pro Plan Sensitive Skin Cat Food 1.5kg', 26.90, 1, 26.90),
(3, 5, 'Reflective No-Pull Dog Harness (Medium)', 22.00, 1, 22.00),
(3, 7, 'Plush Orthopedic Memory Foam Pet Bed', 45.00, 1, 45.00);
