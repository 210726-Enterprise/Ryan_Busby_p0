package com.revature.service;

import com.revature.model.Account;
import com.revature.model.Customer;
import com.revature.model.Transaction;
import com.revature.collection.RevaList;

public interface AccountService {

    RevaList<Account> getAccounts(Customer c);
//    Account getAccountById(int accountId);
    RevaList<Account> getTypeAccounts(RevaList<Account> accounts, int type);
    boolean Deposit(Customer c, Account a, Double amount);
    boolean Withdrawal(Customer c, Account a, Double amount);
    boolean Transfer(Customer c, Account fromAccount, Account toAccount, double amount);
    void createAccount(Account a, Customer c); // you didn't insert into customer_account, stored procedure!!
    void deleteAccount(Account a, Customer c);
    boolean joinCustomer(Account a, Customer c, int customerToJoinId);
    boolean removeJoinedCustomer(Account a, Customer c, int customerIdToRemove);
    boolean changeAccountOwner(Account a, Customer old_owner, int newOwnerId);
    boolean checkOwnerShip(Account a, Customer c);
    RevaList<Transaction> getAccountTransactions(Account a);

}
