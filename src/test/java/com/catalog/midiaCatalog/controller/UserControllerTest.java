package com.catalog.midiaCatalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.catalog.midiacatalog.controller.UserController;
import com.catalog.midiacatalog.dto.User.UserRegistrationDTO;
import com.catalog.midiacatalog.dto.User.UserResponseDTO;
import com.catalog.midiacatalog.exception.DataValidationException;
import com.catalog.midiacatalog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private ObjectMapper objectMapper;

    private UserResponseDTO userResponseDTO;
    private UserRegistrationDTO validUserRegistrationDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        userResponseDTO = new UserResponseDTO(
            1L,
            "Jhon",
            "jhon@gmail.com");
        validUserRegistrationDTO = new UserRegistrationDTO(
            "Jhon",
            "jhon@gmail.com",
            "Password123@");
    }

    @Test
    void testRegisterUserSuccess() throws Exception {
        when(userService.register(any(UserRegistrationDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUserRegistrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Jhon"))
                .andExpect(jsonPath("$.email").value("jhon@gmail.com"));
    }

    @Test
    void testRegisterUserWithInvalidData() throws Exception {
        UserRegistrationDTO invalidUser = new UserRegistrationDTO("", "invalid-email", "weak");
        when(userService.register(any(UserRegistrationDTO.class)))
                .thenThrow(new DataValidationException(Arrays.asList(
                        "User name must be informed.",
                        "Invalid email format.",
                        "Password must contain at least 8 characters, one uppercase letter, one number and one special character."
                )));

        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("User name must be informed.")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Invalid email format.")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Password must contain at least 8 characters")));
    }

    @Test
    void testRegisterUserWithDuplicateEmail() throws Exception {
        when(userService.register(any(UserRegistrationDTO.class)))
                .thenThrow(new DataValidationException("Email already registered."));

        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUserRegistrationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Email already registered."));
    }

    @Test
    void testRegisterUserWithNullCredentials() throws Exception {
        when(userService.register(any(UserRegistrationDTO.class)))
                .thenThrow(new DataValidationException("User credentials must be informed."));

        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User credentials must be informed."));
    }

    @Test
    void testRegisterUserWithMalformedJson() throws Exception {
        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json"))
                .andExpect(status().isBadRequest());
    }

    // TODO: removeUser

    // TODO: setPassword

    // TODO: login

    // TODO: update

    // TODO: getUser

    // TODO: getAllUsers

}
