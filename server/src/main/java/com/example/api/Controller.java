package com.example.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @RequestMapping("/")
	public String home() {
		return "Hello World YEAH!";
	}

    @RequestMapping("/test")
    public String test() {
        return "testだよん";
    }
}
