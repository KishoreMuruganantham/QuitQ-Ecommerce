package com.springboot.quitq.controller;

import com.springboot.quitq.dto.response.PaymentRespDto;
import com.springboot.quitq.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/api/payment/{orderId}/complete") ///api/payment/1/complete?txnId=ABC123
    public PaymentRespDto completePayment(@PathVariable long orderId,
                                          @RequestParam(name = "txnId") String txnId){ //orderId=1
        return paymentService.completePayment(orderId, txnId);
    }
}
