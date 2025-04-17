package com.esprit.services;

import com.esprit.modules.Espace;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspaceService implements Iservice {

    private final Connection connection;

    // Constructor
    public EspaceService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void add(Object o) {
        Espace espace = (Espace) o;
        String sql = "INSERT INTO Espace(nomEspace, type, capacite, localisation, prix) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, espace.getNom());
            ps.setString(2, espace.getType());
            ps.setInt(3, espace.getCapacite());
            ps.setString(4, espace.getLocalisation());
            ps.setDouble(5, espace.getPrix());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucune ligne ajoutée.");
            }
            // Get the generated key (ID)
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    espace.setId(rs.getInt(1));
                }
            }
            System.out.println("ID de la ligne ajoutée: " + espace.getId());
        } catch (SQLException e) {
            System.out.println("Erreur en ajout : " + e.getMessage());
        }
    }

    @Override
    public void update(Object o) {
        Espace espace = (Espace) o;  // Cast the object to Espace
        String sql = "UPDATE Espace SET nomEspace=?, type=?, capacite=?, localisation=?, prix=? WHERE espaceId=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, espace.getNom());
            ps.setString(2, espace.getType());
            ps.setInt(3, espace.getCapacite());
            ps.setString(4, espace.getLocalisation());
            ps.setDouble(5, espace.getPrix());
            ps.setInt(6, espace.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucune ligne modifiée.");
            }
            System.out.println("Espace mis à jour avec l'ID : " + espace.getId());
        } catch (SQLException e) {
            System.out.println("Erreur en mise à jour : " + e.getMessage());
        }
    }

    @Override
    public void delete(Object o) {
        Espace espace = (Espace) o;  // Cast the object to Espace
        String sql = "DELETE FROM Espace WHERE espaceId=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, espace.getId());
            ps.executeUpdate();
            System.out.println("Espace supprimé");
        } catch (SQLException e) {
            System.out.println("Erreur en suppression : " + e.getMessage());
        }
    }

    @Override
    public List<Espace> get() {
        List<Espace> espace = new ArrayList<>();

        String req = "SELECT * FROM Espace";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                    espace.add(new Espace(rs.getInt("espaceId"),
                            rs.getString("nomEspace"),
                            rs.getString("type"),
                            rs.getDouble("prix"),
                            rs.getInt("capacite"),
                            rs.getString("localisation")));
                }
            } catch(SQLException e){
                System.out.println(e.getMessage());
            }
            return espace;
        }
}





