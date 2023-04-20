package com.example.api.repository.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import com.example.api.controller.request.googlecalendar.GoogleCalendarRegistRequest;
import com.example.api.controller.request.googlecalendar.GoogleCalendarSearchRequest;
import com.example.api.entity.GoogleUserInfo;
import com.example.api.entity.UserInfo;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.AclRule.Scope;
import com.google.auth.Credentials;
import com.google.auth.appengine.AppEngineCredentials;
import com.google.auth.http.HttpCredentialsAdapter;

@Repository
public class GoogleCalendarRepository {

    @Value("${google-calendar.file-name.credential}")
    public String credentialFileName;

    public com.google.api.services.calendar.model.Calendar requestAddCalendar(UserInfo userInfo) throws IOException, GeneralSecurityException {
        Calendar service = this.generateService();

        return service.calendars().insert(new com.google.api.services.calendar.model.Calendar()
            .setSummary(userInfo.getUserName())
            .setDescription("LINE秘書で作成したカレンダー")
            .setTimeZone("Asia/Tokyo")).execute();
    }
    
    public Event requestRegisEvent(GoogleCalendarRegistRequest request, GoogleUserInfo googleUserInfo) throws FileNotFoundException, IOException, GeneralSecurityException {
        Calendar service = this.generateService();        

        EventDateTime startDateTime = new EventDateTime().setDate(new DateTime(request.getStartDate()));
        EventDateTime endDateTime = new EventDateTime().setDate(new DateTime(request.getEndDate()));

        Event event = new Event()
            .setSummary(request.getTitle())
            .setStart(startDateTime)
            .setEnd(endDateTime);

        return service.events().insert(googleUserInfo.getCalendarId(), event).execute();
    }
    
        public List<Event> requestSearchEvent(GoogleUserInfo googleUserInfo, GoogleCalendarSearchRequest request) throws IOException, GeneralSecurityException {
            Calendar service = this.generateService();
    
            Events events = service.events().list(googleUserInfo.getCalendarId())
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .setTimeMin(new DateTime(request.getStartDate() + "T00:00:00Z"))
                .setTimeMax(new DateTime(request.getEndDate() + "T23:59:59Z"))
                .execute();

            return events.getItems();
        }

    public void addCalendarRole(GoogleUserInfo googleUserInfo, String userEmail) throws IOException, GeneralSecurityException {
        Calendar service = this.generateService();
        System.out.println(service.calendarList().list().execute().toPrettyString());

        Scope scope = new Scope().setType("user").setValue(userEmail);

        AclRule rule = new AclRule();
        rule.setRole("owner");
        rule.setScope(scope);
        
        service.acl().insert(googleUserInfo.getCalendarId(), rule).execute();
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
