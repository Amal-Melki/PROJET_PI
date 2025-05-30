package com.esprit.services;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.io.IOException;

public class EmailService {

    private final String apiKey = "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\n"; //

    private SendGrid sendGrid;

    public EmailService() {
        sendGrid = new SendGrid(apiKey);
    }

    /**
     * Simulated email sending for prototype purposes
     * In production, this would use the SendGrid API to actually send emails
     */
    public void sendEmail(String toEmail, String subject, String body) throws IOException {
        // For prototype, just log the email details to console
        System.out.println("========= SIMULATED EMAIL ==========");
        System.out.println("FROM: Evencia Event Planner <melki.amal@esprit.tn>");
        System.out.println("TO: " + toEmail);
        System.out.println("SUBJECT: " + subject);
        System.out.println("BODY:\n" + body);
        System.out.println("====================================");

        // Uncomment and adapt the below code when ready to use actual SendGrid
        /*
        Email from = new Email("melki.amal@esprit.tn");
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
                System.out.println("Failed to send email. Status Code: " + response.getStatusCode());
                System.out.println("Response Body: " + response.getBody());
            }
        } catch (IOException ex) {
            throw ex;
        }
        */
    }
}
