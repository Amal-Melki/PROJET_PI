package com.esprit.services;

import com.esprit.modules.Equipement;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipementService implements Iservice {

    private Connection connection;

    // Constructor
    public EquipementService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void add(Object o) {
        Equipement equipement = (Equipement) o;
        String sql = "INSERT INTO Equipement(nom, type, quantite, description, estDisponible) VALUES(?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, equipement.getNom());
            ps.setString(2, equipement.getType());
            ps.setInt(3, equipement.getQuantite());
            ps.setString(4, equipement.getDescription());
            ps.setBoolean(5, equipement.isEstDisponible());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucune ligne ajoutée.");
            }
            // Get the generated key (ID)
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    equipement.setId(rs.getInt(1));
                }
            }
            System.out.println("ID de la ligne ajoutée: " + equipement.getId());
        } catch (SQLException e) {
            System.out.println("Erreur en ajout : " + e.getMessage());
        }
    }

    @Override
    public void update(Object o) {
        Equipement equipement = (Equipement) o;  // Cast the object to Equipement
        String sql = "UPDATE Equipement SET nom=?, type=?, quantite=?, description=?, estDisponible=? WHERE ideq=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, equipement.getNom());
            ps.setString(2, equipement.getType());
            ps.setInt(3, equipement.getQuantite());
            ps.setString(4, equipement.getDescription());
            ps.setBoolean(5, equipement.isEstDisponible());
            ps.setInt(6, equipement.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucune ligne modifiée.");
            }
            System.out.println("Equipement mis à jour avec l'ID : " + equipement.getId());
        } catch (SQLException e) {
            System.out.println("Erreur en mise à jour : " + e.getMessage());
        }
    }

    @Override
    public void delete(Object o) {
        Equipement equipement = (Equipement) o;  // Cast the object to Equipement
        String sql = "DELETE FROM Equipement WHERE ideq=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, equipement.getId());
            ps.executeUpdate();
            System.out.println("Equipement supprimé");
        } catch (SQLException e) {
            System.out.println("Erreur en suppression : " + e.getMessage());
        }
    }

    @Override
    public List<Equipement> get() {
        List<Equipement> equipement = new ArrayList<>();


        String req = "SELECT * FROM Equipement";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                equipement.add(new Equipement(rs.getInt("ideq"), rs.getString("nom"), rs.getString("type"), rs.getInt("quantite"), rs.getString("description"), rs.getBoolean("estDisponible")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return equipement;
    }


}