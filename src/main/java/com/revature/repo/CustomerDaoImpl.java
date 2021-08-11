package com.revature.repo;

import com.revature.model.Customer;
import com.revature.model.Transaction;
import com.revature.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import com.revature.collection.RevaList;
import com.revature.collection.RevArrayList;

/**
 * CustomerDaoImpl handles all the create, read, update, and delete operations to the database relevant to customers.
 */
public class CustomerDaoImpl implements CustomerDAO{

    /**
     * Insert into the customer table
     * @param c - Customer object containing the data to be inserted
     * @param password - String the password to persist in the database
     * @return int - the id of the newly created customer
     */
    @Override
    public int insertCustomer(Customer c, String password) {
        int newCustomerId = 0;
        String sql = "INSERT INTO customer (first, last, username, pword) VALUES (?, ?, ?, crypt(?, gen_salt('bf'))) returning id;";
        try (Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, c.getFirst());
            ps.setString(2, c.getLast());
            ps.setString(3, c.getUsername());
            ps.setString(4, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                newCustomerId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newCustomerId;
    }

    /**
     * Select from the customer table
     * @param username - String the username being searched for
     * @param password - String the password being searched for
     * @return Customer object containing their relevant data.
     */
    @Override
    public Customer selectCustomer(String username, String password) {
        String sql = "SELECT * FROM customer WHERE username = ? AND pword = crypt(?, pword)";
        Customer customer = null;
        try (Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                customer = new Customer(
                        rs.getInt("id"),
                        rs.getString("first"),
                        rs.getString("last"),
                        username
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

    /**
     * Select user from customer
     * @param username - String the username being searched for
     * @return boolean - true if that username is in the customer table.
     */
    @Override
    public boolean selectUserName(String username) {
        String sql = "SELECT username FROM customer WHERE username=?";
        boolean usernameExists = false;
        try (Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                usernameExists = true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return usernameExists;
    }

    /**
     * Select from customer table
     * @param customerId - int the id of the customer being searched for
     * @return Customer object containing their data
     */
    @Override
    public Customer selectCustomer(int customerId) {
        Customer c = null;
        String sql = "SELECT * FROM customer where id = ?";
        try(Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                c = new Customer();
                c.setId(rs.getInt("id"));
                c.setFirst(rs.getString("first"));
                c.setLast(rs.getString("last"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * Select all data relevant to a transaction from a join
     * @param c - Customer object containing the id being searched for
     * @return ArrayList of Transaction object
     */
    @Override
    public RevaList<Transaction> selectTransactions(Customer c) {
        String sql =
                "SELECT t.id, t.account_id, c.id as customer_id, t.ts, c.first, c.last, a.nickname, t.amount FROM transaction t \n" +
                        "JOIN customer c ON t.customer_id = c.id \n" +
                        "JOIN account a ON t.account_id = a.id\n" +
                        "WHERE account_id IN \n" +
                        "(SELECT account_id FROM customer_account WHERE customer_id = ?)\n" +
                        "ORDER BY t.ts desc;";
        RevaList<Transaction> transactions = new RevArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, c.getId());
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
}
