package com.esprit.services;

import com.esprit.models.Client;
import com.esprit.models.Evenement;
import com.esprit.models.Reservation;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PDFService {
    
    public void generateReservationPDF(Reservation reservation, Client client, Evenement event) {
        try {
            // Create a file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer la confirmation de réservation");
            fileChooser.setInitialFileName("reservation_" + event.getTitle().replaceAll("\\s+", "_") + ".pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            // Show save dialog
            File file = fileChooser.showSaveDialog(new Stage());
            if (file == null) return; // User cancelled the save dialog
            
            // Create document
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            
            // Add logo or header
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
            Paragraph header = new Paragraph("EventHub - Confirmation de Réservation", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(20);
            document.add(header);
            
            // Add reservation details
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            
            // Reservation number
            Paragraph reservationNumber = new Paragraph("Numéro de réservation: " + reservation.getId_res(), titleFont);
            reservationNumber.setSpacingAfter(10);
            document.add(reservationNumber);
            
            // Date of reservation
            Paragraph reservationDate = new Paragraph("Date de réservation: " + reservation.getDate_res(), normalFont);
            reservationDate.setSpacingAfter(10);
            document.add(reservationDate);
            
            // Event details
            Paragraph eventTitle = new Paragraph("Événement: " + event.getTitle(), titleFont);
            eventTitle.setSpacingAfter(10);
            document.add(eventTitle);
            
            Paragraph eventDate = new Paragraph("Date de l'événement: " + event.getDate_debut(), normalFont);
            eventDate.setSpacingAfter(5);
            document.add(eventDate);
            
            Paragraph eventLocation = new Paragraph("Lieu: " + event.getLatitude() + ", " + event.getLongitude(), normalFont);
            eventLocation.setSpacingAfter(5);
            document.add(eventLocation);
            
            // Client details
            Paragraph clientInfo = new Paragraph("Informations client:", titleFont);
            clientInfo.setSpacingBefore(20);
            clientInfo.setSpacingAfter(10);
            document.add(clientInfo);
            
            Paragraph clientName = new Paragraph("Nom: " + client.getNom_suser() + " " + client.getPrenom_user(), normalFont);
            clientName.setSpacingAfter(5);
            document.add(clientName);
            
            Paragraph clientEmail = new Paragraph("Email: " + client.getEmail_user(), normalFont);
            clientEmail.setSpacingAfter(5);
            document.add(clientEmail);
            
            // Add footer
            Paragraph footer = new Paragraph(
                "Ce document est une confirmation officielle de votre réservation.\n" +
                "Veuillez le conserver précieusement.\n" +
                "Pour toute question, veuillez contacter notre service client.",
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC)
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30);
            document.add(footer);
            
            // Add generation date
            Paragraph generationDate = new Paragraph(
                "Document généré le: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)
            );
            generationDate.setAlignment(Element.ALIGN_RIGHT);
            document.add(generationDate);
            
            document.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du PDF: " + e.getMessage());
        }
    }
} 