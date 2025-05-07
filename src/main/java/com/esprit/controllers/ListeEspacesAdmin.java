package com.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ListeEspacesAdmin {

    @FXML
    private TableColumn<?, ?> colCapacite;

    @FXML
    private TableColumn<?, ?> colDisponibilite;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colLocalisation;

    @FXML
    private TableColumn<?, ?> colNom;

    @FXML
    private TableColumn<?, ?> colPrix;

    @FXML
    private TableColumn<?, ?> colType;

    @FXML
    private TableView<?> tableEspace;

    @FXML
    void enregistrerEspace(ActionEvent event) {

    }

    @FXML
    void retourMenu(ActionEvent event) {

    }

    @FXML
    void supprimerEspace(ActionEvent event) {

    }

}
