package com.esprit.controllers;

import com.esprit.modules.Blog;
import com.esprit.modules.CategorieEnum;
import com.esprit.services.BlogServices;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDate;

public class ModifierBlogController {
    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<CategorieEnum> categorieComboBox;
    @FXML private DatePicker dpDate;
    @FXML private ImageView imageView;
    @FXML private Button modifierButton;

    private Blog blogAModifier;
    private String imagePath;

    public void initialize() {
        // Initialiser la ComboBox des catégories
        categorieComboBox.getItems().setAll(CategorieEnum.values());
    }

    public void setBlogData(Blog blog) {
        this.blogAModifier = blog;

        // Pré-remplir les champs avec les données du blog
        titreField.setText(blog.getTitre());
        descriptionField.setText(blog.getContenu());
        categorieComboBox.setValue(blog.getCategorie());

        // Convertir Timestamp en LocalDate pour le DatePicker
        if (blog.getDate() != null) {
            dpDate.setValue(blog.getDate().toLocalDateTime().toLocalDate());
        }

        // Charger l'image si elle existe
        if (blog.getImage() != null && !blog.getImage().isEmpty()) {
            File file = new File(blog.getImage());
            if (file.exists()) {
                imagePath = blog.getImage();
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }
    }

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();
            imageView.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    @FXML
    private void modifierBlog() {
        if (validateInputs()) {
            // Mettre à jour les données du blog
            blogAModifier.setTitre(titreField.getText());
            blogAModifier.setContenu(descriptionField.getText());
            blogAModifier.setCategorie(categorieComboBox.getValue());
            blogAModifier.setImage(imagePath);

            // Convertir LocalDate en Timestamp
            if (dpDate.getValue() != null) {
                blogAModifier.setDate(Timestamp.valueOf(dpDate.getValue().atStartOfDay()));
            }

            // Appeler le service pour mettre à jour le blog
            BlogServices blogService = new BlogServices();
            try {
                blogService.modifier(blogAModifier);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Le blog a été modifié avec succès.");

                // Fermer la fenêtre après la modification
                ((Stage) modifierButton.getScene().getWindow()).close();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification du blog: " + e.getMessage());
            }
        }
    }

    private boolean validateInputs() {
        if (titreField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez saisir un titre");
            return false;
        }
        if (descriptionField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez saisir un contenu");
            return false;
        }
        if (categorieComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une catégorie");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}