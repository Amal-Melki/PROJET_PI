// EmailService.java (Modifications pour inclure l'ID dans le tableau HTML)
package com.esprit.utils;

import com.esprit.modules.produits.ProduitDerive;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailService {

    private static final String EMAIL_FROM = "ahmetchaouch19@gmail.com";
    private static final String APP_PASSWORD = "votre_mot_de_passe_d_application_ici_sans_espaces";

    private static final String EMAIL_TO_DEFAULT = "ahmetchaouch19@gmail.com";

    public static Properties getGmailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        // properties.put("mail.debug", "true");
        return properties;
    }

    public static Session getEmailSession() {
        Properties properties = getGmailProperties();
        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, APP_PASSWORD);
            }
        });
    }

    public static void sendPaymentConfirmationEmail(
            String recipientEmail,
            String orderNumber,
            double totalAmount,
            Map<ProduitDerive, Integer> panierItems) throws MessagingException {

        Session session = getEmailSession();

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Confirmation de votre commande #" + orderNumber);

            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<html><head>");
            htmlContent.append("<style>");
            htmlContent.append("body { font-family: Arial, sans-serif; font-size: 14px; line-height: 1.6; color: #333; }");
            htmlContent.append("table { border-collapse: collapse; width: 100%; border: 1px solid #ddd; }");
            htmlContent.append("th, td { padding: 8px; border: 1px solid #ddd; }");
            htmlContent.append("th { background-color: #f2f2f2; text-align: left; color: #ff8fb3; font-weight: bold; }");
            htmlContent.append("tr:nth-child(even) { background-color: #f9f9f9; }");
            htmlContent.append(".total-row td { background-color: #ffe6ee; font-weight: bold; color: #ff8fb3; }");
            htmlContent.append("</style>");
            htmlContent.append("</head><body>");

            htmlContent.append("<p>Bonjour,</p>");
            htmlContent.append("<p>Nous vous confirmons que votre commande a été traitée avec succès.</p>");
            htmlContent.append("<h3>Détails de la commande:</h3>");
            htmlContent.append("<p><b>Numéro de commande:</b> ").append(orderNumber).append("<br>");
            htmlContent.append("<b>Date de la commande:</b> ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("</p>");

            htmlContent.append("<h3>Articles commandés:</h3>");
            htmlContent.append("<table border='0' cellpadding='0' cellspacing='0' style='width:100%; border-collapse: collapse;'>");
            htmlContent.append("<thead>");
            htmlContent.append("<tr style='background-color:#ffe6ee; color:#ff8fb3;'>");
            htmlContent.append("<th style='width:8%; text-align:left;'>ID</th>"); // NOUVEAU: Colonne ID
            htmlContent.append("<th style='width:20%; text-align:left;'>Produit</th>");
            htmlContent.append("<th style='width:12%; text-align:left;'>Catégorie</th>");
            htmlContent.append("<th style='width:25%; text-align:left;'>Description</th>");
            htmlContent.append("<th style='width:10%; text-align:right;'>Prix Unitaire</th>");
            htmlContent.append("<th style='width:5%; text-align:center;'>Qté</th>");
            htmlContent.append("<th style='width:20%; text-align:right;'>Total Ligne</th>");
            htmlContent.append("</tr>");
            htmlContent.append("</thead>");
            htmlContent.append("<tbody>");

            for (Map.Entry<ProduitDerive, Integer> entry : panierItems.entrySet()) {
                ProduitDerive produit = entry.getKey();
                int quantity = entry.getValue();
                double lineTotal = produit.getPrix() * quantity;
                htmlContent.append("<tr>");
                htmlContent.append("<td style='text-align:left;'>").append(produit.getId()).append("</td>"); // NOUVEAU: Affichage de l'ID
                htmlContent.append("<td style='text-align:left;'>").append(produit.getNom()).append("</td>");
                htmlContent.append("<td style='text-align:left;'>").append(produit.getCategorie()).append("</td>");
                htmlContent.append("<td style='text-align:left;'>").append(produit.getDescription()).append("</td>");
                htmlContent.append("<td style='text-align:right;'>").append(String.format("%.2f TND", produit.getPrix())).append("</td>");
                htmlContent.append("<td style='text-align:center;'>").append(quantity).append("</td>");
                htmlContent.append("<td style='text-align:right;'>").append(String.format("%.2f TND", lineTotal)).append("</td>");
                htmlContent.append("</tr>");
            }
            htmlContent.append("</tbody>");
            htmlContent.append("<tfoot>");
            htmlContent.append("<tr class='total-row'>");
            htmlContent.append("<td colspan='6' style='text-align:right;'>Total à Payer:</td>"); // colspang de 5 à 6 (car une colonne ID ajoutée)
            htmlContent.append("<td style='text-align:right;'>").append(String.format("%.2f TND", totalAmount)).append("</td>");
            htmlContent.append("</tr>");
            htmlContent.append("</tfoot>");
            htmlContent.append("</table>");
            htmlContent.append("<p style='margin-top: 20px;'>Merci pour votre achat et à bientôt!</p>");
            htmlContent.append("<p>Cordialement,<br>Votre équipe de vente</p>");
            htmlContent.append("</body></html>");

            message.setContent(htmlContent.toString(), "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Email de confirmation HTML envoyé à " + recipientEmail);

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email de confirmation: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Le main de test reste le même
    public static void main(String[] args) {
        ProduitDerive prod1 = new ProduitDerive();
        prod1.setId(101); // Donnez un ID pour le test
        prod1.setNom("Robe de Soirée Élégante");
        prod1.setPrix(150.0);
        prod1.setCategorie("Vêtements");
        prod1.setDescription("Une magnifique robe parfaite pour les soirées.");

        ProduitDerive prod2 = new ProduitDerive();
        prod2.setId(102); // Donnez un ID pour le test
        prod2.setNom("Collier de Perles Fines");
        prod2.setPrix(30.0);
        prod2.setCategorie("Accessoires");
        prod2.setDescription("Collier élégant avec perles de culture véritables.");

        Map<ProduitDerive, Integer> testPanier = new java.util.LinkedHashMap<>();
        testPanier.put(prod1, 2);
        testPanier.put(prod2, 1);

        double testTotal = (prod1.getPrix() * 2) + (prod2.getPrix() * 1);
        String testOrderNum = "TEST_ORDER_" + System.currentTimeMillis();

        try {
            sendPaymentConfirmationEmail(EMAIL_TO_DEFAULT, testOrderNum, testTotal, testPanier);
        } catch (MessagingException e) {
            System.err.println("Échec de l'envoi de l'e-mail de test.");
            e.printStackTrace();
        }
    }
}