package com.springboot.quitq.repository;

import com.springboot.quitq.model.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking,Long> {

    List<OrderTracking> findByOrdersIdOrderByStatusTimeAsc(Long orderId);
}
