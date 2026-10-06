package com.kodewala.ecommerce.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.kodewala.ecommerce.model.Customer;

public class CustomerRepository {
    private Map<Integer, Customer> customers = new HashMap<>();

    public void save(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
    }

    public Optional<Customer> findById(int customerId) {
        return Optional.ofNullable(customers.get(customerId));
    }

    public Map<Integer, Customer> getAllCustomers() {
        return customers;
    }

    public boolean existsById(int customerId) {
        return customers.containsKey(customerId);
    }

    public boolean deleteById(int customerId) {
        return customers.remove(customerId) != null;
    }
}
