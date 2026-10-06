package com.kodewala.ecommerce.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.kodewala.ecommerce.model.Product;

public class ProductRepository {
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public Optional<Product> findById(int productId) {
        return products.stream()
                .filter(p -> p.getProductId() == productId)
                .findFirst();
    }

    public boolean deleteById(int productId) {
        return products.removeIf(p -> p.getProductId() == productId);
    }

    public boolean existsById(int productId) {
        return products.stream().anyMatch(p -> p.getProductId() == productId);
    }
}
