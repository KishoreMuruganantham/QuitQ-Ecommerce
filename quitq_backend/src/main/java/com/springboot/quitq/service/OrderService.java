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
    private final CustomerRepository customerRepository; // injected, file owned by another agent
    private final ShippingAddressRepository shippingAddressRepository; // injected, file owned by another agent
    private final SellerRepository sellerRepository; // injected, file owned by another agent
    private final CartRepository cartRepository; // injected, file owned by another agent
    private final CartItemRepository cartItemRepository; // injected, file owned by another agent
    private final ProductRepository productRepository; // injected, file owned by another agent
    private final CartService cartService; // injected, file owned by another agent

    @Transactional
    public OrderRespDto checkout(Long customerId, Long shippingAddressId, PaymentMethod method) {
        // Step 1: Fetch customer from DB using customerId
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if(optionalCustomer.isEmpty())
            throw new ResourceNotFoundException("Customer id Invalid");
        Customer customer = optionalCustomer.get();

        // Step 1.5: Fetch the shipping address : o.shipping_address -- validation stays on top
        Optional<ShippingAddress> optionalAddress = shippingAddressRepository.findById(shippingAddressId);
        if(optionalAddress.isEmpty())
            throw new ResourceNotFoundException("Invalid shipping address id");

        // Step 2: Fetch the customer's cart + its cart items
        Optional<Cart> optionalCart = cartRepository.findByCustomerId(customerId);
        if(optionalCart.isEmpty())
            throw new InvalidCallException("Cart is empty");

        List<CartItem> cartItems = cartItemRepository.findByCartId(optionalCart.get().getId());
        if(cartItems.isEmpty())
            throw new InvalidCallException("Cart is empty");

        // Step 3: Re-check stock for every cart item -- a clean 400 here beats the raw
        // SQLSTATE error the trg_check_stock_before_order trigger would otherwise raise
        for(CartItem item : cartItems) {
            Product product = item.getProduct();
            if(product.getStockQty() < item.getQuantity())
                throw new InsufficientStockException("Insufficient stock for product");
        }

        // Step 3.5: compute the total from the CART ITEM PRICE SNAPSHOT (ci.price * ci.quantity)
        // NEVER from product.getPrice() -- the catalogue price may have moved since the add to cart
        Double totalAmount = cartItems
                .stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Step 4: Generate the order number
        String orderNumber = "Q" + System.currentTimeMillis();

        // Step 5: Build the Orders entity
        Orders orders = new Orders();
        orders.setOrderNumber(orderNumber);
        orders.setCustomer(customer);
        orders.setShippingAddress(optionalAddress.get());
        orders.setTotalAmount(totalAmount);
        orders.setOrderStatus(OrderStatus.PLACED);

        // Step 6: Save the order FIRST -- identity generation hands us the id, and the
        // trg_order_initial_tracking trigger fires on this INSERT into orders, seeding
        // the first order_tracking row ('Order placed successfully') for us
        Orders savedOrder = ordersRepository.save(orders);

        // Step 7: For each cart item build an OrderItem -- the cart snapshot price
        // carries forward as pricePerUnit so the bill never changes after the fact
        for(CartItem item : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrders(savedOrder);
            orderItem.setProduct(item.getProduct());
            orderItem.setSeller(item.getProduct().getSeller()); // oi.seller
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPricePerUnit(item.getPrice());
            orderItem.setTotalPrice(item.getPrice() * item.getQuantity());
            orderItem.setStatus(OrderStatus.PLACED);
            orderItemRepository.save(orderItem);
        }

        // Step 8: stock -- READ THIS before touching it, the DB triggers already did the work.
        //  The schema ships TWO triggers on order_item:
        //    * trg_check_stock_before_order (BEFORE INSERT) SIGNALs 'Insufficient stock for product'
        //      -- Step 3 already converted that into a clean 400 for the caller.
        //    * trg_reduce_stock_after_order (AFTER INSERT) runs
        //      UPDATE product SET stock_qty = stock_qty - NEW.quantity,
        //      status = CASE WHEN (stock_qty - NEW.quantity) <= 0 THEN 'OUT_OF_STOCK' ...
        //      so by the time we reach this line the DB row ALREADY holds the reduced stock.
        //  So we subtract ONCE, from the pre-order snapshot still held on the managed entity.
        //  That number is the SAME value the trigger just wrote, so the UPDATE hibernate flushes
        //  writes the identical stock_qty back -- this MIRRORS the trigger, it is NOT a silent
        //  second decrement. (And when hibernate ddl-auto=update created the schema there are no
        //  triggers at all, so this IS the decrement.)
        //  Note the trigger's CASE re-reads stock_qty after MySQL's left-to-right SET, so the DB
        //  can flip to OUT_OF_STOCK one step early -- that is DB behaviour, Java mirrors the
        //  plain decrement and flips at zero below.
        for(CartItem item : cartItems) {
            Product product = item.getProduct();
            product.setStockQty(product.getStockQty() - item.getQuantity());
            if(product.getStockQty() <= 0)
                product.setStatus(ProductStatus.OUT_OF_STOCK);
            productRepository.save(product);
        }

        // Step 9: Build the Payment record -- it waits at PENDING until Flow 5 completes it
        Payment payment = new Payment();
        payment.setOrders(savedOrder);
        payment.setPaymentMethod(method);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(totalAmount);
        paymentRepository.save(payment);

        // Step 10: Clear the cart -- the order owns those lines now -- and return the order
        cartService.clearCart(customerId);
        System.out.println("order inserted...");
        return OrderMapper.convertEntityToDto(savedOrder);
    }

    public OrderRespDto getById(long orderId) {
        // Step 1: Check if id is valid
        Optional<Orders> optional = ordersRepository.findById(orderId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid order id");

        return OrderMapper.convertEntityToDto(optional.get());
    }

    public List<OrderRespDto> getByCustomer(long customerId) {
        // Step 1: the customer must exist : o.customer
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if(optionalCustomer.isEmpty())
            throw new ResourceNotFoundException("Customer id Invalid");

        // Step 2: load every order of this customer and map it
        List<Orders> list = ordersRepository.findByCustomerId(customerId);
        return list
                .stream()
                .map(OrderMapper ::convertEntityToDto)
                .toList();
    }

    public List<OrderTrackingRespDto> getTracking(long orderId) {
        // Step 1: the order must exist
        Optional<Orders> optional = ordersRepository.findById(orderId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid order id");

        // Step 2: the timeline, oldest first -- row 1 was seeded by trg_order_initial_tracking
        List<OrderTracking> list = orderTrackingRepository.findByOrdersIdOrderByStatusTimeAsc(orderId);
        return list
                .stream()
                .map(OrderMapper ::convertTrackingToDto)
                .toList();
    }

    public List<OrderItemInfoDto> getBySeller(long sellerId) {
        // Step 1: the seller must exist : oi.seller
        Optional<Seller> optionalSeller = sellerRepository.findById(sellerId);
        if(optionalSeller.isEmpty())
            throw new ResourceNotFoundException("Invalid seller id");

        // Step 2: the join query already flattens order + customer + product per order_item
        return orderItemRepository.getOrdersBySellerId(sellerId);
    }

    public OrderItemInfoDto updateItemStatus(long orderItemId, OrderStatus status) {
        // Step 1: find the order item
        Optional<OrderItem> optional = orderItemRepository.findById(orderItemId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid order item id");

        // Step 2: the seller moves this line forward : PLACED -> PROCESSING -> SHIPPED -> DELIVERED
        OrderItem orderItem = optional.get();
        orderItem.setStatus(status);

        // Step 3: save and map back
        OrderItem saved = orderItemRepository.save(orderItem);
        return OrderMapper.convertOrderItemToInfoDto(saved);
    }
}
