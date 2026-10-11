package com.springboot.quitq.mapper;

import com.springboot.quitq.dto.request.ShippingAddressDto;
import com.springboot.quitq.dto.response.ShippingAddressRespDto;
import com.springboot.quitq.model.ShippingAddress;

public class ShippingAddressMapper {

    public static ShippingAddress convertDtoToEntity(ShippingAddressDto dto){
        ShippingAddress address = new ShippingAddress();
        address.setRecipientName(dto.recipientName());
        address.setPhone(dto.phone());
        address.setStreetAddress(dto.streetAddress());
        address.setCity(dto.city());
        address.setState(dto.state());
        address.setPostalCode(dto.postalCode());
        address.setDefault(dto.isDefault());
        return address;
    }

    public static ShippingAddressRespDto convertEntityToDto(ShippingAddress address){
        return
                new ShippingAddressRespDto (
                        address.getId(),
                        address.getRecipientName(),
                        address.getPhone(),
                        address.getStreetAddress(),
                        address.getCity(),
                        address.getState(),
                        address.getPostalCode(),
                        address.isDefault()
                );
    }
}
