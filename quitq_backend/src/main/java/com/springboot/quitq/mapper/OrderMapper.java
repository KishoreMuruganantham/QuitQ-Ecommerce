package com.springboot.quitq.mapper;

import com.springboot.quitq.dto.response.OrderItemInfoDto;
import com.springboot.quitq.dto.response.OrderRespDto;
import com.springboot.quitq.dto.response.OrderTrackingRespDto;
import com.springboot.quitq.dto.response.PaymentRespDto;
import com.springboot.quitq.model.OrderItem;
import com.springboot.quitq.model.OrderTracking;
import com.springboot.quitq.model.Orders;
import com.springboot.quitq.model.Payment;

public class OrderMapper {

    public static OrderRespDto convertEntityToDto(Orders orders){
        return
                new OrderRespDto (
                        orders.getId(),
                        orders.getOrderNumber(),
                        orders.getTotalAmount(),
                        orders.getOrderStatus(),
                        orders.getOrderDate()
                );
    }

    public static OrderItemInfoDto convertOrderItemToInfoDto(OrderItem orderItem){
        return
                new OrderItemInfoDto (
                        orderItem.getId(),
                        orderItem.getOrders().getOrderNumber(),
                        orderItem.getOrders().getCustomer().getName(),
                        orderItem.getProduct().getName(),
                        orderItem.getQuantity(),
                        orderItem.getTotalPrice(),
                        orderItem.getStatus()
                );
    }

    public static PaymentRespDto convertEntityToDto(Payment payment){
        return
                new PaymentRespDto (
                        payment.getId(),
                        payment.getOrders().getOrderNumber(),
                        payment.getPaymentMethod(),
                        payment.getPaymentStatus(),
                        payment.getTransactionId(),
                        payment.getAmount()
                );
    }

    public static OrderTrackingRespDto convertTrackingToDto(OrderTracking orderTracking){
        return
                new OrderTrackingRespDto (
                        orderTracking.getId(),
                        orderTracking.getStatus(),
                        orderTracking.getStatusTime(),
                        orderTracking.getRemarks()
                );
    }
}
