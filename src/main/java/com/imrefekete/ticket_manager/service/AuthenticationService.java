package com.imrefekete.ticket_manager.service;

import com.imrefekete.ticket_manager.model.request.AuthRequest;
import com.imrefekete.ticket_manager.model.request.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {

    ResponseEntity<?> authenticate(AuthRequest authRequest);
    ResponseEntity<?> register(RegisterRequest registerRequest);
}
