package com.example.controller;


import com.example.model.User;
import com.example.service.CustomUserDetailsService;
import com.example.springproject2.AuthRequest;
import com.example.springproject2.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@RequestMapping("/api")
public class CreateController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public CreateController(CustomUserDetailsService customUserDetailsService){
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/adduser")
    public Mono<ResponseEntity<String>> createUserForAuthorized(@RequestBody AuthRequest authRequest) {
        return createUser(authRequest.getUsername(), authRequest.getPassword())
                .map(savedUser -> ResponseEntity.ok("User created successfully with ID: " + savedUser.getId()))
                .defaultIfEmpty(ResponseEntity.badRequest().body("Failed to create user"));
    }

    private Mono<User> createUser(String username, String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);
        return Mono.justOrEmpty(customUserDetailsService.save(newUser));
    }

    @GetMapping("/fetchuser")
    @ResponseBody
    public User fetchUsers (@RequestParam String username) {
        User user = customUserDetailsService.findByUsername(username);
        return user;
    }

    @PostMapping("/reqheader")
    public Map<String, Object> requestHeaderMethod(
            @RequestHeader(name = "custom-api-header", required = false) String customHeader,
            @RequestHeader(name = "X-Username-filter", required = false) String usernamefilter,
            @RequestHeader(name = "Hello", required = false) String Hello,
            @RequestHeader(name = "Authorization", required = false) String token) {

        Map<String, Object> response = new HashMap<>();
        if (token != null && token.startsWith("Bearer ")) {
            String[] bearerToken = token.split(" ");
            String username = jwtUtil.extractUsername(bearerToken[1]);
            response.put("token", bearerToken[1]);
        }
        response.put("custom-api-header", customHeader);
        response.put("X-Username-filter", usernamefilter);
        response.put("Hello", Hello);
        return response;
    }
}
