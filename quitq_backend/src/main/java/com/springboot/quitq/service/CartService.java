package com.springboot.quitq.service;

import com.springboot.quitq.dto.response.CartInfoDto;
import com.springboot.quitq.dto.response.CartItemInfoDto;
import com.springboot.quitq.enums.ProductStatus;
import com.springboot.quitq.exception.InsufficientStockException;
import com.springboot.quitq.exception.ResourceNotFoundException;
import com.springboot.quitq.mapper.CartMapper;
import com.springboot.quitq.model.Cart;
import com.springboot.quitq.model.CartItem;
import com.springboot.quitq.model.Customer;
import com.springboot.quitq.model.Product;
import com.springboot.quitq.repository.CartItemRepository;
import com.springboot.quitq.repository.CartRepository;
import com.springboot.quitq.repository.CustomerRepository;
import com.springboot.quitq.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public CartItemInfoDto addItem(Long customerId, Long productId, Integer quantity) {

        Optional<Cart> optionalCart = cartRepository.findByCustomerId(customerId);
        Cart cart;
        if(optionalCart.isPresent())
            cart = optionalCart.get();
        else {

            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
            if(optionalCustomer.isEmpty())
                throw new ResourceNotFoundException("Invalid customer id");

            cart = new Cart();
            cart.setCustomer(optionalCustomer.get());
            cart = cartRepository.save(cart);
        }

        Optional<Product> optionalProduct = productRepository.findById(productId);
        if(optionalProduct.isEmpty())
            throw new ResourceNotFoundException("Invalid product id");

        Product product = optionalProduct.get();

        if(product.getStatus() == ProductStatus.OUT_OF_STOCK)
            throw new InsufficientStockException("Product is out of stock");

        List<CartItem> list = cartItemRepository.findByCartId(cart.getId());
        Optional<CartItem> optionalItem = list
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if(optionalItem.isPresent()) {
            CartItem existing = optionalItem.get();
            existing.setQuantity(existing.getQuantity() + quantity);

            CartItem updated = cartItemRepository.save(existing);
            return CartMapper.convertCartItemToInfoDto(updated);
        }

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setPrice(product.getPrice());

        CartItem saved = cartItemRepository.save(cartItem);
        return CartMapper.convertCartItemToInfoDto(saved);
    }

    public CartInfoDto viewCart(Long customerId) {

        Optional<Cart> optional = cartRepository.findByCustomerId(customerId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Cart not found for this customer");

        List<CartItem> list = cartItemRepository.findByCartId(optional.get().getId());
        return CartMapper.convertEntityToDto(optional.get(), list);
    }

    public CartItemInfoDto updateQuantity(Long cartItemId, Integer quantity) {

        Optional<CartItem> optional = cartItemRepository.findById(cartItemId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid cart item id");

        CartItem item = optional.get();
        item.setQuantity(quantity);
        CartItem saved = cartItemRepository.save(item);
        return CartMapper.convertCartItemToInfoDto(saved);
    }

    public void removeItem(Long cartItemId) {

        Optional<CartItem> optional = cartItemRepository.findById(cartItemId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid cart item id");

        cartItemRepository.deleteById(cartItemId);
    }

    public double getCartTotal(Long customerId) {

        Optional<Cart> optional = cartRepository.findByCustomerId(customerId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Cart not found for this customer");

        List<CartItem> list = cartItemRepository.findByCartId(optional.get().getId());

        return list.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

    public void clearCart(Long customerId) {

        Optional<Cart> optional = cartRepository.findByCustomerId(customerId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Cart not found for this customer");

        cartItemRepository.deleteByCartId(optional.get().getId());
    }
}
