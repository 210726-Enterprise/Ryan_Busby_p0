package com.revature.service;

import com.revature.model.Customer;
import com.revature.model.Transaction;
import com.revature.repo.CustomerDAO;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerDAO cDao;

    public CustomerServiceImpl(CustomerDAO cDao) {
        this.cDao = cDao;
    }

    @Override
    public void createCustomer(Customer c, String password) {
        int newCustomerId = cDao.insertCustomer(c, password);
        c.setId(newCustomerId);
    }

    @Override
    public Customer getCustomer(String username, String password) {
        return cDao.selectCustomer(username, password);
    }

    @Override
    public boolean usernameExists(String username) {
        return cDao.selectUserName(username.toLowerCase());
    }

    @Override
    public List<Transaction> getTransactions(Customer c) {
        return cDao.selectTransactions(c);
    }
}
