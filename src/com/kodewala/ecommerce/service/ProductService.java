package com.kodewala.ecommerce.service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.kodewala.ecommerce.exception.InsufficientStockException;
import com.kodewala.ecommerce.exception.InvalidQuantityException;
import com.kodewala.ecommerce.exception.ProductNotFoundException;
import com.kodewala.ecommerce.model.Product;
import com.kodewala.ecommerce.repository.ProductRepository;

public class ProductService {
    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addProduct(Product product) {
        if (product != null) {
            productRepository.addProduct(product);
        }
    }

    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }

    public Product getProductById(int productId) throws ProductNotFoundException {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " does not exist."));
    }

    //serch by name
    public List<Product> searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.getAllProducts().stream()
                .filter(p -> p.getProductName().toLowerCase().contains(keyword.toLowerCase().trim()))
                .collect(Collectors.toList());
    }
    
  //serch by category
    public List<Product> filterByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.getAllProducts().stream()
                .filter(p -> p.getCategory().toLowerCase().contains(category.toLowerCase().trim()))
                .collect(Collectors.toList());
    }

  //serch by brandName
    public List<Product> filterByBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.getAllProducts().stream()
                .filter(p -> p.getBrand().toLowerCase().contains(brand.toLowerCase().trim()))
                .collect(Collectors.toList());
    }

  //serch by priceRange
    public List<Product> filterByPriceRange(double minPrice, double maxPrice) {
        return productRepository.getAllProducts().stream()
                .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }
    
  //sort by price
    public List<Product> sortByPrice(boolean ascending) {
        return productRepository.getAllProducts().stream()
                .sorted(ascending
                        ? Comparator.comparingDouble(Product::getPrice)
                        : Comparator.comparingDouble(Product::getPrice).reversed())
                .collect(Collectors.toList());
    }

    
    public void updatePrice(int productId, double newPrice) throws ProductNotFoundException {
        Product product = getProductById(productId);
        product.setPrice(newPrice);
    }

    public void updateQuantity(int productId, int newQuantity) throws ProductNotFoundException, InvalidQuantityException {
        if (newQuantity < 0) {
            throw new InvalidQuantityException("Quantity cannot be negative: " + newQuantity);
        }
        Product product = getProductById(productId);
        product.setQuantity(newQuantity);
    }

    public void deleteProduct(int productId) throws ProductNotFoundException {
        boolean removed = productRepository.deleteById(productId);
        if (!removed) {
            throw new ProductNotFoundException("Product with ID " + productId + " does not exist.");
        }
    }

    public boolean checkAvailability(int productId, int requestedQuantity)
            throws ProductNotFoundException, InsufficientStockException, InvalidQuantityException {
        if (requestedQuantity <= 0) {
            throw new InvalidQuantityException("Requested quantity must be greater than zero.");
        }
        Product product = getProductById(productId);
        if (product.getQuantity() < requestedQuantity) {
            throw new InsufficientStockException("Requested quantity (" + requestedQuantity + ") exceeds available stock ("
                    + product.getQuantity() + ") for product: " + product.getProductName());
        }
        return true;
    }

    public Set<String> getUniqueCategories() {
        return productRepository.getAllProducts().stream()
                .map(Product::getCategory)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public Set<String> getUniqueBrands() {
        return productRepository.getAllProducts().stream()
                .map(Product::getBrand)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
