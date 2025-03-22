package com.esprit.services;

import com.esprit.modules.Categorie;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieService implements Iservice {

    private Connection connection;

    // Constructor
    public CategorieService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void add(Object o) {
        Categorie categorie = (Categorie) o;
        String sql = "INSERT INTO Categorie(id, nom, description) VALUES(?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categorie.getNom());
            ps.setString(2, categorie.getDescription());
            ps.setString(2, categorie.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucune ligne ajoutée.");
            }
            // Get the generated key (ID)
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    categorie.setId(rs.getInt(1));
                }
            }
            System.out.println("ID de la ligne ajoutée: " + categorie.getId());
        } catch (SQLException e) {
            System.out.println("Erreur en ajout : " + e.getMessage());
        }
    }

    @Override
    public void update(Object o) {
        Categorie categorie = (Categorie) o;
        String sql = "UPDATE Categorie SET nom=?, description=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, categorie.getNom());
            ps.setString(2, categorie.getDescription());
            ps.setInt(3, categorie.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucune ligne modifiée.");
            }
            System.out.println("Categorie mise à jour avec l'ID : " + categorie.getId());
        } catch (SQLException e) {
            System.out.println("Erreur en mise à jour : " + e.getMessage());
        }
    }

    @Override
    public void delete(Object o) {
        Categorie categorie = (Categorie) o;
        String sql = "DELETE FROM Categorie WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categorie.getId());
            ps.executeUpdate();
            System.out.println("Categorie supprimée");
        } catch (SQLException e) {
            System.out.println("Erreur en suppression : " + e.getMessage());
        }
    }

    @Override
    public List<Categorie> get() {
        List<Categorie> categories = new ArrayList<>();

        String req = "SELECT * FROM Categorie";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                categories.add(new Categorie(rs.getInt("id"), rs.getString("nom"), rs.getString("description")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return categories;
    }
}
