package com.springboot.quitq.repository;

import com.springboot.quitq.model.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking,Long> {

    /*
    JpaRepository <Interface>
    save(T) : T
    findAll() : List<T>
    findById(id) : Optional<T>
    delete(T) : void
    deleteById(id) : void
    * */

    List<OrderTracking> findByOrdersIdOrderByStatusTimeAsc(Long orderId); // select ot from OrderTracking ot where ot.orders.id=?1 order by ot.statusTime asc -- the status timeline
}
