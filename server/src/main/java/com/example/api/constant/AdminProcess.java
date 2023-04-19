package com.example.api.constant;

import lombok.Getter;

@Getter
public enum AdminProcess {
    
    HELP("--help", "--help"),
    CREATE_USER("--create-user ", "--create-user [userName]"),
    SET_CALENDER_ID("--set-calender-id ", "--set-calender-id [userName] [calenderId]"),
    ;

    private String prefix;
    private String description;

    private AdminProcess(String prefix, String description) {
        this.prefix = prefix;
        this.description = description;
    }
}
