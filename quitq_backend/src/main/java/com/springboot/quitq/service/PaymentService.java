package com.springboot.quitq.service;

import com.springboot.quitq.dto.response.PaymentRespDto;
import com.springboot.quitq.enums.PaymentStatus;
import com.springboot.quitq.exception.ResourceNotFoundException;
import com.springboot.quitq.mapper.OrderMapper;
import com.springboot.quitq.model.Orders;
import com.springboot.quitq.model.Payment;
import com.springboot.quitq.repository.OrdersRepository;
import com.springboot.quitq.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrdersRepository ordersRepository;
    private final PaymentRepository paymentRepository;

    public PaymentRespDto completePayment(long orderId, String transactionId) {

        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(()-> new ResourceNotFoundException("Invalid order id"));

        Payment payment = paymentRepository.findByOrdersId(orderId)
                .orElseThrow(()-> new ResourceNotFoundException("Payment not found for this order"));

        payment.setTransactionId(transactionId);
        payment.setPaymentStatus(PaymentStatus.COMPLETED);

        Payment saved = paymentRepository.save(payment);
        return OrderMapper.convertEntityToDto(saved);
    }
}
