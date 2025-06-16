package com.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {

    private Connection connection;
    private static DataSource instance;
<<<<<<< HEAD
    private static final String URL = "jdbc:mysql://localhost:3306/gestion-espaces";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
=======


    private final String URL = "jdbc:mysql://localhost:3307/projetpi";

    private final String USERNAME = "root";
    private final String PASSWORD = "";

    private DataSource() {
        connectToDatabase();
    }
    
    /**
     * Attempts to connect to the database
     * If connection fails, it will print a detailed error message
     */
    private void connectToDatabase() {
        try {

            // Try to load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Attempt to connect to the database
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to database successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC Driver not found! Add the MySQL connector to your project.");
            System.err.println(e.getMessage());
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to connect to the database!");
            System.err.println("URL: " + URL);
            System.err.println("Error message: " + e.getMessage());
            System.err.println("Error code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            
            if (e.getErrorCode() == 1049) {
                System.err.println("DATABASE DOES NOT EXIST: Please create the 'gestion-espaces' database first.");
                System.err.println("You can create it using phpMyAdmin or with this SQL command:\n" +
                                  "CREATE DATABASE `gestion-espaces`;\n");
            } else if (e.getErrorCode() == 1045) {
                System.err.println("ACCESS DENIED: Check your MySQL username and password.");
            } else if (e.getErrorCode() == 0 && e.getSQLState().equals("08S01")) {
                System.err.println("CONNECTION REFUSED: Make sure MySQL server is running on localhost:3306.");
                System.err.println("Did you start your XAMPP/MySQL service?");
            }

            // Charger le driver JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to DB !");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found !");
        } catch (SQLException e) {
            System.out.println("Error connecting to DB: " + e.getMessage());
        }
    }

    /**
     * Gets the singleton instance of DataSource
     * @return DataSource instance
     */
    public static DataSource getInstance() {
        if (instance == null) {
        if (instance == null)
            instance = new DataSource();
        }
        return instance;
    }

    /**
     * Gets the database connection
     * If connection is closed or null, attempts to reconnect
     * @return Database connection or null if connection failed
     */
    public Connection getConnection() {
        try {
            // Check if connection is closed or null
            if (connection == null || connection.isClosed()) {
                System.out.println("Connection is null or closed. Attempting to reconnect...");
                connectToDatabase();
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status: " + e.getMessage());
            connectToDatabase();
        }
        return connection;
    }
    
    /**
     * Checks if the database connection is valid
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
