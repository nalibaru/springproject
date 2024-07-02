package com.example.repository;


import com.example.model.User;

public interface UserRepositoryCustom {
    User customFindMethod(String username);
    User save(User newuser);
    User fetchUserBasedOnId(Long id);
}
