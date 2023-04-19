package com.example.api.constant;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum MethodDetailType {
    SCHEDULE_REGIST(MethodType.SCHEDULE, "1"),
    SCHEDULE_SEARCH(MethodType.SCHEDULE, "2"),
    SCHEDULE_DELETE(MethodType.SCHEDULE, "3"),
    ;

    private MethodType method;
    private String methodDetail;

    private MethodDetailType(MethodType method, String methodDetail) {
        this.method = method;
        this.methodDetail = methodDetail;
    }

    public static MethodDetailType of(MethodType method, String detail) {
        return Arrays.asList(MethodDetailType.values()).stream()
            .filter(methodDetail -> methodDetail.getMethod().equals(method)
                && methodDetail.getMethodDetail().equals(detail))
            .findFirst()
            .orElse(null);
    }
}
