package com.revature.repo;

import com.revature.model.Customer;
import com.revature.model.Transaction;
import com.revature.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoImpl implements CustomerDAO{
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
//            e.printStackTrace(); // log this? nah
        }
        return newCustomerId;
    }

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

    @Override
    public List<Transaction> selectTransactions(Customer c) {
        String sql =
                "SELECT t.id, t.account_id, c.id as customer_id, t.ts, c.first, c.last, a.nickname, t.amount FROM transaction t \n" +
                        "JOIN customer c ON t.customer_id = c.id \n" +
                        "JOIN account a ON t.account_id = a.id\n" +
                        "WHERE account_id IN \n" +
                        "(SELECT account_id FROM customer_account WHERE customer_id = ?)\n" +
                        "ORDER BY t.ts desc;";
        List<Transaction> transactions = new ArrayList<>();
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
