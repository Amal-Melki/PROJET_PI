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
        String sql = "INSERT INTO reservationEspace(reservationId, espaceId, dateDebut, dateFin, utilisateur) VALUES(?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reservation.getEspace().getId());
            ps.setDate(2, new java.sql.Date(reservation.getDateDebut().getTime()));
            ps.setDate(3, new java.sql.Date(reservation.getDateFin().getTime()));
            ps.setString(4, reservation.getUtilisateur());

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

            // Gestion des équipements si tu veux les ajouter dans une table de liaison.
            // Tu peux ignorer cette partie si tu ne gères pas les équipements

        } catch (SQLException e) {
            System.out.println("Erreur en ajout : " + e.getMessage());
        }
    }

    @Override
    public void update(Object o) {
        ReservationEspace reservation = (ReservationEspace) o;
        String sql = "UPDATE reservationEspace SET espaceId = ?, date_debut = ?, date_fin = ?, utilisateur = ? WHERE reservationId= ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservation.getEspace().getId());
            ps.setDate(2, new java.sql.Date(reservation.getDateDebut().getTime()));
            ps.setDate(3, new java.sql.Date(reservation.getDateFin().getTime()));
            ps.setString(4, reservation.getUtilisateur());
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
        String sql = "DELETE FROM reservationEspace WHERE reservationId= ?";

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
        String sql = "SELECT * FROM reservationEspace";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                // Attention : ici, il faut que tu charges l'espace depuis son ID
                // soit avec une méthode getEspaceById(id), soit tu crées un Espace vide avec juste l’ID
                Espace espace = new Espace();
                espace.setId(rs.getInt("espaceId"));

                ReservationEspace r = new ReservationEspace(
                        rs.getInt("reservationId"),
                        espace,
                        rs.getDate("date_debut"),
                        rs.getDate("date_fin"),
                        rs.getString("utilisateur"),
                        null // Tu peux charger les équipements séparément si nécessaire
                );
                reservations.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération : " + e.getMessage());
        }

        return reservations;
    }
}
