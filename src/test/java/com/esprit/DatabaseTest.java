package com.esprit;

import com.esprit.utils.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        try {
            Connection connection = DataSource.getInstance().getConnection();
            if (connection != null && !connection.isClosed()) {
                System.out.println("✅ Connexion à la base de données réussie");
                connection.close();
            } else {
                System.err.println("❌ Échec de la connexion à la base");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
