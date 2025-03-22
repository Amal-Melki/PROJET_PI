package com.esprit.utils;

import java.sql.*;

public class DataSource {

    private Connection connection;
    private static DataSource instance;

    private final String URL = "jdbc:mysql://localhost:3306/ghada";
    private final String USERNAME = "root";
    private final String PASSWORD = "";

    private DataSource() {
        try {
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
