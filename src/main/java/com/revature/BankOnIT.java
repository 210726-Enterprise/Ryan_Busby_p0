package com.revature;


import com.revature.presentation.BankPresentation;
import com.revature.presentation.BankPresentationImpl;

public class BankOnIT {
    public static void main(String[] args) {
        BankPresentation bank = new BankPresentationImpl();
        bank.welcome();
    }
}
