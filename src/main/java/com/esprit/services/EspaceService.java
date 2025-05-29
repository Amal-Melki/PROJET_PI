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

public class EspaceService {

    private final Connection connection;

    public EspaceService() {
        connection = DataSource.getInstance().getConnection();
    }

    /**
     * Ajoute un nouvel espace dans la base de données
     *
     * @param espace L'espace à ajouter
     * @return int 1 si l'ajout a réussi, 0 sinon
     */
    public int add(Espace espace) {
        String req = "INSERT INTO espace(nom, type, capacite, localisation, prix, disponibilite, photoUrl, description, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, espace.getNom());
            pst.setString(2, espace.getType());
            pst.setInt(3, espace.getCapacite());
            pst.setString(4, espace.getLocalisation());
            pst.setDouble(5, espace.getPrix());
            pst.setBoolean(6, espace.isDisponibilite());
            pst.setString(7, espace.getPhotoUrl() != null ? espace.getPhotoUrl() : "");
            pst.setString(8, espace.getDescription() != null ? espace.getDescription() : "");
            pst.setString(9, espace.getImage() != null ? espace.getImage() : "");

            int result = pst.executeUpdate();

            if (result > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        espace.setEspaceId(generatedKeys.getInt(1));
                    }
                }
                return result;
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout : " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Ajoute un nouvel espace avec une photo
     * 
     * @param espace L'espace à ajouter
     * @param photoFile Le fichier photo à associer
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean add(Espace espace, File photoFile) {
        try {
            // Créer le dossier images/spaces s'il n'existe pas
            File imagesDir = new File("src/main/resources/images/spaces");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }
            
            // Générer un nom de fichier unique
            String fileName = UUID.randomUUID().toString() + "_" + photoFile.getName();
            String relativePhotoPath = "/images/spaces/" + fileName;
            
            // Copier le fichier dans le dossier resources
            File destFile = new File("src/main/resources" + relativePhotoPath);
            Files.copy(photoFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            // Mettre à jour le chemin de la photo
            espace.setPhotoUrl(relativePhotoPath);
            
            // Ajouter l'espace à la base de données
            return add(espace) == 1;
        } catch (IOException e) {
            System.out.println("Erreur lors de la copie de la photo : " + e.getMessage());
            return false;
        }
    }

    /**
     * Méthode alternative pour ajouter un espace (pour maintenir la compatibilité)
     *
     * @param espace L'espace à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouter(Espace espace) {
        return add(espace) == 1;
    }

    /**
     * Met à jour un espace existant dans la base de données
     *
     * @param espace L'espace à mettre à jour
     * @return int 1 si la mise à jour a réussi, 0 sinon
     */
    public int update(Espace espace) {
        String req = "UPDATE espace SET nom=?, type=?, capacite=?, localisation=?, prix=?, disponibilite=?, photoUrl=?, description=?, image=? WHERE espaceId=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, espace.getNom());
            pst.setString(2, espace.getType());
            pst.setInt(3, espace.getCapacite());
            pst.setString(4, espace.getLocalisation());
            pst.setDouble(5, espace.getPrix());
            pst.setBoolean(6, espace.isDisponibilite());
            pst.setString(7, espace.getPhotoUrl());
            pst.setString(8, espace.getDescription());
            pst.setString(9, espace.getImage());
            pst.setInt(10, espace.getEspaceId());
            
            int result = pst.executeUpdate();
            System.out.println("Espace mis à jour avec l'ID: " + espace.getEspaceId());
            return result;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour : " + e.getMessage());
            e.printStackTrace();
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
                File imagesDir = new File("src/main/resources/images/spaces");
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs();
                }
                
                // Générer un nom de fichier unique
                String fileName = UUID.randomUUID().toString() + "_" + photoFile.getName();
                String relativePhotoPath = "/images/spaces/" + fileName;
                
                // Copier le fichier dans le dossier resources
                File destFile = new File("src/main/resources" + relativePhotoPath);
                Files.copy(photoFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                // Mettre à jour le chemin de la photo
                espace.setPhotoUrl(relativePhotoPath);
            }
            
            // Mettre à jour l'espace dans la base de données
            return update(espace) == 1;
        } catch (IOException e) {
            System.out.println("Erreur lors de la copie de la photo : " + e.getMessage());
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
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, espaceId);
            int result = pst.executeUpdate();
            System.out.println("Espace supprimé avec l'ID: " + espaceId);
            return result;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
            e.printStackTrace();
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
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, espaceId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapResultSetToEspace(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Récupère tous les espaces de la base de données
     *
     * @return Liste de tous les espaces
     */
    public List<Espace> getAll() {
        List<Espace> espaces = new ArrayList<>();
        String req = "SELECT * FROM espace";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Espace espace = mapResultSetToEspace(rs);
                espaces.add(espace);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération : " + e.getMessage());
            e.printStackTrace();
        }
        
        // Si aucun espace n'est trouvé, créer des exemples
        if (espaces.isEmpty()) {
            espaces = createDummyEspaces();
            System.out.println("Aucun espace trouvé dans la base de données, utilisation de données de démonstration");
        }
        
        return espaces;
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
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Espace espace = mapResultSetToEspace(rs);
                espaces.add(espace);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche : " + e.getMessage());
            e.printStackTrace();
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
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Espace espace = mapResultSetToEspace(rs);
                espaces.add(espace);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération : " + e.getMessage());
            e.printStackTrace();
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
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du comptage : " + e.getMessage());
            e.printStackTrace();
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
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du comptage : " + e.getMessage());
            e.printStackTrace();
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
        espace.setEspaceId(rs.getInt("espaceId"));
        espace.setNom(rs.getString("nom"));
        espace.setType(rs.getString("type"));
        espace.setCapacite(rs.getInt("capacite"));
        espace.setLocalisation(rs.getString("localisation"));
        espace.setPrix(rs.getDouble("prix"));
        espace.setDisponibilite(rs.getBoolean("disponibilite"));
        espace.setPhotoUrl(rs.getString("photoUrl"));
        
        // Get description if it exists
        try {
            espace.setDescription(rs.getString("description"));
        } catch (SQLException e) {
            // Column might not exist in older schema versions
            espace.setDescription("");
        }
        
        // Get image if it exists
        try {
            espace.setImage(rs.getString("image"));
        } catch (SQLException e) {
            // Column might not exist in older schema versions
            // If image is not set but photoUrl is, use photoUrl for image
            if (espace.getPhotoUrl() != null && !espace.getPhotoUrl().isEmpty()) {
                espace.setImage(espace.getPhotoUrl());
            }
        }
        
        return espace;
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
}