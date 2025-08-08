package com.catalog.midiacatalog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.catalog.midiacatalog.dto.User.UserLoginDTO;
import com.catalog.midiacatalog.dto.User.UserPwSetDTO;
import com.catalog.midiacatalog.dto.User.UserRegistrationDTO;
import com.catalog.midiacatalog.dto.User.UserResponseDTO;
import com.catalog.midiacatalog.dto.User.UserUpdateDTO;
import com.catalog.midiacatalog.exception.DataNotFoundException;
import com.catalog.midiacatalog.exception.DataValidationException;
import com.catalog.midiacatalog.model.User;
import com.catalog.midiacatalog.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserResponseDTO register(UserRegistrationDTO newUser) {
        if(newUser == null)
            throw new DataValidationException("User credentials must be informed.");
        
        List<String> errors = new ArrayList<>();
        String validation;

        if(newUser.getName() == null || newUser.getName().trim().isEmpty())
            errors.add("User name must be informed.");
        
        validation = validateEmail(newUser.getEmail());
        if(validation != null)
            errors.add(validation);
        else if(userRepository.findByEmail(newUser.getEmail()).isPresent())
            errors.add("Email already registered.");

        validation = validatePassword(newUser.getPassword());
        if(validation != null)
            errors.add(validation);

        if(!errors.isEmpty())
            throw new DataValidationException(errors); 
        
        User user = new User();
        user.setName(newUser.getName());
        user.setEmail(newUser.getEmail());
        user.setPassword(newUser.getPassword());

        userRepository.save(user);

        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail());
    }

    public String setPassword(UserPwSetDTO pwSetDTO){
        if(pwSetDTO == null)
            throw new DataValidationException("User credentials must be informed.");

        List<String> errors = new ArrayList<>();
        String validation;

        validation = validateEmail(pwSetDTO.getEmail());
        if(validation != null)
            errors.add(validation);
        
        validation = validatePassword(pwSetDTO.getPassword());
        if(validation != null)
            errors.add(validation);

        if(!errors.isEmpty())
            throw new DataValidationException(errors); 

        Optional<User> userFound = userRepository.findByEmail(pwSetDTO.getEmail());
        if(!userFound.isPresent())
            throw new DataNotFoundException("No user found for this email.");

        User user = userFound.get();
        user.setPassword(pwSetDTO.getPassword());
        userRepository.save(user);

        return "Password reseted successfuly";
    }

    public UserResponseDTO remove(Long id) {
        if(id == null)
            throw new DataValidationException("User ID must be informed.");

        Optional<User> userFound = userRepository.findById(id);
        if(!userFound.isPresent())
            throw new DataNotFoundException("No user found for this ID.");

        User user = userFound.get();
        userRepository.deleteById(id);
        return new UserResponseDTO(user.getId(),user.getName(),user.getEmail());
    }

    public boolean login(UserLoginDTO userLogin) {
        if(userLogin == null)
            throw new DataValidationException("User credentials must be informed.");
        
        List<String> errors = new ArrayList<>();
        String validation;

        validation = validateEmail(userLogin.getEmail());
        if(validation != null)
            errors.add(validation);
        
        validation = validatePassword(userLogin.getPassword());
        if(validation != null)
            errors.add(validation);

        if(!errors.isEmpty())
            throw new DataValidationException(errors); 

        Optional<User> userFound = userRepository.findByEmail(userLogin.getEmail());
        if(!userFound.isPresent())
            throw new DataNotFoundException("No user found for this email.");

        User user = userFound.get();

        if(user.getPassword() != userLogin.getPassword())
            throw new DataValidationException("Wrong password or email address. Please try again.");
        
        return true; //TODO: add JWT creation
    } 

    public UserResponseDTO update(Long id, UserUpdateDTO userInfo){
        List<String> errors = new ArrayList<>();
        String validation = null;

        if(id == null)
            errors.add("User Id must be informed.");
        if(userInfo == null)
            errors.add("User Informations can't be null.");
        else {
            if(userInfo.getEmail() != null && !userInfo.getEmail().isBlank())
            {
                validation = validateEmail(userInfo.getEmail());
                if(validation != null)
                    errors.add(validation);
                else {
                    Optional<User> existingUser = userRepository.findByEmail(userInfo.getEmail());
                    if(existingUser.isPresent() && !existingUser.get().getId().equals(id))
                        errors.add("Email already registered by another user.");
                }
            }
                
            if(userInfo.getPassword() != null && !userInfo.getPassword().isBlank())
            {
                validation = validatePassword(userInfo.getPassword());
                if(validation != null)
                    errors.add(validation);
            }
        }

        if(!errors.isEmpty())
            throw new DataValidationException(errors);
        
        Optional<User> userFound = userRepository.findById(id);
        if(!userFound.isPresent())
            throw new DataNotFoundException("User not found.");

        User user = userFound.get();
        if(userInfo.getName() != null && !userInfo.getName().isBlank())
            user.setName(userInfo.getName());
        if(userInfo.getEmail() != null && !userInfo.getEmail().isBlank())
            user.setEmail(userInfo.getEmail());
        if(userInfo.getPassword() != null && !userInfo.getPassword().isBlank())
            user.setPassword(userInfo.getPassword());

        userRepository.save(user);
        
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail());
    }

    public UserResponseDTO getUser(Long id) {
        if(id == null)
            throw new DataValidationException("User id must be informed.");

        Optional<User> userFound = userRepository.findById(id);
        if(!userFound.isPresent())
            throw new DataNotFoundException("User not found.");

        User user = userFound.get();
        return new UserResponseDTO( user.getId(), user.getName(), user.getEmail());
    }

    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        if(users.isEmpty())
            throw new DataNotFoundException("No users found in database.");

        return users.map(user -> new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
                ));
    }
    
    // Helper methods
    private String validateEmail(String Email){
        if(Email == null || Email.trim().isEmpty())
            return ("User email must be informed.");
        else if (!Email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) 
            return ("Invalid email format.");
        return null;
    }
    
    private String validatePassword(String passWord)
    {
        if(passWord == null || passWord.trim().isEmpty())
            return ("User password must be informed.");
        else if (!passWord.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"))
            return ("Password must contain at least 8 characters, one uppercase letter, one number and one special character.");
        return null;
    }
}
