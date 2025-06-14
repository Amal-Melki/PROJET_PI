package com.esprit.services;

import com.esprit.models.Evenement;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementService implements IService<Evenement> {
    private Connection connection;

    public EvenementService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(Evenement evenement) {
        String req = "INSERT INTO event (title, description_ev, date_debut, date_fin, latitude, longitude, categories, nbr_places_dispo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, evenement.getTitle());
            pst.setString(2, evenement.getDescription_ev());
            pst.setDate(3, Date.valueOf(evenement.getDate_debut()));
            pst.setDate(4, Date.valueOf(evenement.getDate_fin()));
            pst.setString(5, evenement.getLatitude());
            pst.setString(6, evenement.getLongitude());
            pst.setString(7, evenement.getCategories());
            pst.setInt(8, evenement.getNbr_places_dispo());
            pst.executeUpdate();
            System.out.println("Événement ajouté");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Evenement evenement) {
        String req = "UPDATE event SET title=?, description_ev=?, date_debut=?, date_fin=?, latitude=?, longitude=?, categories=?, nbr_places_dispo=? WHERE id_ev=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, evenement.getTitle());
            pst.setString(2, evenement.getDescription_ev());
            pst.setDate(3, Date.valueOf(evenement.getDate_debut()));
            pst.setDate(4, Date.valueOf(evenement.getDate_fin()));
            pst.setString(5, evenement.getLatitude());
            pst.setString(6, evenement.getLongitude());
            pst.setString(7, evenement.getCategories());
            pst.setInt(8, evenement.getNbr_places_dispo());
            pst.setInt(9, evenement.getId_ev());
            pst.executeUpdate();
            System.out.println("Événement modifié");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(Evenement evenement) {
        String req = "DELETE FROM event WHERE id_ev=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, evenement.getId_ev());
            pst.executeUpdate();
            System.out.println("Événement supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Evenement> rechercher() {
        List<Evenement> evenements = new ArrayList<>();
        String req = "SELECT * FROM event";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                evenements.add(new Evenement(
                    rs.getInt("id_ev"),
                    rs.getString("title"),
                    rs.getString("description_ev"),
                    rs.getDate("date_debut").toLocalDate(),
                    rs.getDate("date_fin").toLocalDate(),
                    rs.getString("latitude"),
                    rs.getString("longitude"),
                    rs.getString("categories"),
                    rs.getInt("nbr_places_dispo")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return evenements;
    }

    public Evenement rechercherParId(int id) {
        String req = "SELECT * FROM event WHERE id_ev=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Evenement(
                    rs.getInt("id_ev"),
                    rs.getString("title"),
                    rs.getString("description_ev"),
                    rs.getDate("date_debut").toLocalDate(),
                    rs.getDate("date_fin").toLocalDate(),
                    rs.getString("latitude"),
                    rs.getString("longitude"),
                    rs.getString("categories"),
                    rs.getInt("nbr_places_dispo")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
} 