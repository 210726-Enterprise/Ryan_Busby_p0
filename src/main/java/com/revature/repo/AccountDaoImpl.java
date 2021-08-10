package com.revature.repo;

import com.revature.model.Account;
import com.revature.model.Customer;
import com.revature.model.Transaction;
import com.revature.util.ConnectionFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AccountDaoImpl implements AccountDAO {
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

    //TODO: implement the RevArrayList class instead of collections
    @Override
    public List<Account> selectCustomerAccount(Customer c) {
        List<Account> accountList = new ArrayList<>();
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

    @Override
    public List<Transaction> selectAccountTransactions(Account a) {
        String sql = "SELECT t.id, t.account_id, c.id as customer_id, t.ts, c.first, c.last, a.nickname, t.amount " +
                "FROM transaction t JOIN customer c ON t.customer_id = c.id " +
                "JOIN account a ON t.account_id = a.id WHERE account_id = ? " +
                "ORDER BY t.ts desc;";
        List<Transaction> transactions = new ArrayList<>();
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
