package com.springboot.quitq.controller;

import com.springboot.quitq.dto.response.OrderItemInfoDto;
import com.springboot.quitq.dto.response.OrderRespDto;
import com.springboot.quitq.dto.response.OrderTrackingRespDto;
import com.springboot.quitq.enums.OrderStatus;
import com.springboot.quitq.enums.PaymentMethod;
import com.springboot.quitq.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/order/checkout/{customerId}/{shippingAddressId}")
    public OrderRespDto checkout(@PathVariable long customerId, @PathVariable long shippingAddressId,
                                 @RequestParam(name = "method", required = false, defaultValue = "COD") PaymentMethod method,
                                 Principal principal){
        return orderService.checkout(customerId, shippingAddressId, method, principal.getName());
    }

    @GetMapping("/api/order/{orderId}")
    public OrderRespDto getById(@PathVariable long orderId, Principal principal){
        return orderService.getById(orderId, principal.getName());
    }

    @GetMapping("/api/order/customer/{customerId}")
    public List<OrderRespDto> getByCustomer(@PathVariable long customerId,
                                            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size,
                                            Principal principal){
        return orderService.getByCustomer(customerId, page, size, principal.getName());
    }

    @GetMapping("/api/order/{orderId}/tracking")
    public List<OrderTrackingRespDto> getTracking(@PathVariable long orderId, Principal principal){
        return orderService.getTracking(orderId, principal.getName());
    }

    @GetMapping("/api/order/seller/{sellerId}")
    public List<OrderItemInfoDto> getBySeller(@PathVariable long sellerId,
                                              @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                              @RequestParam(name = "size", required = false, defaultValue = "20") Integer size,
                                              Principal principal){
        return orderService.getBySeller(sellerId, page, size, principal.getName());
    }

    @PutMapping("/api/order/item/{orderItemId}/status")
    public OrderItemInfoDto updateItemStatus(@PathVariable long orderItemId,
                                             @RequestParam(name = "status") OrderStatus status,
                                             Principal principal){
        return orderService.updateItemStatus(orderItemId, status, principal.getName());
    }

}

