package com.esprit.services;

import com.esprit.modules.Materiels;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceMateriel implements IService<Materiels> {

    private Connection connection;

    public ServiceMateriel() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(Materiels materiel) {
        String req = "INSERT INTO materiel(nom, type, quantite, etat, description) VALUES ('"
                + materiel.getNom() + "', '"
                + materiel.getType() + "', "
                + materiel.getQuantite() + ", '"
                + materiel.getEtat() + "', '"
                + materiel.getDescription() + "')";

        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Matériel ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du matériel : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Materiels materiel) {
        String req = "UPDATE materiel SET nom='" + materiel.getNom() +
                "', type='" + materiel.getType() +
                "', quantite=" + materiel.getQuantite() +
                ", etat='" + materiel.getEtat() +
                "', description='" + materiel.getDescription() +
                "' WHERE id=" + materiel.getId();

        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Matériel modifié avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du matériel : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Materiels materiel) {
        String req = "DELETE FROM materiel WHERE id=" + materiel.getId();
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Matériel supprimé avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du matériel : " + e.getMessage());
        }
    }

    @Override
    public List<Materiels> recuperer() {
        List<Materiels> materiels = new ArrayList<>();
        String req = "SELECT * FROM materiel";

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                materiels.add(new Materiels(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getInt("quantite"),
                        rs.getString("etat"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des matériels : " + e.getMessage());
        }

        return materiels;
    }

    // ✅ Mettre à jour seulement la quantité
    public void mettreAJourQuantite(int idMateriel, int nouvelleQuantite) {
        String req = "UPDATE materiel SET quantite = " + nouvelleQuantite + " WHERE id = " + idMateriel;
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Quantité du matériel mise à jour !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la quantité : " + e.getMessage());
        }
    }

    // ✅ Nouvelle méthode pour récupérer uniquement la quantité actuelle
    public int recupererQuantite(int idMateriel) {
        String req = "SELECT quantite FROM materiel WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, idMateriel);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("quantite");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la quantité : " + e.getMessage());
        }
        return -1; // Retourne -1 si erreur
    }
}
