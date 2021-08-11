package com.revature.repo;

import com.revature.model.Account;
import com.revature.model.Customer;
import com.revature.model.Transaction;

import com.revature.collection.RevaList;

public interface AccountDAO {
    //CREATE
    void insertAccount(Account a);
    void insertAccountOwnerShip(Account a, Customer c);
    boolean insertJoinedCustomer(Account a, int customerToJoinId);
    void insertTransaction(Account a, Customer c, Double amount);

    //READ
    RevaList<Account> selectCustomerAccount(Customer c);
//    Account selectAccountById(int accountId);
    boolean selectAccountOwner(Account a, Customer c);
    RevaList<Transaction> selectAccountTransactions(Account a);
    boolean selectCustomerIsJoined(Account a, int customerId);


    //UPDATE
    void updateAccountOwner(Account a, Customer old_owner, Customer new_owner);
    void updateAccountBalance(Account a, double new_balance);

    //DELETE
    boolean deleteJoinedCustomer(Account a, int customerIdToRemove);
    void deleteAccount(Account a);
}
