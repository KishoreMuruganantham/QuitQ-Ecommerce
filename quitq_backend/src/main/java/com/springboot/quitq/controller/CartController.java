package com.springboot.quitq.controller;

import com.springboot.quitq.dto.response.CartInfoDto;
import com.springboot.quitq.dto.response.CartItemInfoDto;
import com.springboot.quitq.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/api/cart/add/{customerId}/{productId}")
    public CartItemInfoDto addItem(@PathVariable long customerId, @PathVariable long productId,
                                   @RequestParam(name = "quantity", required = false, defaultValue = "1") Integer quantity){
        return cartService.addItem(customerId, productId, quantity);
    }

    @GetMapping("/api/cart/{customerId}")
    public CartInfoDto viewCart(@PathVariable long customerId){
        return cartService.viewCart(customerId);
    }

    @PutMapping("/api/cart/item/{cartItemId}")
    public CartItemInfoDto updateQuantity(@PathVariable long cartItemId, @RequestParam(name = "quantity") Integer quantity){
        return cartService.updateQuantity(cartItemId, quantity);
    }

    @DeleteMapping("/api/cart/item/{cartItemId}")
    public void removeItem(@PathVariable long cartItemId){
        cartService.removeItem(cartItemId);
    }
}
