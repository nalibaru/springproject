package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepositoryCustom;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailServiceImpl implements CustomUserDetailsService {

      private final UserRepositoryCustom userRepository;

      @Autowired
      public CustomUserDetailServiceImpl(UserRepositoryCustom userRepository) {
        this.userRepository = userRepository;
      }


    @Override
    @Transactional
    public User save(User newuser) {
        User user = userRepository.save(newuser);
        return user;
    }

    @Override
    public User findByUsername(String username) {
       User user = userRepository.customFindMethod(username);
       return user;
    }

    @Override
    public User fetchUserBasedOnId(Long id) {
        User user = userRepository.fetchUserBasedOnId(id);
        return user;
    }


}
