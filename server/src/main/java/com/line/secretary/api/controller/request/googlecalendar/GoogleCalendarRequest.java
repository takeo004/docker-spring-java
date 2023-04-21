package com.line.secretary.api.controller.request.googlecalendar;

import com.line.secretary.api.controller.request.LineRequestBase;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper=false)
public class GoogleCalendarRequest extends LineRequestBase {
    
    /**
     * 予定開始日
     */
    private String startDate;

    /**
     * 予定終了日
     */
    private String endDate;

    /**
     * タイトル
     */
    private String title;
}
