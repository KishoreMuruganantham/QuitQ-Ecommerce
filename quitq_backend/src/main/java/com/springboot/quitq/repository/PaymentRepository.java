package com.springboot.quitq.repository;

import com.springboot.quitq.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

    /*
    JpaRepository <Interface>
    save(T) : T
    findAll() : List<T>
    findById(id) : Optional<T>
    delete(T) : void
    deleteById(id) : void
    * */

    Optional<Payment> findByOrdersId(Long orderId); // select pay from Payment pay where pay.orders.id=?1 -- one payment per order (UNIQUE)
}
