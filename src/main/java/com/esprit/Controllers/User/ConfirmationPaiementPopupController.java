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
<<<<<<< HEAD
import java.util.Map; // Import Map
=======
import java.util.List;
>>>>>>> e629795d3374ae43688bf5eaeaceb0d3f9cde727
import java.awt.Desktop; // Import nécessaire pour Desktop

public class ConfirmationPaiementPopupController {

    private Stage dialogStage;
    private ServicePanier servicePanier = new ServicePanier();

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void handleDownloadPdf(ActionEvent event) {
<<<<<<< HEAD
        // Now get the map of products with their quantities
        Map<ProduitDerive, Integer> panierItems = servicePanier.getPanierItemsWithQuantities();

        if (panierItems.isEmpty()) {
=======
        List<ProduitDerive> produitsDansPanier = servicePanier.getProduitsDansPanier();

        if (produitsDansPanier.isEmpty()) {
>>>>>>> e629795d3374ae43688bf5eaeaceb0d3f9cde727
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
<<<<<<< HEAD
                // Pass the map to the PDF generation method
                generatePdfInvoice(file.getAbsolutePath(), panierItems);
=======
                generatePdfInvoice(file.getAbsolutePath(), produitsDansPanier);
>>>>>>> e629795d3374ae43688bf5eaeaceb0d3f9cde727
                showAlert(Alert.AlertType.INFORMATION, "Succès", "La facture a été générée avec succès et enregistrée à:\n" + file.getAbsolutePath());

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

<<<<<<< HEAD
    // Modified method signature to accept Map<ProduitDerive, Integer>
    private void generatePdfInvoice(String filePath, Map<ProduitDerive, Integer> panierItems) throws DocumentException, IOException {
=======
    private void generatePdfInvoice(String filePath, List<ProduitDerive> produits) throws DocumentException, IOException {
>>>>>>> e629795d3374ae43688bf5eaeaceb0d3f9cde727
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

<<<<<<< HEAD
        // Iterate through the Map.Entry to get both product and its quantity
        for (Map.Entry<ProduitDerive, Integer> entry : panierItems.entrySet()) {
            ProduitDerive produit = entry.getKey();
            int quantite = entry.getValue(); // Get the actual quantity from the map

=======
        for (ProduitDerive produit : produits) {
>>>>>>> e629795d3374ae43688bf5eaeaceb0d3f9cde727
            addCellToTable(table, produit.getNom(), fontCell, null, false);
            addCellToTable(table, produit.getCategorie(), fontCell, null, false);
            addCellToTable(table, produit.getDescription(), fontCell, null, false);
            addCellToTable(table, String.format("%.2f TND", produit.getPrix()), fontCell, null, false);

<<<<<<< HEAD
            addCellToTable(table, String.valueOf(quantite), fontCell, null, false); // Use actual quantity
=======
            int quantite = 1; // Supposons quantité = 1 si non géré dans ProduitDerive
            addCellToTable(table, String.valueOf(quantite), fontCell, null, false);
>>>>>>> e629795d3374ae43688bf5eaeaceb0d3f9cde727

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
<<<<<<< HEAD
        // The cart is cleared and redirection happens in PaymentFormController after showAndWait()
        // so no need to do it here.
=======
        // Le panier est vidé et la redirection se fait DANS PaymentFormController après showAndWait()
        // donc pas besoin de le faire ici.
>>>>>>> e629795d3374ae43688bf5eaeaceb0d3f9cde727
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}