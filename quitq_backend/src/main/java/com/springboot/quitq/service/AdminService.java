package com.springboot.quitq.service;

import com.springboot.quitq.dto.response.OrderStatusStatDto;
import com.springboot.quitq.repository.OrdersRepository;
import com.springboot.quitq.repository.SellerRepository;
import com.springboot.quitq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    public List<OrderStatusStatDto> getOrderStatusStat() {

        return ordersRepository.getOrdersByStatusStat();
    }

    public long getUserCount() {
        return userRepository.count();
    }

    public long getSellerCount() {
        return sellerRepository.count();
    }
}
