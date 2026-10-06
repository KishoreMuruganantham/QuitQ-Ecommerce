package com.springboot.quitq.repository;

import com.springboot.quitq.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    /*
    JpaRepository <Interface>
    save(T) : T
    findAll() : List<T>
    findById(id) : Optional<T>
    delete(T) : void
    deleteById(id) : void
    * */
}
// JpaRepository : spring data jpa

// class CategoryRepository implements JpaRepository : implement all these methods
// interface CategoryRepository extends JpaRepository : i get all pre created methods
