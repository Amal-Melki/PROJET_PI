package com.esprit.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.esprit.models.Client;
import com.esprit.models.Admin;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleAuthService {
    private static final String APPLICATION_NAME = "EventHub";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/userinfo.email");
    private static final String CREDENTIALS_FILE_PATH = "tokens/client_secret.json";

    private final NetHttpTransport HTTP_TRANSPORT;
    private final GoogleClientSecrets clientSecrets;
    private final GoogleAuthorizationCodeFlow flow;

    public GoogleAuthService() throws IOException, GeneralSecurityException {
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(new FileInputStream(CREDENTIALS_FILE_PATH)));
        flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();
    }

    public String getGoogleEmail() throws IOException {
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        try {
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
            String accessToken = credential.getAccessToken();

            // Make a request to the userinfo endpoint
            String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
            String response = HTTP_TRANSPORT.createRequestFactory()
                    .buildGetRequest(new com.google.api.client.http.GenericUrl(userInfoUrl))
                    .setHeaders(new com.google.api.client.http.HttpHeaders().setAuthorization("Bearer " + accessToken))
                    .execute()
                    .parseAsString();

            // Parse the response to get the email
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            return jsonResponse.get("email").getAsString();
        } finally {
            receiver.stop();
        }
    }

    public Object authenticateUser(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }

        ClientService clientService = new ClientService();
        AdminService adminService = new AdminService();

        // Check if email exists in client database
        Client client = clientService.findByEmail(email);
        if (client != null) {
            return client;
        }

        // Check if email exists in admin database
        Admin admin = adminService.findByEmail(email);
        if (admin != null) {
            return admin;
        }

        return null;
    }
} 