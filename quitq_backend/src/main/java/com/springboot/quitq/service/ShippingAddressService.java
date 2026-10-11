package com.springboot.quitq.service;

import com.springboot.quitq.dto.request.ShippingAddressDto;
import com.springboot.quitq.dto.response.ShippingAddressRespDto;
import com.springboot.quitq.exception.ResourceNotFoundException;
import com.springboot.quitq.mapper.ShippingAddressMapper;
import com.springboot.quitq.model.Customer;
import com.springboot.quitq.model.ShippingAddress;
import com.springboot.quitq.repository.CustomerRepository;
import com.springboot.quitq.repository.ShippingAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShippingAddressService {

    private final ShippingAddressRepository shippingAddressRepository;
    private final CustomerRepository customerRepository; // injected, file owned by another agent

    public ShippingAddressRespDto add(Long customerId, ShippingAddressDto dto) {
        // Step 1: validate the customer id
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if(optionalCustomer.isEmpty())
            throw new ResourceNotFoundException("Invalid customer id");

        // Step 2: convert dto to entity
        ShippingAddress address = ShippingAddressMapper.convertDtoToEntity(dto);

        // Step 3: attach the customer -- sa.customer
        address.setCustomer(optionalCustomer.get());

        // Step 4: save the record
        ShippingAddress saved = shippingAddressRepository.save(address);
        System.out.println("record inserted...");
        return ShippingAddressMapper.convertEntityToDto(saved);
    }

    public List<ShippingAddressRespDto> getAllByCustomer(Long customerId) {
        // Step 1: validate the customer id
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if(optionalCustomer.isEmpty())
            throw new ResourceNotFoundException("Invalid customer id");

        // Step 2: fetch every address of this customer
        List<ShippingAddress> list = shippingAddressRepository.findByCustomerId(customerId);
        return list
                .stream()
                .map(ShippingAddressMapper ::convertEntityToDto)
                .toList();
    }

    public ShippingAddressRespDto getById(long id) {
        Optional<ShippingAddress> optional = shippingAddressRepository.findById(id);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid address id");

        return ShippingAddressMapper.convertEntityToDto(optional.get());
    }

    public void deleteById(long id) {
        // Step 1: Check if id is valid
        getById(id);
        // Step 2: delete the record
        shippingAddressRepository.deleteById(id);
    }
}
