package com.demo.services.impl;

import com.demo.dto.request.CategoriesRequest;
import com.demo.dto.response.CategoriesResponse;
import com.demo.entities.Categories;
import com.demo.mappers.CategoriesMapper;
import com.demo.repositories.CategoriesRepository;
import com.demo.services.CategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository categoryRepository;
    private final CategoriesMapper categoriesMapper;

    @Override
    public List<CategoriesResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(categoriesMapper::toCategoryResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public CategoriesResponse getById(Long id) {
        Categories category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return categoriesMapper.toCategoryResponse(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public CategoriesResponse create(CategoriesRequest request) {
        Categories category = categoriesMapper.toCategory(request);

        // Nếu có parentId, tìm danh mục cha
        if (request.getParentId() != null) {
            Categories parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parentCategory);
        }

        Categories savedCategory = categoryRepository.save(category);
        return categoriesMapper.toCategoryResponse(savedCategory);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public CategoriesResponse update(Long id, CategoriesRequest request) {
        Categories category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setCatname(request.getCatname());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());

        // Cập nhật danh mục cha
        if (request.getParentId() != null) {
            Categories parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parentCategory);
        } else {
            category.setParent(null);
        }

        Categories updatedCategory = categoryRepository.save(category);
        return categoriesMapper.toCategoryResponse(updatedCategory);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void delete(Long id) {
        Categories category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        categoryRepository.delete(category);
    }

}
