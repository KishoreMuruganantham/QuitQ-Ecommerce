package com.springboot.quitq.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CustomerDto( //add validation annotations
        @NotBlank(message = "Name is required")
        String name,

        String gender, //customer can skip this

        @NotBlank(message = "Contact number is required")
        String contactNumber,

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
}
