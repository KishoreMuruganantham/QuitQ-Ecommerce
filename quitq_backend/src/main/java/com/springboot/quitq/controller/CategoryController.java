package com.springboot.quitq.controller;

import com.springboot.quitq.dto.request.CategoryDto;
import com.springboot.quitq.model.Category;
import com.springboot.quitq.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/api/category/add")
    public Category insertCategory(@Valid @RequestBody CategoryDto categoryDto){
        return categoryService.insert(categoryDto);
    }

    @GetMapping("/api/category/all")
    public List<Category> getAll(){
        return categoryService.getAll();
    }

    @GetMapping("/api/category/{id}")
    public Category getById(@PathVariable long id){
        return categoryService.getById(id);
    }

    @DeleteMapping("/api/category/{id}")
    public void deleteById(@PathVariable long id){
        categoryService.deleteById(id);
    }

    @PutMapping("/api/category/{id}")
    public Category updateCategory(@PathVariable long id,
                                   @Valid @RequestBody CategoryDto categoryDto){
        return categoryService.update(id, categoryDto);
    }
}
