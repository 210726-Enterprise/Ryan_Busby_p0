package com.revature.service;

import com.revature.model.Customer;
import com.revature.model.Transaction;
import com.revature.repo.CustomerDAO;

import com.revature.collection.RevaList;

/**
 * CustomerServiceImpl takes commands from BankPresentationImpl
 * and translates them into commands for CustomerDAO
 */

public class CustomerServiceImpl implements CustomerService {

    private final CustomerDAO cDao;

    public CustomerServiceImpl(CustomerDAO cDao) {
        this.cDao = cDao;
    }

    /**
     * Creates a new customer in the database, invoked by BankPresentation.signUp()
     * @param c - Customer object containing the data to be persisted
     * @param password - String the password entered by the user to be persisted
     */
    @Override
    public void createCustomer(Customer c, String password) {
        int newCustomerId = cDao.insertCustomer(c, password);
        c.setId(newCustomerId);
    }

    /**
     * Invoked by BankPresentation.logIn()
     * @param username - String the username entered at BankPresentation.logIn()
     * @param password - String that password entered at BankPresentation.logIn()
     * @return Customer object, containing the data for the person who logged in
     */
    @Override
    public Customer getCustomer(String username, String password) {
        return cDao.selectCustomer(username, password);
    }

    /**
     * Invoked by BankPresentation.signUp(). Check to see if the entered
     * username is already being used.
     * @param username - String the username being searched for.
     * @return boolean - true if the username exists
     */
    @Override
    public boolean usernameExists(String username) {
        return cDao.selectUserName(username.toLowerCase());
    }

    /**
     * Get all the transactions for all account associated with a customer.
     * @param c - Customer object containing the id which will be used to search for all transactions
     * @return ArrayList of Transactions
     */
    @Override
    public RevaList<Transaction> getTransactions(Customer c) {
        return cDao.selectTransactions(c);
    }
}
