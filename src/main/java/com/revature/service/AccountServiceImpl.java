package com.revature.service;

import com.revature.model.Account;
import com.revature.model.Customer;
import com.revature.model.Transaction;
import com.revature.repo.AccountDAO;
import com.revature.repo.CustomerDAO;
import org.apache.log4j.Logger;

import com.revature.collection.RevaList;
import com.revature.collection.RevArrayList;

/**
 * AccountServiceImpl takes commands from BankPresentationImpl
 * and translates them into commands for AccountDAO
 */

public class AccountServiceImpl implements AccountService {

    private final AccountDAO aDao;
    private final CustomerDAO cDao;

    // init the logger for some warnings in these methods
    private final Logger logger = Logger.getLogger("BankOnIt");

    public AccountServiceImpl(AccountDAO aDao, CustomerDAO cDao) {
        this.aDao = aDao;
        this.cDao = cDao;
    }

    /**
     * Get all the accounts associated with a Customer
     * @param c - Customer object containing the id which will be used to search
     * @return ArrayList of Accounts
     */
    @Override
    public RevaList<Account> getAccounts(Customer c) {
        return aDao.selectCustomerAccount(c);
    }

//    @Override
//    public Account getAccountById(int accountId) {
//        return aDao.selectAccountById(accountId);
//    }

    /**
     * Filter the ArrayList of Accounts for a certain type.
     * @param accounts - ArrayList of Accounts of mixed type
     * @param type - int the type of account to filter for
     * @return ArrayList of Accounts of some type
     */
    @Override
    public RevaList<Account> getTypeAccounts(RevaList<Account> accounts, int type) {
        RevaList<Account> typeAccounts = new RevArrayList<>();
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getType() == type) {
                typeAccounts.add(accounts.get(i));
            }
        }
        return typeAccounts;
    }

    /**
     * Verify the amount is positive, update the balance, record the transaction
     * @param c - Customer object making the Deposit
     * @param a - Account to deposit to
     * @param amount - Double the amount to deposit
     * @return boolean - true if the Deposit goes through.
     */
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

    /**
     * Verify the amount is positive, verify the balance doesn't go negative, update the balance, record the transaction
     * @param c - Customer object making the Withdrawal
     * @param a - Account to withdrawal from
     * @param amount - Double the amount to withdrawal
     * @return boolean - true if the Withdrawal goes through.
     */
    @Override
    public boolean Withdrawal(Customer c, Account a, Double amount) {
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

    /**
     *
     * @param c - Customer performing the transfer
     * @param fromAccount - Account to Withdrawal from.
     * @param toAccount - Account to Deposit to.
     * @param amount - Double how much to Deposit/Withdrawal.
     * @return
     */
    @Override
    public boolean Transfer(Customer c, Account fromAccount, Account toAccount, double amount) {
        boolean withdrawalSuccess = Withdrawal(c, fromAccount, amount);
        boolean depositSuccess = Deposit(c, toAccount, amount);
        return withdrawalSuccess && depositSuccess;
    }

    /**
     * When user chooses to create account, this persists that account data and associates the customer to the account
     * @param a - Account object containing data to persist
     * @param c - Customer object to associate the account to
     */
    @Override
    public void createAccount(Account a, Customer c) {
        aDao.insertAccount(a);
        aDao.insertAccountOwnerShip(a, c);
    }

    /**
     * Verify the owner invoked this, and remove account from database
     * @param a - Account object containing the id used to delete.
     * @param c - Customer object who is invoking the command.
     */
    @Override
    public void deleteAccount(Account a, Customer c) {
        if (aDao.selectAccountOwner(a, c)) {
            aDao.deleteAccount(a);
        }
    }

    /**
     * Verify the person invoking the command is the owner;
     *      if they are not, log a warning, and return false
     *
     *      if they are the owner:
     *          check if the idToJoin is already joined to the account
     *
     *          if they are already joined:
     *              log a warning, and return false
     *
     *          if they are not already joined:
     *              check if the id they entered is a known customer id
     *
     *              if it is a known id:
     *                  call AccountDAO.insertJoinedCustomer, set success to true to be returned as such.
     *              if it is not a known id:
     *                  log a warning and return false.
     *
     * @param a - Account to join a customer to.
     * @param c - Customer invoking the command.
     * @param idToJoin - int id of the customer being joined to an account
     * @return boolean - true if the customer is successfully joined to the account, false if not
     */
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

    /**
     * Check if the customer invoking the command has the same id as the one entered to remove.
     * (Did the owner of the account try and remove themselves?)
     *
     *      If they typed their own id in:
     *          log a warning, and return false
     *
     *      if not:
     *          check and make sure they are the owner of the account
     *
     *          if not: return false
     *
     *          if so:
     *              check if the customer they are trying to remove is joined to the account
     *
     *              if not:
     *                  log a warning and return false
     *              if so:
     *                  proceed with removal, log a success message and return true.
     *
     * @param a - Account to remove customer from
     * @param c - Customer invoking the command
     * @param customerIdToRemove - int the id of the customer to remove
     * @return boolean true if they are successfully removed, false if not
     */
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

    /**
     * If the customer invoking this command isn't the owner: return false.
     * If the newOwnerId is not a known customer: log a warning and return false.
     *      if the newOwnerId is a known customer:
     *          check if they are joined to the account:
     *              if they are not:
     *                  log a warning, return false
     *              if they are:
     *                   proceed with changing owners, log a success message, return true
     * @param a - Account to change the owner of
     * @param old_owner - Customer to revoke ownership
     * @param newOwnerId - int id of customer to give ownership
     * @return boolean - true if changing owner is successful.
     */
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

    /**
     * Check if the customer is the owner of the account
     * @param a Account to check against
     * @param c Customer to check against
     * @return boolean - true if Customer is Account owner
     */
    @Override
    public boolean checkOwnerShip(Account a, Customer c) {
        return aDao.selectAccountOwner(a, c);
    }

    /**
     * Get all the transactions for a given account
     * @param a - Account to get transactions for
     * @return ArrayList containing Transactions objects
     */
    @Override
    public RevaList<Transaction> getAccountTransactions(Account a) {
        return aDao.selectAccountTransactions(a);
    }

}
