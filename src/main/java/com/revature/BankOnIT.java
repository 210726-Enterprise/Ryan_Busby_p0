package com.revature;
import com.revature.presentation.BankPresentation;
import com.revature.presentation.BankPresentationImpl;
import org.apache.log4j.*;

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
