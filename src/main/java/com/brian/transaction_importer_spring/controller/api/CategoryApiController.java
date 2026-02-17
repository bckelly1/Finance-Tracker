package com.brian.transaction_importer_spring.controller.api;

import com.brian.transaction_importer_spring.dto.CategoryDTO;
import com.brian.transaction_importer_spring.entity.Category;
import com.brian.transaction_importer_spring.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
