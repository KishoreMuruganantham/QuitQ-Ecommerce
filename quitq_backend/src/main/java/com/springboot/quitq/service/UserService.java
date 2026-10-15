package com.springboot.quitq.service;

import com.springboot.quitq.dto.request.UserDto;
import com.springboot.quitq.dto.response.UserRespDto;
import com.springboot.quitq.exception.DuplicateEntryException;
import com.springboot.quitq.exception.ResourceNotFoundException;
import com.springboot.quitq.mapper.UserMapper;
import com.springboot.quitq.model.User;
import com.springboot.quitq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void insert(UserDto userDto) {

        Optional<User> existingUsername = userRepository.findByUsername(userDto.username());
        if(existingUsername.isPresent())
            throw new DuplicateEntryException("Username already taken");

        Optional<User> existingEmail = userRepository.findByEmail(userDto.email());
        if(existingEmail.isPresent())
            throw new DuplicateEntryException("Email already registered");

        User user = UserMapper.convertDtoToEntity(userDto);

        userRepository.save(user);
        System.out.println("record inserted...");
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserRespDto getById(long id) {
        Optional<User> optional = userRepository.findById(id);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid user id");

        return UserMapper.convertEntityToDto(optional.get());
    }

    public List<UserRespDto> getAll() {
        List<User> list = userRepository.findAll();
        return list
                .stream()
                .map(UserMapper ::convertEntityToDto)
                .toList();
    }

    public void deleteById(long id) {

        getById(id);

        userRepository.deleteById(id);
    }
}
