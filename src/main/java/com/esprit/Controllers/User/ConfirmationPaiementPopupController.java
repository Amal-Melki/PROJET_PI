package com.esprit.Controllers.User;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.esprit.modules.produits.ProduitDerive;
import com.esprit.services.produits.User.ServicePanier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.awt.Desktop;

// Importations JavaMail - TOUTES CELLES-CI SONT MAINTENANT DANS CE FICHIER
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties; // Également nécessaire pour les propriétés d'e-mail

public class ConfirmationPaiementPopupController {

    private Stage dialogStage;
    private ServicePanier servicePanier = new ServicePanier();

    // Constantes de configuration d'e-mail - MAINTENANT DANS CE FICHIER
    // IMPORTANT : Pour des raisons de sécurité, NE PAS coder en dur des informations d'identification sensibles en production.
    // Utilisez des variables d'environnement ou un système de gestion de configuration sécurisé.
    private static final String EMAIL_FROM = "ahmetchaouch19@gmail.com";
    private static final String APP_PASSWORD = "evuwktgwnpdaqain"; // Assurez-vous que c'est votre mot de passe d'application réel sans espaces
    private static final String CUSTOMER_EMAIL_FOR_RECEIPT = "ahmetchaouch19@gmail.com"; // Changez ceci pour l'adresse e-mail réelle du client

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void handleDownloadPdf(ActionEvent event) {
        Map<ProduitDerive, Integer> panierItems = servicePanier.getPanierItemsWithQuantities();

        if (panierItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Panier Vide", "Impossible de générer une facture pour un panier vide.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer la facture PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));

        String defaultFileName = "Facture_Commande_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
        fileChooser.setInitialFileName(defaultFileName);

        File file = fileChooser.showSaveDialog(dialogStage);

        if (file != null) {
            try {
                // Générer le PDF
                generatePdfInvoice(file.getAbsolutePath(), panierItems);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "La facture a été générée avec succès et enregistrée à:\n" + file.getAbsolutePath());

                // --- NOUVEAU : Envoyer l'e-mail de confirmation ---
                String orderNumber = "CMD-" + System.currentTimeMillis(); // Générer un numéro de commande simple
                double totalAmount = servicePanier.calculerTotalPanier();

                sendPaymentConfirmationEmail(
                        CUSTOMER_EMAIL_FOR_RECEIPT, // Utiliser l'e-mail du client
                        orderNumber,
                        totalAmount,
                        panierItems
                );
                // --- FIN NOUVEAU ---

                // CODE POUR OUVRIR LE PDF AUTOMATIQUEMENT
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException e) {
                        showAlert(Alert.AlertType.ERROR, "Erreur d'ouverture", "Impossible d'ouvrir le fichier PDF. Veuillez l'ouvrir manuellement depuis:\n" + file.getAbsolutePath());
                        System.err.println("Erreur lors de l'ouverture du fichier PDF: " + e.getMessage());
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "Fonctionnalité non supportée", "L'ouverture automatique de fichiers n'est pas supportée sur ce système. Le PDF est enregistré à:\n" + file.getAbsolutePath());
                }

            } catch (DocumentException | IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de génération", "Une erreur est survenue lors de la génération du PDF:\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Signature de méthode modifiée pour accepter Map<ProduitDerive, Integer>
    private void generatePdfInvoice(String filePath, Map<ProduitDerive, Integer> panierItems) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Titre
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, new BaseColor(255, 143, 179));
        Paragraph title = new Paragraph("BON DE COMMANDE", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Informations de la commande
        Font fontInfo = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        document.add(new Paragraph("Date de la commande: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), fontInfo));
        document.add(new Paragraph("Numéro de commande: #" + System.currentTimeMillis(), fontInfo));
        document.add(Chunk.NEWLINE);

        // Tableau des produits (6 colonnes pour Produit, Catégorie, Description, Prix Unitaire, Quantité, Total Ligne)
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        float[] columnWidths = {2.5f, 1.5f, 3.5f, 1.0f, 1.0f, 1.0f}; // Ajustez ces valeurs
        table.setWidths(columnWidths);

        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        BaseColor headerBgColor = new BaseColor(255, 143, 179);

        addCellToTable(table, "Produit", fontHeader, headerBgColor, true);
        addCellToTable(table, "Catégorie", fontHeader, headerBgColor, true);
        addCellToTable(table, "Description", fontHeader, headerBgColor, true);
        addCellToTable(table, "Prix Unitaire", fontHeader, headerBgColor, true);
        addCellToTable(table, "Quantité", fontHeader, headerBgColor, true);
        addCellToTable(table, "Total Ligne", fontHeader, headerBgColor, true);

        double totalGeneral = 0.0;
        Font fontCell = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

        for (Map.Entry<ProduitDerive, Integer> entry : panierItems.entrySet()) {
            ProduitDerive produit = entry.getKey();
            int quantite = entry.getValue();

            addCellToTable(table, produit.getNom(), fontCell, null, false);
            addCellToTable(table, produit.getCategorie(), fontCell, null, false);
            addCellToTable(table, produit.getDescription(), fontCell, null, false);
            addCellToTable(table, String.format("%.2f TND", produit.getPrix()), fontCell, null, false);

            addCellToTable(table, String.valueOf(quantite), fontCell, null, false);

            double totalLigne = produit.getPrix() * quantite;
            addCellToTable(table, String.format("%.2f TND", totalLigne), fontCell, null, false);
            totalGeneral += totalLigne;
        }

        document.add(table);

        // Total général
        Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new BaseColor(255, 128, 160));
        Paragraph totalParagraph = new Paragraph("Total à Payer: " + String.format("%.2f TND", totalGeneral), fontTotal);
        totalParagraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalParagraph);

        Paragraph footer = new Paragraph("Merci pour votre commande !", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);

        document.close();
    }

    private void addCellToTable(PdfPTable table, String text, Font font, BaseColor backgroundColor, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        if (backgroundColor != null) {
            cell.setBackgroundColor(backgroundColor);
        }
        if (isHeader) {
            cell.setBorderColor(new BaseColor(255, 143, 179));
            cell.setBorderWidth(1);
        } else {
            cell.setBorderColor(BaseColor.LIGHT_GRAY);
            cell.setBorderWidth(0.5f);
        }
        table.addCell(cell);
    }

    @FXML
    private void handleClosePopup(ActionEvent event) {
        dialogStage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // --- DÉBUT DU CODE DE L'EXPÉDITEUR D'E-MAIL INTÉGRÉ ---

    /**
     * Configure les propriétés pour la connexion au serveur SMTP de Gmail.
     *
     * @return Objet Properties avec les paramètres SMTP de Gmail.
     */
    public static Properties getGmailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); // Hôte SMTP
        properties.put("mail.smtp.port", "587"); // Port TLS
        properties.put("mail.smtp.auth", "true"); // Activer l'authentification
        properties.put("mail.smtp.starttls.enable", "true"); // Activer le chiffrement TLS
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Spécifier la version TLS pour une meilleure sécurité
        return properties;
    }

    /**
     * Crée une session de messagerie avec authentification.
     *
     * @return Objet Session pour l'envoi d'e-mails.
     */
    public static Session getEmailSession() {
        Properties properties = getGmailProperties();

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, APP_PASSWORD);
            }
        });
    }

    /**
     * Envoie un e-mail de confirmation pour un paiement.
     *
     * @param recipientEmail L'adresse e-mail du destinataire de la confirmation.
     * @param orderNumber Le numéro de commande unique.
     * @param totalAmount Le montant total de la commande.
     * @param panierItems Une carte des produits et de leurs quantités dans la commande.
     */
    public void sendPaymentConfirmationEmail( // Changé en non-statique car il fait partie d'une instance de contrôleur
                                              String recipientEmail,
                                              String orderNumber,
                                              double totalAmount,
                                              Map<ProduitDerive, Integer> panierItems) {

        Session session = getEmailSession();

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Confirmation de votre commande #" + orderNumber);

            // Construire le contenu de l'e-mail
            StringBuilder emailContent = new StringBuilder();
            emailContent.append("Bonjour,\n\n");
            emailContent.append("Nous vous confirmons que votre commande a été traitée avec succès.\n\n");
            emailContent.append("Détails de la commande:\n");
            emailContent.append("Numéro de commande: ").append(orderNumber).append("\n");
            emailContent.append("Date de la commande: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n\n");

            emailContent.append("Articles commandés:\n");
            emailContent.append("--------------------------------------------------------------------------------------------------------------------------------\n");
            emailContent.append(String.format("%-40s %-15s %-10s %-15s\n", "Produit", "Prix Unitaire", "Quantité", "Total Ligne"));
            emailContent.append("--------------------------------------------------------------------------------------------------------------------------------\n");

            for (Map.Entry<ProduitDerive, Integer> entry : panierItems.entrySet()) {
                ProduitDerive produit = entry.getKey();
                int quantity = entry.getValue();
                double lineTotal = produit.getPrix() * quantity;
                emailContent.append(String.format("%-40s %-15.2f %-10d %-15.2f\n",
                        produit.getNom(), produit.getPrix(), quantity, lineTotal));
            }
            emailContent.append("--------------------------------------------------------------------------------------------------------------------------------\n");
            emailContent.append(String.format("%-65s Total à Payer: %.2f TND\n", "", totalAmount));
            emailContent.append("--------------------------------------------------------------------------------------------------------------------------------\n\n");


            emailContent.append("Merci pour votre achat et à bientôt!\n\n");
            emailContent.append("Cordialement,\nVotre équipe de vente");

            message.setText(emailContent.toString());

            Transport.send(message);
            System.out.println("Email de confirmation envoyé à " + recipientEmail);

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email de confirmation: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur d'envoi d'email", "Impossible d'envoyer l'email de confirmation. Vérifiez votre connexion ou les identifiants.");
        }
    }
    // --- FIN DU CODE DE L'EXPÉDITEUR D'E-MAIL INTÉGRÉ ---
}