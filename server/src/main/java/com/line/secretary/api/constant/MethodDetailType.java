package com.line.secretary.api.constant;

import java.util.Arrays;

import lombok.Getter;

@Getter
@SuppressWarnings("raw type")
public enum MethodDetailType {
    SCHEDULE_REGIST(MethodType.SCHEDULE, "1", "registSchedule"),
    SCHEDULE_SEARCH(MethodType.SCHEDULE, "2", "searchSchedule"),
    SCHEDULE_DELETE(MethodType.SCHEDULE, "3", "deleteSchedule"),
    ;

    private MethodType method;
    private String methodDetail;
    private String methodName;

    private MethodDetailType(MethodType method, String methodDetail, String methodName) {
        this.method = method;
        this.methodDetail = methodDetail;
        this.methodName = methodName;
    }

    public static MethodDetailType of(MethodType method, String detail) {
        return Arrays.asList(MethodDetailType.values()).stream()
            .filter(methodDetail -> methodDetail.getMethod().equals(method)
                && methodDetail.getMethodDetail().equals(detail))
            .findFirst()
            .orElse(null);
    }
}
