package com.catalog.midiacatalog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.catalog.midiacatalog.dto.User.UserRegistrationDTO;
import com.catalog.midiacatalog.dto.User.UserResponseDTO;
import com.catalog.midiacatalog.model.User;
import com.catalog.midiacatalog.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserResponseDTO register(UserRegistrationDTO newUser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'register'");
    }

    // register user

    // remove user

    // set password

    // login

}
