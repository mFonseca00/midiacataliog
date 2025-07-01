package com.catalog.midiaCatalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.catalog.midiacatalog.controller.UserController;
import com.catalog.midiacatalog.dto.User.UserLoginDTO;
import com.catalog.midiacatalog.dto.User.UserPwSetDTO;
import com.catalog.midiacatalog.dto.User.UserRegistrationDTO;
import com.catalog.midiacatalog.dto.User.UserResponseDTO;
import com.catalog.midiacatalog.dto.User.UserUpdateDTO;
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
    private UserResponseDTO userResponseDTO2;
    private UserRegistrationDTO validUserRegistrationDTO;
    private UserPwSetDTO userPwSetDTO;
    private UserLoginDTO userLoginDTO;
    private UserUpdateDTO userUpdateDTO;
    private Page<UserResponseDTO> userPage;

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
        userLoginDTO = new UserLoginDTO(
                "jhon@gmail.com",
                "Password123@");
        userUpdateDTO = new UserUpdateDTO(
                "Jhon",
                "jhon@gmail.com",
                "Password123@");
        
        userResponseDTO2 = new UserResponseDTO(2L, "Jane", "jane@gmail.com");
        userPage = new PageImpl<>(Arrays.asList(userResponseDTO, userResponseDTO2), PageRequest.of(0, 10), 2);
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

    @Test
    void testLoginSuccess() throws Exception {
        when(userService.login(any(UserLoginDTO.class)))
                .thenReturn(true);

        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.logged").value(true));
    }

    @Test
    void testLoginWithNullCredentials() throws Exception {
        when(userService.login(any(UserLoginDTO.class)))
                .thenThrow(new DataValidationException("User credentials must be informed."));

        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User credentials must be informed."));
    }

    @Test
    void testLoginNotFoundEmail() throws Exception {
        when(userService.login(any(UserLoginDTO.class)))
                .thenThrow(new DataNotFoundException("No user found for this email."));

        UserLoginDTO notFoundEmail = new UserLoginDTO("notfound@email.com", "Password123@");

        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notFoundEmail)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("No user found for this email.")));
    }

    @Test
    void testLoginWithInvalidData() throws Exception {
        UserLoginDTO invalidData = new UserLoginDTO( "invalid-email", "weak");
        when(userService.login(any(UserLoginDTO.class)))
                .thenThrow(new DataValidationException(Arrays.asList(
                        "Invalid email format.",
                        "Password must contain at least 8 characters, one uppercase letter, one number and one special character."
                )));

        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Invalid email format.")))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Password must contain at least 8 characters")));
    }

    @Test
    void testLoginWrongCredentials() throws Exception {
        UserLoginDTO wrongCredentials = new UserLoginDTO( "email@gmail.com", "Wrong123@");
        when(userService.login(any(UserLoginDTO.class)))
                .thenThrow(new DataValidationException("Wrong password or email address. Please try again."));
        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongCredentials)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Wrong password or email address. Please try again."));
    }

    @Test
    void testLoginWithMalformedJson() throws Exception {
        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        when(userService.update(any(Long.class), any(UserUpdateDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(patch("/user/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Jhon"))
                .andExpect(jsonPath("$.email").value("jhon@gmail.com"));
    }

    @Test
    void testUpdateUserWithInvalidData() throws Exception {
        UserUpdateDTO invalidData = new UserUpdateDTO("", "invalid-email", "weak");
        when(userService.update(any(Long.class), any(UserUpdateDTO.class)))
                .thenThrow(new DataValidationException(Arrays.asList(
                        "User name must be informed.",
                        "Invalid email format.",
                        "Password must contain at least 8 characters, one uppercase letter, one number and one special character."
                )));

        mockMvc.perform(patch("/user/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("User name must be informed.")))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Invalid email format.")))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Password must contain at least 8 characters")));
    }

    @Test
    void testUpdateUserWithDuplicateEmail() throws Exception {
        UserUpdateDTO duplicateEmail = new UserUpdateDTO("John Updated", "existing@email.com", "Password123@");
        when(userService.update(any(Long.class), any(UserUpdateDTO.class)))
                .thenThrow(new DataValidationException("Email already registered."));

        mockMvc.perform(patch("/user/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateEmail)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Email already registered."));
    }

    @Test
    void testUpdateUserWithNullCredentials() throws Exception {
        when(userService.update(any(Long.class), any(UserUpdateDTO.class)))
                .thenThrow(new DataValidationException("User credentials must be informed."));

        mockMvc.perform(patch("/user/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User credentials must be informed."));
    }

    @Test
    void testUpdateUserNotFoundId() throws Exception {
        when(userService.update(any(Long.class), any(UserUpdateDTO.class)))
                .thenThrow(new DataNotFoundException("No user found for this ID."));

        mockMvc.perform(patch("/user/update/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("No user found for this ID.")));
    }

    @Test
    void testUpdateUserWithNonNumericId() throws Exception {
        mockMvc.perform(patch("/user/update/{id}", "abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUserWithMalformedJson() throws Exception {
        mockMvc.perform(patch("/user/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserSuccess() throws Exception {
        when(userService.getUser(any(Long.class))).thenReturn(userResponseDTO);

        mockMvc.perform(get("/user/getUser/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Jhon"))
                .andExpect(jsonPath("$.email").value("jhon@gmail.com"));
    }

    @Test
    void testGetUserWithNonNumericId() throws Exception {
        mockMvc.perform(get("/user/getUser/{id}", "abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }

    @Test
    void testGetUserNotFoundId() throws Exception {
        when(userService.getUser(any(Long.class)))
                .thenThrow(new DataNotFoundException("No user found for this ID."));

        mockMvc.perform(get("/user/getUser/{id}", 2L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("No user found for this ID.")));
    }

    @Test
    void testGetUserWithNullId() throws Exception {
        mockMvc.perform(get("/user/getUser/{id}", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllUsersSuccess() throws Exception {
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(get("/user/getAllUsers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Jhon"))
                .andExpect(jsonPath("$.content[0].email").value("jhon@gmail.com"))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[1].name").value("Jane"))
                .andExpect(jsonPath("$.content[1].email").value("jane@gmail.com"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void testGetAllUsersWithPagination() throws Exception {
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(get("/user/getAllUsers")
                .param("page", "0")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testGetAllUsersEmptyDatabase() throws Exception {
        when(userService.getAllUsers(any(Pageable.class)))
                .thenThrow(new DataNotFoundException("No users found in database."));

        mockMvc.perform(get("/user/getAllUsers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("No users found in database.")));
    }

}
