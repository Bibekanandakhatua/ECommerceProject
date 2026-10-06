package com.kodewala.ecommerce.main;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import com.kodewala.ecommerce.exception.CustomerNotFoundException;
import com.kodewala.ecommerce.exception.InsufficientStockException;
import com.kodewala.ecommerce.exception.InvalidOrderException;
import com.kodewala.ecommerce.exception.InvalidQuantityException;
import com.kodewala.ecommerce.exception.ProductNotFoundException;
import com.kodewala.ecommerce.model.CartItem;
import com.kodewala.ecommerce.model.Customer;
import com.kodewala.ecommerce.model.Order;
import com.kodewala.ecommerce.model.OrderStatus;
import com.kodewala.ecommerce.model.Product;
import com.kodewala.ecommerce.repository.CustomerRepository;
import com.kodewala.ecommerce.repository.OrderRepository;
import com.kodewala.ecommerce.repository.ProductRepository;
import com.kodewala.ecommerce.service.CartService;
import com.kodewala.ecommerce.service.CustomerService;
import com.kodewala.ecommerce.service.OrderService;
import com.kodewala.ecommerce.service.ProductService;

public class EcommerceApplication {
    private static final Scanner scanner = new Scanner(System.in);

    private final ProductService productService;
    private final CustomerService customerService;
    private final CartService cartService;
    private final OrderService orderService;

    public EcommerceApplication() {
        ProductRepository productRepository = new ProductRepository();
        CustomerRepository customerRepository = new CustomerRepository();
        OrderRepository orderRepository = new OrderRepository();

        this.productService = new ProductService(productRepository);
        this.customerService = new CustomerService(customerRepository);
        this.cartService = new CartService();
        this.orderService = new OrderService(orderRepository, productService, customerService, cartService);

        seedSampleData();
    }

    private void seedSampleData() {
        // Pre-populating sample products
        productService.addProduct(new Product(101, "iPhone 15", "Electronics", 79999.00, 10, "Apple"));
        productService.addProduct(new Product(102, "Galaxy S24", "Electronics", 74999.00, 15, "Samsung"));
        productService.addProduct(new Product(103, "MacBook Air M2", "Computers", 99999.00, 8, "Apple"));
        productService.addProduct(new Product(104, "Wireless Headphones", "Accessories", 4999.00, 25, "Sony"));
        productService.addProduct(new Product(105, "Running Shoes", "Footwear", 3499.00, 20, "Nike"));
    }

    public static void main(String[] args) {
        EcommerceApplication app = new EcommerceApplication();
        app.start();
    }

    public void start() {
        while (true) {
            System.out.println("\n=============================================");
            System.out.println("  E-COMMERCE PRODUCT & ORDER MANAGEMENT");
            System.out.println("=============================================");
            System.out.println("1. Browse Products ( guestBrowser Without Registration)");
            System.out.println("2. Customer Portal");
            System.out.println("3. Admin Portal");
            System.out.println("4. Exit");
            System.out.print("Select an option (1-4): ");

            int choice = readIntInput();
            switch (choice) {
                case 1:
                    guestBrowseProductsMenu();
                    break;
                case 2:
                    customerPortalMenu();
                    break;
                case 3:
                    adminMenu();
                    break;
                case 4:
                    System.out.println("\nThank you for using the E-Commerce System. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please select an option between 1 and 4.");
            }
        }
    }

    // ==========================================
    // ADMIN PORTAL
    // ==========================================
    private void adminMenu() {
        while (true) {
            System.out.println("\n---------------------------------------------");
            System.out.println("                ADMIN PORTAL                 ");
            System.out.println("---------------------------------------------");
            System.out.println("1. Add New Product");
            System.out.println("2. View All Products");
            System.out.println("3. Update Product Price");
            System.out.println("4. Update Product Stock / Quantity");
            System.out.println("5. Delete Product");
            System.out.println("6. View All Orders");
            System.out.println("7. Update Order Status");
            System.out.println("8. View All Registered Customers");
            System.out.println("9. View Unique Categories & Brands");
            System.out.println("10. Back to Main Menu");
            System.out.print("Enter your choice (1-10): ");

            int choice = readIntInput();
            switch (choice) {
                case 1:
                    addNewProduct();
                    break;
                case 2:
                    displayProducts(productService.getAllProducts());
                    break;
                case 3:
                    updateProductPrice();
                    break;
                case 4:
                    updateProductQuantity();
                    break;
                case 5:
                    deleteProduct();
                    break;
                case 6:
                    displayOrders(orderService.getAllOrders());
                    break;
                case 7:
                    updateOrderStatus();
                    break;
                case 8:
                    displayCustomers(customerService.getAllCustomers());
                    break;
                case 9:
                    displayUniqueCategoriesAndBrands();
                    break;
                case 10:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addNewProduct() {
        System.out.println("\n--- Add New Product ---");
        System.out.print("Enter Product ID: ");
        int id = readIntInput();

        try {
            productService.getProductById(id);
            System.out.println("Error: A product with ID " + id + " already exists.");
            return;
        } catch (ProductNotFoundException e) {
            // Product ID is available
        }

        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Category: ");
        String category = scanner.nextLine().trim();

        System.out.print("Enter Brand: ");
        String brand = scanner.nextLine().trim();

        System.out.print("Enter Price: ");
        double price = readDoubleInput();

        System.out.print("Enter Stock Quantity: ");
        int qty = readIntInput();

        Product product = new Product(id, name, category, price, qty, brand);
        productService.addProduct(product);
        System.out.println("Product added successfully: " + product);
    }

    private void updateProductPrice() {
        System.out.println("\n--- Update Product Price ---");
        System.out.print("Enter Product ID: ");
        int id = readIntInput();

        System.out.print("Enter New Price: ");
        double newPrice = readDoubleInput();

        try {
            productService.updatePrice(id, newPrice);
            System.out.println("Price updated successfully for Product ID " + id);
        } catch (ProductNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateProductQuantity() {
        System.out.println("\n--- Update Product Stock Quantity ---");
        System.out.print("Enter Product ID: ");
        int id = readIntInput();

        System.out.print("Enter New Quantity: ");
        int newQty = readIntInput();

        try {
            productService.updateQuantity(id, newQty);
            System.out.println("Quantity updated successfully for Product ID " + id);
        } catch (ProductNotFoundException | InvalidQuantityException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        System.out.println("\n--- Delete Product ---");
        System.out.print("Enter Product ID to delete: ");
        int id = readIntInput();

        try {
            productService.deleteProduct(id);
            System.out.println("Product ID " + id + " deleted successfully.");
        } catch (ProductNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateOrderStatus() {
        System.out.println("\n--- Update Order Status ---");
        System.out.print("Enter Order ID: ");
        int orderId = readIntInput();

        System.out.println("Available Statuses:");
        OrderStatus[] statuses = OrderStatus.values();
        for (int i = 0; i < statuses.length; i++) {
            System.out.println((i + 1) + ". " + statuses[i]);
        }
        System.out.print("Select Status (1-" + statuses.length + "): ");
        int statusChoice = readIntInput();

        if (statusChoice < 1 || statusChoice > statuses.length) {
            System.out.println("Invalid status choice.");
            return;
        }

        OrderStatus newStatus = statuses[statusChoice - 1];
        try {
            orderService.updateOrderStatus(orderId, newStatus);
            System.out.println("Order ID " + orderId + " status updated to " + newStatus);
        } catch (InvalidOrderException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void displayUniqueCategoriesAndBrands() {
        System.out.println("\n--- Unique Categories & Brands (Using HashSet) ---");
        Set<String> categories = productService.getUniqueCategories();
        Set<String> brands = productService.getUniqueBrands();

        System.out.println("Categories (" + categories.size() + "): " + categories);
        System.out.println("Brands (" + brands.size() + "): " + brands);
    }

    // ==========================================
    // CUSTOMER PORTAL
    // ==========================================
    private void customerPortalMenu() {
        while (true) {
            System.out.println("\n---------------------------------------------");
            System.out.println("               CUSTOMER PORTAL               ");
            System.out.println("---------------------------------------------");
            System.out.println("1. Browse Products (Without Registration)");
            System.out.println("2. Register New Customer");
            System.out.println("3. Login / Select Customer");
            System.out.println("4. Back to Main Menu");
            System.out.print("Select an option (1-4): ");

            int choice = readIntInput();
            switch (choice) {
                case 1:
                    guestBrowseProductsMenu();
                    break;
                case 2:
                    registerNewCustomer();
                    break;
                case 3:
                    loginCustomer();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice. Please select 1, 2, 3, or 4.");
            }
        }
    }

    private void guestBrowseProductsMenu() {
        while (true) {
            System.out.println("\n---------------------------------------------");
            System.out.println("        BROWSE PRODUCTS (GUEST VIEW)         ");
            System.out.println("---------------------------------------------");
            System.out.println("1. View All Products");
            System.out.println("2. Search Products by Name");
            System.out.println("3. Filter Products by Category");
            System.out.println("4. Filter Products by Brand");
            System.out.println("5. Filter Products by Price Range");
            System.out.println("6. Sort Products by Price");
            System.out.println("7. Buy Product / Add to Cart (Requires Registration)");
            System.out.println("8. Back to Previous Menu");
            System.out.print("Enter choice (1-8): ");

            int choice = readIntInput();
            switch (choice) {
                case 1:
                    displayProducts(productService.getAllProducts());
                    break;
                case 2:
                    System.out.print("Enter product name keyword: ");
                    String keyword = scanner.nextLine().trim();
                    displayProducts(productService.searchByName(keyword));
                    break;
                case 3:
                    System.out.print("Enter category: ");
                    String cat = scanner.nextLine().trim();
                    displayProducts(productService.filterByCategory(cat));
                    break;
                case 4:
                    System.out.print("Enter brand: ");
                    String brand = scanner.nextLine().trim();
                    displayProducts(productService.filterByBrand(brand));
                    break;
                case 5:
                    System.out.print("Enter minimum price: ");
                    double minPrice = readDoubleInput();
                    System.out.print("Enter maximum price: ");
                    double maxPrice = readDoubleInput();
                    displayProducts(productService.filterByPriceRange(minPrice, maxPrice));
                    break;
                case 6:
                    System.out.print("Sort order (1 for Low to High, 2 for High to Low): ");
                    int sortChoice = readIntInput();
                    displayProducts(productService.sortByPrice(sortChoice != 2));
                    break;
                case 7:
                    handleGuestBuy();
                    return;
                case 8:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleGuestBuy() {
        System.out.println("\n---------------------------------------------");
        System.out.println("Note: To buy products, you must be a registered customer.");
        System.out.println("---------------------------------------------");
        System.out.println("1. Login with Existing Customer ID");
        System.out.println("2. Register as a New Customer");
        System.out.println("3. Continue Browsing");
        System.out.print("Select an option (1-3): ");

        int choice = readIntInput();
        switch (choice) {
            case 1:
                Customer loggedInCustomer = loginAndGetCustomer();
                if (loggedInCustomer != null) {
                    addProductToCart(loggedInCustomer.getCustomerId());
                    customerOperationsMenu(loggedInCustomer);
                }
                break;
            case 2:
                Customer newCustomer = registerNewCustomerAndReturn();
                if (newCustomer != null) {
                    addProductToCart(newCustomer.getCustomerId());
                    customerOperationsMenu(newCustomer);
                }
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid choice. Returning to browsing.");
        }
    }

    private Customer registerNewCustomerAndReturn() {
        System.out.println("\n--- Register New Customer ---");

        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter Mobile: ");
        String mobile = scanner.nextLine().trim();

        System.out.print("Enter Address: ");
        String address = scanner.nextLine().trim();

        Customer customer = customerService.registerCustomer(name, email, mobile, address);
        System.out.println("\nCustomer registered successfully!");
        System.out.println("Your Customer ID is: " + customer.getCustomerId());
        return customer;
    }

    private void registerNewCustomer() {
        registerNewCustomerAndReturn();
    }

    private Customer loginAndGetCustomer() {
        System.out.print("\nEnter your Customer ID to login: ");
        int customerId = readIntInput();

        try {
            Customer customer = customerService.getCustomerById(customerId);
            System.out.println("Logged in successfully! Welcome back, " + customer.getName());
            return customer;
        } catch (CustomerNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    private void loginCustomer() {
        Customer customer = loginAndGetCustomer();
        if (customer != null) {
            customerOperationsMenu(customer);
        }
    }

    private void customerOperationsMenu(Customer customer) {
        int customerId = customer.getCustomerId();

        while (true) {
            System.out.println("\n---------------------------------------------");
            System.out.println("  Customer Dashboard: " + customer.getName() + " (ID: " + customerId + ")");
            System.out.println("---------------------------------------------");
            System.out.println("1.  View All Products");
            System.out.println("2.  Search Products by Name ");
            System.out.println("3.  Search Products by Category ");
            System.out.println("4.  Search Products by Brand ");
            System.out.println("5.  Search Products by Price Range ");
            System.out.println("6.  Sort Products by Price ");
            System.out.println("7.  View My Cart & Total ");
            System.out.println("8.  Add Product to Cart");
            System.out.println("9.  Increase Cart Item Quantity");
            System.out.println("10. Decrease Cart Item Quantity");
            System.out.println("11. Remove Item from Cart");
            System.out.println("12. Clear Cart");
            System.out.println("13. Checkout / Place Order");
            System.out.println("14. View My Orders");
            System.out.println("15. Cancel Order");
            System.out.println("16. Update Delivery Address");
            System.out.println("17. Logout");
            System.out.print("Enter choice (1-17): ");

            int choice = readIntInput();
            switch (choice) {
                case 1:
                    displayProducts(productService.getAllProducts());
                    break;
                case 2:
                    System.out.print("Enter product name keyword: ");
                    String keyword = scanner.nextLine().trim();
                    displayProducts(productService.searchByName(keyword));
                    break;
                case 3:
                    System.out.print("Enter category: ");
                    String cat = scanner.nextLine().trim();
                    displayProducts(productService.filterByCategory(cat));
                    break;
                case 4:
                    System.out.print("Enter brand: ");
                    String brand = scanner.nextLine().trim();
                    displayProducts(productService.filterByBrand(brand));
                    break;
                case 5:
                    System.out.print("Enter minimum price: ");
                    double minPrice = readDoubleInput();
                    System.out.print("Enter maximum price: ");
                    double maxPrice = readDoubleInput();
                    displayProducts(productService.filterByPriceRange(minPrice, maxPrice));
                    break;
                case 6:
                    System.out.print("Sort order (1 for Low to High, 2 for High to Low): ");
                    int sortChoice = readIntInput();
                    displayProducts(productService.sortByPrice(sortChoice != 2));
                    break;
                case 7:
                    displayCart(customerId);
                    break;
                case 8:
                    addProductToCart(customerId);
                    break;
                case 9:
                    increaseCartItem(customerId);
                    break;
                case 10:
                    decreaseCartItem(customerId);
                    break;
                case 11:
                    removeCartItem(customerId);
                    break;
                case 12:
                    cartService.clearCart(customerId);
                    System.out.println("Cart cleared successfully.");
                    break;
                case 13:
                    checkout(customerId);
                    break;
                case 14:
                    displayOrders(orderService.getOrdersByCustomerId(customerId));
                    break;
                case 15:
                    cancelCustomerOrder(customerId);
                    break;
                case 16:
                    updateCustomerAddress(customer);
                    break;
                case 17:
                    System.out.println("Logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addProductToCart(int customerId) {
        System.out.print("Enter Product ID to add: ");
        int productId = readIntInput();

        try {
            Product product = productService.getProductById(productId);
            System.out.print("Enter Quantity to add: ");
            int qty = readIntInput();

            cartService.addToCart(customerId, product, qty);
            System.out.println("Added " + qty + " of '" + product.getProductName() + "' to cart.");
        } catch (ProductNotFoundException | InvalidQuantityException | InsufficientStockException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void increaseCartItem(int customerId) {
        System.out.print("Enter Product ID in cart to increase: ");
        int productId = readIntInput();

        try {
            Product product = productService.getProductById(productId);
            System.out.print("Enter additional quantity: ");
            int addQty = readIntInput();

            cartService.increaseQuantity(customerId, productId, addQty, product.getQuantity());
            System.out.println("Quantity increased successfully.");
        } catch (ProductNotFoundException | InvalidQuantityException | InsufficientStockException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void decreaseCartItem(int customerId) {
        System.out.print("Enter Product ID in cart to decrease: ");
        int productId = readIntInput();

        System.out.print("Enter quantity to reduce: ");
        int reduceQty = readIntInput();

        try {
            cartService.decreaseQuantity(customerId, productId, reduceQty);
            System.out.println("Quantity decreased successfully.");
        } catch (ProductNotFoundException | InvalidQuantityException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void removeCartItem(int customerId) {
        System.out.print("Enter Product ID to remove from cart: ");
        int productId = readIntInput();

        try {
            cartService.removeFromCart(customerId, productId);
            System.out.println("Item removed from cart.");
        } catch (ProductNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void checkout(int customerId) {
        System.out.println("\n--- Checkout / Place Order ---");
        try {
            Order order = orderService.placeOrder(customerId);
            System.out.println("Order placed successfully!");
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Status: " + order.getOrderStatus());
            System.out.println("Date: " + order.getOrderDate());
            System.out.println("Total Amount: $" + String.format("%.2f", order.getTotalAmount()));
            System.out.println("Items in Order:");
            for (CartItem item : order.getItems()) {
                System.out.println("  - " + item.getProductName() + " | Qty: " + item.getQuantity()
                        + " | Price: $" + String.format("%.2f", item.getPrice())
                        + " | Subtotal: $" + String.format("%.2f", item.getTotalPrice()));
            }
        } catch (CustomerNotFoundException | InvalidOrderException | InsufficientStockException
                 | ProductNotFoundException | InvalidQuantityException e) {
            System.out.println("Failed to place order: " + e.getMessage());
        }
    }

    private void cancelCustomerOrder(int customerId) {
        System.out.print("Enter Order ID to cancel: ");
        int orderId = readIntInput();

        try {
            Order order = orderService.getOrderById(orderId);
            if (order.getCustomerId() != customerId) {
                System.out.println("Error: Order ID " + orderId + " does not belong to your account.");
                return;
            }
            orderService.cancelOrder(orderId);
            System.out.println("Order ID " + orderId + " has been cancelled successfully. Stock has been restored.");
        } catch (InvalidOrderException | ProductNotFoundException | InvalidQuantityException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateCustomerAddress(Customer customer) {
        System.out.println("Current address: " + customer.getAddress());
        System.out.print("Enter new address: ");
        String newAddress = scanner.nextLine().trim();

        try {
            customerService.updateAddress(customer.getCustomerId(), newAddress);
            System.out.println("Address updated successfully to: " + newAddress);
        } catch (CustomerNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void displayCart(int customerId) {
        List<CartItem> cart = cartService.viewCart(customerId);
        System.out.println("\n--- Current Cart ---");
        if (cart == null || cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        for (CartItem item : cart) {
            System.out.println("  " + item);
        }
        double total = cartService.calculateTotal(customerId);
        System.out.println("---------------------------------------------");
        System.out.printf("Total Cart Amount (Stream API calculated): $%.2f\n", total);
    }

    private void displayProducts(List<Product> products) {
        System.out.println("\n--- Products List ---");
        if (products == null || products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        for (Product product : products) {
            System.out.println("  " + product);
        }
    }

    private void displayOrders(List<Order> orders) {
        System.out.println("\n--- Orders List ---");
        if (orders == null || orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        for (Order order : orders) {
            System.out.println("  " + order);
            if (order.getItems() != null) {
                for (CartItem item : order.getItems()) {
                    System.out.println("     -> " + item.getProductName() + " (Qty: " + item.getQuantity()
                            + ", Subtotal: $" + String.format("%.2f", item.getTotalPrice()) + ")");
                }
            }
        }
    }

    private void displayCustomers(List<Customer> customers) {
        System.out.println("\n--- Registered Customers ---");
        if (customers == null || customers.isEmpty()) {
            System.out.println("No customers registered.");
            return;
        }
        for (Customer customer : customers) {
            System.out.println("  " + customer);
        }
    }

    private int readIntInput() {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Invalid integer. Please re-enter: ");
            }
        }
    }

    private double readDoubleInput() {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Please re-enter: ");
            }
        }
    }
}
