package com.springboot.quitq.controller;

import com.springboot.quitq.config.JwtFilter;
import com.springboot.quitq.config.SecurityConfig;
import com.springboot.quitq.config.VersioningConfig;
import com.springboot.quitq.dto.response.MessageRespDto;
import com.springboot.quitq.dto.response.ResetLinkRespDto;
import com.springboot.quitq.exception.ResourceNotFoundException;
import com.springboot.quitq.service.MyUserDetailsService;
import com.springboot.quitq.service.UserService;
import com.springboot.quitq.utility.JwtUtility;
import com.springboot.quitq.utility.TokenDenylist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, JwtFilter.class, JwtUtility.class, TokenDenylist.class, VersioningConfig.class})
public class PasswordResetApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @Test
    public void forgotPasswordTest_ReturnsMessageAndToken() throws Exception {
        String message = "If the account exists, a password reset link has been generated";
        when(userService.forgotPassword("john_doe")).thenReturn(new ResetLinkRespDto(message, "abc123token"));

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john_doe\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.resetToken").value("abc123token"));

        verify(userService, times(1)).forgotPassword("john_doe");
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void forgotPasswordTest_UnknownAccountSameMessageNoToken() throws Exception {
        String message = "If the account exists, a password reset link has been generated";
        when(userService.forgotPassword("ghost")).thenReturn(new ResetLinkRespDto(message, null));

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ghost\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.resetToken").value(nullValue()));

        verify(userService, times(1)).forgotPassword("ghost");
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void forgotPasswordTest_BlankUsernameRejected() throws Exception {
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"  \"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    public void resetPasswordTest_Success() throws Exception {
        when(userService.resetPassword("validtoken", "newSecret@123"))
                .thenReturn(new MessageRespDto("Password reset successfully"));

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"validtoken\",\"newPassword\":\"newSecret@123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successfully"));

        verify(userService, times(1)).resetPassword("validtoken", "newSecret@123");
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void resetPasswordTest_InvalidTokenRejected() throws Exception {
        when(userService.resetPassword("bogus", "newSecret@123"))
                .thenThrow(new ResourceNotFoundException("Invalid or expired reset token"));

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"bogus\",\"newPassword\":\"newSecret@123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid or expired reset token"));

        verify(userService, times(1)).resetPassword("bogus", "newSecret@123");
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void resetPasswordTest_BlankFieldsRejected() throws Exception {
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"\",\"newPassword\":\"\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }
}
