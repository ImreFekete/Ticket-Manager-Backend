package com.imrefekete.ticket_manager.controller;

import com.imrefekete.ticket_manager.model.request.AuthRequest;
import com.imrefekete.ticket_manager.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationService authService;

    public AuthController(AuthenticationService authService) {
        this.authService = authService;
    }

    @ResponseBody
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        return authService.authenticate(authRequest);
    }
}