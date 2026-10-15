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

    @PostMapping("/api/product/add/{sellerId}")
    public ProductRespDto insertProduct(@Valid @RequestBody ProductDto productDto,
                                        @PathVariable long sellerId){
        return productService.insert(productDto, sellerId);
    }

    @GetMapping("/api/product/{id}")
    public ProductInfoDto getById(@PathVariable long id){
        return productService.getById(id);
    }

    @GetMapping("/api/product/all")
    public List<ProductRespDto> getAll(){
        return productService.getAll();
    }

    @GetMapping("/api/product/brand")
    public List<ProductRespDto> getByBrand(@RequestParam(name = "brand") String brand){
        return productService.getByBrand(brand);
    }

    @GetMapping("/api/product/price")
    public List<ProductRespDto> getByPriceRange(@RequestParam(name = "min") Double min,
                                                @RequestParam(name = "max") Double max){
        return productService.getByPriceRange(min, max);
    }

    @GetMapping("/api/product/search")
    public List<ProductRespDto> searchByName(@RequestParam(name = "keyword") String keyword){
        return productService.searchByName(keyword);
    }

    @GetMapping("/api/product/category/{categoryId}")
    public List<ProductRespDto> getByCategory(@PathVariable long categoryId){
        return productService.getByCategory(categoryId);
    }

    @GetMapping("/api/product/filter")
    public List<ProductRespDto> dynamicFilter(@RequestParam(name = "brand", required = false) String brand,
                                              @RequestParam(name = "categoryId", required = false) Long categoryId,
                                              @RequestParam(name = "minPrice", required = false) Double minPrice,
                                              @RequestParam(name = "maxPrice", required = false) Double maxPrice){
        return productService.dynamicFilter(brand, categoryId, minPrice, maxPrice);
    }

    @PutMapping("/api/product/{id}/out-of-stock")
    public void updateStatus(@PathVariable long id){
        productService.updateStatus(id);
    }

    @DeleteMapping("/api/product/{id}")
    public void deleteById(@PathVariable long id){
        productService.deleteById(id);
    }
}

