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

        Optional<User> optional = userService.findByUsername(loginDto.username());
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid credentials");

        return UserMapper.convertEntityToDto(optional.get());
    }
}
