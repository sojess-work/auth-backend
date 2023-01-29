package com.dmarkdown.auth.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/guest")
public class GuestHomeController {

    @GetMapping("/guest-home")
    public String getGuestHomePage(){

        return "Welcome Home Guest";
    }
}
