package com.esprit.services;

import com.esprit.modules.Blog;
import com.esprit.modules.CategorieEnum;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlogServices implements IService<Blog> {

    private final Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void ajouter(Blog blog) {
        String query = "INSERT INTO blogs (Titre, Contenu, Image, date_creation, Categorie ) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, blog.getTitre());
            statement.setString(2, blog.getContenu());
            statement.setString(3, blog.getImage());
            statement.setTimestamp(4, blog.getDate());  //  utilise Timestamp directement
            statement.setString(5, blog.getCategorie().toString());
            statement.executeUpdate();
            System.out.println(" Blog ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du blog : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Blog blog) {
        String query = "UPDATE blogs SET Titre = ?, Contenu = ?, Image = ?, date_creation = ?, Categorie = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, blog.getTitre());
            statement.setString(2, blog.getContenu());
            statement.setString(3, blog.getImage());
            statement.setTimestamp(4, blog.getDate());  // Timestamp
            statement.setString(5, blog.getCategorie().toString());
            statement.setInt(6, blog.getId());
            statement.executeUpdate();
            System.out.println("Blog modifié avec succès !");
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la modification du blog : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Blog blog) {
        String query = "DELETE FROM blogs WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, blog.getId());
            statement.executeUpdate();
            System.out.println("Blog supprimé avec succès !");
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la suppression du blog : " + e.getMessage());
        }
    }

    @Override
    public List<Blog> recuperer() {
        List<Blog> blogs = new ArrayList<>();
        String query = "SELECT * FROM blogs";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Blog blog = new Blog(
                        resultSet.getString("Titre"),
                        resultSet.getString("Contenu"),
                        resultSet.getString("Image"),
                        resultSet.getTimestamp("date_creation"),  //  Timestamp
                        CategorieEnum.fromString(resultSet.getString("Categorie"))

                );
                blog.setId(resultSet.getInt("id"));
                blogs.add(blog);
            }
            System.out.println(" Blogs récupérés avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des blogs : " + e.getMessage());
        }
        return blogs;
    }
}
