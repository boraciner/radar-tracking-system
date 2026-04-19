package com.radartracker.mapviewerservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrackController {

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }
}
