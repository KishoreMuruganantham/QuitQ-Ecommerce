package com.springboot.quitq.service;

import com.springboot.quitq.dto.response.OrderRespDto;
import com.springboot.quitq.enums.OrderStatus;
import com.springboot.quitq.enums.PaymentMethod;
import com.springboot.quitq.enums.PaymentStatus;
import com.springboot.quitq.enums.ProductStatus;
import com.springboot.quitq.exception.InsufficientStockException;
import com.springboot.quitq.exception.OwnershipException;
import com.springboot.quitq.model.Cart;
import com.springboot.quitq.model.CartItem;
import com.springboot.quitq.model.Customer;
import com.springboot.quitq.model.OrderItem;
import com.springboot.quitq.model.Orders;
import com.springboot.quitq.model.Payment;
import com.springboot.quitq.model.Product;
import com.springboot.quitq.model.Seller;
import com.springboot.quitq.model.ShippingAddress;
import com.springboot.quitq.repository.CartItemRepository;
import com.springboot.quitq.repository.CartRepository;
import com.springboot.quitq.repository.CustomerRepository;
import com.springboot.quitq.repository.OrderItemRepository;
import com.springboot.quitq.repository.OrderTrackingRepository;
import com.springboot.quitq.repository.OrdersRepository;
import com.springboot.quitq.repository.PaymentRepository;
import com.springboot.quitq.repository.ProductRepository;
import com.springboot.quitq.repository.SellerRepository;
import com.springboot.quitq.repository.ShippingAddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderTrackingRepository orderTrackingRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ShippingAddressRepository shippingAddressRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartService cartService;

    @Test
    public void checkoutTest() {
        String username = "john";

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");

        ShippingAddress address = new ShippingAddress();
        address.setId(10L);
        address.setCustomer(customer);

        Cart cart = new Cart();
        cart.setId(50L);
        cart.setCustomer(customer);

        Seller seller = new Seller();
        seller.setId(1L);
        seller.setBusinessName("TechStore");

        Product product = new Product();
        product.setId(100L);
        product.setName("Laptop");
        product.setBrand("Dell");
        product.setPrice(500.0);
        product.setStockQty(10);
        product.setStatus(ProductStatus.IN_STOCK);
        product.setSeller(seller);

        CartItem cartItem = new CartItem();
        cartItem.setId(70L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setPrice(500.0);

        Orders savedOrder = new Orders();
        savedOrder.setId(1L);
        savedOrder.setOrderNumber("Q123");
        savedOrder.setCustomer(customer);
        savedOrder.setShippingAddress(address);
        savedOrder.setTotalAmount(1000.0);
        savedOrder.setOrderStatus(OrderStatus.PLACED);

        when(customerRepository.getByUsername(username)).thenReturn(customer);
        when(shippingAddressRepository.findById(10L)).thenReturn(Optional.of(address));
        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(50L)).thenReturn(List.of(cartItem));
        when(ordersRepository.save(any(Orders.class))).thenReturn(savedOrder);

        OrderRespDto result = orderService.checkout(1L, 10L, PaymentMethod.UPI, username);

        assertEquals("Q123", result.orderNumber());
        assertEquals(1000.0, result.totalAmount());

        verify(customerRepository, times(1)).getByUsername(username);
        verify(shippingAddressRepository, times(1)).findById(10L);
        verify(cartRepository, times(1)).findByCustomerId(1L);
        verify(cartItemRepository, times(1)).findByCartId(50L);
        verify(ordersRepository, times(1)).save(argThat((Orders order) ->
                order.getTotalAmount().equals(1000.0) && order.getOrderStatus() == OrderStatus.PLACED));
        verify(orderItemRepository, times(1)).save(argThat((OrderItem item) ->
                item.getQuantity().equals(2)
                        && item.getPricePerUnit().equals(500.0)
                        && item.getTotalPrice().equals(1000.0)));
        verify(productRepository, times(1)).save(argThat((Product saved) -> saved.getStockQty().equals(8)));
        verify(paymentRepository, times(1)).save(argThat((Payment saved) ->
                saved.getPaymentStatus() == PaymentStatus.PENDING && saved.getAmount().equals(1000.0)));
        verify(cartService, times(1)).clearCart(1L);

        verifyNoMoreInteractions(customerRepository);
        verifyNoMoreInteractions(shippingAddressRepository);
        verifyNoMoreInteractions(cartRepository);
        verifyNoMoreInteractions(cartItemRepository);
        verifyNoMoreInteractions(ordersRepository);
        verifyNoMoreInteractions(orderItemRepository);
        verifyNoMoreInteractions(productRepository);
        verifyNoMoreInteractions(paymentRepository);
        verifyNoMoreInteractions(cartService);
    }

    @Test
    public void checkoutTest_InsufficientStock() {
        String username = "john";

        Customer customer = new Customer();
        customer.setId(1L);

        ShippingAddress address = new ShippingAddress();
        address.setId(10L);
        address.setCustomer(customer);

        Cart cart = new Cart();
        cart.setId(50L);
        cart.setCustomer(customer);

        Product product = new Product();
        product.setId(100L);
        product.setName("Laptop");
        product.setPrice(500.0);
        product.setStockQty(1);
        product.setStatus(ProductStatus.IN_STOCK);

        CartItem cartItem = new CartItem();
        cartItem.setId(70L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setPrice(500.0);

        when(customerRepository.getByUsername(username)).thenReturn(customer);
        when(shippingAddressRepository.findById(10L)).thenReturn(Optional.of(address));
        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(50L)).thenReturn(List.of(cartItem));

        assertThrows(InsufficientStockException.class,
                () -> orderService.checkout(1L, 10L, PaymentMethod.UPI, username));

        verify(customerRepository, times(1)).getByUsername(username);
        verify(shippingAddressRepository, times(1)).findById(10L);
        verify(cartRepository, times(1)).findByCustomerId(1L);
        verify(cartItemRepository, times(1)).findByCartId(50L);
        verify(ordersRepository, never()).save(any(Orders.class));
        verify(orderItemRepository, never()).save(any(OrderItem.class));
        verify(productRepository, never()).save(any(Product.class));
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(cartService, never()).clearCart(anyLong());

        verifyNoMoreInteractions(customerRepository);
        verifyNoMoreInteractions(shippingAddressRepository);
        verifyNoMoreInteractions(cartRepository);
        verifyNoMoreInteractions(cartItemRepository);
        verifyNoMoreInteractions(ordersRepository);
    }

    @Test
    public void getByCustomerTest_OwnershipViolation() {
        String username = "john";

        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.getByUsername(username)).thenReturn(customer);

        assertThrows(OwnershipException.class, () -> orderService.getByCustomer(2L, 0, 5, username));

        verify(customerRepository, times(1)).getByUsername(username);
        verify(ordersRepository, never()).findByCustomerId(anyLong(), any(Pageable.class));

        verifyNoMoreInteractions(customerRepository);
        verifyNoMoreInteractions(ordersRepository);
    }
}
