package com.springboot.quitq.repository;

import com.springboot.quitq.dto.response.ProductRespDto;
import com.springboot.quitq.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    /*
    JpaRepository <Interface>
    save(T) : T
    findAll() : List<T>
    findById(id) : Optional<T>
    delete(T) : void
    deleteById(id) : void
    * */

    List<Product> findByBrand(String brand); // select p from Product p where p.brand=?1

    List<Product> findByPriceBetween(Double min, Double max); // select p from Product p where p.price between ?1 and ?2

    List<Product> findByNameContainingIgnoreCase(String keyword); // select p from Product p where lower(p.name) like lower(concat('%',?1,'%'))

    List<Product> findByCategoryId(Long categoryId); // select p from Product p where p.category.id=?1

    // DYNAMIC FILTER : every param is optional, (?1 IS NULL OR ...) keeps the condition open
    @Query("""
            select p.id as id, p.name as name, p.brand as brand, p.price as price,
            p.stockQty as stockQty, p.status as status
            from Product p
            where (?1 IS NULL OR p.brand = ?1)
            AND  (?2 IS NULL OR p.category.id = ?2)
            AND  (?3 IS NULL OR p.price >= ?3)
            AND  (?4 IS NULL OR p.price <= ?4)
            """)
    List<ProductRespDto> dynamicFilterProduct(String brand, Long categoryId, Double minPrice, Double maxPrice);
}
