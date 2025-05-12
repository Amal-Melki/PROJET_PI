package com.esprit.services;
import com.esprit.modules.Espace;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspaceService {

    private final Connection connection;

    public EspaceService() {
        connection = DataSource.getInstance().getConnection();
    }

    public void add(Espace espace) {
        String req = "INSERT INTO espace(nomEspace, type, capacite, localisation, prix, disponibilite) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, espace.getNom());
            pst.setString(2, espace.getType());
            pst.setInt(3, espace.getCapacite());
            pst.setString(4, espace.getLocalisation());
            pst.setDouble(5, espace.getPrix());
            pst.setBoolean(6, espace.isDisponibilite());
            pst.executeUpdate();
            System.out.println("Espace ajouté !");
        } catch (SQLException e) {
            System.out.println(" Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    public void update(Espace espace) {
        String req = "UPDATE espace SET nomEspace=?, type=?, capacite=?, localisation=?, prix=?, disponibilite=? WHERE espaceId=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, espace.getNom());
            pst.setString(2, espace.getType());
            pst.setInt(3, espace.getCapacite());
            pst.setString(4, espace.getLocalisation());
            pst.setDouble(5, espace.getPrix());
            pst.setBoolean(6, espace.isDisponibilite());
            pst.setInt(7, espace.getId());
            pst.executeUpdate();
            System.out.println("Espace modifié !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    public void delete(int id) {
        String req = "DELETE FROM espace WHERE espaceId=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Espace supprimé !");
        } catch (SQLException e) {
            System.out.println(" Erreur lors de la suppression : " + e.getMessage());
        }
    }

    public List<Espace> getAll() {
        List<Espace> espaces = new ArrayList<>();
        String req = "SELECT * FROM espace";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Espace e = new Espace(
                        rs.getString("nomEspace"),
                        rs.getString("type"),
                        rs.getInt("capacite"),
                        rs.getString("localisation"),
                        rs.getDouble("prix"),
                        rs.getBoolean("disponibilite")
                );
                e.setId(rs.getInt("espaceId"));
                espaces.add(e);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération : " + e.getMessage());
        }
        return espaces;
    }

    public void ajouterEspace(Espace espace) {

    }

    public List<Espace> getEspacesByType(String typeSelectionne) {
        List<Espace> espaces = new ArrayList<>();
        String req = "SELECT * FROM espace WHERE type = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, typeSelectionne);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Espace e = new Espace(
                        rs.getString("nomEspace"),
                        rs.getString("type"),
                        rs.getInt("capacite"),
                        rs.getString("localisation"),
                        rs.getDouble("prix"),
                        rs.getBoolean("disponibilite")
                );
                e.setId(rs.getInt("espaceId"));
                espaces.add(e);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération par type : " + e.getMessage());
        }
        return espaces;
    }

    public void delete(Espace selectedEspace) {
    }
}




