package com.esprit.services;

import com.esprit.modules.ReservationMateriel;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservationMateriel implements IService<ReservationMateriel> {

    private final Connection connection;

    public ServiceReservationMateriel() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(ReservationMateriel r) {
        try {
            ServiceMateriel serviceMateriel = new ServiceMateriel();
            int quantiteStock = serviceMateriel.getQuantiteById(r.getMaterielId());

            if (quantiteStock >= r.getQuantiteReservee()) {
                // Réserver : on réduit le stock
                int nouvelleQuantite = quantiteStock - r.getQuantiteReservee();
                serviceMateriel.mettreAJourQuantite(r.getMaterielId(), nouvelleQuantite);

                String req = "INSERT INTO reservation_materiel (materiel_id, dateDebut, dateFin, quantiteReservee, statut) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(req)) {
                    ps.setInt(1, r.getMaterielId());
                    ps.setDate(2, r.getDateDebut());
                    ps.setDate(3, r.getDateFin());
                    ps.setInt(4, r.getQuantiteReservee());
                    ps.setString(5, r.getStatut());
                    ps.executeUpdate();
                }
                System.out.println("✅ Réservation ajoutée et stock mis à jour !");
            } else {
                System.err.println("❌ Stock insuffisant pour ajouter la réservation.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la réservation : " + e.getMessage());
        }
    }

    @Override
    public void modifier(ReservationMateriel nouvelleReservation) {
        try {
            ReservationMateriel ancienne = getReservationById(nouvelleReservation.getId());

            if (ancienne == null) {
                System.err.println("❌ Ancienne réservation introuvable !");
                return;
            }

            ServiceMateriel serviceMateriel = new ServiceMateriel();

            // Remise à zéro du stock avec l'ancienne quantité
            int stock = serviceMateriel.getQuantiteById(ancienne.getMaterielId());
            if (ancienne.getMaterielId() == nouvelleReservation.getMaterielId()) {
                int stockTemp = stock + ancienne.getQuantiteReservee();

                if (stockTemp >= nouvelleReservation.getQuantiteReservee()) {
                    serviceMateriel.mettreAJourQuantite(nouvelleReservation.getMaterielId(), stockTemp - nouvelleReservation.getQuantiteReservee());
                } else {
                    System.err.println("❌ Stock insuffisant pour la modification !");
                    return;
                }

            } else {
                // Autre matériel : on restitue à l'ancien et débite le nouveau
                int stockAncien = serviceMateriel.getQuantiteById(ancienne.getMaterielId());
                int stockNouveau = serviceMateriel.getQuantiteById(nouvelleReservation.getMaterielId());

                // Restitution à l'ancien
                serviceMateriel.mettreAJourQuantite(ancienne.getMaterielId(), stockAncien + ancienne.getQuantiteReservee());

                // Déduction du nouveau
                if (stockNouveau >= nouvelleReservation.getQuantiteReservee()) {
                    serviceMateriel.mettreAJourQuantite(nouvelleReservation.getMaterielId(), stockNouveau - nouvelleReservation.getQuantiteReservee());
                } else {
                    System.err.println("❌ Stock insuffisant sur le nouveau matériel !");
                    return;
                }
            }

            // Mise à jour en base
            String req = "UPDATE reservation_materiel SET materiel_id = ?, dateDebut = ?, dateFin = ?, quantiteReservee = ?, statut = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(req)) {
                ps.setInt(1, nouvelleReservation.getMaterielId());
                ps.setDate(2, nouvelleReservation.getDateDebut());
                ps.setDate(3, nouvelleReservation.getDateFin());
                ps.setInt(4, nouvelleReservation.getQuantiteReservee());
                ps.setString(5, nouvelleReservation.getStatut());
                ps.setInt(6, nouvelleReservation.getId());
                ps.executeUpdate();
            }

            System.out.println("✅ Réservation modifiée avec succès.");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(ReservationMateriel r) {
        supprimer2(r.getId());
    }

    public void supprimer2(int idReservation) {
        try {
            ReservationMateriel reservation = getReservationById(idReservation);
            if (reservation == null) {
                System.err.println("❌ Réservation introuvable !");
                return;
            }

            // ✅ D'abord suppression
            String req = "DELETE FROM reservation_materiel WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(req)) {
                ps.setInt(1, idReservation);
                int deleted = ps.executeUpdate();

                if (deleted > 0) {
                    // ✅ Ensuite mise à jour du stock
                    ServiceMateriel serviceMateriel = new ServiceMateriel();
                    int stock = serviceMateriel.getQuantiteById(reservation.getMaterielId());
                    serviceMateriel.mettreAJourQuantite(reservation.getMaterielId(), stock + reservation.getQuantiteReservee());
                    System.out.println("✅ Réservation supprimée et stock restauré.");
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public List<ReservationMateriel> recuperer() {
        List<ReservationMateriel> list = new ArrayList<>();
        String req = "SELECT * FROM reservation_materiel";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                list.add(new ReservationMateriel(
                        rs.getInt("id"),
                        rs.getInt("materiel_id"),
                        rs.getDate("dateDebut").toLocalDate(),
                        rs.getDate("dateFin").toLocalDate(),
                        rs.getInt("quantiteReservee"),
                        rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des réservations : " + e.getMessage());
        }

        return list;
    }

    public ReservationMateriel getReservationById(int id) {
        String req = "SELECT * FROM reservation_materiel WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ReservationMateriel(
                        rs.getInt("id"),
                        rs.getInt("materiel_id"),
                        rs.getDate("dateDebut").toLocalDate(),
                        rs.getDate("dateFin").toLocalDate(),
                        rs.getInt("quantiteReservee"),
                        rs.getString("statut")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getReservationById : " + e.getMessage());
        }
        return null;
    }
}
