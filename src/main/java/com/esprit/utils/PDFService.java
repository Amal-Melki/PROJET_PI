package com.esprit.utils;

import com.esprit.models.Espace;
import com.esprit.models.ReservationEspace;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Service utilitaire pour la génération de PDF
 */
public class PDFService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Génère un PDF de confirmation pour une réservation d'espace
     * 
     * @param reservation La réservation à exporter
     * @param espace L'espace réservé
     * @param outputPath Le chemin où sauvegarder le PDF
     * @return Le fichier PDF généré
     * @throws IOException En cas d'erreur lors de la génération du PDF
     */
    public static File generateReservationConfirmation(ReservationEspace reservation, Espace espace, String outputPath) throws IOException {
        // Créer un nouveau document
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float width = page.getMediaBox().getWidth() - 2 * margin;
            float fontSize = 12;
            float leading = 1.5f * fontSize;
            
            // Ajouter le logo si disponible
            try {
                PDImageXObject logo = PDImageXObject.createFromFile("src/main/resources/images/evencia.jpg", document);
                contentStream.drawImage(logo, margin, yStart - 60, 150, 50);
            } catch (IOException e) {
                System.out.println("Logo non trouvé: " + e.getMessage());
            }
            
            // Titre
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.newLineAtOffset(margin, yStart - 80);
            contentStream.showText("Confirmation de Réservation");
            contentStream.endText();
            
            // Référence
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.newLineAtOffset(margin, yStart - 100);
            contentStream.showText("Référence: RES-" + reservation.getReservationId());
            contentStream.endText();
            
            // Date d'émission
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.newLineAtOffset(margin, yStart - 120);
            contentStream.showText("Date d'émission: " + LocalDate.now().format(DATE_FORMATTER));
            contentStream.endText();
            
            // Ligne de séparation
            contentStream.setLineWidth(1);
            contentStream.moveTo(margin, yStart - 140);
            contentStream.lineTo(width + margin, yStart - 140);
            contentStream.stroke();
            
            // Informations client
            float currentY = yStart - 170;
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("Informations Client");
            contentStream.endText();
            currentY -= 20;
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("Nom: " + reservation.getNomClient());
            contentStream.endText();
            currentY -= leading;
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("Email: " + reservation.getEmailClient());
            contentStream.endText();
            currentY -= leading;
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("Téléphone: " + reservation.getTelephoneClient());
            contentStream.endText();
            currentY -= leading * 2;
            
            // Informations de réservation
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("Détails de la Réservation");
            contentStream.endText();
            currentY -= 20;
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("Espace: " + espace.getNom() + " (" + espace.getType() + ")");
            contentStream.endText();
            currentY -= leading;
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("Localisation: " + espace.getLocalisation());
            contentStream.endText();
            currentY -= leading;
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("Capacité: " + espace.getCapacite() + " personnes");
            contentStream.endText();
            currentY -= leading;
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("Du: " + reservation.getDateDebut().format(DATE_FORMATTER) + 
                                   " au: " + reservation.getDateFin().format(DATE_FORMATTER));
            contentStream.endText();
            currentY -= leading;
            
            // Calculer la durée
            long duration = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin()) + 1;
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("Durée: " + duration + " jour(s)");
            contentStream.endText();
            currentY -= leading;
            
            if (reservation.getNombrePersonnes() > 0) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, fontSize);
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Nombre de personnes: " + reservation.getNombrePersonnes());
                contentStream.endText();
                currentY -= leading;
            }
            
            if (reservation.getDescription() != null && !reservation.getDescription().isEmpty()) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, fontSize);
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Description: " + reservation.getDescription());
                contentStream.endText();
                currentY -= leading * 2;
            } else {
                currentY -= leading;
            }
            
            // Informations financières
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("Récapitulatif Financier");
            contentStream.endText();
            currentY -= 20;
            
            double prixParJour = espace.getPrix();
            double total = prixParJour * duration;
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("Prix par jour: " + String.format("%.2f", prixParJour) + " TND");
            contentStream.endText();
            currentY -= leading;
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, fontSize);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("Nombre de jours: " + duration);
            contentStream.endText();
            currentY -= leading;
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("Total: " + String.format("%.2f", total) + " TND");
            contentStream.endText();
            currentY -= leading * 2;
            
            // Conditions et informations légales
            float footerY = 100;
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            contentStream.newLineAtOffset(margin, footerY);
            contentStream.showText("Conditions de réservation :");
            contentStream.endText();
            footerY -= 15;
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 9);
            contentStream.newLineAtOffset(margin, footerY);
            contentStream.showText("1. Toute annulation doit être effectuée au moins 48 heures avant la date de début.");
            contentStream.endText();
            footerY -= 12;
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 9);
            contentStream.newLineAtOffset(margin, footerY);
            contentStream.showText("2. Un acompte de 30% est requis pour confirmer définitivement la réservation.");
            contentStream.endText();
            footerY -= 12;
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 9);
            contentStream.newLineAtOffset(margin, footerY);
            contentStream.showText("3. Merci de présenter ce document lors de votre arrivée.");
            contentStream.endText();
            footerY -= 25;
            
            // Pied de page
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 8);
            contentStream.newLineAtOffset(margin, 50);
            contentStream.showText("Evencia - Services de Location d'Espaces Événementiels");
            contentStream.endText();
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 8);
            contentStream.newLineAtOffset(margin, 40);
            contentStream.showText("Téléphone: +216 71 000 000 | Email: contact@evencia.tn | www.evencia.tn");
            contentStream.endText();
        }
        
        // Assurer que le répertoire de sortie existe
        Files.createDirectories(Paths.get(outputPath).getParent());
        
        // Sauvegarder le document
        document.save(outputPath);
        document.close();
        
        return new File(outputPath);
    }
}
