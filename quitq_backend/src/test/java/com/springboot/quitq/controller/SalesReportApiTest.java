package com.springboot.quitq.controller;

import com.springboot.quitq.config.JwtFilter;
import com.springboot.quitq.config.SecurityConfig;
import com.springboot.quitq.config.VersioningConfig;
import com.springboot.quitq.dto.response.SalesReportRespDto;
import com.springboot.quitq.service.MyUserDetailsService;
import com.springboot.quitq.service.OrderService;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class)
@Import({SecurityConfig.class, JwtFilter.class, JwtUtility.class, TokenDenylist.class, VersioningConfig.class})
public class SalesReportApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtility jwtUtility;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @Test
    public void getSalesReportTest_Seller() throws Exception {
        String username = "sellersales";
        String token = jwtUtility.generateToken(username);

        when(myUserDetailsService.loadUserByUsername(username)).thenReturn(
                new User(username, "$2a$10$abcdefghijklmnopqrstuv", AuthorityUtils.createAuthorityList("ROLE_SELLER")));
        when(orderService.getSalesReport(5L, username)).thenReturn(List.of(
                new SalesReportRespDto("Laptop", 3L, 150000.0),
                new SalesReportRespDto("Phone", 5L, 100000.0)));

        mockMvc.perform(get("/api/order/seller/5/sales-report").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Laptop"))
                .andExpect(jsonPath("$[1].productName").value("Phone"));

        verify(orderService, times(1)).getSalesReport(5L, username);
        verify(myUserDetailsService, times(1)).loadUserByUsername(username);
        verifyNoMoreInteractions(orderService);
        verifyNoMoreInteractions(myUserDetailsService);
    }

    @Test
    public void getSalesReportTest_NoToken() throws Exception {
        mockMvc.perform(get("/api/order/seller/5/sales-report"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(orderService);
        verifyNoInteractions(myUserDetailsService);
    }

    @Test
    public void getSalesReportTest_CustomerForbidden() throws Exception {
        String username = "customersales";
        String token = jwtUtility.generateToken(username);

        when(myUserDetailsService.loadUserByUsername(username)).thenReturn(
                new User(username, "$2a$10$abcdefghijklmnopqrstuv", AuthorityUtils.createAuthorityList("ROLE_CUSTOMER")));

        mockMvc.perform(get("/api/order/seller/5/sales-report").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        verify(myUserDetailsService, times(1)).loadUserByUsername(username);
        verifyNoInteractions(orderService);
        verifyNoMoreInteractions(myUserDetailsService);
    }
}
