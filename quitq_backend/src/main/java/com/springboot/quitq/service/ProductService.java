package com.springboot.quitq.service;

import com.springboot.quitq.dto.request.ProductDto;
import com.springboot.quitq.dto.response.ProductInfoDto;
import com.springboot.quitq.dto.response.ProductRespDto;
import com.springboot.quitq.enums.ProductStatus;
import com.springboot.quitq.exception.OwnershipException;
import com.springboot.quitq.exception.ResourceNotFoundException;
import com.springboot.quitq.mapper.ProductMapper;
import com.springboot.quitq.model.Category;
import com.springboot.quitq.model.Product;
import com.springboot.quitq.model.Seller;
import com.springboot.quitq.repository.CategoryRepository;
import com.springboot.quitq.repository.ProductRepository;
import com.springboot.quitq.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;

    public ProductRespDto insert(ProductDto productDto, long sellerId, String username) {

        Category category = getCategory(productDto.categoryId());

        Seller seller = sellerRepository.getByUsername(username);
        if(seller == null)
            throw new ResourceNotFoundException("Seller not found for the logged in user");
        if(!seller.getId().equals(sellerId))
            throw new OwnershipException("Seller id does not belong to the logged in user");

        Product product = ProductMapper.convertDtoToEntity(productDto);

        product.setCategory(category);
        product.setSeller(seller);
        product.setStatus(ProductStatus.IN_STOCK);

        Product saved = productRepository.save(product);
        log.info("record inserted...");
        return ProductMapper.convertEntityToDto(saved);
    }

    public ProductInfoDto getById(long id) {
        Optional<Product> optional = productRepository.findById(id);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid product id");

        return ProductMapper.convertEntityToInfoDto(optional.get());
    }

    public List<ProductRespDto> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Product> list = productRepository.findAll(pageable).getContent();
        return list
                .stream()
                .map(ProductMapper ::convertEntityToDto)
                .toList();
    }

    public List<ProductRespDto> getByBrand(String brand, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Product> list = productRepository.findByBrand(brand, pageable);
        return list
                .stream()
                .map(ProductMapper ::convertEntityToDto)
                .toList();
    }

    public List<ProductRespDto> getByPriceRange(Double min, Double max, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Product> list = productRepository.findByPriceBetween(min, max, pageable);
        return list
                .stream()
                .map(ProductMapper ::convertEntityToDto)
                .toList();
    }

    public List<ProductRespDto> searchByName(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Product> list = productRepository.findByNameContainingIgnoreCase(keyword, pageable);
        return list
                .stream()
                .map(ProductMapper ::convertEntityToDto)
                .toList();
    }

    public List<ProductRespDto> getByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Product> list = productRepository.findByCategoryId(categoryId, pageable);
        return list
                .stream()
                .map(ProductMapper ::convertEntityToDto)
                .toList();
    }

    public List<ProductRespDto> dynamicFilter(String brand, Long categoryId, Double minPrice, Double maxPrice, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.dynamicFilterProduct(brand, categoryId, minPrice, maxPrice, pageable);
    }

    public List<ProductRespDto> unpurchased(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Product> list = productRepository.findUnpurchased(pageable);
        return list
                .stream()
                .map(ProductMapper ::convertEntityToDto)
                .toList();
    }

    public ProductRespDto update(long id, ProductDto productDto, String username) {

        Optional<Product> optional = productRepository.findById(id);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid product id");

        Product product = optional.get();
        String owner = product.getSeller().getUser().getUsername();
        if(owner == null || !owner.equals(username))
            throw new OwnershipException("Product does not belong to the logged in user");

        Category category = getCategory(productDto.categoryId());

        product.setName(productDto.name());
        product.setBrand(productDto.brand());
        product.setDescription(productDto.description());
        product.setPrice(productDto.price());
        if(productDto.stockQty() != null) {
            product.setStockQty(productDto.stockQty());
            product.setStatus(productDto.stockQty() > 0 ? ProductStatus.IN_STOCK : ProductStatus.OUT_OF_STOCK);
        }
        product.setImageUrl(productDto.imageUrl());
        product.setCategory(category);

        Product saved = productRepository.save(product);
        return ProductMapper.convertEntityToDto(saved);
    }

    public void updateStatus(long id, String username) {

        Optional<Product> optional = productRepository.findById(id);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid product id");

        Product product = optional.get();
        String owner = product.getSeller().getUser().getUsername();
        if(owner == null || !owner.equals(username))
            throw new OwnershipException("Product does not belong to the logged in user");

        product.setStatus(ProductStatus.OUT_OF_STOCK);

        productRepository.save(product);
    }

    public void deleteById(long id, String username) {

        Optional<Product> optional = productRepository.findById(id);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid product id");

        Product product = optional.get();
        String owner = product.getSeller().getUser().getUsername();
        if(owner == null || !owner.equals(username))
            throw new OwnershipException("Product does not belong to the logged in user");

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
}
