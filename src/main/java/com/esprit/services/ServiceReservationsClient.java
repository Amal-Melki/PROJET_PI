package com.esprit.services;

import com.esprit.modules.ReservationMateriel;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservationsClient {

    private final Connection connection;

    public ServiceReservationsClient() {
        connection = DataSource.getInstance().getConnection();
    }

    public List<ReservationMateriel> recupererParClient(int clientId) {
        List<ReservationMateriel> list = new ArrayList<>();
        String req = "SELECT * FROM reservation_materiel WHERE id_client = ?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReservationMateriel r = new ReservationMateriel(
                        rs.getInt("id"),
                        rs.getInt("materiel_id"),
                        rs.getDate("dateDebut").toLocalDate(),
                        rs.getDate("dateFin").toLocalDate(),
                        rs.getInt("quantiteReservee"),
                        rs.getString("statut")
                );
                r.setMontantTotal(rs.getDouble("montant_total"));
                r.setIdClient(rs.getInt("id_client"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération réservations par client : " + e.getMessage());
        }

        return list;
    }

    public boolean annulerReservation(int reservationId) {
        String req = "DELETE FROM reservation_materiel WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, reservationId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'annulation de la réservation : " + e.getMessage());
            return false;
        }
    }

    public boolean modifierReservation(ReservationMateriel r) {
        String req = "UPDATE reservation_materiel SET dateDebut = ?, dateFin = ?, quantiteReservee = ?, montant_total = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setDate(1, r.getDateDebut()); // déjà java.sql.Date
            ps.setDate(2, r.getDateFin());   // déjà java.sql.Date
            ps.setInt(3, r.getQuantiteReservee());
            ps.setDouble(4, r.getMontantTotal());
            ps.setInt(5, r.getId());

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de la réservation : " + e.getMessage());
            return false;
        }
    }
    public boolean supprimer(ReservationMateriel r) {
        String req = "DELETE FROM reservation_materiel WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, r.getId());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression réservation client : " + e.getMessage());
            return false;
        }
    }

}
