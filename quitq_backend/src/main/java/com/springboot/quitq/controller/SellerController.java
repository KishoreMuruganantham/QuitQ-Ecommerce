package com.springboot.quitq.controller;

import com.springboot.quitq.dto.request.SellerDto;
import com.springboot.quitq.dto.response.SellerRespDto;
import com.springboot.quitq.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/api/seller/signup")
    public SellerRespDto signupSeller(@Valid @RequestBody SellerDto sellerDto){
        return sellerService.signup(sellerDto);
    }

    @GetMapping("/api/seller/all")
    public List<SellerRespDto> getAll(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                      @RequestParam(name = "size", required = false, defaultValue = "20") Integer size){
        return sellerService.getAll(page, size);
    }

    @GetMapping("/api/seller/{id}")
    public SellerRespDto getById(@PathVariable long id){
        return sellerService.getById(id);
    }

    @DeleteMapping("/api/seller/{id}")
    public void deleteById(@PathVariable long id){
        sellerService.deleteById(id);
    }
}
