package com.esprit.services;
import com.esprit.models.Espace;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;


public class EspaceService {

    private static final Logger logger = LoggerFactory.getLogger(EspaceService.class);
    private static final String PHOTOS_DIR = System.getProperty("user.dir") + "\\src\\main\\resources\\images\\spaces";
    private static final String WEB_PHOTOS_PATH = "/images/spaces/"; // Chemin accessible via web

    public EspaceService() {
    }

    /**
     * Ajoute un nouvel espace dans la base de données
     *
     * @param espace L'espace à ajouter
     * @return L'ID généré de l'espace ajouté
     * @throws SQLException si l'ajout échoue
     * @throws IllegalArgumentException si les données sont invalides
     */
    public int add(Espace espace) throws SQLException {
        validateEspace(espace);
        String req = "INSERT INTO espace(nom, type, capacite, localisation, prix, disponibilite, photoUrl, description, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement pst = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            
            setEspaceParameters(pst, espace);
            
            int result = pst.executeUpdate();
            if (result == 0) {
                logger.error("Échec de l'ajout de l'espace : aucune ligne affectée");
                throw new SQLException("Échec de l'ajout, aucune ligne affectée");
            }
            
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    logger.error("Échec de la récupération de l'ID généré");
                    throw new SQLException("Échec de la récupération de l'ID généré");
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur SQL lors de l'ajout d'un espace", e);
            throw e;
        }
    }

    /**
     * Ajoute un nouvel espace avec une photo
     *
     * @param espace L'espace à ajouter
     * @param photoFile Le fichier photo à associer
     * @return L'ID généré de l'espace ajouté
     * @throws SQLException si l'ajout échoue
     * @throws IOException si la copie de la photo échoue
     * @throws IllegalArgumentException si les données sont invalides
     */
    public int add(Espace espace, File photoFile) throws SQLException, IOException {
        validateEspace(espace);
        
        // Create images directory if it doesn't exist
        File photosDir = new File(PHOTOS_DIR);
        if (!photosDir.exists()) {
            FileUtils.forceMkdir(photosDir);
        }
        
        // Generate unique filename with original extension
        String photoName = "space_" + UUID.randomUUID() + "." + FilenameUtils.getExtension(photoFile.getName());
        File destFile = new File(photosDir, photoName);
        
        // Copy file using FileUtils for better error handling
        FileUtils.copyFile(photoFile, destFile);
        
        // Set the web-accessible path
        espace.setImage(WEB_PHOTOS_PATH + photoName);
        
        // Insérer directement dans la base avec la photo
        String query = "INSERT INTO espace (nom, type, capacite, localisation, prix, disponibilite, photoUrl, description, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            setEspaceParameters(pst, espace);
            pst.setString(7, espace.getPhotoUrl());
            pst.setString(8, espace.getDescription());
            pst.setString(9, espace.getImage());
            
            pst.executeUpdate();
            
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        
        throw new SQLException("Échec de l'ajout de l'espace, aucun ID généré.");
    }

    /**
     * Valide les données d'un espace avant traitement
     * @param espace L'espace à valider
     * @throws IllegalArgumentException si les données sont invalides
     */
    private void validateEspace(Espace espace) throws IllegalArgumentException {
        if (espace == null) {
            throw new IllegalArgumentException("L'espace ne peut pas être null");
        }
        if (espace.getNom() == null || espace.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'espace est obligatoire");
        }
        if (espace.getCapacite() <= 0) {
            throw new IllegalArgumentException("La capacité doit être positive");
        }
        if (espace.getPrix() < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
    }

    /**
     * Définit les paramètres d'un espace dans un PreparedStatement
     * @param pst Le PreparedStatement
     * @param espace L'espace contenant les données
     * @throws SQLException en cas d'erreur SQL
     */
    private void setEspaceParameters(PreparedStatement pst, Espace espace) throws SQLException {
        pst.setString(1, espace.getNom());
        pst.setString(2, espace.getType());
        pst.setInt(3, espace.getCapacite());
        pst.setString(4, espace.getLocalisation());
        pst.setDouble(5, espace.getPrix());
        pst.setBoolean(6, espace.isDisponibilite());
    }

    /**
     * Méthode alternative pour ajouter un espace (pour maintenir la compatibilité)
     *
     * @param espace L'espace à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouter(Espace espace) {
        try {
            return add(espace) > 0;
        } catch (SQLException | IllegalArgumentException e) {
            logger.error("Erreur lors de l'ajout : " + e.getMessage());
            return false;
        }
    }

    /**
     * Met à jour un espace existant dans la base de données
     *
     * @param espace L'espace à mettre à jour
     * @return int 1 si la mise à jour a réussi, 0 sinon
     */
    public int update(Espace espace) {
        String req = "UPDATE espace SET nom=?, type=?, capacite=?, localisation=?, prix=?, disponibilite=?, photoUrl=?, description=?, image=? WHERE espaceId=?";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement pst = connection.prepareStatement(req)) {
            setEspaceParameters(pst, espace);
            pst.setInt(10, espace.getEspaceId());

            int result = pst.executeUpdate();
            logger.info("Espace mis à jour avec l'ID: " + espace.getEspaceId());
            return result;
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour : " + e.getMessage());
            return 0;
        }
    }

    /**
     * Met à jour un espace existant avec une nouvelle photo
     *
     * @param espace L'espace à mettre à jour
     * @param photoFile Le fichier photo à associer (peut être null pour ne pas changer la photo)
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean update(Espace espace, File photoFile) {
        try {
            // Si un fichier photo est fourni, le traiter
            if (photoFile != null) {
                // Créer le dossier images/spaces s'il n'existe pas
                File imagesDir = new File(PHOTOS_DIR);
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs();
                }

                // Valider le fichier photo
                if (!photoFile.exists()) {
                    throw new IllegalArgumentException("Le fichier photo est invalide ou manquant");
                }

                // Générer un nom de fichier unique
                String fileName = UUID.randomUUID() + "_" + photoFile.getName();
                String relativePhotoPath = WEB_PHOTOS_PATH + fileName;

                // Copier le fichier dans le dossier resources
                File destFile = new File(PHOTOS_DIR + "\\" + fileName);
                Files.copy(photoFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Mettre à jour le chemin de la photo
                espace.setPhotoUrl(relativePhotoPath);
            }

            // Mettre à jour l'espace dans la base de données
            return update(espace) == 1;
        } catch (IOException e) {
            logger.error("Erreur lors de la copie de la photo : " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la mise à jour : " + e.getMessage());
            return false;
        }
    }

    /**
     * Méthode alternative pour modifier un espace (pour maintenir la compatibilité)
     *
     * @param espace L'espace à modifier
     * @return true si la modification a réussi, false sinon
     */
    public boolean modifier(Espace espace) {
        return update(espace) == 1;
    }

    /**
     * Supprime un espace de la base de données
     *
     * @param espaceId L'ID de l'espace à supprimer
     * @return int 1 si la suppression a réussi, 0 sinon
     */
    public int delete(int espaceId) {
        String req = "DELETE FROM espace WHERE espaceId=?";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, espaceId);
            int result = pst.executeUpdate();
            logger.info("Espace supprimé avec l'ID: " + espaceId);
            return result;
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression : " + e.getMessage());
            return 0;
        }
    }

    /**
     * Méthode alternative pour supprimer un espace (pour maintenir la compatibilité)
     *
     * @param espaceId L'ID de l'espace à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimer(int espaceId) {
        return delete(espaceId) == 1;
    }

    /**
     * Récupère un espace par son ID
     *
     * @param espaceId L'ID de l'espace à récupérer
     * @return L'espace trouvé ou null si non trouvé
     */
    public Espace getById(int espaceId) {
        String req = "SELECT * FROM espace WHERE espaceId=?";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, espaceId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEspace(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération : " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupère tous les espaces de la base de données
     *
     * @return Liste de tous les espaces
     */
    public List<Espace> getAll() {
        logger.debug("Récupération de tous les espaces");
        long startTime = System.currentTimeMillis();

        List<Espace> espaces = new ArrayList<>();
        String req = "SELECT * FROM espace";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement pst = connection.prepareStatement(req);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Espace espace = mapResultSetToEspace(rs);
                espaces.add(espace);
            }

            logger.info("Récupération réussie de {} espaces ({} ms)",
                espaces.size(), System.currentTimeMillis() - startTime);
            return espaces;
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des espaces", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Méthode alternative pour récupérer tous les espaces (pour maintenir la compatibilité avec TableauDeBordController)
     *
     * @return Liste de tous les espaces
     */
    public List<Espace> getAllEspaces() {
        return getAll();
    }

    /**
     * Recherche des espaces par nom ou type
     *
     * @param keyword Mot-clé pour la recherche
     * @return Liste des espaces correspondant au critère de recherche
     */
    public List<Espace> searchByNameOrType(String keyword) {
        List<Espace> espaces = new ArrayList<>();
        String req = "SELECT * FROM espace WHERE nom LIKE ? OR type LIKE ?";

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement pst = connection.prepareStatement(req)) {

            String searchKeyword = "%" + keyword + "%";
            pst.setString(1, searchKeyword);
            pst.setString(2, searchKeyword);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Espace espace = mapResultSetToEspace(rs);
                    espaces.add(espace);
                }
            }

        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche d'espaces", e);
        }

        return espaces;
    }


    /**
     * Récupère tous les espaces disponibles
     *
     * @return Liste des espaces disponibles
     */
    public List<Espace> getAllAvailable() {
        List<Espace> espaces = new ArrayList<>();
        String req = "SELECT * FROM espace WHERE disponibilite=true";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement pst = connection.prepareStatement(req);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Espace espace = mapResultSetToEspace(rs);
                espaces.add(espace);
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération : " + e.getMessage());
        }
        return espaces;
    }

    /**
     * Récupère le nombre total d'espaces
     *
     * @return Le nombre total d'espaces
     */
    public int countTotalSpaces() {
        String req = "SELECT COUNT(*) FROM espace";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement pst = connection.prepareStatement(req);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Erreur lors du comptage : " + e.getMessage());
        }
        return 0;
    }

    /**
     * Récupère le nombre d'espaces disponibles
     *
     * @return Le nombre d'espaces disponibles
     */
    public int countAvailableSpaces() {
        String req = "SELECT COUNT(*) FROM espace WHERE disponibilite=true";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement pst = connection.prepareStatement(req);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Erreur lors du comptage : " + e.getMessage());
        }
        return 0;
    }

    /**
     * Helper method to map a ResultSet to an Espace object
     *
     * @param rs ResultSet to map
     * @return Mapped Espace object
     * @throws SQLException if a database access error occurs
     */
    private Espace mapResultSetToEspace(ResultSet rs) throws SQLException {
        Espace espace = new Espace();
        ResultSetMetaData metaData = rs.getMetaData();
        
        espace.setEspaceId(rs.getInt("espaceId"));
        espace.setNom(rs.getString("nom"));
        espace.setType(rs.getString("type"));
        espace.setCapacite(rs.getInt("capacite"));
        espace.setLocalisation(rs.getString("localisation"));
        espace.setPrix(rs.getDouble("prix"));
        espace.setDisponibilite(rs.getBoolean("disponibilite"));
        espace.setPhotoUrl(rs.getString("photoUrl"));

        if (hasColumn(metaData, "description")) {
            espace.setDescription(rs.getString("description"));
        } else {
            espace.setDescription("");
        }

        if (hasColumn(metaData, "image")) {
            espace.setImage(rs.getString("image"));
        } else if (espace.getPhotoUrl() != null && !espace.getPhotoUrl().isEmpty()) {
            espace.setImage(espace.getPhotoUrl());
        }

        return espace;
    }
    
    private boolean hasColumn(ResultSetMetaData metaData, String columnName) throws SQLException {
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            if (metaData.getColumnName(i).equalsIgnoreCase(columnName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Crée des espaces de démonstration pour le prototype
     * @return Liste d'espaces de démonstration
     */
    private List<Espace> createDummyEspaces() {
        // URLs d'images de démonstration
        String[] demoImageUrls = {
            "https://images.unsplash.com/photo-1517457373958-b7bdd4587205?w=600&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1414124488080-0188dcbb8834?w=600&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1497366754035-f200968a6e72?w=600&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1497366811353-6870744d04b2?w=600&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1517457373958-b7bdd4587205?w=600&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1517048676732-d65bc937f952?w=600&auto=format&fit=crop"
        };

        // Créer des espaces de démonstration
        List<Espace> dummySpaces = new ArrayList<>();

        Espace espace1 = new Espace(1, "Grande Salle", "Conférence", 200, "Tunis Centre", 1500.0, true);
        espace1.setImage(demoImageUrls[0]);
        espace1.setDescription("Grande salle adaptée pour des conférences et événements importants.");
        dummySpaces.add(espace1);

        Espace espace2 = new Espace(2, "Jardin Event", "Extérieur", 150, "Gammarth", 2000.0, true);
        espace2.setImage(demoImageUrls[1]);
        espace2.setDescription("Jardin spacieux idéal pour les mariages et les cérémonies en plein air.");
        dummySpaces.add(espace2);

        Espace espace3 = new Espace(3, "Studio Photo", "Studio", 20, "La Marsa", 500.0, false);
        espace3.setImage(demoImageUrls[2]);
        espace3.setDescription("Studio professionnel pour séances photo et petits tournages.");
        dummySpaces.add(espace3);

        Espace espace4 = new Espace(4, "Salle de Réunion A", "Réunion", 15, "Les Berges du Lac", 350.0, true);
        espace4.setImage(demoImageUrls[3]);
        espace4.setDescription("Salle de réunion équipée de technologies modernes pour vos réunions professionnelles.");
        dummySpaces.add(espace4);

        Espace espace5 = new Espace(5, "Salle de Réunion B", "Réunion", 10, "Les Berges du Lac", 250.0, true);
        espace5.setImage(demoImageUrls[4]);
        espace5.setDescription("Petite salle de réunion pour des rencontres professionnelles intimes.");
        dummySpaces.add(espace5);

        Espace espace6 = new Espace(6, "Salle de Conférence", "Conférence", 100, "Sousse", 1000.0, false);
        espace6.setImage(demoImageUrls[5]);
        espace6.setDescription("Salle de conférence spacieuse à Sousse pour des événements professionnels.");
        dummySpaces.add(espace6);

        Espace espace7 = new Espace(7, "Espace Lounge", "Cocktail", 80, "Hammamet", 1200.0, true);
        espace7.setImage(demoImageUrls[0]);
        espace7.setDescription("Espace convivial pour des cocktails et des événements de networking.");
        dummySpaces.add(espace7);

        Espace espace8 = new Espace(8, "Terrasse Vue Mer", "Extérieur", 100, "Hammamet", 1800.0, true);
        espace8.setImage(demoImageUrls[1]);
        espace8.setDescription("Magnifique terrasse avec vue sur la mer pour des événements inoubliables.");
        dummySpaces.add(espace8);

        return dummySpaces;
    }


    public void updateEspace(Espace espace) {
        Connection conn = null;
        try {
            conn = DataSource.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Opérations DB...

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
            }
        }
    }
}