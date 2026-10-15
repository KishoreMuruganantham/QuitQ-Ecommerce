package com.springboot.quitq.mapper;

import com.springboot.quitq.dto.request.UserDto;
import com.springboot.quitq.dto.response.UserRespDto;
import com.springboot.quitq.model.User;

public class UserMapper {

    public static User convertDtoToEntity(UserDto dto){
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(dto.role());
        return user;
    }

    public static UserRespDto convertEntityToDto(User user){
        return
                new UserRespDto (
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole()
                );
    }
}
