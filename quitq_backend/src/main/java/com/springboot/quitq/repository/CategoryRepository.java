package com.springboot.quitq.repository;

import com.springboot.quitq.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {

}

