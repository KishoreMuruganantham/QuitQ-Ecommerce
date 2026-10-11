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

    /* Take this from user either thru react app */
    @PostMapping("/api/category/add")
    public Category insertCategory(@Valid @RequestBody CategoryDto categoryDto){
        return categoryService.insert(categoryDto);
    }

    @GetMapping("/api/category/all")
    public List<Category> getAll(){
        return categoryService.getAll();
    }

    @GetMapping("/api/category/{id}") ///api/category/1
    public Category getById(@PathVariable long id){ //id=1
        return categoryService.getById(id);
    }

    @DeleteMapping("/api/category/{id}")
    public void deleteById(@PathVariable long id){
        categoryService.deleteById(id);
    }

    @PutMapping("/api/category/{id}") ///api/category/1
    public Category updateCategory(@PathVariable long id, //id=1
                                   @Valid @RequestBody CategoryDto categoryDto){
        return categoryService.update(id, categoryDto);
    }
}
