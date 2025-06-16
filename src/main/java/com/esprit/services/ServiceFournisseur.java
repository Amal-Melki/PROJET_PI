package com.esprit.services;

import com.esprit.modules.Fournisseur;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceFournisseur implements IService<Fournisseur> {

    private Connection connection;

    public ServiceFournisseur() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(Fournisseur f) {
        String req = "INSERT INTO fournisseur(nom, email, telephone, adresse) VALUES ('"
                + f.getNom() + "', '"
                + f.getEmail() + "', '"
                + f.getTelephone() + "', '"
                + f.getAdresse() + "')";

        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Fournisseur ajouté !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Fournisseur f) {
        String req = "UPDATE fournisseur SET nom='" + f.getNom()
                + "', email='" + f.getEmail()
                + "', telephone='" + f.getTelephone()
                + "', adresse='" + f.getAdresse()
                + "' WHERE id=" + f.getId();

        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Fournisseur modifié !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(Fournisseur f) {
        String req = "DELETE FROM fournisseur WHERE id=" + f.getId();

        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Fournisseur supprimé !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Fournisseur> recuperer() {
        List<Fournisseur> list = new ArrayList<>();
        String req = "SELECT * FROM fournisseur";

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);

            while (rs.next()) {
                list.add(new Fournisseur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getString("adresse")
                ));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return list;
    }
}
