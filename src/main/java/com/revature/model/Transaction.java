package com.revature.model;

/**
 * Transaction represents a single action on an account balance.
 * id - uniquely identifies the transaction.
 * accountId - the account the action took place on.
 * customerId - the id of the customer who preformed the action.
 * name - the name of the customer who preformed the action.
 * accountNickname - the nickname of the account acted on.
 * timestamp - when the action took place.
 * amount - the amount added or subtracted from the account balance.
 */
public class Transaction {
    private int id;
    private int accountId;
    private int customerId;
    private String name;
    private String accountNickname;
    private String timestamp;
    private Double amount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNickname() {
        return accountNickname;
    }

    public void setAccountNickname(String accountNickname) {
        this.accountNickname = accountNickname;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
