package com.example.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class SampleController {

    @GetMapping("/request")
    public ResponseEntity<String> RequestV1SubMethod() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Custom-Header", "request");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("Farewell, client!");
    }
}
