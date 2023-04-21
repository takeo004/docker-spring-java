package com.line.secretary.api.constant;

import lombok.Getter;

@Getter
public enum AdminProcess {
    
    HELP("--help", "--help"),
    CREATE_USER("--create-user ", "--create-user [ユーザー名]"),
    SET_CALENDAR_ID("--set-calendar-id ", "--set-calendar-id [ユーザー名] [カレンダーID]"),
    ADD_CALENDAR_ROLE("--add-calendar-role ", "--add-calendar-role [ユーザー名] [参照できるようにしたgoogleアカウントのアドレス]"),
    USERS("--users", "--users"),
    ;

    private String prefix;
    private String description;

    private AdminProcess(String prefix, String description) {
        this.prefix = prefix;
        this.description = description;
    }
}
