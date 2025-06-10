package com.esprit.services;

import com.esprit.models.ReservationEspace;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ReservationEspaceService {

    private static final Logger logger = Logger.getLogger(ReservationEspaceService.class.getName());
    private static volatile ReservationEspaceService instance;
    
    public ReservationEspaceService() {}
    
    public static ReservationEspaceService getInstance() {
        if (instance == null) {
            synchronized (ReservationEspaceService.class) {
                if (instance == null) {
                    instance = new ReservationEspaceService();
                }
            }
        }
        return instance;
    }

    // Méthode principale pour récupérer toutes les réservations
    public List<ReservationEspace> getAll(int userId) {
        List<ReservationEspace> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservationespace WHERE id_user = ?";

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
        
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                reservations.add(mapResultSetToReservation(resultSet));
            }
            logger.info("Récupération de " + reservations.size() + " réservations pour l'utilisateur " + userId);
        } catch (SQLException e) {
            logger.severe("Erreur lors de la récupération des réservations: " + e.getMessage());
        }
        return reservations;
    }

    // Mapping ResultSet -> ReservationEspace (unifié)
    private ReservationEspace mapResultSetToReservation(ResultSet rs) throws SQLException {
        ReservationEspace reservation = new ReservationEspace();
        reservation.setReservationId(rs.getInt("reservationId"));
        reservation.setEspaceId(rs.getInt("espaceId"));
        reservation.setNomClient(rs.getString("nomClient"));
        reservation.setEmailClient(rs.getString("emailClient"));
        reservation.setTelephoneClient(rs.getString("telephoneClient"));
        reservation.setDateDebut(rs.getDate("dateDebut").toLocalDate());
        reservation.setDateFin(rs.getDate("dateFin").toLocalDate());
        reservation.setNombrePersonnes(rs.getInt("nombrePersonnes"));
        reservation.setDescription(rs.getString("description"));
        Timestamp dateReservationTimestamp = rs.getTimestamp("dateReservation");
        if (dateReservationTimestamp != null) {
            reservation.setDateReservation(dateReservationTimestamp.toLocalDateTime());
        }
        reservation.setStatus(rs.getString("status"));
        reservation.setPrixTotal(rs.getDouble("prixTotal"));
        return reservation;
    }

    // Gestion centralisée des erreurs
    private void handleDatabaseError(String context, SQLException e) {
        String errorMsg = String.format("[ERREUR] %s\nCode: %d\nMessage: %s",
                context, e.getErrorCode(), e.getMessage());
        logger.severe(errorMsg);

        // TODO: Implémenter une notification utilisateur via Alert
    }

    // Vérifie s'il existe une réservation qui chevauche la nouvelle réservation
    private boolean hasOverlappingReservation(ReservationEspace reservation) {
        String query = "SELECT COUNT(*) FROM reservationespace WHERE espaceId = ? AND status NOT IN ('Annulée', 'Terminée') "
                + "AND ((dateDebut <= ? AND dateFin >= ?) OR (dateDebut <= ? AND dateFin >= ?) OR (dateDebut >= ? AND dateFin <= ?))";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, reservation.getEspaceId());
            pst.setDate(2, Date.valueOf(reservation.getDateFin()));
            pst.setDate(3, Date.valueOf(reservation.getDateDebut()));
            pst.setDate(4, Date.valueOf(reservation.getDateFin()));
            pst.setDate(5, Date.valueOf(reservation.getDateDebut()));
            pst.setDate(6, Date.valueOf(reservation.getDateDebut()));
            pst.setDate(7, Date.valueOf(reservation.getDateFin()));

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            handleDatabaseError("Erreur lors de la vérification des chevauchements de réservation", e);
        }
        return false;
    }

    // Méthode utilitaire pour les paramètres
    private void setReservationParameters(PreparedStatement pst, ReservationEspace r) throws SQLException {
        pst.setInt(1, r.getEspaceId());
        pst.setString(2, r.getNomClient());
        pst.setString(3, r.getEmailClient());
        pst.setString(4, r.getTelephoneClient());
        pst.setDate(5, Date.valueOf(r.getDateDebut()));
        pst.setDate(6, Date.valueOf(r.getDateFin()));
        pst.setInt(7, r.getNombrePersonnes());
        pst.setString(8, r.getDescription());
        pst.setString(9, r.getStatus() != null ? r.getStatus() : "En attente");
    }

    // Méthodes CRUD avec gestion des transactions
    public int add(ReservationEspace reservation) {
        if (hasOverlappingReservation(reservation)) {
            logger.warning("Tentative de création d'une réservation en conflit pour l'espace " + reservation.getEspaceId());
            return -2; // Indicateur de conflit
        }

        String query = "INSERT INTO reservationespace(espaceId, nomClient, emailClient, telephoneClient, "
                + "dateDebut, dateFin, nombrePersonnes, description, status, prixTotal) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataSource.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                setReservationParameters(pst, reservation);
                pst.setDouble(10, reservation.getPrixTotal());

                int affectedRows = pst.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return -1;
                }

                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        conn.commit();
                        return generatedKeys.getInt(1);
                    }
                }
                conn.rollback();
                return -1;
            } catch (SQLException e) {
                conn.rollback();
                logger.severe("Erreur lors de l'ajout d'une réservation: " + e.getMessage());
                return -1;
            }
        } catch (SQLException e) {
            logger.severe("Erreur de connexion: " + e.getMessage());
            return -1;
        }
    }

    // Autres méthodes CRUD (update, delete) avec le même pattern
    public int update(ReservationEspace r) {
        if (hasOverlappingReservation(r)) {
            logger.warning("Tentative de mise à jour d'une réservation en conflit pour l'espace " + r.getEspaceId());
            return 0; // Indicateur de conflit
        }

        String req = "UPDATE reservationespace SET espaceId=?, nomClient=?, emailClient=?, telephoneClient=?, dateDebut=?, dateFin=?, nombrePersonnes=?, description=?, status=?, prixTotal=? WHERE reservationId=?";
        try (Connection conn = DataSource.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement pst = conn.prepareStatement(req)) {
                pst.setInt(1, r.getEspaceId());
                pst.setString(2, r.getNomClient());
                pst.setString(3, r.getEmailClient());
                pst.setString(4, r.getTelephoneClient());
                pst.setDate(5, Date.valueOf(r.getDateDebut()));
                pst.setDate(6, Date.valueOf(r.getDateFin()));
                pst.setInt(7, r.getNombrePersonnes());
                pst.setString(8, r.getDescription());
                pst.setString(9, r.getStatus());
                pst.setDouble(10, r.getPrixTotal());
                pst.setInt(11, r.getReservationId());
                
                int result = pst.executeUpdate();
                conn.commit();
                logger.info("Reservation updated with ID: " + r.getReservationId());
                return result;
            } catch (SQLException e) {
                conn.rollback();
                handleDatabaseError("Erreur lors de la mise à jour d'une réservation", e);
                return 0;
            }
        } catch (SQLException e) {
            handleDatabaseError("Erreur de connexion", e);
            return 0;
        }
    }

    public List<ReservationEspace> getAllReservations() {
        List<ReservationEspace> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservationespace ORDER BY dateDebut DESC";

        try (Connection conn = DataSource.getInstance().getConnection()) {
            if (conn == null || conn.isClosed()) {
                throw new SQLException("La connexion à la base de données est indisponible");
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
                logger.info("Récupération de " + reservations.size() + " réservations");
            }
        } catch (SQLException e) {
            handleDatabaseError("Erreur lors de la récupération des réservations", e);
            throw new RuntimeException("Échec du chargement des réservations", e);
        }
        return reservations;
    }

    // Méthode pour récupérer les réservations par espace
    public List<ReservationEspace> getReservationsByEspace(int espaceId) {
        List<ReservationEspace> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservationespace WHERE espace_id = ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, espaceId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            logger.info("Erreur lors de la récupération des réservations");
            throw new RuntimeException(e);
        }
        return reservations;
    }

    // Méthode pour récupérer les réservations par statut
    public List<ReservationEspace> getByStatus(String status) {
        String query = "SELECT * FROM reservationespace WHERE status = ? ORDER BY dateDebut";
        List<ReservationEspace> reservations = new ArrayList<>();

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, status);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            handleDatabaseError("Erreur lors de la récupération des réservations par statut", e);
        }
        return reservations;
    }

    /**
     * Annule une réservation et retourne true si réussite
     */
    public boolean cancelReservation(int reservationId) throws SQLException {
        String query = "UPDATE reservationespace SET status = 'Annulée' WHERE reservationId = ? AND status NOT IN ('Terminée', 'Annulée')";
        
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement pst = connection.prepareStatement(query)) {
            
            pst.setInt(1, reservationId);
            int rowsAffected = pst.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Réservation annulée : " + reservationId);
                return true;
            } else {
                logger.warning("Échec annulation réservation : " + reservationId + " (déjà annulée/terminée ou ID invalide)");
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur SQL lors de l'annulation", e);
            throw e;
        }
    }

    // Méthode pour confirmer une réservation
    public boolean confirmReservation(int reservationId) {
        String query = "UPDATE reservationespace SET status = 'Confirmée' WHERE reservationId = ?";
        
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            conn.setAutoCommit(false);
            pst.setInt(1, reservationId);
            int rowsAffected = pst.executeUpdate();
            conn.commit();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            handleDatabaseError("Erreur lors de la confirmation de la réservation", e);
            return false;
        }
    }

    // Méthode pour calculer le prix total
    public double calculatePrice(int espaceId, LocalDate startDate, LocalDate endDate) {
        String query = "SELECT prix FROM espace WHERE espaceId = ?";
        
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, espaceId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    double dailyPrice = rs.getDouble("prix");
                    long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                    return dailyPrice * days;
                }
            }
        } catch (SQLException e) {
            handleDatabaseError("Erreur lors du calcul du prix", e);
        }
        return 0;
    }

    public List<ReservationEspace> getReservationsByUser(int userId) {
        List<ReservationEspace> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservationespace WHERE id_user = ? ORDER BY dateDebut DESC";
        
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                reservations.add(mapResultSetToReservation(resultSet));
            }
            logger.info("Récupération de " + reservations.size() + " réservations pour l'utilisateur " + userId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des réservations utilisateur", e);
            throw new RuntimeException("Erreur lors du chargement des réservations", e);
        }
        return reservations;
    }
}