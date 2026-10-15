package com.springboot.quitq.repository;

import com.springboot.quitq.dto.response.ProductRespDto;
import com.springboot.quitq.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findByBrand(String brand);

    List<Product> findByPriceBetween(Double min, Double max);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByCategoryId(Long categoryId);

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
