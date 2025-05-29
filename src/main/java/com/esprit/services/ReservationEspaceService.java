package com.esprit.services;

import com.esprit.models.ReservationEspace;
import com.esprit.utils.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReservationEspaceService {

    private Connection connection;
    private EmailService emailService;

    public ReservationEspaceService() {
        connection = DataSource.getInstance().getConnection();
        emailService = new EmailService();
    }

    public int add(ReservationEspace r) {
        String req = "INSERT INTO reservationespace(espaceId, nomClient, emailClient, telephoneClient, dateDebut, dateFin, nombrePersonnes, description, dateReservation, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?)";
        int generatedId = -1;
        try {
            PreparedStatement pst = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, r.getEspaceId());
            pst.setString(2, r.getNomClient());
            pst.setString(3, r.getEmailClient());
            pst.setString(4, r.getTelephoneClient());
            pst.setDate(5, Date.valueOf(r.getDateDebut()));
            pst.setDate(6, Date.valueOf(r.getDateFin()));
            pst.setInt(7, r.getNombrePersonnes());
            pst.setString(8, r.getDescription());
            pst.setString(9, r.getStatut() != null ? r.getStatut() : "En attente");
            pst.executeUpdate();
            
            // Récupérer l'ID généré
            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedId = generatedKeys.getInt(1);
                r.setReservationId(generatedId); // Mettre à jour l'objet avec l'ID généré
            }
            
            System.out.println("Reservation added with ID: " + generatedId);
        } catch (SQLException e) {
            System.out.println("Error adding reservation: " + e.getMessage());
            e.printStackTrace();
        }
        return generatedId;
    }

    public int update(ReservationEspace r) {
        String req = "UPDATE reservationespace SET espaceId=?, nomClient=?, emailClient=?, telephoneClient=?, dateDebut=?, dateFin=?, nombrePersonnes=?, description=?, statut=? WHERE reservationId=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, r.getEspaceId());
            pst.setString(2, r.getNomClient());
            pst.setString(3, r.getEmailClient());
            pst.setString(4, r.getTelephoneClient());
            pst.setDate(5, Date.valueOf(r.getDateDebut()));
            pst.setDate(6, Date.valueOf(r.getDateFin()));
            pst.setInt(7, r.getNombrePersonnes());
            pst.setString(8, r.getDescription());
            pst.setString(9, r.getStatut() != null ? r.getStatut() : "En attente");
            pst.setInt(10, r.getReservationId());
            int result = pst.executeUpdate();
            System.out.println("Reservation updated with ID: " + r.getReservationId());
            return result;
        } catch (SQLException e) {
            System.out.println("Error updating reservation: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public int delete(int reservationId) {
        String req = "DELETE FROM reservationespace WHERE reservationId=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, reservationId);
            int result = pst.executeUpdate();
            System.out.println("Reservation deleted with ID: " + reservationId);
            return result;
        } catch (SQLException e) {
            System.out.println("Error deleting reservation: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Alias pour la méthode delete (pour maintenir la compatibilité)
     * @param reservationId L'ID de la réservation à supprimer
     */
    public void supprimer(int reservationId) {
        delete(reservationId);
    }

    public List<ReservationEspace> getAll() {
        List<ReservationEspace> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservationespace";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ReservationEspace r = mapResultSetToReservation(rs);
                reservations.add(r);
            }
            System.out.println("Retrieved " + reservations.size() + " reservations from database");
        } catch (SQLException e) {
            System.out.println("Error fetching reservations: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Si aucune réservation n'est trouvée, créer des exemples
        if (reservations.isEmpty()) {
            reservations = createDummyReservations();
            System.out.println("No reservations found in database, using demo data");
        }
        
        return reservations;
    }

    /**
     * Méthode alternative pour récupérer toutes les réservations (pour maintenir la compatibilité)
     * @return Liste de toutes les réservations
     */
    public List<ReservationEspace> getAllReservations() {
        return getAll();
    }

    public List<ReservationEspace> getReservationsBetweenDates(LocalDate start, LocalDate end) {
        List<ReservationEspace> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservationespace WHERE dateDebut >= ? AND dateFin <= ?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setDate(1, Date.valueOf(start));
            pst.setDate(2, Date.valueOf(end));

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ReservationEspace r = mapResultSetToReservation(rs);
                reservations.add(r);
            }
            System.out.println("Retrieved " + reservations.size() + " reservations between dates " + 
                               start + " and " + end);
        } catch (SQLException e) {
            System.out.println("Error fetching reservations between dates: " + e.getMessage());
            e.printStackTrace();
        }
        return reservations;
    }

    public int creerReservation(int espaceId, String nomClient, String emailClient, String telephoneClient,
                                LocalDate dateDebut, LocalDate dateFin, int nombrePersonnes, String description) {
        ReservationEspace r = new ReservationEspace();
        r.setEspaceId(espaceId);
        r.setNomClient(nomClient);
        r.setEmailClient(emailClient);
        r.setTelephoneClient(telephoneClient);
        r.setDateDebut(dateDebut);
        r.setDateFin(dateFin);
        r.setNombrePersonnes(nombrePersonnes);
        r.setDescription(description);
        r.setStatut("En attente");
        
        int reservationId = add(r);
        
        // Le contenu de l'email est maintenant géré dans le contrôleur pour inclure plus de détails
        // et le numéro de réservation
        
        return reservationId;
    }

    public int creerReservation(String currentUserEmail, int espaceId, LocalDate dateDebut, LocalDate dateFin) {
        ReservationEspace r = new ReservationEspace();
        r.setEspaceId(espaceId);
        r.setNomClient("Client"); // Default name
        r.setEmailClient(currentUserEmail);
        r.setTelephoneClient(""); // Will need to be updated later
        r.setDateDebut(dateDebut);
        r.setDateFin(dateFin);
        r.setNombrePersonnes(1); // Default value
        r.setDescription("Réservation en ligne"); // Default description
        r.setStatut("En attente");
        
        // Add the reservation to the database
        return add(r);
    }

    /**
     * Récupère une réservation par son ID
     * @param id L'identifiant de la réservation
     * @return La réservation correspondante ou null si non trouvée
     */
    public ReservationEspace getReservationById(int id) {
        String req = "SELECT * FROM reservationespace WHERE reservationId = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToReservation(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching reservation by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Récupère toutes les réservations pour un espace donné
     * @param espaceId L'ID de l'espace
     * @return Liste des réservations pour cet espace
     */
    public List<ReservationEspace> getReservationsByEspaceId(int espaceId) {
        List<ReservationEspace> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservationespace WHERE espaceId = ?";
        
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, espaceId);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                ReservationEspace r = mapResultSetToReservation(rs);
                reservations.add(r);
            }
            System.out.println("Retrieved " + reservations.size() + " reservations for space ID " + espaceId);
        } catch (SQLException e) {
            System.out.println("Error fetching reservations by space ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reservations;
    }
    
    /**
     * Récupère toutes les réservations pour un client donné
     * @param emailClient L'email du client
     * @return Liste des réservations pour ce client
     */
    public List<ReservationEspace> getReservationsByClient(String emailClient) {
        List<ReservationEspace> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservationespace WHERE emailClient = ?";
        
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, emailClient);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                ReservationEspace r = mapResultSetToReservation(rs);
                reservations.add(r);
            }
            System.out.println("Retrieved " + reservations.size() + " reservations for client " + emailClient);
        } catch (SQLException e) {
            System.out.println("Error fetching reservations by client: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reservations;
    }
    
    /**
     * Helper method to map a ResultSet to a ReservationEspace object
     * @param rs ResultSet to map
     * @return Mapped ReservationEspace object
     * @throws SQLException if a database access error occurs
     */
    private ReservationEspace mapResultSetToReservation(ResultSet rs) throws SQLException {
        ReservationEspace r = new ReservationEspace();
        r.setReservationId(rs.getInt("reservationId"));
        r.setEspaceId(rs.getInt("espaceId"));
        r.setNomClient(rs.getString("nomClient"));
        r.setEmailClient(rs.getString("emailClient"));
        r.setTelephoneClient(rs.getString("telephoneClient"));
        r.setDateDebut(rs.getDate("dateDebut").toLocalDate());
        r.setDateFin(rs.getDate("dateFin").toLocalDate());
        r.setNombrePersonnes(rs.getInt("nombrePersonnes"));
        r.setDescription(rs.getString("description"));
        
        // Get status if it exists
        try {
            r.setStatut(rs.getString("statut"));
        } catch (SQLException e) {
            // Column might not exist in older schema versions
            r.setStatut("En attente");
        }
        
        return r;
    }
    
    /**
     * Crée des réservations de démonstration pour le prototype
     * @return Liste de réservations de démonstration
     */
    private List<ReservationEspace> createDummyReservations() {
        List<ReservationEspace> dummyReservations = new ArrayList<>();
        Random random = new Random();
        LocalDate today = LocalDate.now();
        
        // Noms de clients de démonstration
        String[] clientNames = {
            "Sophie Martin", "Thomas Dubois", "Emma Bernard", "Lucas Robert", 
            "Chloé Petit", "Antoine Durand", "Léa Richard", "Hugo Moreau"
        };
        
        // Emails de démonstration
        String[] clientEmails = {
            "sophie.martin@example.com", "thomas.dubois@example.com", 
            "emma.bernard@example.com", "lucas.robert@example.com",
            "chloe.petit@example.com", "antoine.durand@example.com", 
            "lea.richard@example.com", "hugo.moreau@example.com"
        };
        
        // Téléphones de démonstration
        String[] clientPhones = {
            "0123456789", "0234567890", "0345678901", "0456789012",
            "0567890123", "0678901234", "0789012345", "0890123456"
        };
        
        // Descriptions de démonstration
        String[] descriptions = {
            "Réunion d'entreprise", "Mariage", "Conférence annuelle", 
            "Séminaire de formation", "Fête d'anniversaire", "Exposition", 
            "Lancement de produit", "Atelier"
        };
        
        // Créer des réservations pour les 6 derniers mois avec différents espaces
        for (int i = 0; i < 20; i++) {
            ReservationEspace reservation = new ReservationEspace();
            
            // ID unique pour la réservation
            reservation.setReservationId(i + 1);
            
            // Espace ID entre 1 et 8
            int espaceId = random.nextInt(8) + 1;
            reservation.setEspaceId(espaceId);
            
            // Client aléatoire
            int clientIndex = random.nextInt(clientNames.length);
            reservation.setNomClient(clientNames[clientIndex]);
            reservation.setEmailClient(clientEmails[clientIndex]);
            reservation.setTelephoneClient(clientPhones[clientIndex]);
            
            // Date aléatoire dans les 6 derniers mois
            int randomMonthOffset = random.nextInt(6);
            LocalDate startDate = today.minusMonths(randomMonthOffset).minusDays(random.nextInt(28));
            LocalDate endDate = startDate.plusDays(random.nextInt(5) + 1); // Durée de 1 à 5 jours
            
            reservation.setDateDebut(startDate);
            reservation.setDateFin(endDate);
            
            // Nombre de personnes aléatoire
            reservation.setNombrePersonnes(random.nextInt(50) + 5); // Entre 5 et 54 personnes
            
            // Description aléatoire
            reservation.setDescription(descriptions[random.nextInt(descriptions.length)]);
            
            // Status aléatoire (la plupart confirmées)
            String[] statuses = {"En attente", "En attente", "En attente", "En attente", "Annulée"};
            reservation.setStatut(statuses[random.nextInt(statuses.length)]);
            
            dummyReservations.add(reservation);
        }
        
        return dummyReservations;
    }
    
    /**
     * Récupère le nombre total de réservations
     * @return Le nombre total de réservations
     */
    public int countTotalReservations() {
        String req = "SELECT COUNT(*) FROM reservationespace";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error counting reservations: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Récupère le nombre de réservations par mois
     * @return Map avec le mois (1-12) comme clé et le nombre de réservations comme valeur
     */
    public List<ReservationEspace> getReservationsForMonth(Month month, int year) {
        List<ReservationEspace> reservations = new ArrayList<>();
        
        // Créer les dates de début et de fin du mois
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        
        String req = "SELECT * FROM reservationespace WHERE dateDebut >= ? AND dateDebut <= ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setDate(1, Date.valueOf(startOfMonth));
            pst.setDate(2, Date.valueOf(endOfMonth));
            
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ReservationEspace r = mapResultSetToReservation(rs);
                reservations.add(r);
            }
            System.out.println("Retrieved " + reservations.size() + " reservations for " + 
                              month + " " + year);
        } catch (SQLException e) {
            System.out.println("Error fetching reservations for month: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reservations;
    }


    public int addReservation(ReservationEspace reservation) {
        return 0;
    }
}
