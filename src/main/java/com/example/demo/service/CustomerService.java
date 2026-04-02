package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository repo;
    public CustomerService(CustomerRepository repo) { this.repo = repo; }
    public Customer saveCustomer(Customer customer) { return repo.save(customer); }
    public List<Customer> getAllCustomers() { return repo.findAll(); }
    public Customer getCustomerById(Long id) { return repo.findById(id).orElseThrow(() -> new RuntimeException("Customer not found")); }
    public void deleteCustomer(Long id) { repo.deleteById(id); }
}