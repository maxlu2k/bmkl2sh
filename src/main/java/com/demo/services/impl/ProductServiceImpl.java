package com.demo.services.impl;

import com.demo.entities.Product;
import com.demo.repositories.ProductRepository;
import com.demo.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository repoProduct;

    @Override
    public List<Product> getAll() {
        return repoProduct.findAll();
    }

    @Override
    public Product addProduct(Product prod) {
        return repoProduct.save(prod);
    }

    @Override
    public void deleteProduct(Long id) {
        repoProduct.deleteById(id);
    }

    @Override
    public Product updateProduct(Long id, Product productInput) {
//        Optional<Product> prodOptional = repoProduct.findById(id);
//        if(!prodOptional.isPresent()){
//            throw new RuntimeException("Product not found " + id);
//        }
//        Product prod = prodOptional.get();
        Product prod = repoProduct.findById(id)
                .orElseThrow(()-> new RuntimeException("Product not found id"+id));
        prod.setProdName(productInput.getProdName());
        prod.setProdPrice(productInput.getProdPrice());
        prod.setProdQuantity(productInput.getProdQuantity());
        prod.setProdDescription(productInput.getProdDescription());
        prod.setProdCreated(productInput.getProdCreated());
        prod.setProdCreatedBy(productInput.getProdCreatedBy());
        prod.setCategory(productInput.getCategory());
        prod.setIsActive(productInput.getIsActive());
        return repoProduct.save(prod);
    }


}
