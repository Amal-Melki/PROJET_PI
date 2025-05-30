package com.esprit.services;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.esprit.modules.Commentaire;
import com.esprit.modules.User;
import com.esprit.utils.DataSource;

public class CommentaireService implements IService<Commentaire> {
    private Connection connection;

    public CommentaireService() {
        connection = DataSource.getInstance().getConnection();
    }

    public List<Commentaire> getCommentsByBlogId(int blogID) {
        List<Commentaire> commentaires = new ArrayList<>();
        String req = "SELECT c.*, u.Prenom, u.Nom FROM commentaire c " +
                "JOIN utilisateur u ON c.id = u.id WHERE c.id_Blog = ? ORDER BY c.date_commentaire DESC";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, blogID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Commentaire commentaire = new Commentaire();
                    commentaire.setId_Commentaire(rs.getInt("id_commentaire"));
                    commentaire.setId(rs.getInt("id"));
                    commentaire.setId_Blog(rs.getInt("id_Blog"));
                    commentaire.setDescription(rs.getString("description"));
                    commentaire.setDate_Commentaire(rs.getTimestamp("date_commentaire"));

                    // Créer un utilisateur avec prénom et nom
                    User utilisateur = new User();
                    utilisateur.setPrenom(rs.getString("Prenom"));
                    utilisateur.setNom(rs.getString("Nom"));
                    commentaire.setUtilisateur(utilisateur);

                    commentaires.add(commentaire);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commentaires: " + e.getMessage());
        }
        return commentaires;
    }

    @Override
    public void modifier(Commentaire commentaire) {
        String req = "UPDATE commentaire SET id=?, id_Blog=?, description=?, date_commentaire=? WHERE id_commentaire=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, commentaire.getId());
            ps.setInt(2, commentaire.getId_Blog());
            ps.setString(3, commentaire.getDescription());

            LocalDateTime date = commentaire.getDate_Commentaire() != null ?
                    commentaire.getDate_Commentaire().toLocalDateTime() : LocalDateTime.now();
            ps.setTimestamp(4, Timestamp.valueOf(date));
            ps.setInt(5, commentaire.getId_Commentaire());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Commentaire modifié avec succès !");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du commentaire: " + e.getMessage());
        }
    }

    @Override
    public List<Commentaire> recuperer() {
        List<Commentaire> commentaires = new ArrayList<>();
        String req = "SELECT * FROM commentaire";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Commentaire commentaire = new Commentaire();
                commentaire.setId_Commentaire(rs.getInt("id_commentaire"));
                commentaire.setId(rs.getInt("id"));
                commentaire.setId_Blog(rs.getInt("id_Blog"));
                commentaire.setDescription(rs.getString("description"));
                commentaire.setDate_Commentaire(rs.getTimestamp("date_commentaire"));
                commentaires.add(commentaire);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commentaires: " + e.getMessage());
        }
        return commentaires;
    }

    @Override
    public void ajouter(Commentaire commentaire) {
        String req = "INSERT INTO commentaire(id, id_Blog, description, date_commentaire) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, commentaire.getId());
            ps.setInt(2, commentaire.getId_Blog());
            ps.setString(3, commentaire.getDescription());

            LocalDateTime date = commentaire.getDate_Commentaire() != null ?
                    commentaire.getDate_Commentaire().toLocalDateTime() : LocalDateTime.now();
            ps.setTimestamp(4, Timestamp.valueOf(date));

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    commentaire.setId_Commentaire(generatedKeys.getInt(1));
                }
            }

            System.out.println("Commentaire ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du commentaire: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Commentaire commentaire) {
        String req = "DELETE FROM commentaire WHERE id_commentaire = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, commentaire.getId_Commentaire());
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Commentaire supprimé avec succès !");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du commentaire: " + e.getMessage());
        }
    }
}
