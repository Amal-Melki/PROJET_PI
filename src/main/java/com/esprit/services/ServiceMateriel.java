package com.esprit.services;

import com.esprit.modules.Materiels;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceMateriel implements IService<Materiels> {

    private final Connection connection;

    public ServiceMateriel() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(Materiels materiel) {
        String req = "INSERT INTO materiel (nom, type, quantite, etat, description, image, prix) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, materiel.getNom());
            ps.setString(2, materiel.getType());
            ps.setInt(3, materiel.getQuantite());
            ps.setString(4, materiel.getEtat());
            ps.setString(5, materiel.getDescription());
            ps.setString(6, materiel.getImage());
            ps.setDouble(7, materiel.getPrix());
            ps.executeUpdate();
            System.out.println("✅ Matériel ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout du matériel : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Materiels materiel) {
        String req = "UPDATE materiel SET nom = ?, type = ?, quantite = ?, etat = ?, description = ?, image = ?, prix = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, materiel.getNom());
            ps.setString(2, materiel.getType());
            ps.setInt(3, materiel.getQuantite());
            ps.setString(4, materiel.getEtat());
            ps.setString(5, materiel.getDescription());
            ps.setString(6, materiel.getImage());
            ps.setDouble(7, materiel.getPrix());
            ps.setInt(8, materiel.getId());
            ps.executeUpdate();
            System.out.println("✅ Matériel modifié avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification du matériel : " + e.getMessage());
        }

    }

    @Override
    public void supprimer(Materiels materiel) {
        String req = "DELETE FROM materiel WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, materiel.getId());
            ps.executeUpdate();
            System.out.println("✅ Matériel supprimé avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression du matériel : " + e.getMessage());
        }
    }

    @Override
    public List<Materiels> recuperer() {
        List<Materiels> materiels = new ArrayList<>();
        String req = "SELECT * FROM materiel";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                materiels.add(new Materiels(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getInt("quantite"),
                        rs.getString("etat"),
                        rs.getString("description"),
                        rs.getString("image"),
                        rs.getDouble("prix")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des matériels : " + e.getMessage());
        }

        return materiels;
    }

    public void mettreAJourQuantite(int idMateriel, int nouvelleQuantite) {
        if (nouvelleQuantite < 0) {
            System.err.println("❌ Mise à jour impossible : quantité négative !");
            return;
        }

        String req = "UPDATE materiel SET quantite = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, nouvelleQuantite);
            ps.setInt(2, idMateriel);
            ps.executeUpdate();
            System.out.println("🔄 Stock mis à jour (ID " + idMateriel + ") ➔ Quantité = " + nouvelleQuantite);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de la quantité : " + e.getMessage());
        }
    }

    public int getQuantiteById(int idMateriel) {
        String req = "SELECT quantite FROM materiel WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, idMateriel);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("quantite");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de la quantité : " + e.getMessage());
        }
        return -1;
    }

    public double getPrixById(int idMateriel) {
        String req = "SELECT prix FROM materiel WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, idMateriel);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("prix");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du prix : " + e.getMessage());
        }
        return -1;
    }
    public List<Materiels> getMaterielsDisponibles() {
        List<Materiels> disponibles = new ArrayList<>();
        String req = "SELECT * FROM materiel WHERE etat = 'DISPONIBLE' AND quantite > 0";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                disponibles.add(new Materiels(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getInt("quantite"),
                        rs.getString("etat"),
                        rs.getString("description"),
                        rs.getString("image"),
                        rs.getDouble("prix")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des matériels disponibles : " + e.getMessage());
        }

        return disponibles;
    }

}
