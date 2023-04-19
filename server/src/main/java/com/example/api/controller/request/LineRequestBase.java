package com.example.api.controller.request;

import com.example.api.constant.MethodDetailType;

import lombok.Data;

@Data
public abstract class LineRequestBase {
    
    private MethodDetailType methodDetailType;
}
