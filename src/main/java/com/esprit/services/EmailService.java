package com.esprit.services;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.io.IOException;

public class EmailService {

    private final String apiKey = "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"; //

    private SendGrid sendGrid;

    public EmailService() {
        sendGrid = new SendGrid(apiKey);
    }

    public void sendEmail(String toEmail, String subject, String body) throws IOException {
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
    }
}
