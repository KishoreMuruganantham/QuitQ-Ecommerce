package com.springboot.quitq.repository;

import com.springboot.quitq.dto.response.OrderStatusStatDto;
import com.springboot.quitq.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders,Long> {

    List<Orders> findByCustomerId(Long customerId);

    @Query("""
            select o.orderStatus as orderStatus, count(o.id) as numberOfOrders
            from Orders o
            group by o.orderStatus
            """)
    List<OrderStatusStatDto> getOrdersByStatusStat();
}
