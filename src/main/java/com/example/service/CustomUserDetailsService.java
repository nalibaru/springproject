package com.example.service;


import com.example.model.User;

public interface CustomUserDetailsService {
    User save(User newuser);
    User findByUsername(String username);
    User fetchUserBasedOnId(Long id);
}
