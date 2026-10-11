package com.springboot.quitq.controller;

import com.springboot.quitq.dto.response.OrderItemInfoDto;
import com.springboot.quitq.dto.response.OrderRespDto;
import com.springboot.quitq.dto.response.OrderTrackingRespDto;
import com.springboot.quitq.enums.OrderStatus;
import com.springboot.quitq.enums.PaymentMethod;
import com.springboot.quitq.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/order/checkout/{customerId}/{shippingAddressId}") ///api/order/checkout/1/1?method=UPI
    public OrderRespDto checkout(@PathVariable long customerId, @PathVariable long shippingAddressId,
                                 @RequestParam(name = "method", required = false, defaultValue = "COD") PaymentMethod method){
        return orderService.checkout(customerId, shippingAddressId, method);
    }

    @GetMapping("/api/order/{orderId}") ///api/order/1
    public OrderRespDto getById(@PathVariable long orderId){ //orderId=1
        return orderService.getById(orderId);
    }

    @GetMapping("/api/order/customer/{customerId}") ///api/order/customer/1
    public List<OrderRespDto> getByCustomer(@PathVariable long customerId){ //customerId=1
        return orderService.getByCustomer(customerId);
    }

    @GetMapping("/api/order/{orderId}/tracking") ///api/order/1/tracking
    public List<OrderTrackingRespDto> getTracking(@PathVariable long orderId){ //orderId=1
        return orderService.getTracking(orderId);
    }

    @GetMapping("/api/order/seller/{sellerId}") ///api/order/seller/1
    public List<OrderItemInfoDto> getBySeller(@PathVariable long sellerId){ //sellerId=1
        return orderService.getBySeller(sellerId);
    }

    @PutMapping("/api/order/item/{orderItemId}/status") ///api/order/item/1/status?status=SHIPPED
    public OrderItemInfoDto updateItemStatus(@PathVariable long orderItemId,
                                             @RequestParam(name = "status") OrderStatus status){ //orderItemId=1
        return orderService.updateItemStatus(orderItemId, status);
    }

    /*
    POST API : checkout with body
    OrderDto (customerId, shippingAddressId, paymentMethod)
    * */
}
/*
    Order status timeline -- Flow 4, 5, 6 & 7
    ------------------------------------------
    PLACED -> PROCESSING -> SHIPPED -> DELIVERED   (CANCELLED can be set from any stage)

    order_tracking
    --------------
    row 1  : Order placed successfully
    row 2+ : appended as the order moves

    order_item.status   : seller drives it via PUT /api/order/item/{orderItemId}/status?status=SHIPPED
    payment_status      : PENDING at checkout -> COMPLETED at POST /api/payment/{orderId}/complete?txnId=ABC123
 */
