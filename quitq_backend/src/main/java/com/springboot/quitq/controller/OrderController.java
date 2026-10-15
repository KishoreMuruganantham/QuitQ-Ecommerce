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

    @PostMapping("/api/order/checkout/{customerId}/{shippingAddressId}")
    public OrderRespDto checkout(@PathVariable long customerId, @PathVariable long shippingAddressId,
                                 @RequestParam(name = "method", required = false, defaultValue = "COD") PaymentMethod method){
        return orderService.checkout(customerId, shippingAddressId, method);
    }

    @GetMapping("/api/order/{orderId}")
    public OrderRespDto getById(@PathVariable long orderId){
        return orderService.getById(orderId);
    }

    @GetMapping("/api/order/customer/{customerId}")
    public List<OrderRespDto> getByCustomer(@PathVariable long customerId){
        return orderService.getByCustomer(customerId);
    }

    @GetMapping("/api/order/{orderId}/tracking")
    public List<OrderTrackingRespDto> getTracking(@PathVariable long orderId){
        return orderService.getTracking(orderId);
    }

    @GetMapping("/api/order/seller/{sellerId}")
    public List<OrderItemInfoDto> getBySeller(@PathVariable long sellerId){
        return orderService.getBySeller(sellerId);
    }

    @PutMapping("/api/order/item/{orderItemId}/status")
    public OrderItemInfoDto updateItemStatus(@PathVariable long orderItemId,
                                             @RequestParam(name = "status") OrderStatus status){
        return orderService.updateItemStatus(orderItemId, status);
    }

}

