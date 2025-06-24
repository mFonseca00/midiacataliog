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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.catalog.midiacatalog.dto.Actor.ActorDTO;
import com.catalog.midiacatalog.dto.User.UserLoginDTO;
import com.catalog.midiacatalog.dto.User.UserPwSetDTO;
import com.catalog.midiacatalog.dto.User.UserRegistrationDTO;
import com.catalog.midiacatalog.dto.User.UserResponseDTO;
import com.catalog.midiacatalog.dto.User.UserUpdateDTO;
import com.catalog.midiacatalog.exception.DataNotFoundException;
import com.catalog.midiacatalog.exception.DataValidationException;
import com.catalog.midiacatalog.model.Actor;
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


    @Test
    void testLoginSuccess(){
        UserLoginDTO userTry = new UserLoginDTO(
                user1.getEmail(),
                user1.getPassword()
        );

        when(userRepository.findByEmail(userTry.getEmail())).thenReturn(Optional.of(user1));

        boolean logged = userService.login(userTry); // implement jwt

        assertTrue(logged);
        // add JWT vlidation
        verify(userRepository, times(1)).findByEmail(user1.getEmail());
    }

    @Test
    void testLoginValidation(){
        // Null credentials
        DataValidationException exception = assertThrows(DataValidationException.class, 
            () -> userService.register(null));
        assertEquals("User credentials must be informed.", exception.getMessage());

        // Test all empty fields
        exception = assertThrows(DataValidationException.class,
            () -> userService.login(new UserLoginDTO("", "")));
        assertTrue(exception.getErrors().contains("User email must be informed."));
        assertTrue(exception.getErrors().contains("User password must be informed."));

        // Test all null fields
        exception = assertThrows(DataValidationException.class,
            () -> userService.login(new UserLoginDTO(null, null)));
        assertTrue(exception.getErrors().contains("User email must be informed."));
        assertTrue(exception.getErrors().contains("User password must be informed."));
        
        // Test invalid formats
        exception = assertThrows(DataValidationException.class,
            () -> userService.login(new UserLoginDTO(
                "invalid-email",  // Invalid email
                "weak" // Weak password
            )));
        assertTrue(exception.getErrors().contains("Invalid email format."));
        assertTrue(exception.getErrors().contains("Password must contain at least 8 characters, one uppercase letter, one number and one special character."));
        verify(userRepository, never()).findByEmail(any(String.class));      

    }

    @Test
    void testLoginWrongPassword(){
        UserLoginDTO userTry = new UserLoginDTO(
                user1.getEmail(),
                "passworD123@"
        );

        when(userRepository.findByEmail(userTry.getEmail())).thenReturn(Optional.of(user1));

        DataValidationException exception = assertThrows(DataValidationException.class,
            () -> userService.login(userTry));
        
        assertEquals("Wrong password or email address. Please try again.", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(any(String.class));
    }

    @Test
    void testUpdateSuccess(){
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

        UserUpdateDTO userInfo = new UserUpdateDTO("Joaquim Silva", null, "Newpassword123@");

        UserResponseDTO updated = userService.update(user1.getId(), userInfo);

        assertNotNull(updated);
        assertEquals(user1.getId(), updated.getId());
        assertEquals("Joaquim Silva", updated.getName());
        assertEquals(user1.getEmail(), updated.getEmail()); // Email not changed
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateValidations(){
        // Test null id and informations
        DataValidationException exception = assertThrows(DataValidationException.class,
            () -> userService.update(null, null));
        assertTrue(exception.getErrors().contains("User Id must be informed."));
        assertTrue(exception.getErrors().contains("User Informations can't be null."));

        // Test invalid formats
        exception = assertThrows(DataValidationException.class,
            () -> userService.update(1L, new UserUpdateDTO(
                null, // not update name
                "invalid-email",  // Invalid email
                "weak" // Weak password
            )));
        assertTrue(exception.getErrors().contains("Invalid email format."));
        assertTrue(exception.getErrors().contains("Password must contain at least 8 characters, one uppercase letter, one number and one special character."));

        // Test id not found
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        DataNotFoundException notFound = assertThrows(DataNotFoundException.class,
            () -> userService.update(99L, new UserUpdateDTO(
                "Test",
                "test@email.com"
                ,"TestPass123@"
                )));
        assertEquals("User not found.", notFound.getMessage());
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateDuplicateEmail() {
        UserUpdateDTO userInfo = new UserUpdateDTO(user1.getName(), user2.getEmail(), user1.getPassword());
        
        // Mock: user2 email
        when(userRepository.findByEmail(user2.getEmail())).thenReturn(Optional.of(user2));
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

        DataValidationException exception = assertThrows(DataValidationException.class,
            () -> userService.update(user1.getId(), userInfo));
        assertTrue(exception.getErrors().contains("Email already registered by another user."));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateSameUserEmail() {
        UserUpdateDTO userInfo = new UserUpdateDTO("New name", user1.getEmail(), "Newpassword123@");
        
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

        UserResponseDTO updated = userService.update(user1.getId(), userInfo);

        assertNotNull(updated);
        assertEquals(user1.getId(), updated.getId());
        assertEquals("New name", updated.getName());
        assertEquals(user1.getEmail(), updated.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdatePartialFields() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

        UserUpdateDTO userInfo1 = new UserUpdateDTO("New name", null, null);
        UserResponseDTO updated1 = userService.update(user1.getId(), userInfo1);
        assertEquals("New name", updated1.getName());

        UserUpdateDTO userInfo2 = new UserUpdateDTO(null, "newemail@test.com", null);
        when(userRepository.findByEmail("newemail@test.com")).thenReturn(Optional.empty());
        UserResponseDTO updated2 = userService.update(user1.getId(), userInfo2);
        assertEquals("newemail@test.com", updated2.getEmail());

        UserUpdateDTO userInfo3 = new UserUpdateDTO(null, null, "Newpassword123@");
        UserResponseDTO updated3 = userService.update(user1.getId(), userInfo3);
        assertNotNull(updated3);

        verify(userRepository, times(3)).save(any(User.class));
    }

    @Test
    void testGetActorSuccess(){
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

        UserResponseDTO found = userService.getUser(user1.getId());

        assertNotNull(found);
        assertEquals(user1.getId(), found.getId());
        assertEquals(user1.getName(), found.getName());
        assertEquals(user1.getEmail(), found.getEmail());
        verify(userRepository, times(1)).findById(user1.getId());
    }

    @Test
    void testGetUserValidations(){
        DataValidationException exception = assertThrows(DataValidationException.class,
            () -> userService.getUser(null));
        assertEquals("User id must be informed.", exception.getMessage());

        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        DataNotFoundException notFound = assertThrows(DataNotFoundException.class,
            () -> userService.getUser(99L));
        assertEquals("User not found.", notFound.getMessage());
    }
    
    @Test
    void testGetAllUsersSuccess(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> actorPage = new PageImpl<>(Arrays.asList(user1, user2), pageable, 2);
        
        when(userRepository.findAll(pageable)).thenReturn(actorPage);

        Page<UserResponseDTO> found = userService.getAllUsers(pageable);
        
        assertNotNull(found);
        assertEquals(2, found.getContent().size());
        assertEquals(user1.getId(), found.getContent().get(0).getId());
        assertEquals(user2.getId(), found.getContent().get(1).getId());
        assertEquals(2, found.getTotalElements());
        assertEquals(1, found.getTotalPages());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllUsersEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        
        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        Exception exception = assertThrows(DataNotFoundException.class,
            () -> {
                userService.getAllUsers(pageable);
            });

        assertEquals("No users found in database.", exception.getMessage());
        verify(userRepository, times(1)).findAll(pageable);
    }
}
