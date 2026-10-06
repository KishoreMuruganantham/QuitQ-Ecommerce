package com.springboot.quitq.service;

import com.springboot.quitq.exception.ResourceNotFoundException;
import com.springboot.quitq.model.Category;
import com.springboot.quitq.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category insert(Category category) {
        Category saved = categoryRepository.save(category);
        System.out.println("record inserted...");
        return saved;
    }

    public Category getById(long id) {
        Optional<Category> optional = categoryRepository.findById(id);
        if(optional.isEmpty())
            throw new ResourceNotFoundException("Invalid category id");

        return optional.get();
    }

    public List<Category> getAll() {
        List<Category> list = categoryRepository.findAll();
        list.forEach(System.out :: println);
        return list;
    }

    public void deleteById(long id) {
        // Step 1: Check if id is valid
        getById(id);
        // Step 2: delete the record
        categoryRepository.deleteById(id);
    }
}
