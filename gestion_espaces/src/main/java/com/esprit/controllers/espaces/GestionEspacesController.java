package com.esprit.controllers.espaces;

import com.esprit.core.SharedData;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GestionEspacesController {
    @FXML private Label lblUserInfo;
    @FXML private Label lblSpaceStatus;

    @FXML
    public void initialize() {
        User user = SharedData.getCurrentUser();
        if (user != null) {
            lblUserInfo.setText("Session: " + user.getNom());
            lblSpaceStatus.setText(
                user.isAdmin() ? "Mode administrateur activé" : "Mode consultation"
            );
        }
    }
}
