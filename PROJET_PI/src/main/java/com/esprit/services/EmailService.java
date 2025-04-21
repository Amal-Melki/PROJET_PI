package com.esprit.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private static final String EMAIL = "ghadaomri22@gmail.com";
    private static final String PASSWORD = "ojki bhlr dyvh tcnl";

    public void sendReservationConfirmation(String toEmail, String eventName, String eventDate, String eventLocation) {
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
            message.setSubject("Confirmation de réservation - " + eventName);

            String emailContent = String.format(
                "Cher client,\n\n" +
                "Nous vous confirmons votre réservation pour l'événement suivant :\n\n" +
                "Événement : %s\n" +
                "Date : %s\n" +
                "Lieu : %s\n\n" +
                "Nous vous remercions de votre confiance et nous espérons vous voir bientôt !\n\n" +
                "Cordialement,\n" +
                "L'équipe EventHub",
                eventName, eventDate, eventLocation
            );

            message.setText(emailContent);

            Transport.send(message);
            System.out.println("Email de confirmation envoyé avec succès à : " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        }
    }
} 