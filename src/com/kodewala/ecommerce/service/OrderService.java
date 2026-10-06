package com.kodewala.ecommerce.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import com.kodewala.ecommerce.exception.CustomerNotFoundException;
import com.kodewala.ecommerce.exception.InsufficientStockException;
import com.kodewala.ecommerce.exception.InvalidOrderException;
import com.kodewala.ecommerce.exception.InvalidQuantityException;
import com.kodewala.ecommerce.exception.ProductNotFoundException;
import com.kodewala.ecommerce.model.CartItem;
import com.kodewala.ecommerce.model.Order;
import com.kodewala.ecommerce.model.OrderStatus;
import com.kodewala.ecommerce.model.Product;
import com.kodewala.ecommerce.repository.OrderRepository;

public class OrderService {
    private OrderRepository orderRepository;
    private ProductService productService;
    private CustomerService customerService;
    private CartService cartService;
    private AtomicInteger orderIdGenerator = new AtomicInteger(1001);

    public OrderService(OrderRepository orderRepository, ProductService productService,
                        CustomerService customerService, CartService cartService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.customerService = customerService;
        this.cartService = cartService;
    }

    public synchronized Order placeOrder(int customerId)
            throws CustomerNotFoundException, InvalidOrderException, InsufficientStockException,
                   ProductNotFoundException, InvalidQuantityException {
        // Validate customer
        customerService.validateCustomer(customerId);

        // Fetch cart items
        List<CartItem> cartItems = cartService.viewCart(customerId);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new InvalidOrderException("Cannot place order. Cart is empty for customer ID: " + customerId);
        }

        // Validate stock for all items
        for (CartItem item : cartItems) {
            productService.checkAvailability(item.getProductId(), item.getQuantity());
        }

        // Reduce stock for all items
        for (CartItem item : cartItems) {
            Product product = productService.getProductById(item.getProductId());
            productService.updateQuantity(item.getProductId(), product.getQuantity() - item.getQuantity());
        }

        // Calculate total amount using Stream API via cartService
        double totalAmount = cartService.calculateTotal(customerId);

        // Create independent copy of cart items for this order
        List<CartItem> orderItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            orderItems.add(new CartItem(item.getProductId(), item.getProductName(),
                    item.getPrice(), item.getQuantity(), item.getTotalPrice()));
        }

        // Generate unique Order ID
        int orderId = orderIdGenerator.getAndIncrement();

        // Create and save Order
        Order order = new Order(orderId, customerId, orderItems, totalAmount, OrderStatus.PLACED, LocalDateTime.now());
        orderRepository.save(order);

        // Clear cart after placing order
        cartService.clearCart(customerId);

        return order;
    }

    public Order getOrderById(int orderId) throws InvalidOrderException {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new InvalidOrderException("Order with ID " + orderId + " does not exist."));
    }

    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public List<Order> getOrdersByCustomerId(int customerId) {
        return orderRepository.getAllOrders().stream()
                .filter(order -> order.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }

    public void cancelOrder(int orderId)
            throws InvalidOrderException, ProductNotFoundException, InvalidQuantityException {
        Order order = getOrderById(orderId);

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderException("Order ID " + orderId + " is already CANCELLED.");
        }
        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new InvalidOrderException("Order ID " + orderId + " has already been DELIVERED and cannot be cancelled.");
        }

        // Restore stock for all products in the cancelled order
        for (CartItem item : order.getItems()) {
            Product product = productService.getProductById(item.getProductId());
            productService.updateQuantity(item.getProductId(), product.getQuantity() + item.getQuantity());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
    }

    public void updateOrderStatus(int orderId, OrderStatus newStatus) throws InvalidOrderException {
        Order order = getOrderById(orderId);

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderException("Cannot update status. Order ID " + orderId + " is CANCELLED.");
        }

        order.setOrderStatus(newStatus);
    }
}
