package com.springboot.quitq.repository;

import com.springboot.quitq.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Seller,Long> {

    @Query("""
            select s from Seller s
            JOIN s.user u
            where u.username=?1
            """)
    Seller getByUsername(String sellerUsername);
}
