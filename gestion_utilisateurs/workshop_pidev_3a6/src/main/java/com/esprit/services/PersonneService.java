package com.esprit.services;

import com.esprit.models.Personne;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PersonneService implements IService<Personne> {

    Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void ajouter(Personne personne) {
        String req = "INSERT INTO personne (nom, prenom) VALUES ('"+personne.getNom()+"', '"+personne.getPrenom()+"')";
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Personne ajoutée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Personne personne) {
        String req = "UPDATE personne SET nom='"+personne.getNom()+"' ,prenom='"+personne.getPrenom()+"' WHERE id="+personne.getId();
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Personne modifiée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(Personne personne) {
        String req = "DELETE FROM personne WHERE id="+personne.getId();
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
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
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                personnes.add(new Personne(rs.getInt("id"), rs.getString("nom"), rs.getString("prenom")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return personnes;
    }
}
