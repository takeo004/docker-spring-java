package com.example.api.repository.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.AclRule.Scope;
import com.google.auth.Credentials;
import com.google.auth.appengine.AppEngineCredentials;
import com.google.auth.http.HttpCredentialsAdapter;

@Repository
public class GoogleCalendarRepository {

    @Value("${google-calendar.file-name.credential}")
    public String credentialFileName;

    public com.google.api.services.calendar.model.Calendar requestAddCalendar(String calendarName) throws IOException, GeneralSecurityException {
        Calendar service = this.generateService();

        return service.calendars().insert(new com.google.api.services.calendar.model.Calendar()
            .setSummary(calendarName)
            .setDescription("LINE秘書で作成したカレンダー")
            .setTimeZone("Asia/Tokyo")).execute();
    }
    
    public Event requestRegisEvent(String calendarId, String startDate, String endDate, String title) throws FileNotFoundException, IOException, GeneralSecurityException {
        Calendar service = this.generateService();        

        EventDateTime startDateTime = new EventDateTime().setDate(new DateTime(startDate));
        EventDateTime endDateTime = new EventDateTime().setDate(new DateTime(endDate));

        Event event = new Event()
            .setSummary(title)
            .setStart(startDateTime)
            .setEnd(endDateTime);

        return service.events().insert(calendarId, event).execute();
    }

    public List<Event> requestSearchEvent(String calendarId, String startDate, String endDate) throws IOException, GeneralSecurityException {
        return this.requestSearchEvent(calendarId, startDate, endDate, null);
    }

    public List<Event> requestSearchEvent(String calendarId, String title) throws IOException, GeneralSecurityException {
        return this.requestSearchEvent(calendarId, null, null, title);
    }

    public List<Event> requestSearchEvent(String calendarId, String startDate, String endDate, String title) throws IOException, GeneralSecurityException {
        Calendar service = this.generateService();
        List<Event> eventList = service.events().list(calendarId)
        .setOrderBy("startTime")
        .setSingleEvents(true)
        .setTimeMin(StringUtils.hasText(startDate) ? new DateTime(startDate + "T00:00:00.000+09:00") : null)
        .setTimeMax(StringUtils.hasText(endDate) ? new DateTime(endDate + "T23:59:59.999+09:00") : null)
        .execute()
        .getItems();

        if(!StringUtils.hasText(title)) {
            return eventList;
        }

        return eventList.stream()
                .filter(event -> title.equals(event.getSummary()))
                .collect(Collectors.toList());
    }

    public void requestDeleteEvent(String calendarId, List<Event> eventList) throws IOException, GeneralSecurityException {
        Calendar service = this.generateService();

                eventList.stream()
            .forEach(event -> {
                try {
                    service.events().delete(calendarId, event.getId()).execute();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
    }

    public void addCalendarRole(String calendarId, String userEmail) throws IOException, GeneralSecurityException {
        Calendar service = this.generateService();
        System.out.println(service.calendarList().list().execute().toPrettyString());

        Scope scope = new Scope().setType("user").setValue(userEmail);

        AclRule rule = new AclRule();
        rule.setRole("owner");
        rule.setScope(scope);
        
        service.acl().insert(calendarId, rule).execute();
    }

    private Calendar generateService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Credentials credentials = AppEngineCredentials.fromStream(new ClassPathResource(credentialFileName).getInputStream())
            .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

        return new Calendar.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), new HttpCredentialsAdapter(credentials))
            .setApplicationName("line-secretary")
            .build();
    }
}
