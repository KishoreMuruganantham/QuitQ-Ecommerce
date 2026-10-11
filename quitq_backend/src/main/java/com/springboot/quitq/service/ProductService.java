package com.springboot.quitq.service;

import com.springboot.quitq.dto.request.ProductDto;
import com.springboot.quitq.dto.response.ProductInfoDto;
import com.springboot.quitq.dto.response.ProductRespDto;
import com.springboot.quitq.enums.ProductStatus;
import com.springboot.quitq.exception.ResourceNotFoundException;
import com.springboot.quitq.mapper.ProductMapper;
import com.springboot.quitq.model.Category;
import com.springboot.quitq.model.Product;
import com.springboot.quitq.model.Seller;
import com.springboot.quitq.repository.CategoryRepository;
import com.springboot.quitq.repository.ProductRepository;
import com.springboot.quitq.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;

    public ProductRespDto insert(ProductDto productDto, long sellerId) {
        // Step 1: validate the category id : p.category
        Category category = getCategory(productDto.categoryId());
        // Step 2: validate the seller id : p.seller
        Seller seller = getSeller(sellerId);
        // Step 3: convert dto to entity
        Product product = ProductMapper.convertDtoToEntity(productDto);
        // Step 4: attach the category and the seller, product always starts as IN_STOCK
        product.setCategory(category);
        product.setSeller(seller);
        product.setStatus(ProductStatus.IN_STOCK);
        // Step 5: save the record
        Product saved = productRepository.save(product);
        System.out.println("record inserted...");
        return ProductMapper.convertEntityToDto(saved);
    }

    public ProductInfoDto getById(long id) {
        Optional<Product> optional = productRepository.findById(id);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid product id");

        return ProductMapper.convertEntityToInfoDto(optional.get());
    }

    public List<ProductRespDto> getAll() {
        List<Product> list = productRepository.findAll();
        return list
                .stream()
                .map(ProductMapper ::convertEntityToDto)
                .toList();
    }

    public List<ProductRespDto> getByBrand(String brand) {
        List<Product> list = productRepository.findByBrand(brand);
        return list
                .stream()
                .map(ProductMapper ::convertEntityToDto)
                .toList();
    }

    public List<ProductRespDto> getByPriceRange(Double min, Double max) {
        List<Product> list = productRepository.findByPriceBetween(min, max);
        return list
                .stream()
                .map(ProductMapper ::convertEntityToDto)
                .toList();
    }

    public List<ProductRespDto> searchByName(String keyword) {
        List<Product> list = productRepository.findByNameContainingIgnoreCase(keyword);
        return list
                .stream()
                .map(ProductMapper ::convertEntityToDto)
                .toList();
    }

    public List<ProductRespDto> getByCategory(Long categoryId) {
        List<Product> list = productRepository.findByCategoryId(categoryId);
        return list
                .stream()
                .map(ProductMapper ::convertEntityToDto)
                .toList();
    }

    public List<ProductRespDto> dynamicFilter(String brand, Long categoryId, Double minPrice, Double maxPrice) {
        return productRepository.dynamicFilterProduct(brand, categoryId, minPrice, maxPrice);
    }

    public void updateStatus(long id) {
        // Step 1: fetch the product
        Optional<Product> optional = productRepository.findById(id);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid product id");

        // Step 2: mark the product OUT_OF_STOCK
        Product product = optional.get();
        product.setStatus(ProductStatus.OUT_OF_STOCK);
        // Step 3: save the record
        productRepository.save(product);
    }

    public void deleteById(long id) {
        // Step 1: Check if id is valid
        getById(id);
        // Step 2: delete the record
        productRepository.deleteById(id);
    }

    private Category getCategory(Long categoryId) {
        if(categoryId == null)
            throw new ResourceNotFoundException("Invalid category id");

        Optional<Category> optional = categoryRepository.findById(categoryId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid category id");

        return optional.get();
    }

    private Seller getSeller(long sellerId) {
        Optional<Seller> optional = sellerRepository.findById(sellerId);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid seller id");

        return optional.get();
    }
}
