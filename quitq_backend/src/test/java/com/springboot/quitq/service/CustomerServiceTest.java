package com.springboot.quitq.service;

import com.springboot.quitq.dto.request.CustomerDto;
import com.springboot.quitq.dto.response.CustomerRespDto;
import com.springboot.quitq.exception.DuplicateEntryException;
import com.springboot.quitq.model.Customer;
import com.springboot.quitq.model.User;
import com.springboot.quitq.repository.CustomerRepository;
import com.springboot.quitq.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void signupTest() {
        CustomerDto customerDto = new CustomerDto("John Doe", "Male", "9876543210", "john", "john@mail.com", "raw@123");

        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("raw@123")).thenReturn("$2a$10$abcdefghijklmnopqrstuv");
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerRespDto result = customerService.signup(customerDto);

        assertEquals("john", result.username());
        assertEquals("john@mail.com", result.email());

        verify(passwordEncoder, times(1)).encode("raw@123");
        verify(userRepository, times(1)).save(argThat((User user) -> user.getPassword().startsWith("$2a$")));
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(userRepository, times(1)).findByUsername("john");
        verify(userRepository, times(1)).findByEmail("john@mail.com");

        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(customerRepository);
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    public void signupTest_Duplicate() {
        CustomerDto customerDto = new CustomerDto("John Doe", "Male", "9876543210", "john", "john@mail.com", "raw@123");

        User existingUser = new User();
        existingUser.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(existingUser));

        assertThrows(DuplicateEntryException.class, () -> customerService.signup(customerDto));

        verify(userRepository, times(1)).findByUsername("john");
        verify(userRepository, never()).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(customerRepository);
        verifyNoInteractions(passwordEncoder);
    }
}
