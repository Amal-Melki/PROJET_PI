package com.esprit.Controllers;

import com.esprit.modules.Blog;
import com.esprit.services.BlogServices;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ArticleController implements Initializable {

    @FXML
    private VBox blogContainer;


    private final BlogServices blogServices = new BlogServices();

    @Override

    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("ArticleController loaded !");
        afficherBlogs(); // 👈 ajoute cette ligne
    }


    private void afficherBlogs() {
        System.out.println("Chargement des blogs...");
        List<Blog> blogs = blogServices.recuperer();
        System.out.println("Blogs récupérés : " + blogs.size());


        // Boucle d'affichage
        for (Blog blog : blogs) {
            VBox blogBox = new VBox();
            blogBox.setSpacing(5);
            blogBox.setStyle("""
                -fx-background-color: #ffffff;
                -fx-padding: 15;
                -fx-border-color: #dcdcdc;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.5, 0.0, 2.0);
            """);

            Label titre = new Label(" Titre : " + blog.getTitre());
            titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Text contenuText = new Text(blog.getContenu());
            contenuText.setWrappingWidth(400);
            TextFlow contenu = new TextFlow(new Label(" Contenu : "), contenuText);

            Label date = new Label(" Date : " + blog.getDate());
            Label categorie = new Label("🏷 Catégorie : " + blog.getCategorie());

            blogBox.getChildren().addAll(titre, contenu, date, categorie);
            blogContainer.getChildren().add(blogBox);
        }
    }
}
