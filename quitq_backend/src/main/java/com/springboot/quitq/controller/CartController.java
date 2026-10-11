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

    @PostMapping("/api/cart/add/{customerId}/{productId}") ///api/cart/add/1/2?quantity=1
    public CartItemInfoDto addItem(@PathVariable long customerId, @PathVariable long productId,
                                   @RequestParam(name = "quantity", required = false, defaultValue = "1") Integer quantity){
        return cartService.addItem(customerId, productId, quantity);
    }

    @GetMapping("/api/cart/{customerId}") ///api/cart/1
    public CartInfoDto viewCart(@PathVariable long customerId){ //customerId=1
        return cartService.viewCart(customerId);
    }

    @PutMapping("/api/cart/item/{cartItemId}") ///api/cart/item/3?quantity=3
    public CartItemInfoDto updateQuantity(@PathVariable long cartItemId, @RequestParam(name = "quantity") Integer quantity){
        return cartService.updateQuantity(cartItemId, quantity);
    }

    @DeleteMapping("/api/cart/item/{cartItemId}") ///api/cart/item/3
    public void removeItem(@PathVariable long cartItemId){ //cartItemId=3
        cartService.removeItem(cartItemId);
    }
}
