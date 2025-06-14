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


    public void ajouteradmin(ReservationMateriel r) {
        try {
            ServiceMateriel serviceMateriel = new ServiceMateriel();
            int quantiteStock = serviceMateriel.getQuantiteById(r.getMaterielId());
            double prixUnitaire = serviceMateriel.getPrixById(r.getMaterielId());
            double montantTotal = prixUnitaire * r.getQuantiteReservee();

            if (quantiteStock >= r.getQuantiteReservee()) {
                int nouvelleQuantite = quantiteStock - r.getQuantiteReservee();
                serviceMateriel.mettreAJourQuantite(r.getMaterielId(), nouvelleQuantite);

                // ✅ Ajout de la colonne id_client dans la requête
                String req = "INSERT INTO reservation_materiel (materiel_id, dateDebut, dateFin, quantiteReservee, statut, montant_total) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(req)) {
                    ps.setInt(1, r.getMaterielId());
                    ps.setDate(2, r.getDateDebut());
                    ps.setDate(3, r.getDateFin());
                    ps.setInt(4, r.getQuantiteReservee());
                    ps.setString(5, r.getStatut());
                    ps.setDouble(6, montantTotal);
                     // ✅ insertion du client
                    ps.executeUpdate();
                }
                System.out.println("✅ Réservation ajoutée avec id_client.");
            } else {
                System.err.println("❌ Stock insuffisant pour ajouter la réservation.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la réservation : " + e.getMessage());
        }
    }


    @Override
    public void ajouter(ReservationMateriel r) {
        try {
            ServiceMateriel serviceMateriel = new ServiceMateriel();
            int quantiteStock = serviceMateriel.getQuantiteById(r.getMaterielId());
            double prixUnitaire = serviceMateriel.getPrixById(r.getMaterielId());
            double montantTotal = prixUnitaire * r.getQuantiteReservee();

            if (quantiteStock >= r.getQuantiteReservee()) {
                int nouvelleQuantite = quantiteStock - r.getQuantiteReservee();
                serviceMateriel.mettreAJourQuantite(r.getMaterielId(), nouvelleQuantite);

                // ✅ Ajout de la colonne id_client dans la requête
                String req = "INSERT INTO reservation_materiel (materiel_id, dateDebut, dateFin, quantiteReservee, statut, montant_total, id_client) VALUES (?, ?, ?, ?, ?, ?, 6)";
                try (PreparedStatement ps = connection.prepareStatement(req)) {
                    ps.setInt(1, r.getMaterielId());
                    ps.setDate(2, r.getDateDebut());
                    ps.setDate(3, r.getDateFin());
                    ps.setInt(4, r.getQuantiteReservee());
                    ps.setString(5, r.getStatut());
                    ps.setDouble(6, montantTotal);
                    // ✅ insertion du client
                    ps.executeUpdate();
                }
                System.out.println("✅ Réservation ajoutée avec id_client.");
            } else {
                System.err.println("❌ Stock insuffisant pour ajouter la réservation.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la réservation : " + e.getMessage());
        }
    }


    @Override
    public void modifier(ReservationMateriel r) {
        try {
            ReservationMateriel ancienne = getReservationById(r.getId());
            if (ancienne == null) {
                System.err.println("❌ Réservation introuvable.");
                return;
            }

            ServiceMateriel serviceMateriel = new ServiceMateriel();
            int stock = serviceMateriel.getQuantiteById(ancienne.getMaterielId());

            if (ancienne.getMaterielId() == r.getMaterielId()) {
                int stockTemp = stock + ancienne.getQuantiteReservee();
                if (stockTemp >= r.getQuantiteReservee()) {
                    serviceMateriel.mettreAJourQuantite(r.getMaterielId(), stockTemp - r.getQuantiteReservee());
                } else {
                    System.err.println("❌ Stock insuffisant pour modification.");
                    return;
                }
            } else {
                int stockAncien = serviceMateriel.getQuantiteById(ancienne.getMaterielId());
                int stockNouveau = serviceMateriel.getQuantiteById(r.getMaterielId());

                serviceMateriel.mettreAJourQuantite(ancienne.getMaterielId(), stockAncien + ancienne.getQuantiteReservee());

                if (stockNouveau >= r.getQuantiteReservee()) {
                    serviceMateriel.mettreAJourQuantite(r.getMaterielId(), stockNouveau - r.getQuantiteReservee());
                } else {
                    System.err.println("❌ Stock insuffisant sur le nouveau matériel.");
                    return;
                }
            }

            double prixUnitaire = serviceMateriel.getPrixById(r.getMaterielId());
            double montantTotal = prixUnitaire * r.getQuantiteReservee();

            String req = "UPDATE reservation_materiel SET materiel_id = ?, dateDebut = ?, dateFin = ?, quantiteReservee = ?, statut = ?, montant_total = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(req)) {
                ps.setInt(1, r.getMaterielId());
                ps.setDate(2, r.getDateDebut());
                ps.setDate(3, r.getDateFin());
                ps.setInt(4, r.getQuantiteReservee());
                ps.setString(5, r.getStatut());
                ps.setDouble(6, montantTotal);
                ps.setInt(7, r.getId());
                ps.executeUpdate();
            }

            System.out.println("✅ Réservation modifiée avec mise à jour du montant.");
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
            if (reservation == null) return;

            String req = "DELETE FROM reservation_materiel WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(req)) {
                ps.setInt(1, idReservation);
                int deleted = ps.executeUpdate();
                if (deleted > 0) {
                    ServiceMateriel serviceMateriel = new ServiceMateriel();
                    int stock = serviceMateriel.getQuantiteById(reservation.getMaterielId());
                    serviceMateriel.mettreAJourQuantite(reservation.getMaterielId(), stock + reservation.getQuantiteReservee());
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression : " + e.getMessage());
        }
    }

    @Override
    public List<ReservationMateriel> rechercher() {
        List<ReservationMateriel> list = new ArrayList<>();
        String req = "SELECT * FROM reservation_materiel";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {

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
            System.err.println("❌ Erreur récupération réservations : " + e.getMessage());
        }

        return list;
    }

    public ReservationMateriel getReservationById(int id) {
        String req = "SELECT * FROM reservation_materiel WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
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

                return r;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getReservationById : " + e.getMessage());
        }
        return null;
    }
}
