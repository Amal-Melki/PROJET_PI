package com.esprit.services;

import com.esprit.modules.ReservationMateriel;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.time.LocalDate; // Import nécessaire pour LocalDate
import java.util.ArrayList;
import java.util.List;

public class ServiceReservationsClient {

    private final Connection connection;

    public ServiceReservationsClient() {
        connection = DataSource.getInstance().getConnection();
    }

    public List<ReservationMateriel> recupererParClient(int clientId) {
        List<ReservationMateriel> list = new ArrayList<>();
        String req = "SELECT id, materiel_id, dateDebut, dateFin, quantiteReservee, statut, montant_total, id_client FROM reservation_materiel WHERE id_client = ?";

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
            e.printStackTrace(); // Ajoutez ceci pour un meilleur débogage
        }

        return list;
    }

    public boolean annulerReservation(int reservationId) {
        // Avant de supprimer, récupérez la réservation pour mettre à jour le stock du matériel.
        ReservationMateriel reservationPourAnnuler = null;
        try (PreparedStatement ps = connection.prepareStatement("SELECT materiel_id, quantiteReservee FROM reservation_materiel WHERE id = ?")) {
            ps.setInt(1, reservationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                reservationPourAnnuler = new ReservationMateriel(); // Créez un objet temporaire
                reservationPourAnnuler.setMaterielId(rs.getInt("materiel_id"));
                reservationPourAnnuler.setQuantiteReservee(rs.getInt("quantiteReservee"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des détails pour annuler la réservation : " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        if (reservationPourAnnuler == null) {
            System.err.println("❌ Réservation introuvable pour annulation (ID: " + reservationId + ").");
            return false;
        }

        String req = "DELETE FROM reservation_materiel WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, reservationId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                // Mettre à jour le stock du matériel après annulation
                ServiceMateriel serviceMateriel = new ServiceMateriel(); // Assurez-vous que ServiceMateriel est accessible
                int stockActuel = serviceMateriel.getQuantiteById(reservationPourAnnuler.getMaterielId());
                serviceMateriel.mettreAJourQuantite(reservationPourAnnuler.getMaterielId(), stockActuel + reservationPourAnnuler.getQuantiteReservee());
                System.out.println("✅ Réservation " + reservationId + " annulée et stock matériel mis à jour.");
                return true;
            } else {
                System.err.println("❌ Aucune réservation annulée (ID: " + reservationId + " non trouvé).");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'annulation de la réservation : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean modifierReservation(ReservationMateriel r) {
        // Pour modifier une réservation, il faut aussi gérer les stocks comme dans ServiceReservationMateriel
        // Récupérez l'ancienne réservation pour comparer les quantités et le matériel
        ReservationMateriel ancienneReservation = null;
        try (PreparedStatement ps = connection.prepareStatement("SELECT materiel_id, quantiteReservee FROM reservation_materiel WHERE id = ?")) {
            ps.setInt(1, r.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ancienneReservation = new ReservationMateriel(); // Créez un objet temporaire
                ancienneReservation.setMaterielId(rs.getInt("materiel_id"));
                ancienneReservation.setQuantiteReservee(rs.getInt("quantiteReservee"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de l'ancienne réservation pour modification : " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        if (ancienneReservation == null) {
            System.err.println("❌ Réservation introuvable pour modification (ID: " + r.getId() + ").");
            return false;
        }

        ServiceMateriel serviceMateriel = new ServiceMateriel();

        try {
            // Logique de mise à jour des stocks similaire à ServiceReservationMateriel.modifier()
            if (ancienneReservation.getMaterielId() == r.getMaterielId()) {
                // Même matériel : Ajuster la quantité sur le matériel existant
                int stockActuel = serviceMateriel.getQuantiteById(r.getMaterielId());
                int stockApresRendu = stockActuel + ancienneReservation.getQuantiteReservee();
                if (stockApresRendu >= r.getQuantiteReservee()) {
                    serviceMateriel.mettreAJourQuantite(r.getMaterielId(), stockApresRendu - r.getQuantiteReservee());
                } else {
                    System.err.println("❌ Stock insuffisant pour la nouvelle quantité sur le même matériel.");
                    return false;
                }
            } else {
                // Matériel différent : Remettre l'ancien stock, déduire du nouveau
                serviceMateriel.mettreAJourQuantite(ancienneReservation.getMaterielId(), serviceMateriel.getQuantiteById(ancienneReservation.getMaterielId()) + ancienneReservation.getQuantiteReservee());
                int stockNouveauMateriel = serviceMateriel.getQuantiteById(r.getMaterielId());
                if (stockNouveauMateriel >= r.getQuantiteReservee()) {
                    serviceMateriel.mettreAJourQuantite(r.getMaterielId(), stockNouveauMateriel - r.getQuantiteReservee());
                } else {
                    System.err.println("❌ Stock insuffisant sur le nouveau matériel pour la modification.");
                    return false;
                }
            }

            // Calcul du nouveau montant total
            double prixUnitaire = serviceMateriel.getPrixById(r.getMaterielId());
            r.setMontantTotal(prixUnitaire * r.getQuantiteReservee());


            String req = "UPDATE reservation_materiel SET materiel_id = ?, dateDebut = ?, dateFin = ?, quantiteReservee = ?, statut = ?, montant_total = ? WHERE id = ?";

            try (PreparedStatement ps = connection.prepareStatement(req)) {
                ps.setInt(1, r.getMaterielId()); // Assurez-vous que materiel_id est mis à jour si le matériel change
                ps.setDate(2, Date.valueOf(r.getDateDebut())); // Conversion LocalDate en java.sql.Date
                ps.setDate(3, Date.valueOf(r.getDateFin()));   // Conversion LocalDate en java.sql.Date
                ps.setInt(4, r.getQuantiteReservee());
                ps.setString(5, r.getStatut()); // Assurez-vous que le statut peut être modifié si désiré
                ps.setDouble(6, r.getMontantTotal());
                ps.setInt(7, r.getId());

                int rowsUpdated = ps.executeUpdate();
                System.out.println("✅ Réservation " + r.getId() + " modifiée avec succès.");
                return rowsUpdated > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de la réservation : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cette méthode existe déjà dans ServiceReservationMateriel, mais si elle est utilisée ici
    // pour un contexte client, assurez-vous qu'elle est adaptée.
    // Votre code initial avait une méthode "supprimer" ici qui est un doublon du "annulerReservation"
    // et ne gérait pas le stock. Je l'ai supprimée car "annulerReservation" est plus complète.
    // Si vous aviez l'intention d'avoir une suppression sans impact sur le stock, clarifiez.
    // Par convention, DELETE d'une réservation devrait remettre le stock.
    // Je laisse la méthode supprimer() si elle a un usage différent dans votre logique métier.
    public boolean supprimer(ReservationMateriel r) {
        String req = "DELETE FROM reservation_materiel WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, r.getId());
            int rowsAffected = ps.executeUpdate();
            // Ici, pas de remise en stock par défaut, car 'annulerReservation' le fait.
            // Si c'est une "suppression pure" (par ex. pour une entrée erronée qui n'a pas impacté le stock)
            // alors c'est ok. Sinon, reconsidérez d'appeler la logique de stock ici.
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression réservation client : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}