package com.esprit.services;

import com.esprit.models.Evenement;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "EventHub Calendar";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_EVENTS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private final Calendar service;
    private boolean isAuthorized = false;
    private Credential credential;

    public GoogleCalendarService() {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            System.err.println("Failed to create Google Calendar service: " + e.getMessage());
            throw new RuntimeException("Failed to create Google Calendar service", e);
        }
    }

    public void authorize() throws IOException {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            credential = getCredentials(HTTP_TRANSPORT);
            isAuthorized = true;
        } catch (GeneralSecurityException e) {
            System.err.println("Failed to authorize Google Calendar service: " + e.getMessage());
            throw new IOException("Failed to authorize Google Calendar service", e);
        }
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Create tokens directory if it doesn't exist
        File tokensDir = new File(TOKENS_DIRECTORY_PATH);
        if (!tokensDir.exists()) {
            if (!tokensDir.mkdirs()) {
                throw new IOException("Failed to create tokens directory");
            }
        }

        // Load client secrets
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH + 
                ". Please ensure the credentials.json file is in the resources directory.");
        }

        try {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            // Build flow and trigger user authorization request
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(tokensDir))
                    .setAccessType("offline")
                    .build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        } finally {
            in.close();
        }
    }

    public void createEvent(Evenement event) throws IOException {
        if (!isAuthorized) {
            throw new IllegalStateException("Google Calendar service is not authorized. Call authorize() first.");
        }

        try {
            System.out.println("Creating calendar event for: " + event.getTitle());
            
            Event calendarEvent = new Event()
                    .setSummary(event.getTitle())
                    .setDescription(event.getDescription_ev());

            // Convert event date to Google Calendar DateTime
            LocalDate eventDate = event.getDate_debut();
            ZonedDateTime startDateTime = eventDate.atStartOfDay(ZoneId.systemDefault());
            ZonedDateTime endDateTime = eventDate.plusDays(1).atStartOfDay(ZoneId.systemDefault());

            calendarEvent.setStart(new EventDateTime().setDateTime(new DateTime(startDateTime.toInstant().toEpochMilli())));
            calendarEvent.setEnd(new EventDateTime().setDateTime(new DateTime(endDateTime.toInstant().toEpochMilli())));

            String calendarId = "primary";
            System.out.println("Inserting event into calendar...");
            calendarEvent = service.events().insert(calendarId, calendarEvent).execute();
            System.out.println("Event created successfully: " + calendarEvent.getHtmlLink());
        } catch (Exception e) {
            System.err.println("Error creating calendar event: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void syncEventsToCalendar(List<Evenement> events) throws IOException {
        if (!isAuthorized) {
            throw new IllegalStateException("Google Calendar service is not authorized. Call authorize() first.");
        }

        for (Evenement event : events) {
            try {
                createEvent(event);
            } catch (Exception e) {
                System.err.println("Failed to sync event: " + event.getTitle() + " - " + e.getMessage());
                // Continue with next event instead of stopping the entire sync
            }
        }
    }
} 