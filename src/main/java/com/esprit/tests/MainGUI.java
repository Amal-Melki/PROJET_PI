package com.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

<<<<<<< HEAD
public class MainGUI extends Application {

    public static void main(String[] args) {
        launch(args); // Lance JavaFX
    }
=======
import java.net.URL;
import java.util.Objects;

public class MainGUI extends Application {
>>>>>>> gestion_produits_derives

    @Override
    public void start(Stage primaryStage) {
        try {
<<<<<<< HEAD
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
=======
            // Load the FXML file for the User interface
            URL location = getClass().getResource("/views/SelectionInterface.fxml");
            Parent root = FXMLLoader.load(Objects.requireNonNull(location, "FXML file not found at /views/User/AffichageProduitsUser.fxml"));

            primaryStage.setTitle("Découvrez nos Produits");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement du FXML de l'interface utilisateur:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
>>>>>>> gestion_produits_derives
