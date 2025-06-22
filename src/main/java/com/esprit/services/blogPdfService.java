package com.esprit.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

public class blogPdfService {
    public static void generatePdf(String text, String outputPath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            // Créer une police Helvetica Bold de taille 12
            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            // Ajouter le texte au document
            Paragraph paragraph = new Paragraph(text, font);
            document.add(paragraph);

            document.close();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF: " + e.getMessage());
        }
    }
}