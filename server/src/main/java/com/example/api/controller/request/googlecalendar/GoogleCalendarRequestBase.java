package com.example.api.controller.request.googlecalendar;

import java.util.Date;

import com.example.api.controller.request.LineRequestBase;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper=false)
public class GoogleCalendarRequestBase extends LineRequestBase {
    
    /**
     * 予定開始日
     */
    @JsonFormat(pattern = "yyyy-mm-dd", timezone = "Asia/Tokyo")
    private Date startDate;

    /**
     * 予定終了日
     */
    @JsonFormat(pattern = "yyyy-mm-dd", timezone = "Asia/Tokyo")
    private String endDate;
}
