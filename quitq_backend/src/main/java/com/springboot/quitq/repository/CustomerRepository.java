package com.springboot.quitq.repository;

import com.springboot.quitq.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    @Query("""
            select c from Customer c
            JOIN c.user u
            where u.username=?1
            """)
    Customer getByUsername(String customerUsername);
}
