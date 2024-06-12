package com.imrefekete.ticket_manager.model.request;

import lombok.*;

import java.util.Date;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String email;
    private String password;
}
