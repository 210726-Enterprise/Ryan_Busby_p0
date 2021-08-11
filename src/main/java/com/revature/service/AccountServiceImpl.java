package com.revature.service;

import com.revature.model.Account;
import com.revature.model.Customer;
import com.revature.model.Transaction;
import com.revature.repo.AccountDAO;
import com.revature.repo.CustomerDAO;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AccountServiceImpl implements AccountService {

    private final AccountDAO aDao;
    private final CustomerDAO cDao;

    private final Logger logger = Logger.getLogger("BankOnIt");

    public AccountServiceImpl(AccountDAO aDao, CustomerDAO cDao) {
        this.aDao = aDao;
        this.cDao = cDao;
    }

    @Override
    public List<Account> getAccounts(Customer c) {
        return aDao.selectCustomerAccount(c);
    }

//    @Override
//    public Account getAccountById(int accountId) {
//        return aDao.selectAccountById(accountId);
//    }

    @Override
    public List<Account> getTypeAccounts(List<Account> accounts, int type) {
        List<Account> typeAccounts = new ArrayList<>();
        for (Account a: accounts) {
            if (a.getType() == type) {
                typeAccounts.add(a);
            }
        }
        return typeAccounts;
    }

    @Override
    public boolean Deposit(Customer c, Account a, Double amount) {
        // record the transaction, will need to pass in the customer too
        if (amount > 0) {
            double new_balance = a.getBalance() + amount;
            aDao.updateAccountBalance(a, new_balance);
            aDao.insertTransaction(a, c, amount);
            a.setBalance(new_balance);
            return true;
        }
        return false;
    }

    @Override
    public boolean Withdrawal(Customer c, Account a, Double amount) {
        // record the transaction, pass in the customer
        if (amount > 0) {
            double new_balance = a.getBalance() - amount;
            if (new_balance<=0) {
                logger.warn("You cannot have a negative balance");
                return false;
            }
             else {
                 aDao.updateAccountBalance(a, new_balance);
                 aDao.insertTransaction(a, c, -amount);
                 a.setBalance(new_balance);
                 return true;
            }
        }
        return false;
    }

    @Override
    public boolean Transfer(Customer c, Account fromAccount, Account toAccount, double amount) {
        boolean withdrawalSuccess = Withdrawal(c, fromAccount, amount);
        boolean depositSuccess = Deposit(c, toAccount, amount);
        return withdrawalSuccess && depositSuccess;
    }

    @Override
    public void createAccount(Account a, Customer c) {
        aDao.insertAccount(a);
        aDao.insertAccountOwnerShip(a, c);
    }

    @Override
    public void deleteAccount(Account a, Customer c) {
        if (aDao.selectAccountOwner(a, c)) {
            aDao.deleteAccount(a);
        }
    }

    @Override
    public boolean joinCustomer(Account a, Customer c, int idToJoin) {
        boolean success = false;
        if (aDao.selectAccountOwner(a, c)) {
            boolean alreadyJoined = aDao.selectCustomerIsJoined(a, idToJoin);
            if (alreadyJoined) {
                logger.info(String.format("Customer %s is already a joint customer of %s", idToJoin, a.getNickname()));
            } else {
                Customer toJoinCustomer = cDao.selectCustomer(idToJoin);
                if (toJoinCustomer != null) {
                    success = aDao.insertJoinedCustomer(a, idToJoin);
                    logger.info(String.format("%s %s is now joined to %s", toJoinCustomer.getFirst(), toJoinCustomer.getLast(), a.getNickname()));
                } else {
                    logger.info(String.format("There is no Customer with id=%d", idToJoin));
                }
            }
        } else {
            logger.warn("You are not owner of this account and cannot add customers");
        }
        return success;
    }


    @Override
    public boolean removeJoinedCustomer(Account a, Customer c, int customerIdToRemove) {
        boolean success = false;
        if (c.getId() == customerIdToRemove) {
            logger.info("You cannot remove the owner. Close the account or transfer ownership.");
            return false;
        }
        else if (aDao.selectAccountOwner(a, c)) { // make sure they're the owner, even though the ui protects non-owners from this method
            boolean cusIsJoined = aDao.selectCustomerIsJoined(a, customerIdToRemove);
            if (!cusIsJoined){
                logger.info(String.format("Customer %d is not joined to %s", customerIdToRemove, a.getNickname()));
                return false;
            }
            Customer joined = cDao.selectCustomer(customerIdToRemove);
            success = aDao.deleteJoinedCustomer(a, customerIdToRemove);
            if (success) {
                logger.info(String.format("%s %s has been removed from %s", joined.getFirst(), joined.getLast(), a.getNickname()));
            }
        }
        return success;
    }

    @Override
    public boolean changeAccountOwner(Account a, Customer old_owner, int newOwnerId) {
        Customer newOwner = cDao.selectCustomer(newOwnerId);
        if (aDao.selectAccountOwner(a, old_owner)) { // the UI prevents non owners from seeing this option, but added anyways
            if (newOwner != null){
            boolean newOwnerJoinedToAccount = aDao.selectCustomerIsJoined(a, newOwner.getId());
            if (newOwnerJoinedToAccount) {
                aDao.updateAccountOwner(a, old_owner, newOwner);
                logger.info(String.format("%s %s has been made owner of %s", newOwner.getFirst(), newOwner.getLast(), a.getNickname()));
                return true;
            } else {
                logger.info("Cannot make that customer the owner, join them to the account first");
            }
        } else {
                logger.warn("Invalid customer Id");
            }
        }
        return false;
    }

    @Override
    public boolean checkOwnerShip(Account a, Customer c) {
        return aDao.selectAccountOwner(a, c);
    }

    @Override
    public List<Transaction> getAccountTransactions(Account a) {
        return aDao.selectAccountTransactions(a);
    }

}
