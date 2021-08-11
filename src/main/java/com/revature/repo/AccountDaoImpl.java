package com.revature.repo;

import com.revature.model.Account;
import com.revature.model.Customer;
import com.revature.model.Transaction;
import com.revature.util.ConnectionFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import com.revature.collection.RevaList;
import com.revature.collection.RevArrayList;

/**
 * AccountDaoImpl handles all the create, read, update, and delete operations to the database relevant to accounts.
 */
public class AccountDaoImpl implements AccountDAO {

    /**
     * Adds a new account to the account table.
     * @param a Account object instantiated by user, needing persisted into the database
     */
    @Override
    public void insertAccount(Account a) {
        int newAccountId=0;
        String sql = "INSERT INTO account (nickname, type, balance) VALUES (?, ?, ?) RETURNING id";
        try (Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, a.getNickname());
            ps.setInt(2, a.getType());
            ps.setDouble(3, a.getBalance());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                newAccountId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        a.setId(newAccountId);
    }

    /**
     * Used to join another customer to an existing account by adding a row to the customer_account table.
     * @param a - Account object which will be acted on
     * @param c - Customer object which will be made owner of account
     */
    @Override
    public void insertAccountOwnerShip(Account a, Customer c) {
        String sql = "INSERT INTO customer_account VALUES (?, ?)";
        try(Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, c.getId());
            ps.setInt(2, a.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Join a customer to an account by inserting a row into customer_account
     * @param a - Account object to associate a customer tro
     * @param customerIdToJoin - int the id of the customer to join to the account
     * @return boolean - true if the insert is successful
     */
    @Override
    public boolean insertJoinedCustomer(Account a, int customerIdToJoin) {
        int rowCount = 0;
        String sql = "INSERT INTO customer_account VALUES (?, ?, false)";
        try (Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, customerIdToJoin);
            ps.setInt(2, a.getId());
            rowCount = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowCount > 0;
    }

    /**
     * Insert a transaction into the Transaction table
     * @param a - Account object representing the account that was transacted upon.
     * @param c - Customer object representing the customer who executed the transaction.
     * @param amount - Double the amount that was added or subtracted from the account balance.
     */
    @Override
    public void insertTransaction(Account a, Customer c, Double amount) {
        String sql = "INSERT INTO transaction (account_id, customer_id, amount) VALUES (?,?,?)";
        try(Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, a.getId());
            ps.setInt(2, c.getId());
            ps.setDouble(3, amount);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Check to see if a customer is joined to an account by selecting
     *  from customer_account where conditions are met.
     * @param a - Account object to check if a customer is joined to
     * @param customerIdToJoin - int the id to check if is joined to the account.
     * @return boolean - true if the customer is joined to the account
     */
    public boolean selectCustomerIsJoined(Account a, int customerIdToJoin) {
        String sql = "SELECT FROM customer_account WHERE customer_id = ? and account_id = ?";
        try(Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, customerIdToJoin);
            ps.setInt(2, a.getId());
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Select all the accounts associated with a customer.
     * @param c - Customer object representing the customer whose accounts are being retrieved.
     * @return ArrayList of Account objects
     */
    @Override
    public RevaList<Account> selectCustomerAccount(Customer c) {
        RevaList<Account> accountList = new RevArrayList<>();
        String sql = "SELECT * FROM customer_account ca JOIN account a ON ca.account_id = a.id WHERE ca.customer_id=?";
        try(Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, c.getId());
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Account a = new Account(rs.getString("nickname"), rs.getByte("type"), rs.getDouble("balance"));
                a.setId(rs.getInt("id"));
                accountList.add(a);
            }
        } catch (SQLException e ) {
            e.printStackTrace();
        }
        return accountList;
    }
/*
    @Override
    public Account selectAccountById(int accountId) {
        Account account = new Account();
        String sql = "SELECT * FROM account WHERE id = ?";
        try(Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                account.setId(rs.getInt("id"));
                account.setNickname(rs.getString("nickname"));
                account.setType(rs.getByte("type"));
                account.setBalance(rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }
*/

    /**
     * Selecting owner from customer_account according to method params.
     * @param a - Account object representing the account in question.
     * @param c - Customer object representing the customer in question.
     * @return boolean - true if the customer is the owner of the account.
     */
    public boolean selectAccountOwner(Account a, Customer c) {
        String sql = "SELECT owner FROM customer_account WHERE customer_id=? AND account_id=?";
        boolean is_owner = false;
        try (Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, c.getId());
            ps.setInt(2, a.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                is_owner = rs.getBoolean("owner");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return is_owner;
    }

    /**
     * Select all the transactions for one account.
     * @param a - Account object representing the account of interest
     * @return ArrayList of Transaction object associated with the Account
     */
    @Override
    public RevaList<Transaction> selectAccountTransactions(Account a) {
        String sql = "SELECT t.id, t.account_id, c.id as customer_id, t.ts, c.first, c.last, a.nickname, t.amount " +
                "FROM transaction t JOIN customer c ON t.customer_id = c.id " +
                "JOIN account a ON t.account_id = a.id WHERE account_id = ? " +
                "ORDER BY t.ts desc;";
        RevaList<Transaction> transactions = new RevArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, a.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setAccountId(rs.getInt("account_id"));
                transaction.setCustomerId(rs.getInt("customer_id"));
                String name = String.format("%s %s", rs.getString("first"), rs.getString("last"));
                transaction.setName(name);
                transaction.setAccountNickname(rs.getString("nickname"));
                String ts = new SimpleDateFormat("MM-dd-yyyy hh:mm aa").format(rs.getTimestamp("ts"));
                transaction.setTimestamp(ts);
                transaction.setAmount(rs.getDouble("amount"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Update customer_account, set owner=false where customer_id=old_owner , set owner=true where customer_id=new_owner
     * This is done via a stored procedure named 'change_owner'.
     * @param a - Account object to update the owner of.
     * @param old_owner - Customer object representing the former owner of the account.
     * @param new_owner - Customer object representing the new_owner of the account.
     */
    @Override
    public void updateAccountOwner(Account a, Customer old_owner, Customer new_owner) {
        String sql = "CALL CHANGE_OWNER(?, ?, ?)";
        try (Connection connection = ConnectionFactory.getConnection()) {
            CallableStatement proc = connection.prepareCall(sql);
            proc.setInt(1, old_owner.getId());
            proc.setInt(2, new_owner.getId());
            proc.setInt(3, a.getId());
            proc.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the balance column of the 'account' table
     * @param a - Account object representing the account being updated
     * @param new_balance - Double the new value to set balance to
     */
    @Override
    public void updateAccountBalance(Account a, double new_balance) {
        // record the transaction. stored procedure?
        String sql = "UPDATE account SET balance = ? WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDouble(1, new_balance);
            ps.setInt(2, a.getId());
            ps.execute();
        } catch (SQLException e) {
            // catch check constraint from postgres that balance doesn't go below 0
            // insert transaction
            e.printStackTrace();
        }
    }

    /**
     * Delete a row from customer_account
     * @param a - Account object representing the account to remove a joined customer from
     * @param customerIdToRemove - int the id of the customer to remove
     * @return boolean - true if delete had an effect.
     */
    @Override
    public boolean deleteJoinedCustomer(Account a, int customerIdToRemove) {
        String sql = "DELETE FROM customer_account WHERE customer_id = ? AND account_id = ? AND owner=false";
        int rowsEffected = 0;
        try (Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, customerIdToRemove);
            ps.setInt(2, a.getId());
            rowsEffected = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowsEffected > 0;
    }

    /**
     * Delete a row from the 'account' table.
     * @param a - Account object representing the account to delete
     */
    @Override
    public void deleteAccount(Account a) {
        String sql = "DELETE FROM account WHERE id = ?";
        try(Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, a.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
