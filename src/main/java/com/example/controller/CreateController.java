package com.example.controller;


import com.example.model.User;
import com.example.service.CustomUserDetailsService;
import com.example.springproject2.AuthRequest;
import com.example.springproject2.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@RequestMapping("/api")
public class CreateController {
    private static final Logger logger = LoggerFactory.getLogger(CreateController.class);
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
    public Mono<ResponseEntity<String>> createUserForAuthorized(@RequestBody User user) {
        return createUser(user)
                .map(savedUser -> ResponseEntity.ok("User created successfully with ID: " + savedUser.getId()))
                .defaultIfEmpty(ResponseEntity.badRequest().body("Failed to create user"));
    }


    private Mono<User> createUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return Mono.justOrEmpty(customUserDetailsService.save(user));
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

    @PostMapping("/editrequest")
    public Map<String, Object> editRequestMethod(
            @RequestHeader(name = "project-name", required = false) String projectName,
            @RequestHeader(name = "Hello", required = false) String Hello,
            @RequestHeader(name = "Authorization", required = false) String token,
            @RequestHeader(name = "custom-api-header", required = false) String apiheader,@RequestBody String data) {

        Map<String, Object> response = new HashMap<>();
        if (token != null && token.startsWith("Bearer ")) {
            String[] bearerToken = token.split(" ");
            String username = jwtUtil.extractUsername(bearerToken[1]);
            response.put("token", bearerToken[1]);
        }
        if(projectName == null || projectName.isEmpty())
        {
            projectName = "SpringBoot";
        }
        response.put("project-name", projectName);
        response.put("Hello", Hello);
        response.put("modified-data",data);
        response.put("api-header",apiheader);
        return response;
    }

    @PostMapping("/editresponse")
    public User editResponseMethod(
            @RequestHeader(name = "project-name", required = false) String projectName,
            @RequestHeader(name = "Hello", required = false) String Hello,
            @RequestHeader(name = "Authorization", required = false) String token,
            @RequestParam(name="username") String username) {
            User user = customUserDetailsService.findByUsername(username);
            return user;
    }

    @PostMapping("/editreqid")
    public User editRequestFromCloud(@RequestParam(name="id") Long id) {
        logger.info("Downstream param id "+id);
        User user = customUserDetailsService.fetchUserBasedOnId(id);
        return user;
    }


}
