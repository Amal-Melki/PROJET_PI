package com.esprit.Controllers;

import com.esprit.modules.Blog;
import com.esprit.modules.CategorieEnum;
import com.esprit.modules.Share;
import com.esprit.services.*;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClientBlogController {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy 'à' HH:mm", Locale.FRENCH);

    @FXML private TilePane BlogsTilePane;
    @FXML private ComboBox<CategorieEnum> categorieFilterCombo;
    @FXML private Button addBlogButton;
    @FXML private ImageView logoImage;

    private final BlogServices blogService = new BlogServices();
    private final LikeService likeService = new LikeService();
    private final CommentaireService commentaireService = new CommentaireService();
    private final ShareService shareService = new ShareService();

    private int id_client = 1; // ID du client connecté
    private final boolean isClientContext = true; // Contexte client

    @FXML
    public void initialize() {
        setupUI();
        loadBlogs();

    }

    private void setupUI() {
        BlogsTilePane.setPrefColumns(3);
        BlogsTilePane.setHgap(20);
        BlogsTilePane.setVgap(20);
        BlogsTilePane.setStyle("-fx-padding: 20px;");

        categorieFilterCombo.getItems().setAll(CategorieEnum.values());
        categorieFilterCombo.setConverter(new StringConverter<CategorieEnum>() {
            @Override public String toString(CategorieEnum categorie) {
                return categorie != null ? categorie.toString() : "";
            }
            @Override public CategorieEnum fromString(String string) {
                return CategorieEnum.fromString(string);
            }
        });

        addBlogButton.setOnAction(e -> openAddBlogWindow());
    }

    private void loadBlogs() {
        try {
            List<Blog> blogs = blogService.recuperer();
            displayBlogs(blogs);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors du chargement des blogs: " + e.getMessage());
        }
    }

    private void displayBlogs(List<Blog> blogs) {
        BlogsTilePane.getChildren().clear();

        if (blogs.isEmpty()) {
            showAlert(AlertType.INFORMATION, "Information", "Aucun blog disponible.");
            return;
        }

        blogs.forEach(blog -> {
            try {
                VBox blogBox = createBlogBox(blog);
                BlogsTilePane.getChildren().add(blogBox);
            } catch (Exception e) {
                System.err.println("Erreur création blog ID " + blog.getId() + ": " + e.getMessage());
            }
        });
    }

    private VBox createBlogBox(Blog blog) {
        VBox blogBox = new VBox(10);
        blogBox.getStyleClass().add("blog-item");
        blogBox.setPrefWidth(300);
        blogBox.setMaxWidth(300);

        // Animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), blogBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Titre
        Text titleText = new Text(blog.getTitre());
        titleText.getStyleClass().add("blog-title");
        titleText.setWrappingWidth(280);

        // Catégorie
        String categorieDisplay = blog.getCategorie() != null ? blog.getCategorie().toString() : "Non catégorisé";
        Text categorieText = new Text("Catégorie: " + categorieDisplay);
        categorieText.getStyleClass().add("blog-categorie");
        categorieText.setWrappingWidth(280);

        // Date
        String formattedDate = blog.getDate().toLocalDateTime().format(DATE_FORMATTER);
        Text dateText = new Text("Publié le: " + formattedDate);
        dateText.getStyleClass().add("blog-date");
        dateText.setWrappingWidth(280);

        // Image
        ImageView imageView = createBlogImageView(blog);

        // Contenu
        Text contentText = new Text(blog.getContenu().length() > 150 ?
                blog.getContenu().substring(0, 150) + "..." : blog.getContenu());
        contentText.setWrappingWidth(280);

        // Boutons d'interaction
        HBox buttonsBox = createInteractionButtons(blog);

        // Assemblage final
        blogBox.getChildren().addAll(
                titleText, imageView, categorieText,
                dateText, contentText, buttonsBox
        );

        return blogBox;
    }

    private ImageView createBlogImageView(Blog blog) {
        ImageView imageView = new ImageView();
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
                System.err.println("Erreur chargement image: " + e.getMessage());
            }
        }
        return imageView;
    }

    private HBox createInteractionButtons(Blog blog) {
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        // Bouton Like
        Button likeButton = new Button("♥");
        Label likeCountLabel = new Label();
        setupLikeButton(blog, likeButton, likeCountLabel);

        // Bouton Commentaire
        Button commentButton = new Button("💬");
        Label commentCountLabel = new Label();
        setupCommentButton(blog, commentButton, commentCountLabel);

        // Bouton Partage
        Button shareButton = new Button("↗️");
        Label shareCountLabel = new Label();
        setupShareButton(blog, shareButton, shareCountLabel);

        buttonsBox.getChildren().addAll(
                likeButton, likeCountLabel,
                commentButton, commentCountLabel,
                shareButton, shareCountLabel
        );

        return buttonsBox;
    }

    private void setupLikeButton(Blog blog, Button button, Label countLabel) {
        try {
            int count = likeService.getLikeCount(blog.getId(), isClientContext);
            countLabel.setText(String.valueOf(count));
            if (likeService.hasUserLiked(blog.getId(), id_client, isClientContext)) {
                button.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            countLabel.setText("0");
        }

        button.setOnAction(e -> handleLike(blog, button, countLabel));
    }

    private void setupCommentButton(Blog blog, Button button, Label countLabel) {
        try {
            int count = commentaireService.getCommentaires(blog.getId()).size();
            countLabel.setText(String.valueOf(count));
        } catch (Exception e) {
            countLabel.setText("0");
        }

        button.setOnAction(e -> handleComment(blog));
    }

    private void setupShareButton(Blog blog, Button button, Label countLabel) {
        // Chargement asynchrone du compteur
        new Thread(() -> {
            try {
                int count = shareService.getShareCount(blog.getId());
                Platform.runLater(() -> countLabel.setText(String.valueOf(count)));
            } catch (SQLException e) {
                Platform.runLater(() -> countLabel.setText("0"));
            }
        }).start();

        button.setOnAction(e -> handleShare(blog, button, countLabel));
    }

    private void handleLike(Blog blog, Button button, Label countLabel) {
        try {
            if (likeService.hasUserLiked(blog.getId(), id_client, isClientContext)) {
                likeService.supprimerLike(blog.getId(), id_client, isClientContext);
                button.setStyle("-fx-text-fill: black;");
            } else {
                likeService.ajouterLike(blog.getId(), id_client, isClientContext);
                button.setStyle("-fx-text-fill: red;");
            }
            updateLikeCount(blog.getId(), countLabel);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur like: " + e.getMessage());
        }
    }

    private void updateLikeCount(int blogId, Label countLabel) {
        new Thread(() -> {
            try {
                int count = likeService.getLikeCount(blogId, isClientContext);
                Platform.runLater(() -> countLabel.setText(String.valueOf(count)));
            } catch (SQLException e) {
                Platform.runLater(() -> showAlert(AlertType.ERROR, "Erreur", "Erreur MAJ like"));
            }
        }).start();
    }

    private void handleComment(Blog blog) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Commentaires - " + blog.getTitre());

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Écrivez votre commentaire...");

        ListView<String> commentsList = new ListView<>();
        refreshComments(commentsList, blog.getId());

        ButtonType publishButton = new ButtonType("Publier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(publishButton, ButtonType.CANCEL);

        VBox dialogContent = new VBox(10, commentsList, commentArea);
        dialogContent.setPadding(new Insets(15));
        dialog.getDialogPane().setContent(dialogContent);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == publishButton && !commentArea.getText().isEmpty()) {
                try {
                    commentaireService.ajouterCommentaire(blog.getId(), id_client, commentArea.getText());
                    refreshComments(commentsList, blog.getId());
                    commentArea.clear();
                } catch (Exception e) {
                    showAlert(AlertType.ERROR, "Erreur", "Erreur commentaire: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void handleShare(Blog blog, Button button, Label countLabel) {
        button.setDisable(true);

        new Thread(() -> {
            try {
                if (!shareService.hasClientShared(blog.getId(), id_client)) {
                    Share share = new Share(blog.getId(), id_client, new Timestamp(System.currentTimeMillis()));
                    shareService.ajouterShare(share);

                    Platform.runLater(() -> {
                        updateShareCount(blog.getId(), countLabel);
                        showTooltip(button, "Partagé avec succès!");
                    });
                } else {
                    Platform.runLater(() ->
                            showAlert(AlertType.INFORMATION, "Information", "Vous avez déjà partagé ce blog"));
                }
            } catch (SQLException e) {
                Platform.runLater(() ->
                        showAlert(AlertType.ERROR, "Erreur", "Échec partage: " + e.getMessage()));
            } finally {
                Platform.runLater(() -> button.setDisable(false));
            }
        }).start();
    }

    private void updateShareCount(int blogId, Label countLabel) {
        try {
            int count = shareService.getShareCount(blogId);
            countLabel.setText(String.valueOf(count));
        } catch (SQLException e) {
            countLabel.setText("0");
        }
    }

    private void showTooltip(Button button, String message) {
        Tooltip tooltip = new Tooltip(message);
        tooltip.setAutoHide(true);
        tooltip.show(button,
                button.localToScreen(button.getBoundsInLocal()).getMaxX(),
                button.localToScreen(button.getBoundsInLocal()).getMaxY());

        new Thread(() -> {
            try {
                Thread.sleep(1500);
                Platform.runLater(tooltip::hide);
            } catch (InterruptedException ignored) {}
        }).start();
    }

    private void refreshComments(ListView<String> listView, int blogId) {
        try {
            List<String> comments = commentaireService.getCommentaires(blogId);
            Platform.runLater(() -> {
                listView.getItems().clear();
                listView.getItems().addAll(comments);
            });
        } catch (Exception e) {
            System.err.println("Erreur chargement commentaires: " + e.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    private void openAddBlogWindow() {
        try {
            Stage stage = new Stage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/BlogView.fxml"))));
            stage.setTitle("Ajouter un blog");
            stage.show();
            stage.setOnHidden(e -> loadBlogs());
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur ouverture fenêtre: " + e.getMessage());
        }
    }

    @FXML
    private void filterByCategory() {
        CategorieEnum selected = categorieFilterCombo.getValue();
        if (selected != null) {
            try {
                List<Blog> filtered = blogService.recuperer().stream()
                        .filter(b -> selected.equals(b.getCategorie()))
                        .collect(Collectors.toList());
                displayBlogs(filtered);
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Erreur", "Erreur filtrage: " + e.getMessage());
            }
        } else {
            showAlert(AlertType.WARNING, "Attention", "Sélectionnez une catégorie");
        }
    }

    @FXML
    private void resetFilter() {
        categorieFilterCombo.getSelectionModel().clearSelection();
        loadBlogs();
    }

    public void readMore(ActionEvent actionEvent) {
    }


}