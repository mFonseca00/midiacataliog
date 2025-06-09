package com.catalog.midiaCatalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.catalog.midiacatalog.dto.User.UserRegistrationDTO;
import com.catalog.midiacatalog.dto.User.UserResponseDTO;
import com.catalog.midiacatalog.exception.DataValidationException;
import com.catalog.midiacatalog.model.User;
import com.catalog.midiacatalog.repository.UserRepository;
import com.catalog.midiacatalog.service.UserService;

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
    void testRegisterUserSuccess(){
        UserRegistrationDTO newUser = new UserRegistrationDTO(user1.getName(), user1.getEmail(), user1.getPassword());
        UserResponseDTO saved = userService.register(newUser);

        assertNotNull(saved);
        assertEquals(user1.getId(),saved.getId());
        assertEquals(user1.getName(),saved.getName());
        assertEquals(user1.getEmail(),saved.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserFailEmailAlreadyUsed(){
        
    }

    @Test
    void testRegisterUserFailInvalidPassword(){
        
    }

    @Test
    void testRegisterUserFailUserNull(){
        UserRegistrationDTO newUser = new UserRegistrationDTO(null, user1.getEmail(), user1.getPassword());
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                userService.register(null);
            });

        assertEquals("User name must be informed.", exception.getMessage());
    }

    @Test
    void testRegisterUserFailEmailNull(){
        UserRegistrationDTO newUser = new UserRegistrationDTO(user1.getName(), null, user1.getPassword());
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                userService.register(null);
            });

        assertEquals("User email must be informed.", exception.getMessage());
    }

    @Test
    void testRegisterUserFailPasswordNull(){
        UserRegistrationDTO newUser = new UserRegistrationDTO(user1.getName(), user1.getEmail(), null);
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                userService.register(null);
            });

        assertEquals("User password must be informed.", exception.getMessage());
    }

    @Test
    void testRegisterUserFailUserEmpty(){
        UserRegistrationDTO newUser = new UserRegistrationDTO("", user1.getEmail(), user1.getPassword());
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                userService.register(null);
            });

        assertEquals("User name must be informed.", exception.getMessage());
    }

    @Test
    void testRegisterUserFailEmailEmpty(){
        UserRegistrationDTO newUser = new UserRegistrationDTO(user1.getName(), "", user1.getPassword());
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                userService.register(null);
            });

        assertEquals("User email must be informed.", exception.getMessage());
    }

    @Test
    void testRegisterUserFailPasswordEmpty(){
        UserRegistrationDTO newUser = new UserRegistrationDTO(user1.getName(), user1.getEmail(), "");
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                userService.register(null);
            });

        assertEquals("User password must be informed.", exception.getMessage());
    }

    @Test
    void testRegisterUserFailNullUserCredentials(){
        Exception exception = assertThrows(DataValidationException.class,
            () -> {
                userService.register(null);
            });

        assertEquals("User credentials must be informed.", exception.getMessage());
    }

    // remove user

    // set password

    // login
}
