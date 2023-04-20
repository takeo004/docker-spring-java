package com.example.api.constant;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum State {
    
    SET_CALENDER_ID_CONFIRM("1", "1"),
    ;

    private String state;
    private String detail;

    private State(String state, String detail) {
        this.state = state;
        this.detail = detail;
    }

    public static State of(String state, String detail) {
        return Arrays.asList(State.values()).stream()
            .filter(value -> value.getState().equals(state))
            .filter(value -> value.getDetail().equals(detail))
            .findFirst()
            .orElse(null);
    }
}
