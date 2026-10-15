package com.springboot.quitq.controller;

import com.springboot.quitq.dto.response.OrderStatusStatDto;
import com.springboot.quitq.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/api/admin/analytics/orders-by-status")
    public List<OrderStatusStatDto> getOrderStatusStat(){
        return adminService.getOrderStatusStat();
    }

    @GetMapping("/api/admin/users/count")
    public long getUserCount(){
        return adminService.getUserCount();
    }

    @GetMapping("/api/admin/sellers/count")
    public long getSellerCount(){
        return adminService.getSellerCount();
    }
}
