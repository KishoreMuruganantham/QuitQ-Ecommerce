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
    private final UserRepository userRepository; // injected, file owned by another agent
    private final SellerRepository sellerRepository; // injected, file owned by another agent

    public List<OrderStatusStatDto> getOrderStatusStat() {
        // group by o.orderStatus done inside the repository
        return ordersRepository.getOrdersByStatusStat();
    }

    public long getUserCount() {
        return userRepository.count(); // select count(u.id) from User u
    }

    public long getSellerCount() {
        return sellerRepository.count(); // select count(s.id) from Seller s
    }
}
