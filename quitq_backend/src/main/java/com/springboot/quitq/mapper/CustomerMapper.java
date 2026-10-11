package com.springboot.quitq.mapper;

import com.springboot.quitq.dto.request.CustomerDto;
import com.springboot.quitq.dto.response.CustomerRespDto;
import com.springboot.quitq.model.Customer;
import com.springboot.quitq.model.User;

public class CustomerMapper {

    public static Customer convertDtoToEntity(CustomerDto dto, User user){
        Customer customer = new Customer();
        customer.setName(dto.name());
        customer.setGender(dto.gender());
        customer.setContactNumber(dto.contactNumber());
        customer.setUser(user); //c.user -- the profile always points back to its user
        return customer;
    }

    public static CustomerRespDto convertEntityToDto(Customer customer){
        return
                new CustomerRespDto (
                        customer.getId(),
                        customer.getName(),
                        customer.getGender(),
                        customer.getContactNumber(),
                        customer.getUser().getUsername(),
                        customer.getUser().getEmail()
                );
    }
}
