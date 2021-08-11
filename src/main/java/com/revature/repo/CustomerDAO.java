package com.revature.repo;

import com.revature.model.Customer;
import com.revature.model.Transaction;

import com.revature.collection.RevaList;

public interface CustomerDAO {
    //CREATE
    int insertCustomer(Customer c, String password);

    //READ
    RevaList<Transaction> selectTransactions(Customer c);
    Customer selectCustomer(String username, String password);
    Customer selectCustomer(int customerId);
    boolean selectUserName(String username);


    //UPDATE
    // void updateCustomer(Customer c);

    //DELETE
    // void deleteCustomer(Customer c);
}
