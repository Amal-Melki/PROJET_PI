package com.esprit.Controllers;

import com.esprit.modules.Blog;
import com.esprit.modules.CategorieEnum;
import com.esprit.services.BlogServices;
import com.esprit.services.CommentaireService;
import com.esprit.services.LikeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
    @FXML private Button btnOuvrirBlog;
    @FXML private Button likeButton;
    @FXML private Label likeCount;
    @FXML private Button commentButton;

    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;
    @FXML private Button btnVoirLocation;

    @FXML private Button btnDownloadPDF;
    private final LikeService likeService = new LikeService();
    private final CommentaireService commentaireService = new CommentaireService();
    private int currentUserId = 1; // À remplacer par l'ID utilisateur connecté

    private final BlogServices blogService = new BlogServices();
    private String imagePath;

    @FXML
    public void initialize() {
        categorieComboBox.getItems().setAll(CategorieEnum.values());
        addButton.setDisable(true);

        titreField.textProperty().addListener((obs, oldVal, newVal) -> checkFields());
        descriptionField.textProperty().addListener((obs, oldVal, newVal) -> checkFields());
        categorieComboBox.valueProperty().addListener((obs, oldVal, newVal) -> checkFields());
        latitudeField.textProperty().addListener((obs, oldVal, newVal) -> checkFields());
        longitudeField.textProperty().addListener((obs, oldVal, newVal) -> checkFields());
    }

    private void checkFields() {
        boolean ready = !titreField.getText().isEmpty()
                && !descriptionField.getText().isEmpty()
                && categorieComboBox.getValue() != null
                && imagePath != null
                && !latitudeField.getText().isEmpty()
                && !longitudeField.getText().isEmpty();

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

            try {
                Image image = new Image(selectedFile.toURI().toString());
                imageView.setImage(image);
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
                    || categorieComboBox.getValue() == null || imagePath == null
                    || latitudeField.getText().isEmpty() || longitudeField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs !");
                return;
            }

            Blog blog = new Blog();
            blog.setTitre(titreField.getText().trim());
            blog.setContenu(descriptionField.getText().trim());
            blog.setCategorie(categorieComboBox.getValue());
            blog.setImage(imagePath);
            blog.setDate(new Timestamp(System.currentTimeMillis()));

            try {
                blog.setLatitude(Double.parseDouble(latitudeField.getText().trim()));
                blog.setLongitude(Double.parseDouble(longitudeField.getText().trim()));
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Latitude ou longitude invalide !");
                return;
            }

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
        latitudeField.clear();
        longitudeField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleDownloadPdf(ActionEvent event) {
        System.out.println("Téléchargement PDF déclenché.");

        try {
            String fileURL = "https://mozilla.github.io/pdf.js/web/compressed.tracemonkey-pldi-09.pdf";
            HttpURLConnection httpConn = (HttpURLConnection) new URL(fileURL).openConnection();
            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "blog.pdf";
                FileChooser fileChooser = new FileChooser();

                // Ajouter le filtre avant d'ouvrir la boîte de dialogue
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf");
                fileChooser.getExtensionFilters().add(extFilter);

                fileChooser.setInitialFileName(fileName);

                File fileToSave = fileChooser.showSaveDialog(btnDownloadPDF.getScene().getWindow());

                if (fileToSave != null) {
                    try (InputStream inputStream = httpConn.getInputStream()) {
                        Files.copy(inputStream, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        showAlert(Alert.AlertType.INFORMATION, "Succès", "Fichier téléchargé !");
                    }
                }

            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Fichier introuvable !");
            }
            httpConn.disconnect();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Téléchargement échoué : " + e.getMessage());
        }
    }

    @FXML
    private void retour() {
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }

    public ImageView getLogo() {
        return logo;
    }

    public ImageView getSlogo() {
        return Slogo;
    }

    @FXML
    private void showMap() {
        System.out.println("showMap called");

        // ✅ Coordonnées fixes, pas de latitudeField/longitudeField
        double lat = 36.8065; // latitude de Tunis
        double lon = 10.1815; // longitude de Tunis

        try {
            String url = "https://www.openstreetmap.org/?mlat=" + lat + "&mlon=" + lon + "#map=15/" + lat + "/" + lon;

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new java.net.URI(url));
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le navigateur.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la carte : " + e.getMessage());
        }
    }



}
