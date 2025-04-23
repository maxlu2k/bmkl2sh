package com.demo.services;

import com.demo.dto.request.CategoriesRequest;
import com.demo.dto.response.CategoriesResponse;

import java.util.List;

public interface CategoriesService {
    List<CategoriesResponse> getAll();
    CategoriesResponse getById(Long id);
    CategoriesResponse create(CategoriesRequest request);
    CategoriesResponse update(Long id, CategoriesRequest request);
    void delete(Long id);
}
