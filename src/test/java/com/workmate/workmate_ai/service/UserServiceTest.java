package com.workmate.workmate_ai.service;

import com.workmate.workmate_ai.entity.User;
import com.workmate.workmate_ai.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.workmate.workmate_ai.exception.InvalidCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.workmate.workmate_ai.exception.EmailAlreadyExistsException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Test
    void createUser_shouldEncodePasswordBeforeSaving() {

        // Arrange
        UserRepository userRepository = mock(UserRepository.class);
        UserService userService = new UserService(userRepository);

        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User savedUser = userService.createUser(user);

        // Assert
        assertNotEquals(
                "password123",
                savedUser.getPassword()
        );

        assertTrue(
                savedUser.getPassword().startsWith("$2")
        );

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUserInRepository = userCaptor.getValue();

        assertEquals(
                "test@example.com",
                savedUserInRepository.getEmail()
        );

        assertNotEquals(
                "password123",
                savedUserInRepository.getPassword()
        );
    }

    @Test
    void login_shouldReturnUser_whenCredentialsAreCorrect() {

        // Arrange
        UserRepository userRepository = mock(UserRepository.class);
        UserService userService = new UserService(userRepository);

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        String rawPassword = "password123";

        // Generate a real BCrypt password for the test user
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        user.setPassword(encoder.encode(rawPassword));

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        // Act
        User loggedInUser =
                userService.login("test@example.com", rawPassword);

        // Assert
        assertNotNull(loggedInUser);
        assertEquals("test@example.com", loggedInUser.getEmail());
        assertEquals("Test User", loggedInUser.getName());
    }

    @Test
    void login_shouldThrowException_whenPasswordIsWrong() {

        // Arrange
        UserRepository userRepository = mock(UserRepository.class);
        UserService userService = new UserService(userRepository);

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode("correctPassword"));

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        // Act + Assert
        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> userService.login(
                                "test@example.com",
                                "wrongPassword"
                        )
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );
    }

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {

        // Arrange
        UserRepository userRepository = mock(UserRepository.class);
        UserService userService = new UserService(userRepository);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(existingUser));

        User newUser = new User();
        newUser.setName("Another User");
        newUser.setEmail("test@example.com");
        newUser.setPassword("password123");

        // Act + Assert
        EmailAlreadyExistsException exception =
                assertThrows(
                        EmailAlreadyExistsException.class,
                        () -> userService.createUser(newUser)
                );

        assertEquals(
                "Email is already registered",
                exception.getMessage()
        );

        // Make sure the duplicate user was NOT saved
        verify(userRepository, never()).save(any(User.class));
    }
}