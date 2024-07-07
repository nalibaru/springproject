package com.example.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class SimpleController {

    @GetMapping("/request")
    public ResponseEntity<String> RequestSubMethod() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Custom-Header", "request");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("Farewell, client!");
    }

    @GetMapping("/setreqheader")
    public Map<String, Object> RequestSubHeaderMethod(@RequestHeader(name="Hello", required =false) String Hello) {
        //HttpHeaders responseHeaders = new HttpHeaders();
        Map<String, Object> response = new HashMap<>();
        response.put("Hello", Hello);
        return response;
    }

    @GetMapping("/setresheader")
    public ResponseEntity<String> ResponseSubHeaderMethod() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Custom-Header", "request");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("Farewell, client!");
    }

    @GetMapping("/remreqheader")
    public Map<String, Object> RequestRemoveSubHeaderMethod(@RequestHeader(name="Hello", required =false) String Hello) {
        Map<String, Object> response = new HashMap<>();
        response.put("Hello", Hello);
        return response;
    }

    @GetMapping("/remresheader")
    public ResponseEntity<String> ResponseRemoveSubHeaderMethod() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Custom-Header", "request");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("Farewell, client!");
    }

    @GetMapping("/rewriteres")
    public ResponseEntity<String> RewriteResponsePassword(@RequestHeader(name="password", required=false) String password) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Custom-Header", "rewriteres");
        if (password != null) {
            responseHeaders.set("X-Request-Red", "password=1234");
        }
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("Farewell, client!");
    }

}
