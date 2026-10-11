package com.springboot.quitq.repository;

import com.springboot.quitq.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    /*
    JpaRepository <Interface>
    save(T) : T
    findAll() : List<T>
    findById(id) : Optional<T>
    delete(T) : void
    deleteById(id) : void
    * */

    Optional<User> findByUsername(String username); // select u from User u where u.username=?1

    Optional<User> findByEmail(String email); // select u from User u where u.email=?1
}
