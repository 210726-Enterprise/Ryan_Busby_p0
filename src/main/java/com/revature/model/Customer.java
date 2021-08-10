package com.revature.model;

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
