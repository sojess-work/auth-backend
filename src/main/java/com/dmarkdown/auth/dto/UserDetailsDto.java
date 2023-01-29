package com.dmarkdown.auth.dto;

import lombok.Data;

@Data
public class UserDetailsDto {

    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
}
