package com.esprit.tests;

import com.esprit.services.EmailService;

import java.io.IOException;

public class EmailServiceTest {
    public static void main(String[] args) {
        EmailService emailService = new EmailService();

        String testEmail = "wael2chenoufi@gmail.com"; // Replace with your test email address
        String subject = "Test Email from PROJET_PI";
        String body = "This is a test email sent from the EmailService test.";

        try {
            emailService.sendEmail(testEmail, subject, body);
            System.out.println("Test email sent successfully to " + testEmail);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to send test email: " + e.getMessage());
        }
    }
}
