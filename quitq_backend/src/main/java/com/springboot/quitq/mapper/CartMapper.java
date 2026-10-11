package com.springboot.quitq.mapper;

import com.springboot.quitq.dto.response.CartInfoDto;
import com.springboot.quitq.dto.response.CartItemInfoDto;
import com.springboot.quitq.model.Cart;
import com.springboot.quitq.model.CartItem;

import java.util.List;

public class CartMapper {

    public static CartInfoDto convertEntityToDto(Cart cart, List<CartItem> list){
        return
                new CartInfoDto (
                        cart.getId(),
                        cart.getCustomer().getName(),
                        list
                                .stream()
                                .map(CartMapper ::convertCartItemToInfoDto)
                                .toList(),
                        list
                                .stream()
                                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                                .sum()
                );
    }

    public static CartItemInfoDto convertCartItemToInfoDto(CartItem item){
        return
                new CartItemInfoDto (
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice() * item.getQuantity()
                );
    }
}
