package com.pawmart.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration manager for MySQL database connectivity.
 * Supports external configuration files, environment variables, and safe WAMP defaults.
 */
public class DatabaseConfig {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3306;
    private static final String DEFAULT_DATABASE = "pawmart_petshop";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static String host = DEFAULT_HOST;
    private static int port = DEFAULT_PORT;
    private static String database = DEFAULT_DATABASE;
    private static String username = DEFAULT_USER;
    private static String password = DEFAULT_PASSWORD;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        // 1. Try to load from pawmart_db.properties in working directory or classpath
        Properties props = new Properties();
        File propFile = new File("pawmart_db.properties");
        if (propFile.exists()) {
            try (InputStream in = new FileInputStream(propFile)) {
                props.load(in);
            } catch (Exception ignored) {}
        } else {
            try (InputStream in = DatabaseConfig.class.getResourceAsStream("/pawmart_db.properties")) {
                if (in != null) {
                    props.load(in);
                }
            } catch (Exception ignored) {}
        }

        // 2. Read values from properties or environment or fallback
        host = getVal(props, "db.host", "PAWMART_DB_HOST", DEFAULT_HOST);
        String portStr = getVal(props, "db.port", "PAWMART_DB_PORT", String.valueOf(DEFAULT_PORT));
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            port = DEFAULT_PORT;
        }

        database = getVal(props, "db.name", "PAWMART_DB_NAME", DEFAULT_DATABASE);
        username = getVal(props, "db.user", "PAWMART_DB_USER", DEFAULT_USER);
        password = getVal(props, "db.password", "PAWMART_DB_PASSWORD", DEFAULT_PASSWORD);
    }

    private static String getVal(Properties props, String propKey, String envKey, String fallback) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal.trim();
        }
        String sysVal = System.getProperty(propKey);
        if (sysVal != null && !sysVal.trim().isEmpty()) {
            return sysVal.trim();
        }
        String pVal = props.getProperty(propKey);
        if (pVal != null && !pVal.trim().isEmpty()) {
            return pVal.trim();
        }
        return fallback;
    }

    public static String getJdbcUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    }

    public static String getServerJdbcUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    public static String getHost() {
        return host;
    }

    public static int getPort() {
        return port;
    }

    public static String getDatabase() {
        return database;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setCustomConfig(String newHost, int newPort, String newDb, String newUser, String newPass) {
        host = newHost;
        port = newPort;
        database = newDb;
        username = newUser;
        password = newPass;
    }
}
