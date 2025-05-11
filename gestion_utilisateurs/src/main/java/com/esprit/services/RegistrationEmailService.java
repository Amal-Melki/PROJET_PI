package com.esprit.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class RegistrationEmailService {
    private static final String EMAIL = "sellamimaryemm@gmail.com";
    private static final String PASSWORD = "nvfz ebjb nnhb cccj";

    public void sendWelcomeEmail(String toEmail, String firstName, String lastName) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Bienvenue sur notre plateforme !");

            String emailContent = String.format(
                "Cher(e) %s %s,\n\n" +
                "Nous sommes ravis de vous accueillir sur notre plateforme !\n\n" +
                "Votre compte a été créé avec succès avec les informations suivantes :\n" +
                "Email : %s\n" +
                "Nom : %s\n" +
                "Prénom : %s\n\n" +
                "Vous pouvez maintenant vous connecter et profiter de tous nos services.\n\n" +
                "Cordialement,\n" +
                "L'équipe Evencia",
                firstName,
                lastName,
                toEmail,
                lastName,
                firstName
            );

            message.setText(emailContent);
            Transport.send(message);
            System.out.println("Email de bienvenue envoyé avec succès à : " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email de bienvenue : " + e.getMessage());
            e.printStackTrace();
        }
    }
} 