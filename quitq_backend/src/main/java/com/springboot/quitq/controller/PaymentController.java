package com.springboot.quitq.controller;

import com.springboot.quitq.dto.response.PaymentRespDto;
import com.springboot.quitq.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/api/payment/{orderId}/complete")
    public PaymentRespDto completePayment(@PathVariable long orderId,
                                          @RequestParam(name = "txnId") String txnId,
                                          Principal principal){
        return paymentService.completePayment(orderId, txnId, principal.getName());
    }
}
