package com.revature.model;

/**
 * Account represents a single account.
 * id - the unique identifier.
 * nickname - whatever the customer wants to call it.
 * type - 1 for checking, 2 for savings.
 * balance - how much money is contained in the account.
 */
public class Account {
    private int id;
    private String nickname;
    private byte type;
    private double balance;

    public Account() {
    }

    public Account(String nickname, byte type, double balance) {
        this.nickname = nickname;
        this.type = type;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", type=" + type +
                ", balance=" + balance +
                '}';
    }
}
