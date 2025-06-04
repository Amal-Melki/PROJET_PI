package com.esprit.controllers;

import com.esprit.models.Espace;
import com.esprit.services.EspaceService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class AjouterEspaceController implements Initializable {

    @FXML
    private Label lblFormTitle;

    @FXML
    private TextField txtName;

    @FXML
    private ComboBox<String> comboType;

    @FXML
    private TextField txtCapacity;

    @FXML
    private TextField txtLocation;

    @FXML
    private TextField txtPrice;

    @FXML
    private CheckBox checkAvailable;

    @FXML
    private TextArea txtDescription;

    @FXML
    private Button btnUploadImage;

    @FXML
    private Label lblImageStatus;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private TextField txtPhotoUrl;

    private EspaceService espaceService;
    private Espace espaceToEdit;
    private boolean isEditMode = false;
    private GestionEspacesController parentController;
    private String selectedImagePath = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        espaceService = new EspaceService();

        // Populate the type dropdown with standard types
        setupTypeComboBox();

        // Add numeric validation to capacity and price fields
        setupNumericValidation();
    }

    /**
     * Initializes the form data based on whether it's in add or edit mode
     * @param editMode true if editing an existing space, false if adding a new one
     */
    public void initData(boolean editMode) {
        this.isEditMode = editMode;

        if (editMode) {
            lblFormTitle.setText("Modifier Espace");
            btnSave.setText("Mettre à jour");

            if (espaceToEdit != null) {
                // Fill the form with space data
                txtName.setText(espaceToEdit.getNom());
                comboType.setValue(espaceToEdit.getType());
                txtCapacity.setText(String.valueOf(espaceToEdit.getCapacite()));
                txtLocation.setText(espaceToEdit.getLocalisation());
                txtPrice.setText(String.valueOf(espaceToEdit.getPrix()));
                checkAvailable.setSelected(espaceToEdit.isDisponibilite());
                txtDescription.setText(espaceToEdit.getDescription());
                txtPhotoUrl.setText(espaceToEdit.getPhotoUrl());

                // Update image status if there's an image
                if (espaceToEdit.getImage() != null && !espaceToEdit.getImage().isEmpty()) {
                    selectedImagePath = espaceToEdit.getImage();
                    lblImageStatus.setText("Image actuelle : " + getFileName(selectedImagePath));
                }
            }
        } else {
            lblFormTitle.setText("Ajouter Nouvel Espace");
            btnSave.setText("Enregistrer");

            // Set default values for new space
            checkAvailable.setSelected(true);
        }
    }

    /**
     * Sets up the space type dropdown with predefined types
     */
    private void setupTypeComboBox() {
        List<String> types = Arrays.asList(
                "Conférence", "Réunion", "Fête", "Formation", "Extérieur",
                "Studio", "Cocktail", "Mariage", "Concert", "Autre"
        );
        comboType.setItems(FXCollections.observableArrayList(types));
    }

    /**
     * Adds numeric validation to capacity and price text fields
     */
    private void setupNumericValidation() {
        // Validation for capacity field (integers only)
        txtCapacity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtCapacity.setText(newValue.replaceAll("\\D", ""));
            }
        });

        // Validation for price field (decimal numbers)
        txtPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtPrice.setText(oldValue);
            }
        });
    }

    /**
     * Handles the image upload button click
     */
    @FXML
    private void handleUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) btnUploadImage.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Créer le dossier de destination s'il n'existe pas
                File destDir = new File("src/main/resources/images/espaces");
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }

                // Générer un nom de fichier unique
                String fileName = UUID.randomUUID().toString() + "_" + selectedFile.getName();
                File destFile = new File(destDir, fileName);

                // Copier le fichier
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Stocker le chemin relatif
                selectedImagePath = "/images/espaces/" + fileName;
                lblImageStatus.setText("Image sélectionnée : " + selectedFile.getName());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de copie",
                        "Impossible de copier l'image : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Extracts the file name from a path
     */
    private String getFileName(String path) {
        if (path == null || path.isEmpty()) return "";
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }

    /**
     * Handles the save/update button click
     */
    @FXML
    private void handleSave() {
        System.out.println("handleSave called"); // Debug log
        if (!validateForm()) {
            System.out.println("Validation failed"); // Debug log
            return;
        }

        try {
            Espace space = createOrUpdateSpaceFromForm();
            int result;

            if (isEditMode) {
                result = espaceService.update(space);
            } else {
                result = espaceService.add(space);
            }

            if (result > 0) {
                showAlert(
                        Alert.AlertType.INFORMATION,
                        isEditMode ? "Espace Mis à Jour" : "Espace Ajouté",
                        null,
                        isEditMode ? "L'espace a été mis à jour avec succès." : "Le nouvel espace a été ajouté avec succès."
                );

                if (parentController != null) {
                    parentController.handleSpaceSaved();
                }

                closeDialog();
            } else {
                showAlert(
                        Alert.AlertType.ERROR,
                        "Erreur",
                        "Échec de l'opération",
                        "L'opération n'a pas pu être effectuée. Veuillez vérifier les données et réessayer."
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(
                    Alert.AlertType.ERROR,
                    "Erreur",
                    "Erreur technique",
                    "Une erreur s'est produite : " + e.getMessage()
            );
        }

    }

    /**
     * Creates or updates a space object from form data
     */
    private Espace createOrUpdateSpaceFromForm() {
        Espace space;

        if (isEditMode && espaceToEdit != null) {
            space = espaceToEdit;
        } else {
            space = new Espace();
        }

        space.setNom(txtName.getText());
        space.setType(comboType.getValue());
        space.setCapacite(Integer.parseInt(txtCapacity.getText()));
        space.setLocalisation(txtLocation.getText());
        space.setPrix(Double.parseDouble(txtPrice.getText()));
        space.setDisponibilite(checkAvailable.isSelected());
        space.setDescription(txtDescription.getText());
        space.setPhotoUrl(txtPhotoUrl.getText());

        // Priorité à l'URL si les deux sont fournis
        if (selectedImagePath != null && (txtPhotoUrl.getText() == null || txtPhotoUrl.getText().isEmpty())) {
            space.setImage(selectedImagePath);
            space.setPhotoUrl(selectedImagePath);
        }

        return space;
    }

    /**
     * Validates the form before saving
     */
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (txtName.getText().trim().isEmpty()) {
            errors.append("- Le nom de l'espace est requis.\n");
            txtName.setStyle("-fx-border-color: red;");
        } else {
            txtName.setStyle("");
        }

        if (comboType.getValue() == null || comboType.getValue().trim().isEmpty()) {
            errors.append("- Le type d'espace est requis.\n");
            comboType.setStyle("-fx-border-color: red;");
        } else {
            comboType.setStyle("");
        }

        if (txtCapacity.getText().trim().isEmpty()) {
            errors.append("- La capacité est requise.\n");
            txtCapacity.setStyle("-fx-border-color: red;");
        } else {
            try {
                int capacity = Integer.parseInt(txtCapacity.getText().trim());
                if (capacity <= 0) {
                    errors.append("- La capacité doit être un nombre supérieur à 0.\n");
                    txtCapacity.setStyle("-fx-border-color: red;");
                } else {
                    txtCapacity.setStyle("");
                }
            } catch (NumberFormatException e) {
                errors.append("- La capacité doit être un nombre valide.\n");
                txtCapacity.setStyle("-fx-border-color: red;");
            }
        }

        if (txtLocation.getText().trim().isEmpty()) {
            errors.append("- La localisation est requise.\n");
            txtLocation.setStyle("-fx-border-color: red;");
        } else {
            txtLocation.setStyle("");
        }

        if (txtPrice.getText().trim().isEmpty()) {
            errors.append("- Le prix est requis.\n");
            txtPrice.setStyle("-fx-border-color: red;");
        } else {
            try {
                double price = Double.parseDouble(txtPrice.getText().trim());
                if (price <= 0) {
                    errors.append("- Le prix doit être un nombre supérieur à 0.\n");
                    txtPrice.setStyle("-fx-border-color: red;");
                } else {
                    txtPrice.setStyle("");
                }
            } catch (NumberFormatException e) {
                errors.append("- Le prix doit être un nombre valide.\n");
                txtPrice.setStyle("-fx-border-color: red;");
            }
        }

        if (!errors.isEmpty()) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Formulaire Incomplet",
                    "Veuillez corriger les erreurs suivantes :",
                    errors.toString()
            );
            return false;
        }

        return true;
    }

    /**
     * Handles the cancel button click
     */
    @FXML
    private void handleCancel() {
        // Confirm if user wants to cancel
        if (hasFormChanges()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Annuler les modifications");
            alert.setContentText("Êtes-vous sûr de vouloir annuler ? Toutes les modifications seront perdues.");

            ButtonType buttonTypeYes = new ButtonType("Oui");
            ButtonType buttonTypeNo = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() != buttonTypeYes) {
                return;
            }
        }

        closeDialog();
    }

    /**
     * Checks if the form has unsaved changes
     */
    private boolean hasFormChanges() {
        if (isEditMode && espaceToEdit != null) {
            return !txtName.getText().equals(espaceToEdit.getNom())
                    || !comboType.getValue().equals(espaceToEdit.getType())
                    || !txtCapacity.getText().equals(String.valueOf(espaceToEdit.getCapacite()))
                    || !txtLocation.getText().equals(espaceToEdit.getLocalisation())
                    || !txtPrice.getText().equals(String.valueOf(espaceToEdit.getPrix()))
                    || checkAvailable.isSelected() != espaceToEdit.isDisponibilite()
                    || !txtDescription.getText().equals(espaceToEdit.getDescription())
                    || !txtPhotoUrl.getText().equals(espaceToEdit.getPhotoUrl())
                    || (selectedImagePath != null && !selectedImagePath.equals(espaceToEdit.getImage()));
        } else {
            return !txtName.getText().isEmpty()
                    || comboType.getValue() != null
                    || !txtCapacity.getText().isEmpty()
                    || !txtLocation.getText().isEmpty()
                    || !txtPrice.getText().isEmpty()
                    || !txtDescription.getText().isEmpty()
                    || !txtPhotoUrl.getText().isEmpty()
                    || selectedImagePath != null;
        }
    }

    /**
     * Closes the dialog
     */
    private void closeDialog() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * Shows an alert dialog
     */
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Set the parent controller for callback
     */
    public void setParentController(GestionEspacesController controller) {
        this.parentController = controller;
    }

    /**
     * Set the space to edit
     */
    public void setEspaceToEdit(Espace espace) {
        this.espaceToEdit = espace;
    }
}
