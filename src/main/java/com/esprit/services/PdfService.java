package com.esprit.services;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class PdfService {
    public static void generatePdf(String text, String outputPath) {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(doc, page)) {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.newLineAtOffset(50, 700);
                content.showText(text);
                content.endText();
            }

            doc.save(new File(outputPath));
        } catch (IOException e) {
            throw new RuntimeException("Erreur PDF: " + e.getMessage());
        }
    }
}