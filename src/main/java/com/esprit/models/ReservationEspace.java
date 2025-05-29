package com.esprit.models;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import com.esprit.services.EspaceService;

public class ReservationEspace {
    private int reservationId;
    private int espaceId;
    private String nomClient;
    private String emailClient;
    private String telephoneClient;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int nombrePersonnes;
    private String description;
    private LocalDateTime dateReservation;
    private String status;
    private double prixTotal;

    public ReservationEspace() {
        // Default constructor
        this.dateReservation = LocalDateTime.now();
        this.status = "En attente";
    }

    public ReservationEspace(int reservationId, int espaceId, String nomClient, String emailClient,
                           String telephoneClient, LocalDate dateDebut, LocalDate dateFin,
                           int nombrePersonnes, String description, String status) {
        this.reservationId = reservationId;
        this.espaceId = espaceId;
        this.nomClient = nomClient;
        this.emailClient = emailClient;
        this.telephoneClient = telephoneClient;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.nombrePersonnes = nombrePersonnes;
        this.description = description;
        this.dateReservation = LocalDateTime.now();
        this.status = status;
    }

    public ReservationEspace(int espaceId, String nomClient, String emailClient,
                           String telephoneClient, LocalDate dateDebut, LocalDate dateFin,
                           int nombrePersonnes, String description) {
        this.espaceId = espaceId;
        this.nomClient = nomClient;
        this.emailClient = emailClient;
        this.telephoneClient = telephoneClient;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.nombrePersonnes = nombrePersonnes;
        this.description = description;
        this.dateReservation = LocalDateTime.now();
        this.status = "En attente";
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getEspaceId() {
        return espaceId;
    }

    public void setEspaceId(int espaceId) {
        this.espaceId = espaceId;
    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getEmailClient() {
        return emailClient;
    }

    public void setEmailClient(String emailClient) {
        this.emailClient = emailClient;
    }

    public String getTelephoneClient() {
        return telephoneClient;
    }

    public void setTelephoneClient(String telephoneClient) {
        this.telephoneClient = telephoneClient;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public int getNombrePersonnes() {
        return nombrePersonnes;
    }

    public void setNombrePersonnes(int nombrePersonnes) {
        this.nombrePersonnes = nombrePersonnes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }

    /**
     * Méthode alternative pour maintenir la compatibilité avec le code existant
     * @return le statut
     * @deprecated Utiliser getStatut() à la place
     */
    public String getStatus() {
        return getStatut();
    }

    /**
     * Retourne le statut de la réservation
     * @return le statut
     */
    public String getStatut() {
        return status;
    }

    /**
     * Méthode alternative pour maintenir la compatibilité avec le code existant
     * @param status le nouveau statut
     * @deprecated Utiliser setStatut(String) à la place
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Définit le statut de la réservation
     * @param statut le nouveau statut
     */
    public void setStatut(String statut) {
        this.status = statut;
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }

    @Override
    public String toString() {
        return "ReservationEspace{" +
                "reservationId=" + reservationId +
                ", espaceId=" + espaceId +
                ", nomClient='" + nomClient + '\'' +
                ", emailClient='" + emailClient + '\'' +
                ", telephoneClient='" + telephoneClient + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", nombrePersonnes=" + nombrePersonnes +
                ", description='" + description + '\'' +
                ", dateReservation=" + dateReservation +
                ", status='" + status + '\'' +
                ", prixTotal=" + prixTotal +
                '}';
    }

    public void initData(Espace espaceSelectionne) {
        this.espaceId = espaceSelectionne.getId();
    }

    public void setEspace(Espace selectedSpace) {
        if (selectedSpace != null) {
            this.espaceId = selectedSpace.getId();
        }
    }

    public void setEmail(String email) {
        if (email != null) {
            this.emailClient = email.trim();
        }
    }

    public void setTelephone(String telephone) {
        if (telephone != null) {
            this.telephoneClient = telephone.trim();
        }
    }

    public Integer getId() {
        return reservationId;
    }

    /**
     * Calcule et retourne le montant total de la réservation
     * en fonction du prix de l'espace et de la durée de réservation
     * @return le montant total de la réservation
     */
    public Double getMontant() {
        if (dateDebut == null || dateFin == null) {
            return prixTotal > 0 ? prixTotal : 0.0;
        }
        
        // Si prixTotal est déjà défini, l'utiliser
        if (prixTotal > 0) {
            return prixTotal;
        }
        
        // Sinon, calculer en fonction de la durée
        long nbJours = ChronoUnit.DAYS.between(dateDebut, dateFin) + 1; // +1 pour inclure le jour de fin
        
        // Utiliser le service EspaceService pour obtenir le prix de l'espace
        try {
            EspaceService espaceService = new EspaceService();
            Espace espace = espaceService.getById(espaceId);
            if (espace != null) {
                return espace.getPrixLocation() * nbJours;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul du montant: " + e.getMessage());
        }
        
        return 0.0;
    }
}