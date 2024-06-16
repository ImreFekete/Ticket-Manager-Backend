package com.imrefekete.ticket_manager.exception;

import com.imrefekete.ticket_manager.model.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<ErrorResponse> userNameInUseException(DataConflictException e) {
        HttpStatus statusCode = HttpStatus.CONFLICT;
        ErrorResponse errorResponse = new ErrorResponse(statusCode, e.getMessage());
        return new ResponseEntity<>(errorResponse, statusCode);
    }
}
