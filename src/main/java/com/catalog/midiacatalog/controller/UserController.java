package com.catalog.midiacatalog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.catalog.midiacatalog.dto.MessageResponseDTO;
import com.catalog.midiacatalog.dto.User.LoginResponseDTO;
import com.catalog.midiacatalog.dto.User.UserLoginDTO;
import com.catalog.midiacatalog.dto.User.UserPwSetDTO;
import com.catalog.midiacatalog.dto.User.UserRegistrationDTO;
import com.catalog.midiacatalog.dto.User.UserResponseDTO;
import com.catalog.midiacatalog.dto.User.UserUpdateDTO;
import com.catalog.midiacatalog.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        UserResponseDTO response = userService.register(userRegistrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<UserResponseDTO> remove(@PathVariable Long id){
        UserResponseDTO response = userService.remove(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/set-password")
    public ResponseEntity<MessageResponseDTO> setPassword(@RequestBody UserPwSetDTO pwSetDTO){
        String response = userService.setPassword(pwSetDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponseDTO(response));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody UserLoginDTO userLogin){
        boolean response = userService.login(userLogin);
        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDTO(response));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO){
        UserResponseDTO response = userService.update(id, userUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id){
        UserResponseDTO response = userService.getUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable){
        Page<UserResponseDTO> response = userService.getAllUsers(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
}
