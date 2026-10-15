package com.springboot.quitq.controller;

import com.springboot.quitq.dto.request.ShippingAddressDto;
import com.springboot.quitq.dto.response.ShippingAddressRespDto;
import com.springboot.quitq.service.ShippingAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AddressController {

    private final ShippingAddressService shippingAddressService;

    @PostMapping("/api/address/add/{customerId}")
    public ShippingAddressRespDto addAddress(@PathVariable long customerId, @Valid @RequestBody ShippingAddressDto dto){
        return shippingAddressService.add(customerId, dto);
    }

    @GetMapping("/api/address/all/{customerId}")
    public List<ShippingAddressRespDto> getAllByCustomer(@PathVariable long customerId){
        return shippingAddressService.getAllByCustomer(customerId);
    }

    @GetMapping("/api/address/{id}")
    public ShippingAddressRespDto getById(@PathVariable long id){
        return shippingAddressService.getById(id);
    }

    @DeleteMapping("/api/address/{id}")
    public void deleteById(@PathVariable long id){
        shippingAddressService.deleteById(id);
    }
}
