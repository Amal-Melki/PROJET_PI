package com.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AffichePersonne {

    @FXML
    private Label lbNom;

    @FXML
    private Label lbPrenom;

    public void setLbNom(String nom) {
        this.lbNom.setText(nom);
    }

    public void setLbPrenom(String prenom) {
        this.lbPrenom.setText(prenom);
    }
}
