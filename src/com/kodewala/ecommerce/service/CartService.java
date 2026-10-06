package com.kodewala.ecommerce.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.kodewala.ecommerce.exception.InsufficientStockException;
import com.kodewala.ecommerce.exception.InvalidQuantityException;
import com.kodewala.ecommerce.exception.ProductNotFoundException;
import com.kodewala.ecommerce.model.CartItem;
import com.kodewala.ecommerce.model.Product;

public class CartService {
    // Required Collection: HashMap<Integer, List<CartItem>>
    private Map<Integer, List<CartItem>> customerCarts = new HashMap<>();

    public void addToCart(int customerId, Product product, int quantity)
            throws InvalidQuantityException, InsufficientStockException {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity to add must be greater than zero.");
        }

        List<CartItem> cart = customerCarts.computeIfAbsent(customerId, k -> new ArrayList<>());

        Optional<CartItem> existingItemOpt = cart.stream()
                .filter(item -> item.getProductId() == product.getProductId())
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + quantity;
            if (newQuantity > product.getQuantity()) {
                throw new InsufficientStockException("Cannot add " + quantity + " more. Only "
                        + product.getQuantity() + " available in stock (Cart already has " + existingItem.getQuantity() + ").");
            }
            existingItem.setQuantity(newQuantity);
            existingItem.setTotalPrice(existingItem.getPrice() * newQuantity);
        } else {
            if (quantity > product.getQuantity()) {
                throw new InsufficientStockException("Requested quantity (" + quantity
                        + ") exceeds available stock (" + product.getQuantity() + ") for product: " + product.getProductName());
            }
            CartItem newItem = new CartItem(product.getProductId(), product.getProductName(),
                    product.getPrice(), quantity, product.getPrice() * quantity);
            cart.add(newItem);
        }
    }

    public void removeFromCart(int customerId, int productId) throws ProductNotFoundException {
        List<CartItem> cart = customerCarts.get(customerId);
        if (cart == null || cart.isEmpty()) {
            throw new ProductNotFoundException("Cart is empty. Product with ID " + productId + " is not in cart.");
        }

        boolean removed = cart.removeIf(item -> item.getProductId() == productId);
        if (!removed) {
            throw new ProductNotFoundException("Product with ID " + productId + " is not found in cart.");
        }
    }

    public void increaseQuantity(int customerId, int productId, int count, int availableStock)
            throws InvalidQuantityException, InsufficientStockException, ProductNotFoundException {
        if (count <= 0) {
            throw new InvalidQuantityException("Increase count must be greater than zero.");
        }
        List<CartItem> cart = customerCarts.get(customerId);
        if (cart == null || cart.isEmpty()) {
            throw new ProductNotFoundException("Cart is empty.");
        }

        CartItem item = cart.stream()
                .filter(i -> i.getProductId() == productId)
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found in cart."));

        int updatedQuantity = item.getQuantity() + count;
        if (updatedQuantity > availableStock) {
            throw new InsufficientStockException("Cannot increase quantity. Available stock is " + availableStock
                    + ", but desired quantity is " + updatedQuantity + ".");
        }

        item.setQuantity(updatedQuantity);
        item.setTotalPrice(item.getPrice() * updatedQuantity);
    }

    public void decreaseQuantity(int customerId, int productId, int count)
            throws InvalidQuantityException, ProductNotFoundException {
        if (count <= 0) {
            throw new InvalidQuantityException("Decrease count must be greater than zero.");
        }
        List<CartItem> cart = customerCarts.get(customerId);
        if (cart == null || cart.isEmpty()) {
            throw new ProductNotFoundException("Cart is empty.");
        }

        CartItem item = cart.stream()
                .filter(i -> i.getProductId() == productId)
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found in cart."));

        if (item.getQuantity() <= count) {
            cart.remove(item);
        } else {
            int updatedQuantity = item.getQuantity() - count;
            item.setQuantity(updatedQuantity);
            item.setTotalPrice(item.getPrice() * updatedQuantity);
        }
    }

    public List<CartItem> viewCart(int customerId) {
        return customerCarts.getOrDefault(customerId, Collections.emptyList());
    }

    public double calculateTotal(int customerId) {
        List<CartItem> cart = customerCarts.getOrDefault(customerId, Collections.emptyList());
        return cart.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    public void clearCart(int customerId) {
        List<CartItem> cart = customerCarts.get(customerId);
        if (cart != null) {
            cart.clear();
        }
    }
}
