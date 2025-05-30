package com.esprit.tests;

import com.esprit.services.EmailService;

import java.io.IOException;

public class EmailServiceTest {
    public static void main(String[] args) {
        try {
            EmailService emailService = new EmailService();
            
            String testEmail = "wael2chenoufi@gmail.com";
            String subject = "Test Email from PROJET_PI";
            String body = "This is a test email sent from the EmailService.";

            System.out.println("Attempting to send test email...");
            emailService.sendEmail(testEmail, subject, body);
            System.out.println("Test completed.");
            
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
