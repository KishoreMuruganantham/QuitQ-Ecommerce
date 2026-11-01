package com.springboot.quitq.repository;

import com.springboot.quitq.dto.response.DetailsDto;
import com.springboot.quitq.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("""
            select u
            from User u
            where u.username=?1 AND u.isActive = ?2
            """)
    User loadUserByUsername(String username, boolean isActive);

    @Query("""
            select u.id,u.username,u.isActive,u.role
            from User u
            where u.username=?1
            """)
    DetailsDto getUserDetails(String loggedInUsername);
}
