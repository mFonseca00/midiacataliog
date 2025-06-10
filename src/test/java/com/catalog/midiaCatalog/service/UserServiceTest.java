package com.catalog.midiaCatalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.catalog.midiacatalog.dto.User.UserPwSetDTO;
import com.catalog.midiacatalog.dto.User.UserRegistrationDTO;
import com.catalog.midiacatalog.dto.User.UserResponseDTO;
import com.catalog.midiacatalog.exception.DataNotFoundException;
import com.catalog.midiacatalog.exception.DataValidationException;
import com.catalog.midiacatalog.model.User;
import com.catalog.midiacatalog.repository.UserRepository;
import com.catalog.midiacatalog.service.UserService;

import lombok.Data;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = new User();
        user1.setId(1L);
        user1.setEmail("joaquim@gmail.com");
        user1.setName("Joaquim");
        user1.setPassword("Senhaforte123@");

        user2 = new User();
        user2.setId(2L);
        user2.setEmail("joana@gmail.com");
        user2.setName("Joana");
        user2.setPassword("Senhaforte123@");

        user3 = new User();
        user3.setId(1L);
        user3.setEmail("jorge@gmail.com");
        user3.setName("Jorge");
        user3.setPassword("Senhaforte123@");
    }

    @Test
    void testRegisterUserSuccess() {
        UserRegistrationDTO validUser = new UserRegistrationDTO(user1.getName(), user1.getEmail(), user1.getPassword());
        when(userRepository.save(any())).thenReturn(user1);
        
        UserResponseDTO response = userService.register(validUser);
        
        assertNotNull(response);
        assertEquals(user1.getName(), response.getName());
        assertEquals(user1.getEmail(), response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test
    void testRegisterUserValidations() {
        // Null credentials
        DataValidationException exception = assertThrows(DataValidationException.class, 
            () -> userService.register(null));
        assertEquals("User credentials must be informed.", exception.getMessage());

        // Test all empty fields
        exception = assertThrows(DataValidationException.class,
            () -> userService.register(new UserRegistrationDTO("", "", "")));
        assertTrue(exception.getErrors().contains("User name must be informed."));
        assertTrue(exception.getErrors().contains("User email must be informed."));
        assertTrue(exception.getErrors().contains("User password must be informed."));

        // Test all null fields
        exception = assertThrows(DataValidationException.class,
            () -> userService.register(new UserRegistrationDTO(null, null, null)));
        assertTrue(exception.getErrors().contains("User name must be informed."));
        assertTrue(exception.getErrors().contains("User email must be informed."));
        assertTrue(exception.getErrors().contains("User password must be informed."));
        
        // Test invalid formats
        exception = assertThrows(DataValidationException.class,
            () -> userService.register(new UserRegistrationDTO(
                "Test",
                "invalid-email",  // Invalid email
                "weak" // Weak password
            )));
        assertTrue(exception.getErrors().contains("Invalid email format."));
        assertTrue(exception.getErrors().contains("Password must contain at least 8 characters, one uppercase letter, one number and one special character."));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterDuplicateEmail() {
        UserRegistrationDTO newUser = new UserRegistrationDTO(user1.getName(), user1.getEmail(), user1.getPassword());
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        
        DataValidationException exception = assertThrows(DataValidationException.class,
            () -> userService.register(newUser));
        assertEquals("Email already registered.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSetPasswordSuccess(){ 
        UserPwSetDTO dto = new UserPwSetDTO(user1.getEmail(), "NovaSenah123@");
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));

        String response = userService.setPassword(dto);

        assertEquals(user1.getPassword(), dto.getPassword());
        assertEquals(response, "Passowrd rested successfuly");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testSetPasswordValidations(){
        // Null credentials
        DataValidationException exception = assertThrows(DataValidationException.class, 
            () -> userService.setPassword(null));
        assertEquals("User credentials must be informed.", exception.getMessage());

         // Test empty fields
        exception = assertThrows(DataValidationException.class,
            () -> userService.setPassword(new UserPwSetDTO("", "")));
        assertTrue(exception.getErrors().contains("User email must be informed."));
        assertTrue(exception.getErrors().contains("User password must be informed."));

        // Test null fields
        exception = assertThrows(DataValidationException.class,
            () -> userService.setPassword(new UserPwSetDTO(null, null)));
        assertTrue(exception.getErrors().contains("User email must be informed."));
        assertTrue(exception.getErrors().contains("User password must be informed."));

        // Test invalid formats
        exception = assertThrows(DataValidationException.class,
            () -> userService.setPassword(new UserPwSetDTO(
                "invalid-email",  // Invalid email
                "weak" // Weak password
            )));
        assertTrue(exception.getErrors().contains("Invalid email format."));
        assertTrue(exception.getErrors().contains("Password must contain at least 8 characters, one uppercase letter, one number and one special character."));

        // Test email not found
        DataNotFoundException notFound = assertThrows(DataNotFoundException.class,
             () -> userService.setPassword(new UserPwSetDTO(
                "email@email.com",  // non registred email
                "SenhaForte123@" 
            )));
        assertEquals(notFound.getMessage(),"No user found for this email.");
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRemoveUserSuccess(){ 
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

        UserResponseDTO removed = userService.remove(user1.getId());

        assertEquals(user1.getId(), removed.getId());
        assertEquals(user1.getName(), removed.getName());
        assertEquals(user1.getEmail(), removed.getEmail());
        verify(userRepository, times(1)).deleteById(user1.getId());
    }

    @Test
    void testRemoveUserValidation(){

        DataValidationException nullId = assertThrows(DataValidationException.class, 
            () -> userService.remove(null));
        assertEquals("User ID must be informed.", nullId.getMessage());

        DataNotFoundException notFoundId = assertThrows(DataNotFoundException.class, 
            () -> userService.remove(66L));
        assertEquals("No user found for this ID.", notFoundId.getMessage());
        verify(userRepository, never()).deleteById(any(Long.class));

    }


    // login

    // getUser

    // getAllUsers
}
