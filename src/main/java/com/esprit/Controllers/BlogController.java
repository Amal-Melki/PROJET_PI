package com.esprit.Controllers;

import com.esprit.modules.Blog;
import com.esprit.modules.CategorieEnum;
import com.esprit.services.BlogServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Timestamp;

public class BlogController {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<CategorieEnum> categorieComboBox;
    @FXML private Button uploadImageButton;
    @FXML private Button addButton;
    @FXML private ImageView imageView;
    @FXML private ImageView logo;
    @FXML private ImageView Slogo;
    @FXML private Button     btnOuvrirBlog ;
            ;



    private final BlogServices blogService = new BlogServices();
    private String imagePath;

    @FXML
    public void initialize() {
        // Remplir la ComboBox
        categorieComboBox.getItems().setAll(CategorieEnum.values());

        // Désactiver le bouton au début
        addButton.setDisable(true);

        // Écouteurs de validation
        titreField.textProperty().addListener((obs, oldVal, newVal) -> checkFields());
        descriptionField.textProperty().addListener((obs, oldVal, newVal) -> checkFields());
        categorieComboBox.valueProperty().addListener((obs, oldVal, newVal) -> checkFields());
    }

    private void checkFields() {
        boolean ready = !titreField.getText().isEmpty()
                && !descriptionField.getText().isEmpty()
                && categorieComboBox.getValue() != null
                && imagePath != null;
        addButton.setDisable(!ready);
    }

    @FXML
    public void UploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(uploadImageButton.getScene().getWindow());
        if (selectedFile != null) {
            if (selectedFile.length() > 5 * 1024 * 1024) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "L'image ne doit pas dépasser 5MB");
                return;
            }

            imagePath = selectedFile.getAbsolutePath();
            uploadImageButton.setText("Image: " + selectedFile.getName());
            uploadImageButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

            // Afficher preview
            try {
                Image image = new Image(selectedFile.toURI().toString());
                imageView.setImage(image); // tu l'avais déjà dans ton FXML
            } catch (Exception e) {
                System.err.println("Erreur d'affichage de l'image: " + e.getMessage());
            }

            checkFields();
        }
    }

    @FXML
    private void ajouterBlog() {
        try {
            if (titreField.getText().isEmpty() || descriptionField.getText().isEmpty()
                    || categorieComboBox.getValue() == null || imagePath == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs !");
                return;
            }

            Blog blog = new Blog();
            blog.setTitre(titreField.getText().trim());
            blog.setContenu(descriptionField.getText().trim());
            blog.setCategorie(categorieComboBox.getValue());
            blog.setImage(imagePath);
            blog.setDate(new Timestamp(System.currentTimeMillis())); // correspondance à Post

            blogService.ajouter(blog);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Publication ajoutée avec succès !");
            resetFields();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void resetFields() {
        titreField.clear();
        descriptionField.clear();
        categorieComboBox.getSelectionModel().clearSelection();
        imagePath = null;
        imageView.setImage(null);
        uploadImageButton.setText("Choisir une image");
        uploadImageButton.setStyle("");
        addButton.setDisable(true);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public ImageView getLogo() {
        return logo;
    }

    public ImageView getSlogo() {
        return Slogo;
    }


    @FXML
    private void retour() {
        // Fermer la fenêtre actuelle
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();



    }






}
