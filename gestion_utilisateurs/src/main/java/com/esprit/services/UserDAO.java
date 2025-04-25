package com.esprit.services;

import com.esprit.modules.utilisateurs.User;
import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
    void ajouterUtilisateur(User user) throws SQLException;
    User getUtilisateurParId(int id) throws SQLException;
    void modifierUtilisateur(User user) throws SQLException;
    void supprimerUtilisateur(int id) throws SQLException;
    List<User> getTousLesUtilisateurs() throws SQLException;
    User login(String username, String password) throws SQLException;
    void changePassword(User user, String nouveauMotDePasse) throws SQLException;
    List<User> rechercherParNom(String nom) throws SQLException;
    User rechercherParEmail(String email) throws SQLException;
    List<User> getUtilisateursParRole(String roleName) throws SQLException;
    int compterUtilisateursParRole(String role) throws SQLException;
    int getNombreTotalUtilisateurs() throws SQLException;
}