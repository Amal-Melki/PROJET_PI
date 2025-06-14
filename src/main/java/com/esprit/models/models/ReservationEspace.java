package com.esprit.models.models;

import com.esprit.services.EspaceService;
import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ReservationEspace {
    private final IntegerProperty reservationId = new SimpleIntegerProperty();
    private final IntegerProperty espaceId = new SimpleIntegerProperty();
    private final StringProperty nomClient = new SimpleStringProperty();
    private final StringProperty emailClient = new SimpleStringProperty();
    private final StringProperty telephoneClient = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dateDebut = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> dateFin = new SimpleObjectProperty<>();
    private final IntegerProperty nombrePersonnes = new SimpleIntegerProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> dateReservation = new SimpleObjectProperty<>();
    private final StringProperty status = new SimpleStringProperty();
    private final DoubleProperty prixTotal = new SimpleDoubleProperty();

    public ReservationEspace() {
        this.dateReservation.set(LocalDateTime.now());
        this.status.set("En attente");
    }

    public ReservationEspace(int reservationId, int espaceId, String nomClient, String emailClient,
                           String telephoneClient, LocalDate dateDebut, LocalDate dateFin,
                           int nombrePersonnes, String description, String status) {
        this.reservationId.set(reservationId);
        this.espaceId.set(espaceId);
        this.nomClient.set(nomClient);
        this.emailClient.set(emailClient);
        this.telephoneClient.set(telephoneClient);
        this.dateDebut.set(dateDebut);
        this.dateFin.set(dateFin);
        this.nombrePersonnes.set(nombrePersonnes);
        this.description.set(description);
        this.dateReservation.set(LocalDateTime.now());
        this.status.set(status);
    }

    public ReservationEspace(int espaceId, String nomClient, String emailClient,
                           String telephoneClient, LocalDate dateDebut, LocalDate dateFin,
                           int nombrePersonnes, String description) {
        this.espaceId.set(espaceId);
        this.nomClient.set(nomClient);
        this.emailClient.set(emailClient);
        this.telephoneClient.set(telephoneClient);
        this.dateDebut.set(dateDebut);
        this.dateFin.set(dateFin);
        this.nombrePersonnes.set(nombrePersonnes);
        this.description.set(description);
        this.dateReservation.set(LocalDateTime.now());
        this.status.set("En attente");
    }

    public int getReservationId() { return reservationId.get(); }
    public void setReservationId(int id) { this.reservationId.set(id); }
    public IntegerProperty reservationIdProperty() { return reservationId; }

    public int getEspaceId() { return espaceId.get(); }
    public void setEspaceId(int id) { this.espaceId.set(id); }
    public IntegerProperty espaceIdProperty() { return espaceId; }

    public String getNomClient() { return nomClient.get(); }
    public void setNomClient(String nom) { this.nomClient.set(nom); }
    public StringProperty nomClientProperty() { return nomClient; }

    public String getEmailClient() { return emailClient.get(); }
    public void setEmailClient(String email) { this.emailClient.set(email); }
    public StringProperty emailClientProperty() { return emailClient; }

    public String getTelephoneClient() { return telephoneClient.get(); }
    public void setTelephoneClient(String telephone) { this.telephoneClient.set(telephone); }
    public StringProperty telephoneClientProperty() { return telephoneClient; }

    public LocalDate getDateDebut() { return dateDebut.get(); }
    public void setDateDebut(LocalDate date) { this.dateDebut.set(date); }
    public ObjectProperty<LocalDate> dateDebutProperty() { return dateDebut; }

    public LocalDate getDateFin() { return dateFin.get(); }
    public void setDateFin(LocalDate date) { this.dateFin.set(date); }
    public ObjectProperty<LocalDate> dateFinProperty() { return dateFin; }

    public int getNombrePersonnes() { return nombrePersonnes.get(); }
    public void setNombrePersonnes(int nombre) { this.nombrePersonnes.set(nombre); }
    public IntegerProperty nombrePersonnesProperty() { return nombrePersonnes; }

    public String getDescription() { return description.get(); }
    public void setDescription(String desc) { this.description.set(desc); }
    public StringProperty descriptionProperty() { return description; }

    public LocalDateTime getDateReservation() { return dateReservation.get(); }
    public void setDateReservation(LocalDateTime date) { this.dateReservation.set(date); }
    public ObjectProperty<LocalDateTime> dateReservationProperty() { return dateReservation; }

    public String getStatus() { return status.get(); }
    public void setStatus(String statut) { this.status.set(statut); }
    public StringProperty statusProperty() { return status; }

    public double getPrixTotal() { return prixTotal.get(); }
    public void setPrixTotal(double prix) { this.prixTotal.set(prix); }
    public DoubleProperty prixTotalProperty() { return prixTotal; }

    @Override
    public String toString() {
        return "ReservationEspace{" +
                "reservationId=" + reservationId.get() +
                ", espaceId=" + espaceId.get() +
                ", nomClient='" + nomClient.get() + '\'' +
                ", emailClient='" + emailClient.get() + '\'' +
                ", telephoneClient='" + telephoneClient.get() + '\'' +
                ", dateDebut=" + dateDebut.get() +
                ", dateFin=" + dateFin.get() +
                ", nombrePersonnes=" + nombrePersonnes.get() +
                ", description='" + description.get() + '\'' +
                ", dateReservation=" + dateReservation.get() +
                ", status='" + status.get() + '\'' +
                ", prixTotal=" + prixTotal.get() +
                '}';
    }

    public void initData(Espace espaceSelectionne) {
        this.espaceId.set(espaceSelectionne.getId());
    }

    public void setEspace(Espace selectedSpace) {
        if (selectedSpace != null) {
            this.espaceId.set(selectedSpace.getId());
        }
    }

    public void setEmail(String email) {
        if (email != null) {
            this.emailClient.set(email.trim());
        }
    }

    public void setTelephone(String telephone) {
        if (telephone != null) {
            this.telephoneClient.set(telephone.trim());
        }
    }

    public Integer getId() {
        return reservationId.get();
    }

    public Double getMontant() {
        if (dateDebut.get() == null || dateFin.get() == null) {
            return prixTotal.get() > 0 ? prixTotal.get() : 0.0;
        }
        
        // Si prixTotal est déjà défini, l'utiliser
        if (prixTotal.get() > 0) {
            return prixTotal.get();
        }
        
        // Sinon, calculer en fonction de la durée
        long nbJours = ChronoUnit.DAYS.between(dateDebut.get(), dateFin.get()) + 1; // +1 pour inclure le jour de fin
        
        // Utiliser le service EspaceService pour obtenir le prix de l'espace
        try {
            EspaceService espaceService = new EspaceService();
            Espace espace = espaceService.getById(espaceId.get());
            if (espace != null) {
                return espace.getPrixLocation() * nbJours;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul du montant: " + e.getMessage());
        }
        
        return 0.0;
    }

    public String getUserId() {
        return this.emailClient.get(); // Utilisation de l'email comme identifiant utilisateur
    }

    public Integer getNbrPersonnes() {
        return 0;
    }
}