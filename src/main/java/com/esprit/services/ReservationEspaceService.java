package com.esprit.services;

import com.esprit.modules.Espace;
import com.esprit.modules.ReservationEspace;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationEspaceService implements Iservice {

    private final Connection connection;

    public ReservationEspaceService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void add(Object o) {
        ReservationEspace reservation = (ReservationEspace) o;
        String sql = "INSERT INTO reservationespace(espaceId, user_id, dateDebut, dateFin) VALUES(?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reservation.getEspace().getId());
            ps.setInt(2, reservation.getUserId());
            ps.setDate(3, new java.sql.Date(reservation.getDateDebut().getTime()));
            ps.setDate(4, new java.sql.Date(reservation.getDateFin().getTime()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucune ligne ajoutée.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    reservation.setReservationId(rs.getInt(1));
                }
            }

            System.out.println("Réservation ajoutée avec ID : " + reservation.getReservationId());

        } catch (SQLException e) {
            System.out.println("Erreur en ajout : " + e.getMessage());
        }
    }

    @Override
    public void update(Object o) {
        ReservationEspace reservation = (ReservationEspace) o;
        String sql = "UPDATE reservationespace SET espaceId = ?, user_id = ?, dateDebut = ?, dateFin = ? WHERE reservationId= ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservation.getEspace().getId());
            ps.setInt(2, reservation.getUserId());
            ps.setDate(3, new java.sql.Date(reservation.getDateDebut().getTime()));
            ps.setDate(4, new java.sql.Date(reservation.getDateFin().getTime()));
            ps.setInt(5, reservation.getReservationId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucune ligne modifiée.");
            }

            System.out.println("Réservation mise à jour avec l'ID : " + reservation.getReservationId());

        } catch (SQLException e) {
            System.out.println("Erreur en mise à jour : " + e.getMessage());
        }
    }

    @Override
    public void delete(Object o) {
        ReservationEspace reservation = (ReservationEspace) o;
        String sql = "DELETE FROM reservationespace WHERE reservationId= ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservation.getReservationId());
            ps.executeUpdate();
            System.out.println("Réservation supprimée.");
        } catch (SQLException e) {
            System.out.println("Erreur en suppression : " + e.getMessage());
        }
    }

    @Override
    public List<ReservationEspace> get() {
        List<ReservationEspace> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservationespace";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Espace espace = new Espace();
                espace.setId(rs.getInt("espaceId"));

                ReservationEspace reservation = new ReservationEspace(
                        rs.getInt("reservationId"),
                        espace,
                        rs.getDate("dateDebut"),
                        rs.getDate("dateFin"),
                        rs.getInt("user_id"),
                        null
                );
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération : " + e.getMessage());
        }

        return reservations;
    }
}
