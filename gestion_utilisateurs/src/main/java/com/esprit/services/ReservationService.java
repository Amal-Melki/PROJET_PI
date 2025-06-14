package com.esprit.services;

import com.esprit.models.Reservation;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationService implements IService<Reservation> {
    private Connection connection;

    public ReservationService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(Reservation reservation) {
        String req = "INSERT INTO reservation (date_res, id_user, id_ev, status) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, reservation.getDate_res());
            pst.setInt(2, reservation.getId_user());
            pst.setInt(3, reservation.getId_ev());
            pst.setString(4, reservation.getStatus());
            pst.executeUpdate();
            
            // Get the generated ID
            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                reservation.setId_res(generatedKeys.getInt(1));
            }
            
            System.out.println("Réservation ajoutée avec ID: " + reservation.getId_res());
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Reservation reservation) {
        String req = "UPDATE reservation SET status=? WHERE id_res=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, reservation.getStatus());
            pst.setInt(2, reservation.getId_res());
            int rowsAffected = pst.executeUpdate();
            System.out.println("Réservation modifiée. ID: " + reservation.getId_res() + ", Status: " + reservation.getStatus() + ", Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(Reservation reservation) {
        String req = "DELETE FROM reservation WHERE id_res=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, reservation.getId_res());
            pst.executeUpdate();
            System.out.println("Réservation supprimée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Reservation> rechercher() {
        List<Reservation> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                reservations.add(new Reservation(
                    rs.getInt("id_res"),
                    rs.getString("date_res"),
                    rs.getInt("id_user"),
                    rs.getInt("id_ev"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return reservations;
    }

    public List<Reservation> rechercherParClient(int idUser) {
        List<Reservation> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation WHERE id_user=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, idUser);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                reservations.add(new Reservation(
                    rs.getInt("id_res"),
                    rs.getString("date_res"),
                    rs.getInt("id_user"),
                    rs.getInt("id_ev"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return reservations;
    }

    public List<Reservation> rechercherParEvenement(int idEvenement) {
        List<Reservation> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation WHERE id_ev=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, idEvenement);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                reservations.add(new Reservation(
                    rs.getInt("id_res"),
                    rs.getString("date_res"),
                    rs.getInt("id_user"),
                    rs.getInt("id_ev"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return reservations;
    }
} 