package com.imrefekete.ticket_manager.model.response;

import lombok.*;

@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String name;
    private String token;
}
