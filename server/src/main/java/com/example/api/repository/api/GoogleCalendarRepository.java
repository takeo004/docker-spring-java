package com.example.api.repository.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.stereotype.Repository;

import com.example.api.controller.request.googlecalendar.GoogleCalendarRegistRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Value;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.Credentials;
import com.google.auth.appengine.AppEngineCredentials;
import com.google.auth.http.HttpCredentialsAdapter;

@Repository
public class GoogleCalendarRepository {

    @Value("${google-calendar.path.credential}")
    public String credentialPath;
    
    public static void requestGoogleCalendar(GoogleCalendarRegistRequest request) throws FileNotFoundException, IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Credentials credentials = AppEngineCredentials.fromStream(new FileInputStream("C:\\Users\\user\\workspace\\docker-spring-java\\server\\src\\main\\resources\\google-calendar-credentials.json"))
            .createScoped(Collections.singleton(CalendarScopes.CALENDAR_EVENTS));

        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), new HttpCredentialsAdapter(credentials))
            .setApplicationName("line-secretary")
            .build();
        

        EventDateTime startDateTime = new EventDateTime().setDate(new DateTime(request.getStartDate()));
        EventDateTime endDateTime = new EventDateTime().setDate(new DateTime(request.getEndDate()));

        Event event = new Event()
            .setSummary(request.getTitle())
            .setStart(startDateTime)
            .setEnd(endDateTime);

        event = service.events().insert("logwhitesesame.2@gmail.com", event).execute();
    }
    
}
