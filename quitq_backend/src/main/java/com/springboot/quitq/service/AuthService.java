package com.springboot.quitq.service;

import com.springboot.quitq.dto.request.LoginDto;
import com.springboot.quitq.dto.response.UserRespDto;
import com.springboot.quitq.exception.ResourceNotFoundException;
import com.springboot.quitq.mapper.UserMapper;
import com.springboot.quitq.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    public UserRespDto login(LoginDto loginDto) {
        // Step 1: look up the user by username only
        // TODO: compare the password too once bcrypt + spring security module starts
        Optional<User> optional = userService.findByUsername(loginDto.username());
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid credentials");

        // Step 2: no jwt, no token -- just reply with the logged in profile
        return UserMapper.convertEntityToDto(optional.get());
    }
}
