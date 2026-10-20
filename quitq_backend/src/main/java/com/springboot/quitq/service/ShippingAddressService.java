package com.springboot.quitq.service;

import com.springboot.quitq.dto.request.ShippingAddressDto;
import com.springboot.quitq.dto.response.ShippingAddressRespDto;
import com.springboot.quitq.exception.OwnershipException;
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
    private final CustomerRepository customerRepository;

    public ShippingAddressRespDto add(Long customerId, ShippingAddressDto dto, String username) {

        Customer customer = customerRepository.getByUsername(username);
        if(customer == null)
            throw new ResourceNotFoundException("Customer not found for the logged in user");
        if(!customer.getId().equals(customerId))
            throw new OwnershipException("Customer id does not belong to the logged in user");

        ShippingAddress address = ShippingAddressMapper.convertDtoToEntity(dto);

        address.setCustomer(customer);

        ShippingAddress saved = shippingAddressRepository.save(address);
        System.out.println("record inserted...");
        return ShippingAddressMapper.convertEntityToDto(saved);
    }

    public List<ShippingAddressRespDto> getAllByCustomer(Long customerId, String username) {

        Customer customer = customerRepository.getByUsername(username);
        if(customer == null)
            throw new ResourceNotFoundException("Customer not found for the logged in user");
        if(!customer.getId().equals(customerId))
            throw new OwnershipException("Customer id does not belong to the logged in user");

        List<ShippingAddress> list = shippingAddressRepository.findByCustomerId(customerId);
        return list
                .stream()
                .map(ShippingAddressMapper ::convertEntityToDto)
                .toList();
    }

    public ShippingAddressRespDto getById(long id, String username) {
        Optional<ShippingAddress> optional = shippingAddressRepository.findById(id);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid address id");

        ShippingAddress address = optional.get();
        String owner = address.getCustomer().getUser().getUsername();
        if(owner == null || !owner.equals(username))
            throw new OwnershipException("Address does not belong to the logged in user");

        return ShippingAddressMapper.convertEntityToDto(address);
    }

    public void deleteById(long id, String username) {

        getById(id, username);

        shippingAddressRepository.deleteById(id);
    }
}
