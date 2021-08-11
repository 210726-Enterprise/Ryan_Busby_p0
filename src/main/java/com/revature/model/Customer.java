package com.revature.model;

/**
 * Customer represents a person who has a username and password for this application
 * id - The unique identifier.
 * first - Their inputted first name.
 * last - Their inputted last name.
 * username - Their inputted username.
 *
 * Their password is saved in the database, and a comparison is made
 * when they log in, but is never brought into memory from the database (its encrypted),
 * or explicitly attached to this.
 */
public class Customer {
    private int id;
    private String first;
    private String last;
    private String username;

    public Customer() {
    }

    public Customer(String first, String last, String username) {
        this.first = first;
        this.last = last;
        this.username = username;
    }

    public Customer(int id, String first, String last, String username) {
        this.id = id;
        this.first = first;
        this.last = last;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
