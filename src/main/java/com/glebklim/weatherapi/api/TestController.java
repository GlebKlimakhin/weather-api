package com.glebklim.weatherapi.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TestController {

    @Autowired
    private TestService service;

    @GetMapping
    public ResponseEntity<String> getWeatherFromCurrentLocation() {
        return ResponseEntity.ok(service.getCurrentWeatherForYourLocation());
    }
}
