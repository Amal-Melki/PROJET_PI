package com.esprit.services;

import com.esprit.models.Personne;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonneService2 implements IService<Personne> {

    Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void ajouter(Personne personne) {
        String req = "INSERT INTO personne (nom, prenom) VALUES (?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(2, personne.getPrenom());
            pst.setString(1, personne.getNom());
            pst.executeUpdate();
            System.out.println("Personne ajoutée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Personne personne) {
        String req = "UPDATE personne SET nom=? ,prenom=? WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, personne.getNom());
            pst.setString(2, personne.getPrenom());
            pst.setInt(3, personne.getId());
            pst.executeUpdate();
            System.out.println("Personne modifiée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(Personne personne) {
        String req = "DELETE FROM personne WHERE id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, personne.getId());
            pst.executeUpdate();
            System.out.println("Personne supprimée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Personne> rechercher() {
        List<Personne> personnes = new ArrayList<>();

        String req = "SELECT * FROM personne";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                personnes.add(new Personne(rs.getInt("id"), rs.getString("nom"), rs.getString("prenom")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return personnes;
    }
}
