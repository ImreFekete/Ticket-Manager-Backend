package com.imrefekete.ticket_manager.service;

import com.imrefekete.ticket_manager.exception.DataConflictException;
import com.imrefekete.ticket_manager.model.entity.User;
import com.imrefekete.ticket_manager.model.request.AuthRequest;
import com.imrefekete.ticket_manager.model.request.RegisterRequest;
import com.imrefekete.ticket_manager.model.response.AuthResponse;
import com.imrefekete.ticket_manager.model.response.ErrorResponse;
import com.imrefekete.ticket_manager.repository.UserRepository;
import com.imrefekete.ticket_manager.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUsernameAlreadyInUse() {
        RegisterRequest request = new RegisterRequest();
        request.setUserName("testuser");
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(new User()));

        DataConflictException exception = assertThrows(DataConflictException.class, () -> {
            authenticationService.register(request);
        });

        assertEquals("Username is already in use", exception.getMessage());
    }

    @Test
    void testRegisterEmailAlreadyInUse() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        when(userRepository.findUserByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        DataConflictException exception = assertThrows(DataConflictException.class, () -> {
            authenticationService.register(request);
        });

        assertEquals("Email is already in use", exception.getMessage());
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setUserName("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findUserByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(1);
            return user;
        });

        long userId = authenticationService.register(request);
        assertEquals(1, userId);
    }

    @Test
    void testAuthenticateSuccess() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");

        UserDetails userDetails = new org.springframework.security.core.userdetails.User("testuser", "password", Collections.emptyList());
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("testToken");

        ResponseEntity<?> response = authenticationService.authenticate(authRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertNotNull(authResponse);
        assertEquals("testuser", authResponse.getName());
        assertEquals("testToken", authResponse.getToken());
    }

    @Test
    void testAuthenticateFailure() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        ResponseEntity<?> response = authenticationService.authenticate(authRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Invalid username or password", errorResponse.getMessage());
    }
}