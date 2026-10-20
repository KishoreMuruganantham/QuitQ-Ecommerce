package com.springboot.quitq.service;

import com.springboot.quitq.dto.request.CustomerDto;
import com.springboot.quitq.dto.response.CustomerRespDto;
import com.springboot.quitq.enums.Role;
import com.springboot.quitq.exception.DuplicateEntryException;
import com.springboot.quitq.exception.ResourceNotFoundException;
import com.springboot.quitq.mapper.CustomerMapper;
import com.springboot.quitq.model.Customer;
import com.springboot.quitq.model.User;
import com.springboot.quitq.repository.CustomerRepository;
import com.springboot.quitq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerRespDto signup(CustomerDto customerDto) {

        Optional<User> existingUsername = userRepository.findByUsername(customerDto.username());
        if(existingUsername.isPresent())
            throw new DuplicateEntryException("Username already taken");

        Optional<User> existingEmail = userRepository.findByEmail(customerDto.email());
        if(existingEmail.isPresent())
            throw new DuplicateEntryException("Email already registered");

        User user = new User();
        user.setUsername(customerDto.username());
        user.setEmail(customerDto.email());
        user.setPassword(customerDto.password());
        user.setRole(Role.ROLE_CUSTOMER);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        Customer customer = CustomerMapper.convertDtoToEntity(customerDto, user);

        Customer saved = customerRepository.save(customer);
        System.out.println("record inserted...");
        return CustomerMapper.convertEntityToDto(saved);
    }

    public CustomerRespDto getById(long id) {
        Optional<Customer> optional = customerRepository.findById(id);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid customer id");

        return CustomerMapper.convertEntityToDto(optional.get());
    }

    public List<CustomerRespDto> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Customer> list = customerRepository.findAll(pageable).getContent();
        return list
                .stream()
                .map(CustomerMapper ::convertEntityToDto)
                .toList();
    }

    public void deleteById(long id) {

        getById(id);

        customerRepository.deleteById(id);
    }
}
