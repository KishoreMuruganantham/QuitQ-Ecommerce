package com.springboot.quitq.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.quitq.config.JwtFilter;
import com.springboot.quitq.config.SecurityConfig;
import com.springboot.quitq.config.VersioningConfig;
import com.springboot.quitq.dto.request.ProductDto;
import com.springboot.quitq.dto.response.ProductInfoDto;
import com.springboot.quitq.dto.response.ProductRespDto;
import com.springboot.quitq.enums.ProductStatus;
import com.springboot.quitq.exception.OwnershipException;
import com.springboot.quitq.service.MyUserDetailsService;
import com.springboot.quitq.service.ProductService;
import com.springboot.quitq.utility.JwtUtility;
import com.springboot.quitq.utility.TokenDenylist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
@Import({SecurityConfig.class, JwtFilter.class, JwtUtility.class, TokenDenylist.class, VersioningConfig.class})
public class ProductApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtility jwtUtility;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @Test
    public void updateProductTest_Seller() throws Exception {
        String username = "sellerput";
        String token = jwtUtility.generateToken(username);
        ProductDto productDto = new ProductDto("Gaming Laptop", "Dell", "Updated", 75000.0, 5, "laptop.png", 1L);
        ProductRespDto productRespDto = new ProductRespDto(100L, "Gaming Laptop", "Dell", 75000.0, 5, ProductStatus.IN_STOCK, "laptop.png");
        String body = new ObjectMapper().writeValueAsString(productDto);

        when(myUserDetailsService.loadUserByUsername(username)).thenReturn(
                new User(username, "$2a$10$abcdefghijklmnopqrstuv", AuthorityUtils.createAuthorityList("ROLE_SELLER")));
        when(productService.update(eq(100L), any(ProductDto.class), eq(username))).thenReturn(productRespDto);

        mockMvc.perform(put("/api/product/100")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gaming Laptop"))
                .andExpect(jsonPath("$.price").value(75000.0));

        verify(productService, times(1)).update(eq(100L), argThat((ProductDto dto) ->
                dto.name().equals("Gaming Laptop") && dto.stockQty().equals(5)), eq(username));
        verify(myUserDetailsService, times(1)).loadUserByUsername(username);
        verifyNoMoreInteractions(productService);
        verifyNoMoreInteractions(myUserDetailsService);
    }

    @Test
    public void updateProductTest_NoToken() throws Exception {
        ProductDto productDto = new ProductDto("Gaming Laptop", "Dell", "Updated", 75000.0, 5, "laptop.png", 1L);
        String body = new ObjectMapper().writeValueAsString(productDto);

        mockMvc.perform(put("/api/product/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(productService);
        verifyNoInteractions(myUserDetailsService);
    }

    @Test
    public void updateProductTest_CustomerForbidden() throws Exception {
        String username = "customerput";
        String token = jwtUtility.generateToken(username);
        ProductDto productDto = new ProductDto("Gaming Laptop", "Dell", "Updated", 75000.0, 5, "laptop.png", 1L);
        String body = new ObjectMapper().writeValueAsString(productDto);

        when(myUserDetailsService.loadUserByUsername(username)).thenReturn(
                new User(username, "$2a$10$abcdefghijklmnopqrstuv", AuthorityUtils.createAuthorityList("ROLE_CUSTOMER")));

        mockMvc.perform(put("/api/product/100")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());

        verify(myUserDetailsService, times(1)).loadUserByUsername(username);
        verifyNoInteractions(productService);
        verifyNoMoreInteractions(myUserDetailsService);
    }

    @Test
    public void getBySellerTest_Owner() throws Exception {
        String username = "techstore";
        String token = jwtUtility.generateToken(username);

        ProductInfoDto productInfoDto = new ProductInfoDto(3L, "iPhone", "Apple", 90000.0, 5,
                "Electronics", "TechStore", "iphone.png", "Great camera", ProductStatus.IN_STOCK, 1L);

        when(myUserDetailsService.loadUserByUsername(username)).thenReturn(
                new User(username, "$2a$10$abcdefghijklmnopqrstuv", AuthorityUtils.createAuthorityList("ROLE_SELLER")));
        when(productService.getBySeller(1L, username)).thenReturn(List.of(productInfoDto));

        mockMvc.perform(get("/api/product/seller/1").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].name").value("iPhone"))
                .andExpect(jsonPath("$[0].imageUrl").value("iphone.png"))
                .andExpect(jsonPath("$[0].description").value("Great camera"))
                .andExpect(jsonPath("$[0].status").value("IN_STOCK"))
                .andExpect(jsonPath("$[0].categoryId").value(1));

        verify(productService, times(1)).getBySeller(1L, username);
        verify(myUserDetailsService, times(1)).loadUserByUsername(username);
        verifyNoMoreInteractions(productService);
        verifyNoMoreInteractions(myUserDetailsService);
    }

    @Test
    public void getBySellerTest_OtherSellerForbidden() throws Exception {
        String username = "fashionhub";
        String token = jwtUtility.generateToken(username);

        when(myUserDetailsService.loadUserByUsername(username)).thenReturn(
                new User(username, "$2a$10$abcdefghijklmnopqrstuv", AuthorityUtils.createAuthorityList("ROLE_SELLER")));
        when(productService.getBySeller(1L, username))
                .thenThrow(new OwnershipException("Seller id does not belong to the logged in user"));

        mockMvc.perform(get("/api/product/seller/1").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Seller id does not belong to the logged in user"));

        verify(productService, times(1)).getBySeller(1L, username);
        verify(myUserDetailsService, times(1)).loadUserByUsername(username);
        verifyNoMoreInteractions(productService);
        verifyNoMoreInteractions(myUserDetailsService);
    }

    @Test
    public void getBySellerTest_CustomerForbidden() throws Exception {
        String username = "johndoe";
        String token = jwtUtility.generateToken(username);

        when(myUserDetailsService.loadUserByUsername(username)).thenReturn(
                new User(username, "$2a$10$abcdefghijklmnopqrstuv", AuthorityUtils.createAuthorityList("ROLE_CUSTOMER")));

        mockMvc.perform(get("/api/product/seller/1").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        verify(myUserDetailsService, times(1)).loadUserByUsername(username);
        verifyNoInteractions(productService);
        verifyNoMoreInteractions(myUserDetailsService);
    }

    @Test
    public void getBySellerTest_NoToken() throws Exception {
        mockMvc.perform(get("/api/product/seller/1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(productService);
        verifyNoInteractions(myUserDetailsService);
    }
}
