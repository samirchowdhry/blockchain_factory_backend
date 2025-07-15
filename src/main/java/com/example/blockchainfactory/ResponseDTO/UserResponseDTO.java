package com.example.blockchainfactory.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class UserResponseDTO {
    private Integer id;
    private String displayName;
    private String password;
    private String username;
    private String roles;
    private Instant createdAt;
    private Instant updatedAt;
}
