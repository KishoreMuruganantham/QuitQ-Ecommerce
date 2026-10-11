package com.springboot.quitq.controller;

import com.springboot.quitq.dto.request.ProductDto;
import com.springboot.quitq.dto.response.ProductInfoDto;
import com.springboot.quitq.dto.response.ProductRespDto;
import com.springboot.quitq.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /* seller adds his own product : /api/product/add/1 */
    @PostMapping("/api/product/add/{sellerId}")
    public ProductRespDto insertProduct(@Valid @RequestBody ProductDto productDto,
                                        @PathVariable long sellerId){ //sellerId=1
        return productService.insert(productDto, sellerId);
    }

    @GetMapping("/api/product/{id}") ///api/product/1
    public ProductInfoDto getById(@PathVariable long id){ //id=1
        return productService.getById(id);
    }

    @GetMapping("/api/product/all")
    public List<ProductRespDto> getAll(){
        return productService.getAll();
    }

    @GetMapping("/api/product/brand") ///api/product/brand?brand=nike
    public List<ProductRespDto> getByBrand(@RequestParam(name = "brand") String brand){ //brand=nike
        return productService.getByBrand(brand);
    }

    @GetMapping("/api/product/price") ///api/product/price?min=100&max=500
    public List<ProductRespDto> getByPriceRange(@RequestParam(name = "min") Double min,
                                                @RequestParam(name = "max") Double max){ //min=100&max=500
        return productService.getByPriceRange(min, max);
    }

    @GetMapping("/api/product/search") ///api/product/search?keyword=shirt
    public List<ProductRespDto> searchByName(@RequestParam(name = "keyword") String keyword){ //keyword=shirt
        return productService.searchByName(keyword);
    }

    @GetMapping("/api/product/category/{categoryId}") ///api/product/category/1
    public List<ProductRespDto> getByCategory(@PathVariable long categoryId){ //categoryId=1
        return productService.getByCategory(categoryId);
    }

    /* ALL params are optional : /api/product/filter?brand=nike&categoryId=1&minPrice=100&maxPrice=500 */
    @GetMapping("/api/product/filter")
    public List<ProductRespDto> dynamicFilter(@RequestParam(name = "brand", required = false) String brand,
                                              @RequestParam(name = "categoryId", required = false) Long categoryId,
                                              @RequestParam(name = "minPrice", required = false) Double minPrice,
                                              @RequestParam(name = "maxPrice", required = false) Double maxPrice){
        return productService.dynamicFilter(brand, categoryId, minPrice, maxPrice);
    }

    @PutMapping("/api/product/{id}/out-of-stock") ///api/product/1/out-of-stock
    public void updateStatus(@PathVariable long id){ //id=1
        productService.updateStatus(id);
    }

    @DeleteMapping("/api/product/{id}")
    public void deleteById(@PathVariable long id){
        productService.deleteById(id);
    }
}
/*
    DYNAMIC FILTER IDEA : one query serves every combination of search boxes
    -----------------------------------------------------------------------
    /api/product/filter?brand=nike&categoryId=1&minPrice=100&maxPrice=500
    /api/product/filter?brand=nike
    /api/product/filter?minPrice=100
    (?1 IS NULL OR p.brand = ?1) -> if param is not given, condition stays open, all rows pass
    same idea for ?2 categoryId, ?3 minPrice, ?4 maxPrice
 */
