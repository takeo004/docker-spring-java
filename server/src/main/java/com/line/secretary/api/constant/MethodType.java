package com.line.secretary.api.constant;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum MethodType {
    SCHEDULE("1", "googleCalendarController"),
    ;

    private String method;
    private String controllerName;

    private MethodType(String method, String controllerName) {
        this.method = method;
        this.controllerName = controllerName;
    }

    public static MethodType of(String value) {
        return Arrays.asList(MethodType.values()).stream()
            .filter(method -> method.getMethod().equals(value))
            .findFirst()
            .orElse(null);
    }
}
