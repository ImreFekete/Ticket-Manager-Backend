package com.imrefekete.ticket_manager.service;

import com.imrefekete.ticket_manager.request.AuthRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {

    ResponseEntity<?> authenticate(AuthRequest authRequest);

}
