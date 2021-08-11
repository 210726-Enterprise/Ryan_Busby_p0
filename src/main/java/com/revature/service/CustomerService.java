package com.revature.service;

import com.revature.model.Customer;
import com.revature.model.Transaction;

import com.revature.collection.RevaList;

public interface CustomerService {
    void createCustomer(Customer c, String password);
    Customer getCustomer(String username, String password);
    boolean usernameExists(String username);
    RevaList<Transaction> getTransactions(Customer c);
}
