package com.springboot.quitq.mapper;

import com.springboot.quitq.dto.request.ProductDto;
import com.springboot.quitq.dto.response.ProductInfoDto;
import com.springboot.quitq.dto.response.ProductRespDto;
import com.springboot.quitq.model.Product;

public class ProductMapper {

    public static Product convertDtoToEntity(ProductDto dto){
        Product product = new Product();
        product.setName(dto.name());
        product.setBrand(dto.brand());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStockQty(dto.stockQty());
        product.setImageUrl(dto.imageUrl());
        return product;
    }

    public static ProductRespDto convertEntityToDto(Product product){
        return
                new ProductRespDto (
                        product.getId(),
                        product.getName(),
                        product.getBrand(),
                        product.getPrice(),
                        product.getStockQty(),
                        product.getStatus()
                );
    }

    public static ProductInfoDto convertEntityToInfoDto(Product product){
        return
                new ProductInfoDto (
                        product.getId(),
                        product.getName(),
                        product.getBrand(),
                        product.getPrice(),
                        product.getStockQty(),
                        product.getCategory() .getName(),
                        product.getSeller() .getBusinessName(),
                        product.getImageUrl()
                );
    }
}
