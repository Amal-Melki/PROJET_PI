package com.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MesReservationsClient {

    @FXML
    private Button btnAnnulerReservation;

    @FXML
    private Button btnRetour;

    @FXML
    private Button btnVoirDetails;

    @FXML
    private TableColumn<?, ?> colDateDebut;

    @FXML
    private TableColumn<?, ?> colDateFin;

    @FXML
    private TableColumn<?, ?> colEspace;

    @FXML
    private TableColumn<?, ?> colReservationId;

    @FXML
    private TableColumn<?, ?> colStatus;

    @FXML
    private TableView<?> tableReservations;

    @FXML
    void annulerReservation(ActionEvent event) {

    }

    @FXML
    void retourConsulterEspaces(ActionEvent event) {

    }

    @FXML
    void voirDetails(ActionEvent event) {

    }

}
