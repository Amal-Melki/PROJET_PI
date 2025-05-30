package com.esprit.tests;

import com.esprit.services.CommentaireService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class mainProg extends Application {

    private static final Logger LOGGER = Logger.getLogger(mainProg.class.getName());

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BlogRecuperer.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Gestion de Blog");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur au démarrage de l'application :", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
        CommentaireService c = new CommentaireService();
    }

    /*
    // Partie console – Gestion des produits dérivés
    import com.esprit.modules.produits.ProduitDerive;
    import com.esprit.services.produits.Admin.ServiceProduitDerive;
    import com.esprit.modules.produits.CommandeProduit;

    import java.sql.Date;
    import java.util.List;

    public static void main(String[] args) {
        ServiceProduitDerive serviceProduit = new ServiceProduitDerive();

        ProduitDerive produit = new ProduitDerive(
            "T-shirt Event",
            "Vêtements",
            25.99,
            100,
            "T-shirt coton avec logo de l'événement",
            "http://example.com/tshirt.jpg"
        );
        serviceProduit.ajouter(produit);
        System.out.println("Produit ajouté : " + produit.getNom());

        System.out.println("\nListe des produits dérivés :");
        List<ProduitDerive> produits = serviceProduit.recuperer();
        for (ProduitDerive p : produits) {
            System.out.println(p);
        }
    }
    */
}
