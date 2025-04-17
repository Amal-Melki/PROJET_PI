package com.esprit.services;

import com.esprit.modules.Materiel;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterielService implements Iservice {

    private Connection connection;

    public MaterielService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void add(Object o) {
        Materiel materiel = (Materiel) o;
        String sql = "INSERT INTO materiel(nom, type, quantite, description, estDisponible) VALUES(?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, materiel.getNom());
            ps.setString(2, materiel.getType());
            ps.setInt(3, materiel.getQuantite());
            ps.setString(4, materiel.getDescription());
            ps.setBoolean(5, materiel.isEstDisponible());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucune ligne ajoutée.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    materiel.setId(rs.getInt(1));
                }
            }

            System.out.println("ID de la ligne ajoutée: " + materiel.getId());
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public void update(Object o) {
        Materiel materiel = (Materiel) o;
        String sql = "UPDATE materiel SET nom=?, type=?, quantite=?, description=?, estDisponible=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, materiel.getNom());
            ps.setString(2, materiel.getType());
            ps.setInt(3, materiel.getQuantite());
            ps.setString(4, materiel.getDescription());
            ps.setBoolean(5, materiel.isEstDisponible());
            ps.setInt(6, materiel.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucune ligne modifiée.");
            }

            System.out.println("Matériel mis à jour avec l'ID : " + materiel.getId());
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @Override
    public void delete(Object o) {
        Materiel materiel = (Materiel) o;
        String sql = "DELETE FROM materiel WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, materiel.getId());
            ps.executeUpdate();
            System.out.println("Matériel supprimé.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public List<Materiel> get() {
        List<Materiel> materiels = new ArrayList<>();
        String req = "SELECT * FROM materiel";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Materiel materiel = new Materiel(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getInt("quantite"),
                        rs.getString("description"),
                        rs.getBoolean("estDisponible")
                );
                materiels.add(materiel);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération : " + e.getMessage());
        }

        return materiels;
    }
}
