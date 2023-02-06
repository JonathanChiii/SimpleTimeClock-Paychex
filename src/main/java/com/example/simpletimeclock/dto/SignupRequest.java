package com.example.simpletimeclock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignupRequest {
    @NotBlank
    @Size(min = 10, max = 20)
    private String username;

    @NotBlank
    @Size(min = 10, max = 40)
    private String password;
}
