package com.kodewala.ecommerce.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.kodewala.ecommerce.model.Order;

public class OrderRepository {
    private List<Order> orders = new ArrayList<>();

    public void save(Order order) {
        orders.add(order);
    }

    public List<Order> getAllOrders() {
        return orders;
    }

    public Optional<Order> findById(int orderId) {
        return orders.stream()
                .filter(o -> o.getOrderId() == orderId)
                .findFirst();
    }

    public List<Order> findByCustomerId(int customerId) {
        return orders.stream()
                .filter(o -> o.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }
}
