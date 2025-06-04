package com.esprit.utils;

import java.sql.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSource {
    private static DataSource instance;
    private HikariDataSource ds;
    private static final Logger logger = LoggerFactory.getLogger(DataSource.class);
    
    // Configuration with environment variable fallbacks
    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/gestion-espaces?useSSL=false&serverTimezone=UTC");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "");

    private DataSource() {
        try {
            logger.info("Attempting to connect to database at: " + URL);
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(URL);
            config.setUsername(USER);
            config.setPassword(PASSWORD);
            
            // Connection validation settings
            config.setConnectionTestQuery("SELECT 1");
            config.setInitializationFailTimeout(3000); // Fail fast if connection fails
            config.setConnectionTimeout(10000); // 10 seconds
            
            this.ds = new HikariDataSource(config);
            
            // Test connection immediately
            try (Connection conn = ds.getConnection()) {
                logger.info("Successfully connected to database");
            }
        } catch (Exception e) {
            logger.error("Failed to initialize database connection", e);
            throw new RuntimeException("""
                Database connection failed. Please verify:
                1. MySQL server is running
                2. Database 'gestion-espaces' exists
                3. Credentials are correct (user: """ + USER + ")"
                + "\nCurrent connection URL: " + URL, e);
        }
    }

    public static synchronized DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void close() {
        if (ds != null) {
            ds.close();
        }
    }
}
