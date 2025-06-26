package com.catalog.midiaCatalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.catalog.midiacatalog.controller.UserController;
import com.catalog.midiacatalog.dto.User.UserPwSetDTO;
import com.catalog.midiacatalog.dto.User.UserRegistrationDTO;
import com.catalog.midiacatalog.dto.User.UserResponseDTO;
import com.catalog.midiacatalog.exception.DataNotFoundException;
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
    private UserPwSetDTO userPwSetDTO;

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
        userPwSetDTO = new UserPwSetDTO(
                "jhon@gmail.com",
                "Password1234@");
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
                .andExpect(jsonPath("$.message").value(Matchers.containsString("User name must be informed.")))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Invalid email format.")))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Password must contain at least 8 characters")));
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

    @Test
    void testRemoveUserSuccess() throws Exception {
        when(userService.remove(any(Long.class))).thenReturn(userResponseDTO);

        mockMvc.perform(delete("/user/remove/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Jhon"))
                .andExpect(jsonPath("$.email").value("jhon@gmail.com"));
    }

    @Test
    void testRemoveWithNonNumericId() throws Exception {
        mockMvc.perform(delete("/user/remove/{id}", "abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }

    @Test
    void testRemoveNotFoundId() throws Exception {
        when(userService.remove(any(Long.class)))
                .thenThrow(new DataNotFoundException("No user found for this ID."));

        mockMvc.perform(delete("/user/remove/{id}", 2L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("No user found for this ID.")));
    }

    @Test
    void testSetPasswordSuccess() throws Exception {
        when(userService.setPassword(any(UserPwSetDTO.class)))
                .thenReturn("Password reseted successfuly");

        mockMvc.perform(patch("/user/set-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPwSetDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Password reseted successfuly"));
    }

    @Test
    void testSetPasswordWithNullCredentials() throws Exception {
        when(userService.setPassword(any(UserPwSetDTO.class)))
                .thenThrow(new DataValidationException("User credentials must be informed."));

        mockMvc.perform(patch("/user/set-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User credentials must be informed."));
    }

    @Test
    void testSetPasswordNotFoundEmail() throws Exception {
        when(userService.setPassword(any(UserPwSetDTO.class)))
                .thenThrow(new DataNotFoundException("No user found for this email."));

        UserPwSetDTO notFoundEmail = new UserPwSetDTO("notfound@email.com", "Password123@");

        mockMvc.perform(patch("/user/set-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notFoundEmail)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("No user found for this email.")));
    }

    @Test
    void testSetPasswordWithInvalidData() throws Exception {
        UserPwSetDTO invalidData = new UserPwSetDTO( "invalid-email", "weak");
        when(userService.setPassword(any(UserPwSetDTO.class)))
                .thenThrow(new DataValidationException(Arrays.asList(
                        "Invalid email format.",
                        "Password must contain at least 8 characters, one uppercase letter, one number and one special character."
                )));

        mockMvc.perform(patch("/user/set-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Invalid email format.")))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Password must contain at least 8 characters")));
    }

    @Test
    void testSetPasswordUserWithMalformedJson() throws Exception {
        mockMvc.perform(patch("/user/set-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json"))
                .andExpect(status().isBadRequest());
    }

    // TODO: login

    // TODO: update

    // TODO: getUser

    // TODO: getAllUsers

}
