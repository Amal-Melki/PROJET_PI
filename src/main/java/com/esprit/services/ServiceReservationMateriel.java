package com.esprit.services;

import com.esprit.modules.ReservationMateriel;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservationMateriel implements IService<ReservationMateriel> {

    private Connection connection;

    public ServiceReservationMateriel() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(ReservationMateriel r) {
        String req = "INSERT INTO reservation_materiel(materiel_id, dateDebut, dateFin, quantiteReservee, statut) VALUES ("
                + r.getMaterielId() + ", '"
                + r.getDateDebut() + "', '"
                + r.getDateFin() + "', "
                + r.getQuantiteReservee() + ", '"
                + r.getStatut() + "')";

        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Réservation ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(ReservationMateriel r) {
        String req = "UPDATE reservation_materiel SET materiel_id=" + r.getMaterielId()
                + ", dateDebut='" + r.getDateDebut()
                + "', dateFin='" + r.getDateFin()
                + "', quantiteReservee=" + r.getQuantiteReservee()
                + ", statut='" + r.getStatut()
                + "' WHERE id=" + r.getId();

        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Réservation modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(ReservationMateriel reservationMateriel) {
        // Pas utilisé, on utilise supprimer2
    }

    public void supprimer2(int idReservation) {
        try {
            // 🔥 Récupérer d'abord la réservation avant suppression
            ReservationMateriel reservation = getReservationById(idReservation);

            if (reservation != null) {
                // 🔥 Mettre à jour la quantité du matériel
                ServiceMateriel serviceMateriel = new ServiceMateriel();
                int idMateriel = reservation.getMaterielId();
                int quantiteARecuperer = reservation.getQuantiteReservee();

                // Récupérer la quantité actuelle
                int quantiteActuelle = serviceMateriel.recupererQuantite(idMateriel);
                int nouvelleQuantite = quantiteActuelle + quantiteARecuperer;

                serviceMateriel.mettreAJourQuantite(idMateriel, nouvelleQuantite);
            }

            // ❌ Ensuite supprimer la réservation
            String sql = "DELETE FROM reservation_materiel WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, idReservation);
                ps.executeUpdate();
                System.out.println("Réservation supprimée !");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public List<ReservationMateriel> recuperer() {
        List<ReservationMateriel> list = new ArrayList<>();
        String req = "SELECT * FROM reservation_materiel";

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);

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
            System.out.println(e.getMessage());
        }

        return list;
    }

    // 🔥 Méthode utilitaire pour récupérer une réservation spécifique par ID
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
            System.out.println(e.getMessage());
        }
        return null;
    }
}
