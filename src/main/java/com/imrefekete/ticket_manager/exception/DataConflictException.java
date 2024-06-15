package com.imrefekete.ticket_manager.exception;

public class UserNameInUseException extends RuntimeException {
    public UserNameInUseException(String message) {
        super(message);
    }
}
