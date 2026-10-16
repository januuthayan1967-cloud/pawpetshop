package com.pawmart.model;

/**
 * Role-based authorization levels for PawMart.
 */
public enum Role {
    MANAGER("Store Manager"),
    CASHIER("Cashier");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role fromString(String roleStr) {
        if (roleStr == null) return null;
        for (Role r : values()) {
            if (r.name().equalsIgnoreCase(roleStr.trim()) || r.displayName.equalsIgnoreCase(roleStr.trim())) {
                return r;
            }
        }
        return CASHIER; // Default fallback
    }
}
