package com.springboot.quitq.service;

import com.springboot.quitq.dto.request.CategoryDto;
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

    public Category insert(CategoryDto categoryDto) {

        Category category = new Category();
        category.setName(categoryDto.name());
        category.setDescription(categoryDto.description());
        category.setSeq(categoryDto.seq());

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

        getById(id);

        categoryRepository.deleteById(id);
    }

    public Category update(Long id, CategoryDto categoryDto) {

        Category category = getById(id);

        category.setName(categoryDto.name());
        category.setDescription(categoryDto.description());
        category.setSeq(categoryDto.seq());

        return categoryRepository.save(category);
    }
}
