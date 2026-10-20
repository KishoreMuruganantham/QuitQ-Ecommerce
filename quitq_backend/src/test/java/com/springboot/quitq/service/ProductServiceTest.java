package com.springboot.quitq.service;

import com.springboot.quitq.dto.response.ProductInfoDto;
import com.springboot.quitq.dto.response.ProductRespDto;
import com.springboot.quitq.enums.ProductStatus;
import com.springboot.quitq.exception.ResourceNotFoundException;
import com.springboot.quitq.model.Category;
import com.springboot.quitq.model.Product;
import com.springboot.quitq.model.Seller;
import com.springboot.quitq.repository.CategoryRepository;
import com.springboot.quitq.repository.ProductRepository;
import com.springboot.quitq.repository.SellerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Test
    public void getAllTest() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setBrand("Dell");
        product1.setPrice(50000.0);
        product1.setStockQty(10);
        product1.setStatus(ProductStatus.IN_STOCK);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Phone");
        product2.setBrand("Apple");
        product2.setPrice(90000.0);
        product2.setStockQty(5);
        product2.setStatus(ProductStatus.IN_STOCK);

        when(productRepository.findAll(PageRequest.of(0, 2)))
                .thenReturn(new PageImpl<>(List.of(product1, product2)));

        List<ProductRespDto> result = productService.getAll(0, 2);

        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).name());

        verify(productRepository, times(1)).findAll(PageRequest.of(0, 2));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void getByIdTest() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Seller seller = new Seller();
        seller.setId(1L);
        seller.setBusinessName("TechStore");

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setBrand("Dell");
        product.setDescription("Thin and light laptop");
        product.setPrice(50000.0);
        product.setStockQty(10);
        product.setImageUrl("laptop.png");
        product.setStatus(ProductStatus.IN_STOCK);
        product.setCategory(category);
        product.setSeller(seller);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductInfoDto result = productService.getById(1L);

        assertEquals("Laptop", result.name());
        assertEquals("Electronics", result.categoryName());
        assertEquals("TechStore", result.sellerName());

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void getByIdTest_Invalid() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getById(99L));

        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void unpurchasedTest() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setBrand("Dell");
        product1.setPrice(50000.0);
        product1.setStockQty(10);
        product1.setStatus(ProductStatus.IN_STOCK);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Phone");
        product2.setBrand("Apple");
        product2.setPrice(90000.0);
        product2.setStockQty(5);
        product2.setStatus(ProductStatus.IN_STOCK);

        when(productRepository.findUnpurchased(PageRequest.of(0, 20)))
                .thenReturn(List.of(product1, product2));

        List<ProductRespDto> result = productService.unpurchased(0, 20);

        assertEquals(2, result.size());

        verify(productRepository, times(1)).findUnpurchased(PageRequest.of(0, 20));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void dynamicFilterTest() {
        ProductRespDto dto1 = new ProductRespDto(1L, "Laptop", "Dell", 50000.0, 10, ProductStatus.IN_STOCK);
        ProductRespDto dto2 = new ProductRespDto(2L, "Phone", "Apple", 90000.0, 5, ProductStatus.IN_STOCK);

        when(productRepository.dynamicFilterProduct("Dell", 1L, 100.0, 200.0, PageRequest.of(0, 5)))
                .thenReturn(List.of(dto1, dto2));

        List<ProductRespDto> result = productService.dynamicFilter("Dell", 1L, 100.0, 200.0, 0, 5);

        assertEquals(2, result.size());

        verify(productRepository, times(1))
                .dynamicFilterProduct("Dell", 1L, 100.0, 200.0, PageRequest.of(0, 5));
        verifyNoMoreInteractions(productRepository);
    }
}
