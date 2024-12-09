package com.demo.services;

import com.demo.entities.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAll();
    Product addProduct(Product prod);
    void deleteProduct(Long id);
    Product updateProduct(Long id,Product prod);

}
