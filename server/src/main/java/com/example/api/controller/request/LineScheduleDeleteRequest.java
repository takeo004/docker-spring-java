package com.example.api.controller.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class LineScheduleDeleteRequest extends LineScheduleRequestBase {

    /**
     * 予定のタイトル
     */
    private String title;
    
}
