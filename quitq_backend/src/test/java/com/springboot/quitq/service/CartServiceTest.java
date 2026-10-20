package com.springboot.quitq.service;

import com.springboot.quitq.dto.response.CartItemInfoDto;
import com.springboot.quitq.enums.ProductStatus;
import com.springboot.quitq.exception.OwnershipException;
import com.springboot.quitq.model.Cart;
import com.springboot.quitq.model.CartItem;
import com.springboot.quitq.model.Customer;
import com.springboot.quitq.model.Product;
import com.springboot.quitq.repository.CartItemRepository;
import com.springboot.quitq.repository.CartRepository;
import com.springboot.quitq.repository.CustomerRepository;
import com.springboot.quitq.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    public void addItemTest() {
        String username = "john";

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");

        Cart cart = new Cart();
        cart.setId(50L);
        cart.setCustomer(customer);

        Product product = new Product();
        product.setId(100L);
        product.setName("Laptop");
        product.setBrand("Dell");
        product.setPrice(50000.0);
        product.setStockQty(10);
        product.setStatus(ProductStatus.IN_STOCK);

        CartItem savedItem = new CartItem();
        savedItem.setId(9L);
        savedItem.setCart(cart);
        savedItem.setProduct(product);
        savedItem.setQuantity(3);
        savedItem.setPrice(50000.0);

        when(customerRepository.getByUsername(username)).thenReturn(customer);
        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartId(50L)).thenReturn(List.of());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(savedItem);

        CartItemInfoDto result = cartService.addItem(1L, 100L, 3, username);

        assertEquals(3, result.quantity());
        assertEquals(50000.0, result.price());

        verify(customerRepository, times(1)).getByUsername(username);
        verify(cartRepository, times(1)).findByCustomerId(1L);
        verify(productRepository, times(1)).findById(100L);
        verify(cartItemRepository, times(1)).findByCartId(50L);
        verify(cartItemRepository, times(1)).save(argThat((CartItem item) ->
                item.getPrice().equals(product.getPrice()) && item.getQuantity().equals(3)));

        verifyNoMoreInteractions(customerRepository);
        verifyNoMoreInteractions(cartRepository);
        verifyNoMoreInteractions(productRepository);
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    public void viewCartTest_OwnershipViolation() {
        String username = "john";

        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.getByUsername(username)).thenReturn(customer);

        assertThrows(OwnershipException.class, () -> cartService.viewCart(2L, username));

        verify(customerRepository, times(1)).getByUsername(username);
        verify(cartRepository, never()).findByCustomerId(anyLong());

        verifyNoMoreInteractions(customerRepository);
        verifyNoMoreInteractions(cartRepository);
    }
}
