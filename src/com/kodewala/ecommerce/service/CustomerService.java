package com.kodewala.ecommerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import com.kodewala.ecommerce.exception.CustomerNotFoundException;
import com.kodewala.ecommerce.model.Customer;
import com.kodewala.ecommerce.repository.CustomerRepository;

public class CustomerService {
    private CustomerRepository customerRepository;
    private final AtomicInteger customerIdGenerator = new AtomicInteger(1001);

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer registerCustomer(String name, String email, String mobile, String address) {
        int id = customerIdGenerator.getAndIncrement();
        Customer customer = new Customer(id, name, email, mobile, address);
        customerRepository.save(customer);
        return customer;
    }

    public Customer registerCustomer(Customer customer) {
        if (customer != null) {
            if (customer.getCustomerId() <= 0) {
                customer.setCustomerId(customerIdGenerator.getAndIncrement());
            }
            customerRepository.save(customer);
        }
        return customer;
    }

    public Customer getCustomerById(int customerId) throws CustomerNotFoundException {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + customerId + " does not exist."));
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customerRepository.getAllCustomers().values());
    }

    public List<Customer> searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomers();
        }
        return customerRepository.getAllCustomers().values().stream()
                .filter(c -> c.getName().toLowerCase().contains(keyword.toLowerCase().trim()))
                .collect(Collectors.toList());
    }

    public void updateAddress(int customerId, String newAddress) throws CustomerNotFoundException {
        Customer customer = getCustomerById(customerId);
        customer.setAddress(newAddress);
    }

    public boolean validateCustomer(int customerId) throws CustomerNotFoundException {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }
        return true;
    }
}
