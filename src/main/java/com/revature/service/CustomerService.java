package com.revature.service;

import com.revature.model.Customer;
import com.revature.model.Transaction;

import java.util.List;

public interface CustomerService {
    void createCustomer(Customer c, String password);
    Customer getCustomer(String username, String password);
    boolean usernameExists(String username);
    List<Transaction> getTransactions(Customer c);
}
