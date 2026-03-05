package com.brian.transaction_importer_spring.controller.api;

import com.brian.transaction_importer_spring.dto.CategoryDTO;
import com.brian.transaction_importer_spring.entity.Category;
import com.brian.transaction_importer_spring.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryApiController {

    private final CategoryRepository categoryRepository;

    @GetMapping("/")
    public List<CategoryDTO> findAllCategories() {
        return categoryRepository.findAll().stream().map(CategoryDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CategoryDTO findAccountJsonById(@PathVariable final Long id) {
        Category category = categoryRepository.findById(id).get();
        if (categoryRepository == null) {
            return null;
        }

        return new CategoryDTO(category);
    }

    @PutMapping
    public CategoryDTO updateCategory(@RequestBody final CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(categoryDTO.getId()).get();
        if (categoryDTO.getParentCategoryId() == null) {
            Category parentCategory = categoryRepository.findById(categoryDTO.getParentCategoryId()).get();
            category.setParent(parentCategory);
        }

        category.setName(categoryDTO.getName());
        return new CategoryDTO(categoryRepository.save(category));
    }

    @PostMapping
    public CategoryDTO createCategory(@RequestBody final CategoryDTO categoryDTO) {
        Category parentCategory = categoryRepository.findById(categoryDTO.getParentCategoryId()).get();

        Category  category = new Category();
        if (parentCategory != null) {
            category.setParent(parentCategory);
        }
        category.setName(categoryDTO.getName());
        return new CategoryDTO(categoryRepository.save(category));
    }
}
