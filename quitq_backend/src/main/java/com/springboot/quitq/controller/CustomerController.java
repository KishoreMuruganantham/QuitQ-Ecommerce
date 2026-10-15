package com.springboot.quitq.controller;

import com.springboot.quitq.dto.request.CustomerDto;
import com.springboot.quitq.dto.response.CustomerRespDto;
import com.springboot.quitq.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/api/customer/signup")
    public CustomerRespDto signupCustomer(@Valid @RequestBody CustomerDto customerDto){
        return customerService.signup(customerDto);
    }

    @GetMapping("/api/customer/all")
    public List<CustomerRespDto> getAll(){
        return customerService.getAll();
    }

    @GetMapping("/api/customer/{id}")
    public CustomerRespDto getById(@PathVariable long id){
        return customerService.getById(id);
    }

    @DeleteMapping("/api/customer/{id}")
    public void deleteById(@PathVariable long id){
        customerService.deleteById(id);
    }
}
