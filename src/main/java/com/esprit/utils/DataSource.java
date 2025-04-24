package com.esprit.utils;

import java.sql.*;

public class DataSource {

    private Connection connection;
    private static DataSource instance;

    private DataSource() {
        try {
            String URL = "jdbc:mysql://localhost:3306/GESTION-ESPACES";
            String USERNAME = "root";
            String PASSWORD = "";
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to DB !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static DataSource getInstance() {
        if(instance == null)
            instance = new DataSource();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
