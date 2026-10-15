package com.springboot.quitq.repository;

import com.springboot.quitq.dto.response.OrderItemInfoDto;
import com.springboot.quitq.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

    List<OrderItem> findByOrdersId(Long orderId);

    @Query("""
            select new com.springboot.quitq.dto.response.OrderItemInfoDto(oi.id,o.orderNumber,
            c.name,p.name,oi.quantity,oi.totalPrice,oi.status)
            from OrderItem oi
            JOIN oi.orders o
            JOIN o.customer c
            JOIN oi.product p
            where oi.seller.id = ?1
            """)
    List<OrderItemInfoDto> getOrdersBySellerId(Long sellerId);
}
