package com.demo.controllers;


import com.demo.entities.Product;
import com.demo.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping("/api/products")
@Tag(name = "Product Controller")
public class ProductController {
    @Autowired
    ProductService prodService;

    @Operation(summary = "Get user",description = "API Get all user")
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = prodService.getAll();
        return ResponseEntity.ok(products);
        //return http status 200 ok, with object product. Usage when finish return data from server no need create new data
    }

    @Operation(summary = "Add Product",description = "API Add more Product")
    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product prod) {
        Product createProd = prodService.addProduct(prod);
        return new ResponseEntity<>(createProd, HttpStatus.CREATED);
        //create a new ResponseEntity and return an object createProd with status 201 CREATED
    }

    @Operation(summary = "Remove Product",description = "API delete product by id (Ex:localhost:8080/api/products/delete/1)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            prodService.deleteProduct(id);
            return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Update Product",description = "API update product by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Product updatedProduct = prodService.updateProduct(id, productDetails);
        return ResponseEntity.ok(updatedProduct);
    }
}
