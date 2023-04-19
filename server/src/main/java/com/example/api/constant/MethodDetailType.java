package com.example.api.constant;

import java.util.Arrays;

import com.example.api.controller.request.googlecalendar.GoogleCalendarDeleteRequest;
import com.example.api.controller.request.googlecalendar.GoogleCalendarRegistRequest;
import com.example.api.controller.request.googlecalendar.GoogleCalendarSearchRequest;

import lombok.Getter;

@Getter
@SuppressWarnings("raw type")
public enum MethodDetailType {
    SCHEDULE_REGIST(MethodType.SCHEDULE, "1", "registSchedule", GoogleCalendarRegistRequest.class),
    SCHEDULE_SEARCH(MethodType.SCHEDULE, "2", "searchSchedule", GoogleCalendarSearchRequest.class),
    SCHEDULE_DELETE(MethodType.SCHEDULE, "3", "deleteSchedule", GoogleCalendarDeleteRequest.class),
    ;

    private MethodType method;
    private String methodDetail;
    private String methodName;
    private Class<?> requestClass;

    private MethodDetailType(MethodType method, String methodDetail, String methodName, Class<?> requestClass) {
        this.method = method;
        this.methodDetail = methodDetail;
        this.methodName = methodName;
        this.requestClass = requestClass;
    }

    public static MethodDetailType of(MethodType method, String detail) {
        return Arrays.asList(MethodDetailType.values()).stream()
            .filter(methodDetail -> methodDetail.getMethod().equals(method)
                && methodDetail.getMethodDetail().equals(detail))
            .findFirst()
            .orElse(null);
    }
}
