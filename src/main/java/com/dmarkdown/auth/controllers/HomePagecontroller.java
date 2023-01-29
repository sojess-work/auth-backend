package com.dmarkdown.auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomePagecontroller {

    @GetMapping("/home-page")
    public ResponseEntity<String> getHomePage(){

        return ResponseEntity.ok("HEllo welcome to hoempage");
    }

}
