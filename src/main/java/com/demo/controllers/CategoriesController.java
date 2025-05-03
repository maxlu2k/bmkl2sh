package com.demo.controllers;

import com.demo.dto.request.CategoriesRequest;
import com.demo.dto.response.CategoriesResponse;
import com.demo.services.impl.CategoriesServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category Controller")
public class CategoriesController {

    @Autowired
    private CategoriesServiceImpl categoriesService;

    @GetMapping
    public ResponseEntity<List<CategoriesResponse>> getAll() {
        return ResponseEntity.ok(categoriesService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriesResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriesService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CategoriesResponse> create(@RequestBody CategoriesRequest request) {
        return ResponseEntity.ok(categoriesService.create(request));
    }

    @PostMapping
    public ResponseEntity<CategoriesResponse> createNoID(@RequestBody CategoriesRequest request) {
        return ResponseEntity.ok(categoriesService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriesResponse> update(@PathVariable Long id, @RequestBody CategoriesRequest request) {
        return ResponseEntity.ok(categoriesService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriesService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

