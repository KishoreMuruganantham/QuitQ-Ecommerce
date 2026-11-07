package com.springboot.quitq.controller;

import com.springboot.quitq.dto.response.DetailsDto;
import com.springboot.quitq.dto.response.MessageRespDto;
import com.springboot.quitq.dto.response.TokenDto;
import com.springboot.quitq.service.UserService;
import com.springboot.quitq.utility.JwtUtility;
import com.springboot.quitq.utility.TokenDenylist;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtility jwtUtility;
    private final UserService userService;
    private final TokenDenylist tokenDenylist;

    @GetMapping("/api/auth/login")
    public TokenDto login(Principal principal){
        String loggedInUsername = principal.getName();
        String token = jwtUtility.generateToken(loggedInUsername);
        return new TokenDto(token,
                jwtUtility.extractExpiration(token),
                LocalDateTime.now().toString());
    }

    @GetMapping("/api/auth/user-details")
    public DetailsDto getUserDetails(Principal principal){
        String loggedInUsername = principal.getName();
        return userService.getUserDetails(loggedInUsername);
    }

    @PostMapping("/api/auth/logout")
    public MessageRespDto logout(@RequestHeader(value = "Authorization", required = false) String authorizationHeader){
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7).trim();
            if (!token.isEmpty()) {
                tokenDenylist.deny(token);
            }
        }
        return new MessageRespDto("Logged out successfully");
    }
}
