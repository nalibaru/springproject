package com.example.controller;

import com.example.springproject2.AuthRequest;
import com.example.springproject2.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.ArrayList;

@RestController
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private JwtUtil jwtUtil;

    private final ReactiveUserDetailsService userDetailsService;

    @Autowired
    public AuthenticationController(ReactiveUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/authenticate")
    public Mono<ResponseEntity<String>> authenticate(@RequestBody AuthRequest authRequest) {
        logger.info("Authentication request received for username: {}", authRequest.getUsername());
        return userDetailsService.findByUsername(authRequest.getUsername())
                .doOnNext(userDetails -> logger.debug("User found in DB: {}", authRequest.getUsername()))
                .filter(userDetails -> {
                    boolean matches = passwordEncoder.matches(authRequest.getPassword(), userDetails.getPassword());
                    if (matches) {
                        logger.debug("Password matched for user: {}", authRequest.getUsername());
                    } else {
                        logger.warn("Password mismatch for user: {}", authRequest.getUsername());
                    }
                    return matches;
                })
                .map(userDetails -> {
                    ArrayList<String> roles = new ArrayList<>();
                    roles.add("Admin");
                    String token = jwtUtil.generateToken(userDetails.getUsername(),roles);
                    logger.info("Generated JWT token for user: {}", authRequest.getUsername());
                    return ResponseEntity.ok(token);
                })
                .defaultIfEmpty(ResponseEntity.status(401).build())
                .doOnError(e -> logger.error("Authentication error: {}", e.getMessage()));
    }
}
