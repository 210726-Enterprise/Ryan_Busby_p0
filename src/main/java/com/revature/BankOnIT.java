package com.revature;
import com.revature.presentation.BankPresentation;
import com.revature.presentation.BankPresentationImpl;
import org.apache.log4j.*;

//TODO: Implement CustomerDAO.selectCustomer(int id) when joining or dis-joining a customer from an account to flag if they entered a non-existent customer id (as done in changeAccountOwner)
//TODO: Normalize all parameter inputs to take Customer, Account
//TODO: Organize BankPresentationImpl : break into other presentation classes (one for each page?)
//TODO: A customer "HAS AN" account, an account "HAS A" transaction -> explore use of nested classes to possibly simplify things
//TODO: Never print stack trace, just give a nice message but log the stack trace to a file
//TODO: Print a list of joined customers when user begins process of dis-joining the customer. Make a list of FirstName LastName and a number to select the name, this is better than needing to know the id of the person to dis-join
//TODO: Hide password from showing when getting typed out

public class BankOnIT {

    private final Logger logger = LogManager.getLogger(BankOnIT.class);

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static void main(String[] args) {
        // configure the logger for the application
        ConsoleAppender ca = new ConsoleAppender();
        ca.setThreshold(Level.INFO);
        ca.setLayout(new PatternLayout(ANSI_RED+"%n%p - %m%n%n"+ANSI_RESET));
        ca.activateOptions();
        Logger.getRootLogger().addAppender(ca);
        Logger.getLogger(BankOnIT.class);

        // init the bank presentation implementation and call the first menu
        BankPresentation bank = new BankPresentationImpl();
        bank.welcome();
    }
}
