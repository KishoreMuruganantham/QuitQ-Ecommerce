package com.springboot.quitq.controller;

import com.springboot.quitq.config.JwtFilter;
import com.springboot.quitq.config.SecurityConfig;
import com.springboot.quitq.config.VersioningConfig;
import com.springboot.quitq.dto.response.ProductRespDto;
import com.springboot.quitq.enums.ProductStatus;
import com.springboot.quitq.service.CartService;
import com.springboot.quitq.service.MyUserDetailsService;
import com.springboot.quitq.service.ProductService;
import com.springboot.quitq.utility.JwtUtility;
import com.springboot.quitq.utility.TokenDenylist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {ProductController.class, CartController.class})
@Import({SecurityConfig.class, JwtFilter.class, JwtUtility.class, TokenDenylist.class, VersioningConfig.class})
public class VersioningApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @Test
    public void getAllTest_VersionedPath() throws Exception {
        ProductRespDto productRespDto = new ProductRespDto(100L, "Laptop", "Dell", 50000.0, 10, ProductStatus.IN_STOCK, "laptop.png");

        when(productService.getAll(0, 20)).thenReturn(List.of(productRespDto));

        mockMvc.perform(get("/api/v1/product/all"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-API-Version", "v1"))
                .andExpect(jsonPath("$[0].name").value("Laptop"));

        verify(productService, times(1)).getAll(0, 20);
        verifyNoMoreInteractions(productService);
        verifyNoInteractions(cartService);
        verifyNoInteractions(myUserDetailsService);
    }

    @Test
    public void viewCartTest_VersionedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/cart/1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(cartService);
        verifyNoInteractions(productService);
        verifyNoInteractions(myUserDetailsService);
    }

    @Test
    public void getAllTest_UnversionedPath() throws Exception {
        ProductRespDto productRespDto = new ProductRespDto(100L, "Laptop", "Dell", 50000.0, 10, ProductStatus.IN_STOCK, "laptop.png");

        when(productService.getAll(0, 20)).thenReturn(List.of(productRespDto));

        mockMvc.perform(get("/api/product/all"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("X-API-Version"))
                .andExpect(jsonPath("$[0].name").value("Laptop"));

        verify(productService, times(1)).getAll(0, 20);
        verifyNoMoreInteractions(productService);
        verifyNoInteractions(myUserDetailsService);
    }
}
