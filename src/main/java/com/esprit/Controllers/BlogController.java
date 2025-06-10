package com.esprit.Controllers;
import javafx.geometry.Insets;
import com.esprit.modules.Blog;
import com.esprit.modules.CategorieEnum;
import com.esprit.services.BlogServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

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
    @FXML private Button aiButton;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;
    @FXML private Button btnVoirLocation;
    @FXML private Button btnChoisirIA;
    @FXML private Button btnDownloadPDF;


    @FXML private TextField cityField;   // champ texte où l'utilisateur écrit la ville
    @FXML private Button btnShowWeather;  // bouton "Afficher Weather"
    @FXML private Label weatherResultLabel;
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

    // Nouvelle méthode pour ouvrir la fenêtre de carte

    public void openMapWindow() {
        try {
            URL fxmlLocation = getClass().getResource("/mapstreet.fxml");
            if (fxmlLocation == null) {
                System.out.println("❌ mapstreet.fxml introuvable !");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Localisation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void handleChatGPT(ActionEvent event) {
        Stage chatStage = new Stage();
        chatStage.setTitle("ChatGPT");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.CENTER);

        TextArea chatArea = new TextArea();
        chatArea.setPromptText("Écrivez votre message ici...");
        chatArea.setWrapText(true);

        Button sendButton = new Button("Envoyer");

        TextArea responseArea = new TextArea();
        responseArea.setWrapText(true);
        responseArea.setEditable(false);
        responseArea.setPromptText("Réponse de ChatGPT");

        sendButton.setOnAction(e -> {
            String userInput = chatArea.getText();
            if (!userInput.isEmpty()) {
                String response = "Réponse de ChatGPT : " + userInput; // Remplacer par appel API réel
                responseArea.setText(response);
            }
        });

        vbox.getChildren().addAll(chatArea, sendButton, responseArea);

        Scene scene = new Scene(vbox, 400, 300);
        chatStage.setScene(scene);
        chatStage.show();
    }

    private void fetchWeather(String city) {
        String apiKey = "YOUR_API_KEY";  // Remplace par ta clé OpenWeatherMap
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city +
                "&appid=" + apiKey + "&units=metric&lang=fr";

        // On utilise un thread séparé pour ne pas bloquer l'UI
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String json = response.body();

                // Parse JSON simple avec Gson (assure-toi d’avoir la librairie Gson)
                JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

                if (obj.has("main")) {
                    JsonObject main = obj.getAsJsonObject("main");
                    double temp = main.get("temp").getAsDouble();
                    String description = obj.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();

                    // Mise à jour de l'interface graphique dans le thread JavaFX
                    Platform.runLater(() -> {
                        weatherResultLabel.setText("Température: " + temp + " °C, " + description);
                    });
                } else {
                    Platform.runLater(() -> weatherResultLabel.setText("Ville non trouvée"));
                }
            } catch (Exception e) {
                Platform.runLater(() -> weatherResultLabel.setText("Erreur lors de la récupération météo"));
            }
        }).start();
    }

    @FXML
    private void onShowWeatherClicked() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            weatherResultLabel.setText("Merci d'entrer une ville");
            return;
        }
        fetchWeather(city);
    }
    private void copierImageDansRessources(File sourceFile) {
        try {
            // Chemin vers ton dossier ressources blog_images
            File targetDir = new File("src/main/resources/blog_images");
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            // Fichier de destination dans le dossier blog_images
            File destFile = new File(targetDir, sourceFile.getName());

            // Copier le fichier (remplace s'il existe déjà)
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Chemin relatif pour afficher + sauvegarder en DB
            String cheminRelatif = "/blog_images/" + sourceFile.getName();
            System.out.println("Image copiée : " + cheminRelatif);

            // Affiche l’image dans ImageView
            Image image = new Image(getClass().getResourceAsStream(cheminRelatif));
            imageView.setImage(image);

            // Ici tu peux sauvegarder cheminRelatif en variable ou en DB

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la copie de l'image.");
        }
    }





}