package com.springboot.quitq.service;

import com.springboot.quitq.dto.response.PaymentRespDto;
import com.springboot.quitq.enums.OrderStatus;
import com.springboot.quitq.enums.PaymentMethod;
import com.springboot.quitq.enums.PaymentStatus;
import com.springboot.quitq.exception.ResourceNotFoundException;
import com.springboot.quitq.model.Customer;
import com.springboot.quitq.model.Orders;
import com.springboot.quitq.model.Payment;
import com.springboot.quitq.model.User;
import com.springboot.quitq.repository.OrdersRepository;
import com.springboot.quitq.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    public void completePaymentTest() {
        User user = new User();
        user.setUsername("john");

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUser(user);

        Orders orders = new Orders();
        orders.setId(1L);
        orders.setOrderNumber("Q123");
        orders.setCustomer(customer);
        orders.setTotalAmount(500.0);
        orders.setOrderStatus(OrderStatus.PLACED);

        Payment payment = new Payment();
        payment.setId(7L);
        payment.setOrders(orders);
        payment.setPaymentMethod(PaymentMethod.UPI);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(500.0);

        when(ordersRepository.findById(1L)).thenReturn(Optional.of(orders));
        when(paymentRepository.findByOrdersId(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentRespDto result = paymentService.completePayment(1L, "TXN123", "john");

        assertEquals("TXN123", result.transactionId());
        assertEquals(PaymentStatus.COMPLETED, result.paymentStatus());
        assertEquals("Q123", result.orderNumber());

        verify(ordersRepository, times(1)).findById(1L);
        verify(paymentRepository, times(1)).findByOrdersId(1L);
        verify(paymentRepository, times(1)).save(argThat((Payment saved) ->
                "TXN123".equals(saved.getTransactionId()) && saved.getPaymentStatus() == PaymentStatus.COMPLETED));

        verifyNoMoreInteractions(ordersRepository);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    public void completePaymentTest_Invalid() {
        when(ordersRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.completePayment(99L, "TXN123", "john"));

        verify(ordersRepository, times(1)).findById(99L);
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(paymentRepository, never()).findByOrdersId(anyLong());

        verifyNoMoreInteractions(ordersRepository);
        verifyNoMoreInteractions(paymentRepository);
    }
}
