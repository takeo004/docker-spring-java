package com.example.api.controller.request.googlecalendar;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class GoogleCalendarDeleteRequest extends GoogleCalendarRequestBase {

    /**
     * 予定のタイトル
     */
    private String title;
    
}
