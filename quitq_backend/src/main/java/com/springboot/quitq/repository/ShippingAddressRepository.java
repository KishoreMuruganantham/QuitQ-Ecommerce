package com.springboot.quitq.repository;

import com.springboot.quitq.model.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress,Long> {

    /*
    JpaRepository <Interface>
    save(T) : T
    findAll() : List<T>
    findById(id) : Optional<T>
    delete(T) : void
    deleteById(id) : void
    * */

    List<ShippingAddress> findByCustomerId(Long customerId); // select sa from ShippingAddress sa where sa.customer.id=?1
}
// JpaRepository : spring data jpa

// class ShippingAddressRepository implements JpaRepository : implement all these methods
// interface ShippingAddressRepository extends JpaRepository : i get all pre created methods
