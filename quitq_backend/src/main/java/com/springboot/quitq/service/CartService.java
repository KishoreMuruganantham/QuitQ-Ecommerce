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
    private final CustomerRepository customerRepository; // injected, file owned by another agent
    private final ProductRepository productRepository; // injected, file owned by another agent

    public CartItemInfoDto addItem(Long customerId, Long productId, Integer quantity) {
        // Step 1: find or create the customer's Cart -- one cart per customer (UNIQUE)
        Optional<Cart> optionalCart = cartRepository.findByCustomerId(customerId);
        Cart cart;
        if(optionalCart.isPresent())
            cart = optionalCart.get();
        else {
            // Step 1.1: no cart yet -- load the customer and create one
            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
            if(optionalCustomer.isEmpty())
                throw new ResourceNotFoundException("Invalid customer id");

            cart = new Cart();
            cart.setCustomer(optionalCustomer.get());
            cart = cartRepository.save(cart);
        }

        // Step 2: find the Product
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if(optionalProduct.isEmpty())
            throw new ResourceNotFoundException("Invalid product id");

        Product product = optionalProduct.get();
        // Focus on validation --- second
        if(product.getStatus() == ProductStatus.OUT_OF_STOCK)
            throw new InsufficientStockException("Product is out of stock");

        // Step 3: product already inside the cart? just bump the quantity
        List<CartItem> list = cartItemRepository.findByCartId(cart.getId());
        Optional<CartItem> optionalItem = list
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if(optionalItem.isPresent()) {
            CartItem existing = optionalItem.get();
            existing.setQuantity(existing.getQuantity() + quantity);
            // Step 4: save
            CartItem updated = cartItemRepository.save(existing);
            return CartMapper.convertCartItemToInfoDto(updated);
        }

        // Step 3.5: snapshot the price, do not read from product at render time
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setPrice(product.getPrice());

        // Step 4: save
        CartItem saved = cartItemRepository.save(cartItem);
        return CartMapper.convertCartItemToInfoDto(saved);
    }

    public CartInfoDto viewCart(Long customerId) {
        // Step 1: the cart must exist for this customer
        Optional<Cart> optional = cartRepository.findByCustomerId(customerId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Cart not found for this customer");

        // Step 2: load every line of the cart and map it
        List<CartItem> list = cartItemRepository.findByCartId(optional.get().getId());
        return CartMapper.convertEntityToDto(optional.get(), list);
    }

    public CartItemInfoDto updateQuantity(Long cartItemId, Integer quantity) {
        // Step 1: find the cart item
        Optional<CartItem> optional = cartItemRepository.findById(cartItemId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid cart item id");

        // Step 2: change the quantity and save
        CartItem item = optional.get();
        item.setQuantity(quantity);
        CartItem saved = cartItemRepository.save(item);
        return CartMapper.convertCartItemToInfoDto(saved);
    }

    public void removeItem(Long cartItemId) {
        // Step 1: Check if id is valid
        Optional<CartItem> optional = cartItemRepository.findById(cartItemId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid cart item id");

        // Step 2: delete the record
        cartItemRepository.deleteById(cartItemId);
    }

    public double getCartTotal(Long customerId) {
        // Step 1: the cart must exist for this customer
        Optional<Cart> optional = cartRepository.findByCustomerId(customerId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Cart not found for this customer");

        // Step 2: load the lines of the cart
        List<CartItem> list = cartItemRepository.findByCartId(optional.get().getId());

        // Step 3: total = snapshot price * quantity, summed over every line
        return list.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

    public void clearCart(Long customerId) {
        // Step 1: the cart must exist for this customer
        Optional<Cart> optional = cartRepository.findByCustomerId(customerId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Cart not found for this customer");

        // Step 2: delete every line of this cart
        cartItemRepository.deleteByCartId(optional.get().getId());
    }
}
