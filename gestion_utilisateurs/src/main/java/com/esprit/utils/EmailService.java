package com.esprit.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService {

    private static final String EMAIL = "evencia.esprit@outlook.com";      // Ton adresse Outlook
    private static final String PASSWORD = "Evenciaesprit25";              // Ton mot de passe Outlook (compte normal, pas besoin de mot de passe d'application)

    public static void sendVerificationEmail(String recipientEmail, String verificationCode) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.office365.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Confirmation d'ajout de fournisseur");
            message.setText("Bonjour,\n\nVotre compte fournisseur a été enregistré avec succès.\n\nCordialement,\nEvencia Team");

            Transport.send(message);
            System.out.println("✅ Email envoyé à : " + recipientEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
