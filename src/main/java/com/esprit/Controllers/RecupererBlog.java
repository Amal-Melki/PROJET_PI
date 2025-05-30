package com.esprit.Controllers;

import com.esprit.modules.Blog;
import com.esprit.modules.CategorieEnum;
import com.esprit.services.BlogServices;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.animation.FadeTransition;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.effect.DropShadow;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecupererBlog {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy 'à' HH:mm", Locale.FRENCH);

    @FXML private TilePane BlogsTilePane;
    @FXML private ComboBox<CategorieEnum> categorieFilterCombo;

    @FXML private Button addBlogButton;

    private final BlogServices blogService = new BlogServices();

    @FXML
    public void initialize() {
        // Configurer le TilePane
        BlogsTilePane.setPrefColumns(3);
        BlogsTilePane.setHgap(20);
        BlogsTilePane.setVgap(20);
        BlogsTilePane.setStyle("-fx-padding: 20px;");

        // Initialiser la ComboBox des catégories
        categorieFilterCombo.getItems().setAll(CategorieEnum.values());
        categorieFilterCombo.setConverter(new StringConverter<CategorieEnum>() {
            @Override
            public String toString(CategorieEnum categorie) {
                return categorie != null ? categorie.toString() : "";
            }

            @Override
            public CategorieEnum fromString(String string) {
                return CategorieEnum.fromString(string);
            }
        });


        addBlogButton.setOnAction(e -> openAddBlogWindow());

        loadBlogs();
    }

    @FXML
    private void openAddBlogWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BlogView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un nouveau blog");
            stage.show();

            // Rafraîchir la liste après ajout (optionnel)
            stage.setOnHidden(e -> loadBlogs());
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'interface d'ajout de blog");
            e.printStackTrace();
        }
    }
    private void loadBlogs() {



        try {
            List<Blog> blogs = blogService.recuperer();
            displayBlogs(blogs);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors du chargement des blogs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void filterByCategory() {
        CategorieEnum selectedCategory = categorieFilterCombo.getValue();
        if (selectedCategory != null) {
            try {
                List<Blog> allBlogs = blogService.recuperer();
                List<Blog> filteredBlogs = allBlogs.stream()
                        .filter(blog -> selectedCategory.equals(blog.getCategorie()))
                        .collect(Collectors.toList());

                displayBlogs(filteredBlogs);
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Erreur", "Erreur lors du filtrage: " + e.getMessage());
            }
        } else {
            showAlert(AlertType.WARNING, "Attention", "Veuillez sélectionner une catégorie");
        }
    }

    @FXML
    private void resetFilter() {
        categorieFilterCombo.getSelectionModel().clearSelection();
        loadBlogs();
    }

    private void displayBlogs(List<Blog> blogs) {
        BlogsTilePane.getChildren().clear();

        if (blogs.isEmpty()) {
            showAlert(AlertType.INFORMATION, "Information", "Aucun blog disponible pour le moment.");
            return;
        }

        for (Blog blog : blogs) {
            try {
                VBox blogBox = createBlogBox(blog);
                BlogsTilePane.getChildren().add(blogBox);
            } catch (Exception e) {
                System.err.println("Erreur lors de la création du blog ID " + blog.getId() + ": " + e.getMessage());
            }
        }
    }

    private VBox createBlogBox(Blog blog) {
        VBox blogBox = new VBox(10);
        blogBox.getStyleClass().add("blog-item");
        blogBox.setPrefWidth(300);
        blogBox.setMaxWidth(300);

        // Animation d'apparition
        blogBox.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), blogBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Animation de survol
        blogBox.setOnMouseEntered(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), blogBox);
            scaleUp.setToX(1.03);
            scaleUp.setToY(1.03);
            scaleUp.play();

            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.rgb(0, 0, 0, 0.3));
            shadow.setRadius(10);
            shadow.setSpread(0.1);
            blogBox.setEffect(shadow);
        });

        blogBox.setOnMouseExited(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), blogBox);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();

            blogBox.setEffect(null);
        });

        // Titre
        Text titleText = new Text(blog.getTitre());
        titleText.getStyleClass().add("blog-title");
        titleText.setWrappingWidth(280);

        // Catégorie
        String categorieDisplay = blog.getCategorie() != null ?
                blog.getCategorie().toString() : "Non catégorisé";
        Text categorieText = new Text("Catégorie: " + categorieDisplay);
        categorieText.getStyleClass().add("blog-categorie");
        categorieText.setWrappingWidth(280);

        // Date
        String formattedDate = blog.getDate().toLocalDateTime().format(DATE_FORMATTER);
        Text dateText = new Text("Publié le: " + formattedDate);
        dateText.getStyleClass().add("blog-date");
        dateText.setWrappingWidth(280);

        // Image
        ImageView imageView = new ImageView();
        imageView.getStyleClass().add("blog-image");
        if (blog.getImage() != null && !blog.getImage().isEmpty()) {
            try {
                File file = new File(blog.getImage());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                    imageView.setFitWidth(280);
                    imageView.setFitHeight(180);
                    imageView.setPreserveRatio(false);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
            }
        }

        // Contenu (version courte)
        String shortContent = blog.getContenu().length() > 150 ?
                blog.getContenu().substring(0, 150) + "..." : blog.getContenu();
        Text contentText = new Text(shortContent);
        contentText.setWrappingWidth(280);

        // Boutons d'action
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        Button modifierButton = new Button("Modifier");
        Button supprimerButton = new Button("Supprimer");

        buttonsBox.getChildren().addAll(modifierButton, supprimerButton);

        // Événements des boutons
        modifierButton.setOnAction(e -> modifierBlog(blog));
        supprimerButton.setOnAction(e -> supprimerBlog(blog));

        blogBox.getChildren().addAll(
                titleText,
                imageView,
                categorieText,
                dateText,
                contentText,
                buttonsBox
        );

        return blogBox;
    }

    private void modifierBlog(Blog blog) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierBlogView.fxml"));
            Parent root = loader.load();

            ModifierBlogController controller = loader.getController();
            controller.setBlogData(blog);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier le blog");
            stage.show();
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'éditeur de blog");
            e.printStackTrace();
        }
    }

    private void supprimerBlog(Blog blog) {
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce blog ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                blogService.supprimer(blog);
                loadBlogs();
                showAlert(AlertType.INFORMATION, "Succès", "Le blog a été supprimé avec succès.");
            } catch (Exception ex) {
                showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la suppression du blog : " + ex.getMessage());
            }
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML


    public void ouvrirBlogView(javafx.event.ActionEvent actionEvent) { try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BlogView.fxml")); // chemin absolu depuis /resources
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Ajout Blog");
        stage.setScene(new Scene(root));
        stage.show();  // Ouvre une nouvelle fenêtre
    } catch (Exception e) {
        System.err.println("Erreur lors de l'ouverture de BlogView.fxml : " + e.getMessage());
    }
    }
    @FXML
    private void callApiExample() {
        new Thread(() -> {
            String apiResponse = ApiClientController.fetchDataFromApi();
            Platform.runLater(() -> {
                System.out.println("Réponse API: " + apiResponse);
                // Mettre à jour l'UI ici
            });
        }).start();
    }
    @FXML
    private TextArea apiResponseArea; // Doit correspondre au fx:id dans FXML

    @FXML
    private void handleApiCall() {
        new Thread(() -> {
            String response = ApiClientController.fetchDataFromApi();
            Platform.runLater(() -> {
                apiResponseArea.setText(response);
                showAlert(Alert.AlertType.INFORMATION, "API", "Données reçues avec succès");
            });
        }).start();
}}