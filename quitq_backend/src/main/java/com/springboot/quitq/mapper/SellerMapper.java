package com.springboot.quitq.mapper;

import com.springboot.quitq.dto.request.SellerDto;
import com.springboot.quitq.dto.response.SellerRespDto;
import com.springboot.quitq.model.Seller;
import com.springboot.quitq.model.User;

public class SellerMapper {

    public static Seller convertDtoToEntity(SellerDto dto, User user){
        Seller seller = new Seller();
        seller.setBusinessName(dto.businessName());
        seller.setContactNumber(dto.contactNumber());
        seller.setCity(dto.city());
        seller.setAddress(dto.address());
        seller.setUser(user);
        return seller;
    }

    public static SellerRespDto convertEntityToDto(Seller seller){
        return
                new SellerRespDto (
                        seller.getId(),
                        seller.getBusinessName(),
                        seller.getContactNumber(),
                        seller.getCity(),
                        seller.getAddress(),
                        seller.getUser().getUsername(),
                        seller.getUser().getEmail()
                );
    }
}
