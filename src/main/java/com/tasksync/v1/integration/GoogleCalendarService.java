package com.tasksync.v1.integration;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import com.tasksync.v1.model.mysql.entity.Task;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "TaskSync Calendar";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/credentials.json";

    private Credential getCredentials() throws IOException, GeneralSecurityException {
        InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public Calendar getCalendarService() throws IOException, GeneralSecurityException {
        return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void criarEvento() throws Exception {
        Calendar service = getCalendarService();

        Event event = new Event()
                .setSummary("Reunião de Teste")
                .setLocation("Google Meet")
                .setDescription("Evento criado via integração com API Google Calendar.");

        DateTime startDateTime = new DateTime("2025-05-17T10:00:00-03:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Sao_Paulo");
        event.setStart(start);

        DateTime endDateTime = new DateTime("2025-05-17T11:00:00-03:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Sao_Paulo");
        event.setEnd(end);

        EventAttendee[] attendees = new EventAttendee[] {
                new EventAttendee().setEmail("email1@exemplo.com"),
                new EventAttendee().setEmail("email2@exemplo.com"),
        };
        event.setAttendees(List.of(attendees));

        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(List.of(reminderOverrides));
        event.setReminders(reminders);

        Event createdEvent = service.events().insert("primary", event).execute();
        System.out.printf("Evento criado: %s\n", createdEvent.getHtmlLink());
    }

    public Event criarEventoTarefa(Task task) throws Exception {
        Calendar service = getCalendarService();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String alertaFormatado = task.getAlertaEm().format(formatter);

        Event event = new Event()
                .setSummary(task.getDescricao())
                .setDescription(task.getObservacoes())
                .setStart(new EventDateTime()
                        .setDateTime(new DateTime(alertaFormatado))
                        .setTimeZone("America/Sao_Paulo"))
                .setEnd(new EventDateTime()
                        .setDateTime(new DateTime(task.getAlertaEm().plusMinutes(30).format(formatter)))
                        .setTimeZone("America/Sao_Paulo"));

        Event createdEvent = service.events().insert("primary", event).execute();
        return createdEvent;
    }


    public void atualizarEventoTarefa(Task task) throws Exception {
        if (task.getGoogleEventId() == null) {
            throw new IllegalStateException("Tarefa não possui Google Event ID para atualização.");
        }

        Calendar service = getCalendarService();

        Event event = service.events().get("primary", task.getGoogleEventId()).execute();

        event.setSummary(task.getDescricao());
        event.setDescription(task.getObservacoes());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String alertaFormatado = task.getAlertaEm().format(formatter);

        event.setStart(new EventDateTime()
                .setDateTime(new DateTime(alertaFormatado))
                .setTimeZone("America/Sao_Paulo"));
        event.setEnd(new EventDateTime()
                .setDateTime(new DateTime(task.getAlertaEm().plusMinutes(30).format(formatter)))
                .setTimeZone("America/Sao_Paulo"));

        service.events().update("primary", task.getGoogleEventId(), event).execute();
    }

    public void excluirEventoTarefa(String googleEventId) throws Exception {
        if (googleEventId == null || googleEventId.isBlank()) {
            throw new IllegalArgumentException("ID do evento do Google Calendar é inválido ou nulo.");
        }

        Calendar service = getCalendarService();
        service.events().delete("primary", googleEventId).execute();
    }

}