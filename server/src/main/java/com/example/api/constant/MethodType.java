package com.example.api.constant;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum MethodType {
    SCHEDULE("1"),
    ;

    private String method;

    private MethodType(String method) {
        this.method = method;
    }

    public static MethodType of(String value) {
        return Arrays.asList(MethodType.values()).stream()
            .filter(method -> method.getMethod().equals(value))
            .findFirst()
            .orElse(null);
    }
}
