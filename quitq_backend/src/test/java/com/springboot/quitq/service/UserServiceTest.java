package com.springboot.quitq.service;

import com.springboot.quitq.dto.response.DetailsDto;
import com.springboot.quitq.enums.Role;
import com.springboot.quitq.exception.InactiveUserException;
import com.springboot.quitq.model.Customer;
import com.springboot.quitq.model.Seller;
import com.springboot.quitq.repository.CustomerRepository;
import com.springboot.quitq.repository.SellerRepository;
import com.springboot.quitq.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void getUserDetailsTest_Seller() {
        long userId = 2L;
        when(userRepository.getUserDetails("techstore"))
                .thenReturn(new DetailsDto(userId, "techstore", true, Role.ROLE_SELLER));

        Seller seller = new Seller();
        seller.setId(1L);
        seller.setBusinessName("TechStore");
        when(sellerRepository.getByUsername("techstore")).thenReturn(seller);

        DetailsDto result = userService.getUserDetails("techstore");

        assertEquals(1L, result.id().longValue());
        assertNotEquals(userId, result.id().longValue());
        assertEquals("techstore", result.username());
        assertEquals(Role.ROLE_SELLER, result.role());
        assertTrue(result.isActive());

        verify(userRepository, times(1)).getUserDetails("techstore");
        verify(sellerRepository, times(1)).getByUsername("techstore");
        verifyNoInteractions(customerRepository);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(sellerRepository);
    }

    @Test
    public void getUserDetailsTest_Customer() {
        when(userRepository.getUserDetails("john_doe"))
                .thenReturn(new DetailsDto(4L, "john_doe", true, Role.ROLE_CUSTOMER));

        Customer customer = new Customer();
        customer.setId(9L);
        when(customerRepository.getByUsername("john_doe")).thenReturn(customer);

        DetailsDto result = userService.getUserDetails("john_doe");

        assertEquals(9L, result.id().longValue());
        assertEquals("john_doe", result.username());
        assertEquals(Role.ROLE_CUSTOMER, result.role());

        verify(userRepository, times(1)).getUserDetails("john_doe");
        verify(customerRepository, times(1)).getByUsername("john_doe");
        verifyNoInteractions(sellerRepository);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    public void getUserDetailsTest_Admin() {
        when(userRepository.getUserDetails("admin"))
                .thenReturn(new DetailsDto(1L, "admin", true, Role.ROLE_ADMIN));

        DetailsDto result = userService.getUserDetails("admin");

        assertEquals(1L, result.id().longValue());
        assertEquals(Role.ROLE_ADMIN, result.role());

        verify(userRepository, times(1)).getUserDetails("admin");
        verifyNoInteractions(sellerRepository);
        verifyNoInteractions(customerRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void getUserDetailsTest_Inactive() {
        when(userRepository.getUserDetails("disabled"))
                .thenReturn(new DetailsDto(5L, "disabled", false, Role.ROLE_CUSTOMER));

        assertThrows(InactiveUserException.class, () -> userService.getUserDetails("disabled"));

        verify(userRepository, times(1)).getUserDetails("disabled");
        verifyNoInteractions(sellerRepository);
        verifyNoInteractions(customerRepository);
        verifyNoMoreInteractions(userRepository);
    }
}
