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
    public List<ProductRespDto> getAll(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                       @RequestParam(name = "size", required = false, defaultValue = "20") Integer size){
        return productService.getAll(page, size);
    }

    @GetMapping("/api/product/brand")
    public List<ProductRespDto> getByBrand(@RequestParam(name = "brand") String brand,
                                           @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                           @RequestParam(name = "size", required = false, defaultValue = "20") Integer size){
        return productService.getByBrand(brand, page, size);
    }

    @GetMapping("/api/product/price")
    public List<ProductRespDto> getByPriceRange(@RequestParam(name = "min") Double min,
                                                @RequestParam(name = "max") Double max,
                                                @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                @RequestParam(name = "size", required = false, defaultValue = "20") Integer size){
        return productService.getByPriceRange(min, max, page, size);
    }

    @GetMapping("/api/product/search")
    public List<ProductRespDto> searchByName(@RequestParam(name = "keyword") String keyword,
                                             @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                             @RequestParam(name = "size", required = false, defaultValue = "20") Integer size){
        return productService.searchByName(keyword, page, size);
    }

    @GetMapping("/api/product/category/{categoryId}")
    public List<ProductRespDto> getByCategory(@PathVariable long categoryId,
                                              @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                              @RequestParam(name = "size", required = false, defaultValue = "20") Integer size){
        return productService.getByCategory(categoryId, page, size);
    }

    @GetMapping("/api/product/filter")
    public List<ProductRespDto> dynamicFilter(@RequestParam(name = "brand", required = false) String brand,
                                              @RequestParam(name = "categoryId", required = false) Long categoryId,
                                              @RequestParam(name = "minPrice", required = false) Double minPrice,
                                              @RequestParam(name = "maxPrice", required = false) Double maxPrice,
                                              @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                              @RequestParam(name = "size", required = false, defaultValue = "20") Integer size){
        return productService.dynamicFilter(brand, categoryId, minPrice, maxPrice, page, size);
    }

    @GetMapping("/api/product/unpurchased")
    public List<ProductRespDto> unpurchased(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size){
        return productService.unpurchased(page, size);
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

