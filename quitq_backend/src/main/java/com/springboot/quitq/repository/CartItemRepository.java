package com.springboot.quitq.repository;

import com.springboot.quitq.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    /*
    JpaRepository <Interface>
    save(T) : T
    findAll() : List<T>
    findById(id) : Optional<T>
    delete(T) : void
    deleteById(id) : void
    * */

    List<CartItem> findByCartId(Long cartId); // select ci from CartItem ci where ci.cart.id=?1

    void deleteByCartId(Long cartId); // delete from CartItem ci where ci.cart.id=?1
}
// JpaRepository : spring data jpa

// class CartItemRepository implements JpaRepository : implement all these methods
// interface CartItemRepository extends JpaRepository : i get all pre created methods
