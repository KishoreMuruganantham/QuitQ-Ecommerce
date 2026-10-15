package com.springboot.quitq.service;

import com.springboot.quitq.dto.request.SellerDto;
import com.springboot.quitq.dto.response.SellerRespDto;
import com.springboot.quitq.enums.Role;
import com.springboot.quitq.exception.DuplicateEntryException;
import com.springboot.quitq.exception.ResourceNotFoundException;
import com.springboot.quitq.mapper.SellerMapper;
import com.springboot.quitq.model.Seller;
import com.springboot.quitq.model.User;
import com.springboot.quitq.repository.SellerRepository;
import com.springboot.quitq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    public SellerRespDto signup(SellerDto sellerDto) {

        Optional<User> existingUsername = userRepository.findByUsername(sellerDto.username());
        if(existingUsername.isPresent())
            throw new DuplicateEntryException("Username already taken");

        Optional<User> existingEmail = userRepository.findByEmail(sellerDto.email());
        if(existingEmail.isPresent())
            throw new DuplicateEntryException("Email already registered");

        User user = new User();
        user.setUsername(sellerDto.username());
        user.setEmail(sellerDto.email());
        user.setPassword(sellerDto.password());
        user.setRole(Role.ROLE_SELLER);

        userRepository.save(user);

        Seller seller = SellerMapper.convertDtoToEntity(sellerDto, user);

        Seller saved = sellerRepository.save(seller);
        System.out.println("record inserted...");
        return SellerMapper.convertEntityToDto(saved);
    }

    public SellerRespDto getById(long id) {
        Optional<Seller> optional = sellerRepository.findById(id);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid seller id");

        return SellerMapper.convertEntityToDto(optional.get());
    }

    public List<SellerRespDto> getAll() {
        List<Seller> list = sellerRepository.findAll();
        return list
                .stream()
                .map(SellerMapper ::convertEntityToDto)
                .toList();
    }

    public void deleteById(long id) {

        getById(id);

        sellerRepository.deleteById(id);
    }
}
