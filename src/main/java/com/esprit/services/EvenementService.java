package com.esprit.services;

import com.esprit.modules.Evenement;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementService implements Iservice {

    private Connection connection;

    // Constructor
    public EvenementService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void add(Object o) {
        Evenement equipement = (Evenement) o;
        String sql = "INSERT INTO Equipement(nom, type, quantite, description, estDisponible) VALUES(?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, Evenement.getNom());
            ps.setString(2, Evenement.getType());
            ps.setInt(3, Evenement.getQuantite());
            ps.setString(4, Evenement.getDescription());
            ps.setBoolean(5, Evenement.isEstDisponible());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucune ligne ajoutée.");
            }
            // Get the generated key (ID)
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Evenement.setId(rs.getInt(1));
                }
            }
            System.out.println("ID de la ligne ajoutée: " + Evenement.getId());
        } catch (SQLException e) {
            System.out.println("Erreur en ajout : " + e.getMessage());
        }
    }

    @Override
    public void update(Object o) {
        Evenement evenement = (Evenement) o;  // Cast the object to Equipement
        String sql = "UPDATE Evenement SET nom=?, type=?, quantite=?, description=?, estDisponible=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, evenement.getNom());
            ps.setString(2, evenement.getType());
            ps.setInt(3, evenement.getQuantite());
            ps.setString(4, evenement.getDescription());
            ps.setBoolean(5, evenement.isEstDisponible());
            ps.setInt(6, evenement.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucune ligne modifiée.");
            }
            System.out.println("evenement mis à jour avec l'ID : " + evenement.getId());
        } catch (SQLException e) {
            System.out.println("Erreur en mise à jour : " + e.getMessage());
        }
    }

    @Override
    public void delete(Object o) {
        Evenement evenement = (Evenement) o;  // Cast the object to Evenement
        String sql = "DELETE FROM Evenement WHERE ideq=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, evenement.getId());
            ps.executeUpdate();
            System.out.println("evenement supprimé");
        } catch (SQLException e) {
            System.out.println("Erreur en suppression : " + e.getMessage());
        }
    }

    @Override
    public List<Evenement> get() {
        List<Evenement> evenement = new ArrayList<>();


        String req = "SELECT * FROM Evenement";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                evenement.add(new Evenement(rs.getInt("ideq"), rs.getString("nom"), rs.getString("type"), rs.getInt("quantite"), rs.getString("description"), rs.getBoolean("estDisponible")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return evenement;
    }


}