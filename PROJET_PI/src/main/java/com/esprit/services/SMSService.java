package com.esprit.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SMSService {
    // Your Twilio Account SID and Auth Token
    private static final String ACCOUNT_SID = "AC1a845498efef04d9a3e8072936fa868a";
    private static final String AUTH_TOKEN = "fdc2f9c92d69b531936ac4dff707fa84";
    // Replace this with your actual Twilio phone number from your Twilio console
    private static final String TWILIO_PHONE_NUMBER = "+14155238886"; // Example US Twilio number

    public SMSService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public String generateRandomPassword() {
        // Generate a random 8-character password
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }

    public boolean sendPasswordResetSMS(String phoneNumber, String newPassword) {
        try {
            // Format the phone number to E.164 format
            String formattedNumber = formatPhoneNumber(phoneNumber);
            System.out.println("Sending SMS to: " + formattedNumber);
            System.out.println("From Twilio number: " + TWILIO_PHONE_NUMBER);
            
            // Create the message
            Message message = Message.creator(
                    new PhoneNumber(formattedNumber),
                    new PhoneNumber(TWILIO_PHONE_NUMBER),
                    "Votre nouveau mot de passe est: " + newPassword
            ).create();

            System.out.println("SMS sent successfully. SID: " + message.getSid());
            return true;
        } catch (Exception e) {
            System.err.println("Error sending SMS: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String formatPhoneNumber(String phoneNumber) {
        // Remove any non-digit characters
        String digits = phoneNumber.replaceAll("[^0-9]", "");
        
        // If the number is 8 digits (Tunisian format), add the country code
        if (digits.length() == 8) {
            digits = "216" + digits;
        }
        
        // Add + prefix
        return "+" + digits;
    }
} 