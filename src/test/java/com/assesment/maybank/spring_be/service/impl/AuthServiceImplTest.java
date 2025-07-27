package com.assesment.maybank.spring_be.service.impl;

import com.assesment.maybank.spring_be.dto.LoginDto;
import com.assesment.maybank.spring_be.dto.LoginRequest;
import com.assesment.maybank.spring_be.dto.RegisterRequest;
import com.assesment.maybank.spring_be.entity.User;
import com.assesment.maybank.spring_be.exception.LoginFailedException;
import com.assesment.maybank.spring_be.exception.UserNotFoundException;
import com.assesment.maybank.spring_be.service.UserService;
import com.assesment.maybank.spring_be.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private User mockUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("testuser");
        mockUser.setPassword("testpassword");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("testpassword");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("newpassword");
    }

    @Test
    void login_ShouldReturnLoginDto_WhenCredentialsAreValid() {

        String expectedToken = "jwt-token-123";

        when(userService.getUserEntityByUsername("testuser")).thenReturn(mockUser);
        when(jwtUtil.generateToken(userId)).thenReturn(expectedToken);

        LoginDto result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals(userId.toString(), result.id());
        assertEquals("testuser", result.username());
        assertEquals(expectedToken, result.token());

        verify(userService).getUserEntityByUsername("testuser");
        verify(jwtUtil).generateToken(userId);
    }

    @Test
    void login_ShouldThrowLoginFailedException_WhenUserNotFound() {

        when(userService.getUserEntityByUsername("testuser"))
                .thenThrow(new UserNotFoundException("User not found"));

        assertThrows(LoginFailedException.class,
                () -> authService.login(loginRequest));

        verify(userService).getUserEntityByUsername("testuser");
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void login_ShouldThrowLoginFailedException_WhenPasswordIsIncorrect() {

        loginRequest.setPassword("wrongpassword");

        when(userService.getUserEntityByUsername("testuser")).thenReturn(mockUser);

        assertThrows(LoginFailedException.class,
                () -> authService.login(loginRequest));

        verify(userService).getUserEntityByUsername("testuser");
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void login_ShouldThrowLoginFailedException_WhenPasswordIsNull() {

        loginRequest.setPassword(null);

        when(userService.getUserEntityByUsername("testuser")).thenReturn(mockUser);

        assertThrows(LoginFailedException.class,
                () -> authService.login(loginRequest));

        verify(userService).getUserEntityByUsername("testuser");
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void login_ShouldHandleDifferentUsernames() {

        String differentUsername = "anotheruser";
        UUID differentUserId = UUID.randomUUID();
        String expectedToken = "different-jwt-token";

        User differentUser = new User();
        differentUser.setId(differentUserId);
        differentUser.setUsername(differentUsername);
        differentUser.setPassword("anotherpassword");

        LoginRequest differentLoginRequest = new LoginRequest();
        differentLoginRequest.setUsername(differentUsername);
        differentLoginRequest.setPassword("anotherpassword");

        when(userService.getUserEntityByUsername(differentUsername)).thenReturn(differentUser);
        when(jwtUtil.generateToken(differentUserId)).thenReturn(expectedToken);

        LoginDto result = authService.login(differentLoginRequest);

        assertNotNull(result);
        assertEquals(differentUserId.toString(), result.id());
        assertEquals(differentUsername, result.username());
        assertEquals(expectedToken, result.token());

        verify(userService).getUserEntityByUsername(differentUsername);
        verify(jwtUtil).generateToken(differentUserId);
    }

    @Test
    void login_ShouldHandleEmptyPassword() {

        loginRequest.setPassword("");
        mockUser.setPassword("testpassword");

        when(userService.getUserEntityByUsername("testuser")).thenReturn(mockUser);

        assertThrows(LoginFailedException.class,
                () -> authService.login(loginRequest));

        verify(userService).getUserEntityByUsername("testuser");
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void login_ShouldHandleSpecialCharactersInPassword() {

        String specialPassword = "p@ssw0rd!@#$%";
        mockUser.setPassword(specialPassword);
        loginRequest.setPassword(specialPassword);
        String expectedToken = "special-jwt-token";

        when(userService.getUserEntityByUsername("testuser")).thenReturn(mockUser);
        when(jwtUtil.generateToken(userId)).thenReturn(expectedToken);

        LoginDto result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals(userId.toString(), result.id());
        assertEquals("testuser", result.username());
        assertEquals(expectedToken, result.token());

        verify(userService).getUserEntityByUsername("testuser");
        verify(jwtUtil).generateToken(userId);
    }

    @Test
    void login_ShouldExtractCorrectUserDetails() {

        UUID specificUserId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        String specificUsername = "specificuser";
        String specificPassword = "specificpassword";
        String expectedToken = "specific-jwt-token";

        User specificUser = new User();
        specificUser.setId(specificUserId);
        specificUser.setUsername(specificUsername);
        specificUser.setPassword(specificPassword);

        LoginRequest specificLoginRequest = new LoginRequest();
        specificLoginRequest.setUsername(specificUsername);
        specificLoginRequest.setPassword(specificPassword);

        when(userService.getUserEntityByUsername(specificUsername)).thenReturn(specificUser);
        when(jwtUtil.generateToken(specificUserId)).thenReturn(expectedToken);

        LoginDto result = authService.login(specificLoginRequest);

        assertNotNull(result);
        assertEquals("550e8400-e29b-41d4-a716-446655440000", result.id());
        assertEquals(specificUsername, result.username());
        assertEquals(expectedToken, result.token());

        verify(userService).getUserEntityByUsername(specificUsername);
        verify(jwtUtil).generateToken(specificUserId);
    }
}