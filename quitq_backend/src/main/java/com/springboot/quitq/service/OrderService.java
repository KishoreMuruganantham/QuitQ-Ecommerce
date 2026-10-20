package com.springboot.quitq.service;

import com.springboot.quitq.dto.response.OrderItemInfoDto;
import com.springboot.quitq.dto.response.OrderRespDto;
import com.springboot.quitq.dto.response.OrderTrackingRespDto;
import com.springboot.quitq.enums.OrderStatus;
import com.springboot.quitq.enums.PaymentMethod;
import com.springboot.quitq.enums.PaymentStatus;
import com.springboot.quitq.enums.ProductStatus;
import com.springboot.quitq.exception.InsufficientStockException;
import com.springboot.quitq.exception.InvalidCallException;
import com.springboot.quitq.exception.ResourceNotFoundException;
import com.springboot.quitq.mapper.OrderMapper;
import com.springboot.quitq.model.Cart;
import com.springboot.quitq.model.CartItem;
import com.springboot.quitq.model.Customer;
import com.springboot.quitq.model.OrderItem;
import com.springboot.quitq.model.OrderTracking;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final OrderTrackingRepository orderTrackingRepository;
    private final CustomerRepository customerRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final SellerRepository sellerRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Transactional
    public OrderRespDto checkout(Long customerId, Long shippingAddressId, PaymentMethod method) {

        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if(optionalCustomer.isEmpty())
            throw new ResourceNotFoundException("Customer id Invalid");
        Customer customer = optionalCustomer.get();

        Optional<ShippingAddress> optionalAddress = shippingAddressRepository.findById(shippingAddressId);
        if(optionalAddress.isEmpty())
            throw new ResourceNotFoundException("Invalid shipping address id");

        Optional<Cart> optionalCart = cartRepository.findByCustomerId(customerId);
        if(optionalCart.isEmpty())
            throw new InvalidCallException("Cart is empty");

        List<CartItem> cartItems = cartItemRepository.findByCartId(optionalCart.get().getId());
        if(cartItems.isEmpty())
            throw new InvalidCallException("Cart is empty");

        for(CartItem item : cartItems) {
            Product product = item.getProduct();
            if(product.getStockQty() < item.getQuantity())
                throw new InsufficientStockException("Insufficient stock for product");
        }

        Double totalAmount = cartItems
                .stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        String orderNumber = "Q" + System.currentTimeMillis();

        Orders orders = new Orders();
        orders.setOrderNumber(orderNumber);
        orders.setCustomer(customer);
        orders.setShippingAddress(optionalAddress.get());
        orders.setTotalAmount(totalAmount);
        orders.setOrderStatus(OrderStatus.PLACED);

        Orders savedOrder = ordersRepository.save(orders);

        for(CartItem item : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrders(savedOrder);
            orderItem.setProduct(item.getProduct());
            orderItem.setSeller(item.getProduct().getSeller());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPricePerUnit(item.getPrice());
            orderItem.setTotalPrice(item.getPrice() * item.getQuantity());
            orderItem.setStatus(OrderStatus.PLACED);
            orderItemRepository.save(orderItem);
        }

        for(CartItem item : cartItems) {
            Product product = item.getProduct();
            product.setStockQty(product.getStockQty() - item.getQuantity());
            if(product.getStockQty() <= 0)
                product.setStatus(ProductStatus.OUT_OF_STOCK);
            productRepository.save(product);
        }

        Payment payment = new Payment();
        payment.setOrders(savedOrder);
        payment.setPaymentMethod(method);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(totalAmount);
        paymentRepository.save(payment);

        cartService.clearCart(customerId);
        System.out.println("order inserted...");
        return OrderMapper.convertEntityToDto(savedOrder);
    }

    public OrderRespDto getById(long orderId) {

        Optional<Orders> optional = ordersRepository.findById(orderId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid order id");

        return OrderMapper.convertEntityToDto(optional.get());
    }

    public List<OrderRespDto> getByCustomer(long customerId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if(optionalCustomer.isEmpty())
            throw new ResourceNotFoundException("Customer id Invalid");

        List<Orders> list = ordersRepository.findByCustomerId(customerId, pageable);
        return list
                .stream()
                .map(OrderMapper ::convertEntityToDto)
                .toList();
    }

    public List<OrderTrackingRespDto> getTracking(long orderId) {

        Optional<Orders> optional = ordersRepository.findById(orderId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid order id");

        List<OrderTracking> list = orderTrackingRepository.findByOrdersIdOrderByStatusTimeAsc(orderId);
        return list
                .stream()
                .map(OrderMapper ::convertTrackingToDto)
                .toList();
    }

    public List<OrderItemInfoDto> getBySeller(long sellerId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Optional<Seller> optionalSeller = sellerRepository.findById(sellerId);
        if(optionalSeller.isEmpty())
            throw new ResourceNotFoundException("Invalid seller id");

        return orderItemRepository.getOrdersBySellerId(sellerId, pageable);
    }

    public OrderItemInfoDto updateItemStatus(long orderItemId, OrderStatus status) {

        Optional<OrderItem> optional = orderItemRepository.findById(orderItemId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid order item id");

        OrderItem orderItem = optional.get();
        orderItem.setStatus(status);

        OrderItem saved = orderItemRepository.save(orderItem);
        return OrderMapper.convertOrderItemToInfoDto(saved);
    }
}
