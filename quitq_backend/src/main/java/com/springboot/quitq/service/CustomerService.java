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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerRespDto signup(CustomerDto customerDto) {
        // Focus on validation -- second
        // Step 1: check if username and email are already taken
        Optional<User> existingUsername = userRepository.findByUsername(customerDto.username());
        if(existingUsername.isPresent())
            throw new DuplicateEntryException("Username already taken");

        Optional<User> existingEmail = userRepository.findByEmail(customerDto.email());
        if(existingEmail.isPresent())
            throw new DuplicateEntryException("Email already registered");

        // Focus on functionality --- First
        // Step 2: build the User row with customer role
        User user = new User();
        user.setUsername(customerDto.username());
        user.setEmail(customerDto.email());
        user.setPassword(customerDto.password()); // TODO: bcrypt when spring security module starts
        user.setRole(Role.ROLE_CUSTOMER);

        // Step 3: save the user first, the profile needs the generated id
        userRepository.save(user);

        // Step 4: build the profile entity and attach the user
        Customer customer = CustomerMapper.convertDtoToEntity(customerDto, user);

        // Step 5: save the profile
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

    public List<CustomerRespDto> getAll() {
        List<Customer> list = customerRepository.findAll();
        return list
                .stream()
                .map(CustomerMapper ::convertEntityToDto)
                .toList();
    }

    public void deleteById(long id) {
        // Step 1: Check if id is valid
        getById(id);
        // Step 2: delete the record
        customerRepository.deleteById(id);
    }
}
