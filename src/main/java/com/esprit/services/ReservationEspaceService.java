package com.esprit.services;

import com.esprit.modules.ReservationEspace;
import com.esprit.utils.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class ReservationEspaceService {

    private Connection connection;
    private EmailService emailService;

    public ReservationEspaceService() {
        connection = DataSource.getInstance().getConnection();
        emailService = new EmailService();
    }

    public void add(ReservationEspace r) {
        String req = "INSERT INTO reservationespace(espaceId, user, dateDebut, dateFin) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, r.getEspaceId());
            pst.setString(2, r.getUser());
            pst.setDate(3, Date.valueOf(r.getDateDebut()));
            pst.setDate(4, Date.valueOf(r.getDateFin()));
            pst.executeUpdate();
            System.out.println(" Reservation added !");
        } catch (SQLException e) {
            System.out.println(" Error adding reservation: " + e.getMessage());
        }
    }

    public void update(ReservationEspace r) {
        String req = "UPDATE reservationespace SET espaceId=?, user=?, dateDebut=?, dateFin=? WHERE reservationId=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, r.getEspaceId());
            pst.setString(2, r.getUser());
            pst.setDate(3, Date.valueOf(r.getDateDebut()));
            pst.setDate(4, Date.valueOf(r.getDateFin()));
            pst.setInt(5, r.getReservationId());
            pst.executeUpdate();
            System.out.println(" Reservation updated !");
        } catch (SQLException e) {
            System.out.println(" Error updating reservation: " + e.getMessage());
        }
    }

    public void delete(int reservationId) {
        String req = "DELETE FROM reservationespace WHERE reservationId=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, reservationId);
            pst.executeUpdate();
            System.out.println(" Reservation deleted !");
        } catch (SQLException e) {
            System.out.println(" Error deleting reservation: " + e.getMessage());
        }
    }

    public List<ReservationEspace> getAll() {
        List<ReservationEspace> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservationespace";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ReservationEspace r = new ReservationEspace(
                        rs.getInt("espaceId"),
                        rs.getString("user"),
                        rs.getDate("dateDebut").toLocalDate(),
                        rs.getDate("dateFin").toLocalDate()
                );
                reservations.add(r);
            }
        } catch (SQLException e) {
            System.out.println(" Error fetching reservations: " + e.getMessage());
        }
        return reservations;
    }

    public List<ReservationEspace> getReservationsBetweenDates(LocalDate start, LocalDate end) {
        List<ReservationEspace> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservationespace WHERE dateDebut >= ? AND dateFin <= ?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setDate(1, Date.valueOf(start));
            pst.setDate(2, Date.valueOf(end));

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ReservationEspace r = new ReservationEspace(
                        rs.getInt("espaceId"),
                        rs.getString("user"),
                        rs.getDate("dateDebut").toLocalDate(),
                        rs.getDate("dateFin").toLocalDate()
                );
                r.setReservationId(rs.getInt("reservationId"));
                reservations.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des réservations entre deux dates : " + e.getMessage());
        }
        return reservations;
    }

    public void creerReservation(String currentUser, int espaceId, LocalDate dateDebut, LocalDate dateFin) {
        ReservationEspace r = new ReservationEspace(espaceId, currentUser, dateDebut, dateFin);
        add(r);
        try {
            String subject = "Confirmation de réservation";
            String body = "Bonjour " + currentUser + ",\n\nVotre réservation pour l'espace " + espaceId + " du " + dateDebut + " au " + dateFin + " a été confirmée.\n\nMerci.";
            emailService.sendEmail(currentUser, subject, body);
        } catch (IOException e) {
            System.out.println("Erreur lors de l'envoi de l'email de confirmation : " + e.getMessage());
        }
    }
}
