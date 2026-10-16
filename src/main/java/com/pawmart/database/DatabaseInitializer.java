package com.pawmart.database;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Automates PawMart database schema creation, table initialization, and seed verification.
 */
public class DatabaseInitializer {

    public static boolean initializeDatabase() {
        try {
            // Step 1: Ensure database exists
            ensureDatabaseExists();

            // Step 2: Ensure tables exist
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement()) {

                // Create users table
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS `users` (
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
                        INDEX `idx_users_role` (`role`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """);

                // Create categories table
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS `categories` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `name` VARCHAR(100) NOT NULL UNIQUE,
                        `description` TEXT,
                        `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """);

                // Create products table
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS `products` (
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
                        CONSTRAINT `fk_products_category`
                            FOREIGN KEY (`category_id`)
                            REFERENCES `categories` (`id`)
                            ON DELETE RESTRICT
                            ON UPDATE CASCADE
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """);

                // Create sales table
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS `sales` (
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
                        CONSTRAINT `fk_sales_cashier`
                            FOREIGN KEY (`cashier_id`)
                            REFERENCES `users` (`id`)
                            ON DELETE RESTRICT
                            ON UPDATE CASCADE
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """);

                // Create sale_items table
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS `sale_items` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `sale_id` INT NOT NULL,
                        `product_id` INT NOT NULL,
                        `product_name` VARCHAR(150) NOT NULL,
                        `unit_price` DECIMAL(10, 2) NOT NULL,
                        `quantity` INT NOT NULL,
                        `line_total` DECIMAL(10, 2) NOT NULL,
                        INDEX `idx_sale_items_sale` (`sale_id`),
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
                """);

                // Seed categories if empty
                seedCategoriesIfEmpty(conn);

                // Seed products if empty
                seedProductsIfEmpty(conn);

                // Seed or verify default user accounts with valid BCrypt hashes
                seedUsersIfEmpty(conn);
            }

            return true;
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Initialization error: " + e.getMessage());
            return false;
        }
    }

    private static void ensureDatabaseExists() throws SQLException {
        try (Connection serverConn = DriverManager.getConnection(
                DatabaseConfig.getServerJdbcUrl(),
                DatabaseConfig.getUsername(),
                DatabaseConfig.getPassword());
             Statement stmt = serverConn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + DatabaseConfig.getDatabase()
                    + "` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
        }
    }

    public static boolean hasManagerAccount() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE role = 'MANAGER' AND status = 'ACTIVE'")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("[DatabaseInitializer] Check manager error: " + e.getMessage());
        }
        return false;
    }

    public static boolean createInitialManager(String fullName, String username, String email, String plainPassword) {
        String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
        String sql = "INSERT INTO users (full_name, username, email, password_hash, role, status) VALUES (?, ?, ?, ?, 'MANAGER', 'ACTIVE')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, hash);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DatabaseInitializer] Create initial manager error: " + e.getMessage());
            return false;
        }
    }

    private static void seedCategoriesIfEmpty(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM categories")) {
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate("""
                    INSERT INTO `categories` (`id`, `name`, `description`) VALUES
                    (1, 'Dog Food', 'Premium dry kibble, wet food cans, and raw nutrition for dogs'),
                    (2, 'Cat Food', 'Nutritious salmon, tuna, poultry, and special dietary cat meals'),
                    (3, 'Pet Toys', 'Interactive chew toys, balls, plushies, scratching posts, and wands'),
                    (4, 'Pet Accessories', 'Collars, harnesses, leashes, feeding bowls, tags, and travel crates'),
                    (5, 'Grooming & Care', 'Shampoos, conditioners, de-shedding combs, nail clippers, and wipes'),
                    (6, 'Pet Beds & Housing', 'Orthopedic cushions, plush donuts, cat trees, and cave beds'),
                    (7, 'Pet Health & Hygiene', 'Vitamins, flea & tick prevention, dental gels, and puppy pads'),
                    (8, 'Small Pets & Birds', 'Seed mixes, pellets, mineral blocks, hay, and cage supplies');
                """);
            }
        }
    }

    private static void seedProductsIfEmpty(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate("""
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
                """);
            }
        }
    }

    private static void seedUsersIfEmpty(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String adminHash = BCrypt.hashpw("admin123", BCrypt.gensalt(12));
                String cashierHash = BCrypt.hashpw("cashier123", BCrypt.gensalt(12));
                String sql = "INSERT INTO users (id, full_name, username, email, password_hash, role, status) VALUES " +
                        "(1, 'PawMart Store Manager', 'admin', 'manager@pawmart.com', ?, 'MANAGER', 'ACTIVE'), " +
                        "(2, 'Emma Watson (Cashier)', 'cashier', 'emma@pawmart.com', ?, 'CASHIER', 'ACTIVE')";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, adminHash);
                    ps.setString(2, cashierHash);
                    ps.executeUpdate();
                }
            } else {
                // Ensure default demo users have valid hashes
                String adminHash = BCrypt.hashpw("admin123", BCrypt.gensalt(12));
                String cashierHash = BCrypt.hashpw("cashier123", BCrypt.gensalt(12));
                try (PreparedStatement ps1 = conn.prepareStatement("UPDATE users SET password_hash = ? WHERE username = 'admin'");
                     PreparedStatement ps2 = conn.prepareStatement("UPDATE users SET password_hash = ? WHERE username = 'cashier'")) {
                    ps1.setString(1, adminHash);
                    ps1.executeUpdate();
                    ps2.setString(1, cashierHash);
                    ps2.executeUpdate();
                }
            }
        }
    }
}
