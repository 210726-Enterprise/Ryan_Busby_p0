package com.revature.presentation;

import com.revature.model.Account;
import com.revature.model.Customer;
import com.revature.model.Transaction;
import com.revature.collection.RevaList;

public interface BankPresentation {
    void welcome();
    void logIn();
    void invalidCredentials();
    void signUp();
    void createAccount(Customer c);
    void accountSummary(Customer c);
    void accountDetails(Customer c, Account a);
    void deposit(Customer c, Account a);
    void withdrawal(Customer c, Account a);
    void transfer(Customer c, RevaList<Account> checking, RevaList<Account> savings);
    void viewTransactions(Customer c, Account a, RevaList<Transaction> t);
    void viewAllTransactions(Customer c, RevaList<Transaction> t);
    void transferAccountOwner(Customer c, Account a);
    void addCustomerToAccount(Account a, Customer c);
    void removeCustomerFromAccount(Account a, Customer c);
    void deleteAccount(Customer c, Account a);
}
