package com.springboot.quitq.controller;

import com.springboot.quitq.dto.request.UserDto;
import com.springboot.quitq.dto.response.UserRespDto;
import com.springboot.quitq.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/user/add")
    public void insertUser(@Valid @RequestBody UserDto userDto){
        userService.insert(userDto);
    }

    @GetMapping("/api/user/{id}")
    public UserRespDto getById(@PathVariable long id){
        return userService.getById(id);
    }

    @GetMapping("/api/user/all")
    public List<UserRespDto> getAll(){
        return userService.getAll();
    }

    @DeleteMapping("/api/user/{id}")
    public void deleteById(@PathVariable long id){
        userService.deleteById(id);
    }
}
