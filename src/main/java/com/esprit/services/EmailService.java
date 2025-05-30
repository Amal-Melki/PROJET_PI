package com.esprit.services;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

public class EmailService {
    private final SendGrid sendGrid;
    private final String fromEmail;

    public EmailService() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("email-config.properties")) {
            prop.load(input);
            String apiKey = prop.getProperty("sendgrid.api.key");
            this.fromEmail = prop.getProperty("sendgrid.from.email");
            
            if (apiKey == null || apiKey.isEmpty()) {
                System.err.println("Error: SendGrid API key not configured");
                this.sendGrid = null;
            } else {
                this.sendGrid = new SendGrid(apiKey);
            }
        } catch (IOException ex) {
            System.err.println("Error loading email configuration: " + ex.getMessage());
            throw new RuntimeException("Failed to initialize EmailService", ex);
        }
    }

    /**
     * Simulated email sending for prototype purposes
     * In production, this would use the SendGrid API to actually send emails
     */
    public void sendEmail(String toEmail, String subject, String body) throws IOException {
        if (sendGrid == null) {
            System.out.println("Email service disabled. Would have sent to: " + toEmail);
            return;
        }

        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("Email sent successfully to " + toEmail);
            } else {
                System.err.println("Failed to send email. Status: " + response.getStatusCode());
                System.err.println("Response: " + response.getBody());
            }
        } catch (IOException ex) {
            System.err.println("Error sending email: " + ex.getMessage());
            throw ex;
        }
    }

    public void sendEmailWithAttachment(String toEmail, String subject, String body, File attachment) throws IOException {
        if (sendGrid == null) {
            System.out.println("Email service disabled. Would have sent to: " + toEmail);
            return;
        }

        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        // Add attachment
        if (attachment != null && attachment.exists()) {
            Attachments attachments = new Attachments();
            attachments.setFilename(attachment.getName());
            attachments.setType("application/pdf");
            attachments.setContent(java.util.Base64.getEncoder().encodeToString(Files.readAllBytes(attachment.toPath())));
            mail.addAttachments(attachments);
        }

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("Email with attachment sent successfully to " + toEmail);
            } else {
                System.err.println("Failed to send email. Status: " + response.getStatusCode());
                System.err.println("Response: " + response.getBody());
            }
        } catch (IOException ex) {
            System.err.println("Error sending email: " + ex.getMessage());
            throw ex;
        }
    }
}
