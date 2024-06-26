package com.imrefekete.ticket_manager.service;

import com.imrefekete.ticket_manager.enums.Role;
import com.imrefekete.ticket_manager.exception.DataConflictException;
import com.imrefekete.ticket_manager.model.entity.User;
import com.imrefekete.ticket_manager.model.request.AuthRequest;
import com.imrefekete.ticket_manager.model.request.RegisterRequest;
import com.imrefekete.ticket_manager.model.response.AuthResponse;
import com.imrefekete.ticket_manager.repository.UserRepository;
import com.imrefekete.ticket_manager.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testRegister_success() throws DataConflictException {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserName("testUser");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setDateOfBirth(LocalDate.of(2000, 1, 1));
        registerRequest.setPassword("password");
        registerRequest.setEmail("test@example.com");

        User savedUser = User.builder()
                .userId(1)
                .username(registerRequest.getUserName())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .dateOfBirth(registerRequest.getDateOfBirth())
                .password("encodedPassword")
                .email(registerRequest.getEmail())
                .role(Role.USER)
                .build();

        when(userRepository.findUserByUsername(registerRequest.getUserName())).thenReturn(Optional.empty());
        when(userRepository.findUserByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        long userId = authenticationService.register(registerRequest);

        assertEquals(1L, userId);
        verify(userRepository).findUserByUsername(registerRequest.getUserName());
        verify(userRepository).findUserByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_usernameConflict() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserName("testUser");
        registerRequest.setEmail("test@example.com");

        when(userRepository.findUserByUsername(registerRequest.getUserName())).thenReturn(Optional.of(new User()));

        DataConflictException exception = assertThrows(DataConflictException.class, () -> {
            authenticationService.register(registerRequest);
        });

        assertEquals("Username is already in use", exception.getMessage());
        verify(userRepository).findUserByUsername(registerRequest.getUserName());
        verify(userRepository, never()).findUserByEmail(registerRequest.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_emailConflict() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserName("testUser");
        registerRequest.setEmail("test@example.com");

        when(userRepository.findUserByUsername(registerRequest.getUserName())).thenReturn(Optional.empty());
        when(userRepository.findUserByEmail(registerRequest.getEmail())).thenReturn(Optional.of(new User()));

        DataConflictException exception = assertThrows(DataConflictException.class, () -> {
            authenticationService.register(registerRequest);
        });

        assertEquals("Email is already in use", exception.getMessage());
        verify(userRepository).findUserByUsername(registerRequest.getUserName());
        verify(userRepository).findUserByEmail(registerRequest.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAuthenticate_success() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testUser");
        authRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        user.setUserId(1);
        user.setUsername("testUser");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("token");

        ResponseEntity<?> response = authenticationService.authenticate(authRequest);

        assertNotNull(response);
        assertTrue(response.getBody() instanceof AuthResponse);
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertEquals(1L, authResponse.getUserId());
        assertEquals("token", authResponse.getToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findUserByUsername("testUser");
        verify(userDetailsService).loadUserByUsername("testUser");
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void testAuthenticate_badCredentials() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testUser");
        authRequest.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authenticationService.authenticate(authRequest);
        });

        assertEquals("Bad credentials", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findUserByUsername(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }
}
