package com.springboot.quitq.controller;

import com.springboot.quitq.config.JwtFilter;
import com.springboot.quitq.config.SecurityConfig;
import com.springboot.quitq.config.VersioningConfig;
import com.springboot.quitq.dto.response.DetailsDto;
import com.springboot.quitq.enums.Role;
import com.springboot.quitq.service.MyUserDetailsService;
import com.springboot.quitq.service.UserService;
import com.springboot.quitq.utility.JwtUtility;
import com.springboot.quitq.utility.TokenDenylist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, JwtFilter.class, JwtUtility.class, TokenDenylist.class, VersioningConfig.class})
public class AuthLogoutTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtility jwtUtility;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @Test
    public void loginTest() throws Exception {
        String username = "loginuser";
        String token = jwtUtility.generateToken(username);

        when(myUserDetailsService.loadUserByUsername(username)).thenReturn(
                new User(username, "$2a$10$abcdefghijklmnopqrstuv", AuthorityUtils.createAuthorityList("ROLE_CUSTOMER")));

        mockMvc.perform(get("/api/auth/login").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        verify(myUserDetailsService, times(1)).loadUserByUsername(username);
        verifyNoInteractions(userService);
        verifyNoMoreInteractions(myUserDetailsService);
    }

    @Test
    public void logoutTest_TokenDeniedAfterLogout() throws Exception {
        String username = "logoutuser";
        String token = jwtUtility.generateToken(username);

        when(myUserDetailsService.loadUserByUsername(username)).thenReturn(
                new User(username, "$2a$10$abcdefghijklmnopqrstuv", AuthorityUtils.createAuthorityList("ROLE_CUSTOMER")));
        when(userService.getUserDetails(username)).thenReturn(
                new DetailsDto(1L, username, true, Role.ROLE_CUSTOMER));

        mockMvc.perform(get("/api/auth/user-details").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username));

        mockMvc.perform(post("/api/auth/logout").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));

        mockMvc.perform(get("/api/auth/user-details").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());

        verify(userService, times(1)).getUserDetails(username);
        verify(myUserDetailsService, times(3)).loadUserByUsername(username);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(myUserDetailsService);
    }

    @Test
    public void logoutTest_Unauthenticated() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
        verifyNoInteractions(myUserDetailsService);
    }
}
