package com.springboot.quitq.repository;

import com.springboot.quitq.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    /*
    JpaRepository <Interface>
    save(T) : T
    findAll() : List<T>
    findById(id) : Optional<T>
    delete(T) : void
    deleteById(id) : void
    * */

    Optional<Cart> findByCustomerId(Long customerId); // select cart from Cart cart where cart.customer.id=?1 -- one cart per customer (UNIQUE)
}
// JpaRepository : spring data jpa

// class CartRepository implements JpaRepository : implement all these methods
// interface CartRepository extends JpaRepository : i get all pre created methods
