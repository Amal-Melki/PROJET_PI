package com.esprit.services;

import com.esprit.modules.ReservationMateriel;
import com.esprit.utils.DataSource;
import java.sql.*;
import java.time.LocalDate; // Import nécessaire pour LocalDate
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

                // Note: La colonne id_client n'est pas insérée ici. Si elle est NOT NULL en DB, cela causera une erreur.
                // Considérez d'ajouter 'id_client' avec une valeur par défaut ou un paramètre si nécessaire.
                String req = "INSERT INTO reservation_materiel (materiel_id, dateDebut, dateFin, quantiteReservee, statut, montant_total) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(req)) {
                    ps.setInt(1, r.getMaterielId());
                    ps.setDate(2, Date.valueOf(r.getDateDebut())); // Conversion LocalDate en java.sql.Date
                    ps.setDate(3, Date.valueOf(r.getDateFin()));   // Conversion LocalDate en java.sql.Date
                    ps.setInt(4, r.getQuantiteReservee());
                    ps.setString(5, r.getStatut());
                    ps.setDouble(6, montantTotal);
                    ps.executeUpdate();
                }
                System.out.println("✅ Réservation ajoutée avec succès (par admin).");
            } else {
                System.err.println("❌ Stock insuffisant pour ajouter la réservation.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la réservation (admin) : " + e.getMessage());
            e.printStackTrace(); // Pour un débogage complet
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

                // Utilisation de r.getIdClient() pour insérer l'ID du client.
                String req = "INSERT INTO reservation_materiel (materiel_id, dateDebut, dateFin, quantiteReservee, statut, montant_total, id_client) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(req)) {
                    ps.setInt(1, r.getMaterielId());
                    ps.setDate(2, Date.valueOf(r.getDateDebut())); // Conversion LocalDate en java.sql.Date
                    ps.setDate(3, Date.valueOf(r.getDateFin()));   // Conversion LocalDate en java.sql.Date
                    ps.setInt(4, r.getQuantiteReservee());
                    ps.setString(5, r.getStatut());
                    ps.setDouble(6, montantTotal);
                    ps.setInt(7, r.getIdClient()); // Utilisation de l'idClient de l'objet ReservationMateriel
                    ps.executeUpdate();
                }
                System.out.println("✅ Réservation ajoutée avec succès.");
            } else {
                System.err.println("❌ Stock insuffisant pour ajouter la réservation.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la réservation : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(ReservationMateriel r) {
        try {
            ReservationMateriel ancienne = getReservationById(r.getId());
            if (ancienne == null) {
                System.err.println("❌ Réservation introuvable pour la modification.");
                return;
            }

            ServiceMateriel serviceMateriel = new ServiceMateriel();
            int stockAncienMateriel = serviceMateriel.getQuantiteById(ancienne.getMaterielId());

            if (ancienne.getMaterielId() == r.getMaterielId()) {
                // Le matériel reste le même : Rétablir l'ancienne quantité, puis déduire la nouvelle
                int stockDisponible = stockAncienMateriel + ancienne.getQuantiteReservee();
                if (stockDisponible >= r.getQuantiteReservee()) {
                    serviceMateriel.mettreAJourQuantite(r.getMaterielId(), stockDisponible - r.getQuantiteReservee());
                } else {
                    System.err.println("❌ Stock insuffisant pour modifier la quantité sur le même matériel.");
                    return;
                }
            } else {
                // Le matériel a changé : Rétablir le stock de l'ancien matériel, puis déduire du nouveau
                serviceMateriel.mettreAJourQuantite(ancienne.getMaterielId(), stockAncienMateriel + ancienne.getQuantiteReservee());

                int stockNouveauMateriel = serviceMateriel.getQuantiteById(r.getMaterielId());
                if (stockNouveauMateriel >= r.getQuantiteReservee()) {
                    serviceMateriel.mettreAJourQuantite(r.getMaterielId(), stockNouveauMateriel - r.getQuantiteReservee());
                } else {
                    System.err.println("❌ Stock insuffisant sur le nouveau matériel pour la modification.");
                    return;
                }
            }

            double prixUnitaire = serviceMateriel.getPrixById(r.getMaterielId());
            double montantTotal = prixUnitaire * r.getQuantiteReservee();

            String req = "UPDATE reservation_materiel SET materiel_id = ?, dateDebut = ?, dateFin = ?, quantiteReservee = ?, statut = ?, montant_total = ?, id_client = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(req)) {
                ps.setInt(1, r.getMaterielId());
                ps.setDate(2, Date.valueOf(r.getDateDebut())); // Conversion LocalDate
                ps.setDate(3, Date.valueOf(r.getDateFin()));   // Conversion LocalDate
                ps.setInt(4, r.getQuantiteReservee());
                ps.setString(5, r.getStatut());
                ps.setDouble(6, montantTotal);
                ps.setInt(7, r.getIdClient()); // Mise à jour de l'id_client
                ps.setInt(8, r.getId());
                ps.executeUpdate();
            }

            System.out.println("✅ Réservation modifiée avec mise à jour du montant et des stocks.");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification : " + e.getMessage());
            e.printStackTrace();
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
                System.err.println("❌ Réservation à supprimer introuvable.");
                return;
            }

            String req = "DELETE FROM reservation_materiel WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(req)) {
                ps.setInt(1, idReservation);
                int deletedRows = ps.executeUpdate();
                if (deletedRows > 0) {
                    ServiceMateriel serviceMateriel = new ServiceMateriel();
                    int stockActuel = serviceMateriel.getQuantiteById(reservation.getMaterielId());
                    serviceMateriel.mettreAJourQuantite(reservation.getMaterielId(), stockActuel + reservation.getQuantiteReservee());
                    System.out.println("✅ Réservation supprimée et stock matériel mis à jour.");
                } else {
                    System.err.println("❌ Aucune réservation supprimée (ID non trouvé ou autre problème).");
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression de la réservation : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<ReservationMateriel> rechercher() {
        List<ReservationMateriel> list = new ArrayList<>();
        // Sélection explicite des colonnes, y compris id_client
        String req = "SELECT id, materiel_id, dateDebut, dateFin, quantiteReservee, statut, montant_total, id_client FROM reservation_materiel";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) { // Ligne de déclaration du ResultSet corrigée

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
                r.setIdClient(rs.getInt("id_client")); // Récupération de l'id_client
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération réservations : " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public ReservationMateriel getReservationById(int id) {
        // Sélection explicite des colonnes, y compris id_client
        String req = "SELECT id, materiel_id, dateDebut, dateFin, quantiteReservee, statut, montant_total, id_client FROM reservation_materiel WHERE id = ?";
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
                r.setIdClient(rs.getInt("id_client")); // Récupération de l'id_client

                return r;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getReservationById : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
