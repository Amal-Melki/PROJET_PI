package com.esprit.tests;

import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.Admin.ServiceProduitDerive;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class MainGUI extends Application {

    public static void main(String[] args) {
        launch(args); // Lance JavaFX
    }

    @Override
    public void start(Stage primaryStage) {
        ServiceProduitDerive serviceProduit = new ServiceProduitDerive();


        // ✅ AJOUT PRODUIT DÉRIVÉ
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

        // ✅ AFFICHAGE DES PRODUITS
        System.out.println("\nListe des produits dérivés :");
        List<ProduitDerive> produits = serviceProduit.recuperer();
        for (ProduitDerive p : produits) {
            System.out.println(p);
        }
        try {
            // ⚠ Ancienne interface : Ajout Matériel
            // FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutMateriel.fxml"));

            // ⚠ Liste des matériels avec bouton "Modifier"
            // FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierMateriel.fxml"));

            // ⚠ Formulaire d’ajout de fournisseur
            // FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutFournisseur.fxml"));

            // ⚠ Liste des fournisseurs avec bouton Modifier
            // FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierFournisseur.fxml"));

            // ⚠ Formulaire d'ajout de réservation
            // FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutReservation.fxml"));

            // ⚠ Liste des réservations avec bouton Modifier
            // FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierReservation.fxml"));

            // ✅ Interface d'accueil principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("🏠 Accueil - Gestion des Ressources");
            primaryStage.show();

        } catch (Exception e) {
            System.out.println("Erreur au démarrage : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
