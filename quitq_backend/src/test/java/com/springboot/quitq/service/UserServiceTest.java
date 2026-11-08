package com.springboot.quitq.service;

import com.springboot.quitq.dto.response.DetailsDto;
import com.springboot.quitq.dto.response.MessageRespDto;
import com.springboot.quitq.dto.response.ResetLinkRespDto;
import com.springboot.quitq.enums.Role;
import com.springboot.quitq.exception.InactiveUserException;
import com.springboot.quitq.exception.ResourceNotFoundException;
import com.springboot.quitq.model.Customer;
import com.springboot.quitq.model.Seller;
import com.springboot.quitq.model.User;
import com.springboot.quitq.repository.CustomerRepository;
import com.springboot.quitq.repository.SellerRepository;
import com.springboot.quitq.repository.UserRepository;
import com.springboot.quitq.utility.PasswordResetTokenStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

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

    @Spy
    private PasswordResetTokenStore passwordResetTokenStore = new PasswordResetTokenStore();

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

    @Test
    public void forgotPasswordTest_ExistingUsername() {
        User user = new User();
        user.setUsername("john_doe");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        ResetLinkRespDto result = userService.forgotPassword("john_doe");

        assertNotNull(result.resetToken());
        assertEquals("If the account exists, a password reset link has been generated", result.message());

        verify(userRepository, times(1)).findByUsername("john_doe");
        verify(userRepository, never()).findByEmail(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void forgotPasswordTest_ExistingEmail() {
        User user = new User();
        user.setUsername("john_doe");
        when(userRepository.findByUsername("john@doe.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@doe.com")).thenReturn(Optional.of(user));

        ResetLinkRespDto result = userService.forgotPassword("john@doe.com");

        assertNotNull(result.resetToken());
        verify(userRepository, times(1)).findByUsername("john@doe.com");
        verify(userRepository, times(1)).findByEmail("john@doe.com");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void forgotPasswordTest_UnknownAccountNoToken() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("ghost")).thenReturn(Optional.empty());

        ResetLinkRespDto result = userService.forgotPassword("ghost");

        assertNull(result.resetToken());
        assertEquals("If the account exists, a password reset link has been generated", result.message());

        verify(userRepository, times(1)).findByUsername("ghost");
        verify(userRepository, times(1)).findByEmail("ghost");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void resetPasswordTest_ValidTokenSingleUse() {
        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("old-encoded");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newSecret@123")).thenReturn("new-encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResetLinkRespDto link = userService.forgotPassword("john_doe");
        assertNotNull(link.resetToken());

        MessageRespDto result = userService.resetPassword(link.resetToken(), "newSecret@123");

        assertEquals("Password reset successfully", result.message());
        assertEquals("new-encoded", user.getPassword());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.resetPassword(link.resetToken(), "anotherSecret@123"));

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("newSecret@123");
        verify(userRepository, times(2)).findByUsername("john_doe");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void resetPasswordTest_InvalidToken() {
        assertThrows(ResourceNotFoundException.class,
                () -> userService.resetPassword("bogus-token", "whatever@123"));

        verifyNoInteractions(userRepository);
    }
}
