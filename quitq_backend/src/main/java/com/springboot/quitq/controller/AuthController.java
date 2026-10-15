package com.springboot.quitq.controller;

import com.springboot.quitq.dto.request.LoginDto;
import com.springboot.quitq.dto.response.UserRespDto;
import com.springboot.quitq.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/auth/login")
    public UserRespDto login(@Valid @RequestBody LoginDto loginDto){
        return authService.login(loginDto);
    }
}
