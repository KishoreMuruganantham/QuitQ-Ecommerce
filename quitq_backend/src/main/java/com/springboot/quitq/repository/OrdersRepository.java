package com.springboot.quitq.repository;

import com.springboot.quitq.dto.response.OrderStatusStatDto;
import com.springboot.quitq.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders,Long> {

    /*
    JpaRepository <Interface>
    save(T) : T
    findAll() : List<T>
    findById(id) : Optional<T>
    delete(T) : void
    deleteById(id) : void
    * */

    List<Orders> findByCustomerId(Long customerId); // select o from Orders o where o.customer.id=?1

    // STAT QUERY : group by the status, every column carries an alias so the record lines up
    @Query("""
            select o.orderStatus as orderStatus, count(o.id) as numberOfOrders
            from Orders o
            group by o.orderStatus
            """)
    List<OrderStatusStatDto> getOrdersByStatusStat();
}
