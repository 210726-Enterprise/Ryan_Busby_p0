package com.revature.presentation;

import com.revature.model.Account;
import com.revature.model.Customer;
import com.revature.model.Transaction;
import com.revature.repo.AccountDAO;
import com.revature.repo.AccountDaoImpl;
import com.revature.repo.CustomerDAO;
import com.revature.repo.CustomerDaoImpl;
import com.revature.service.AccountService;
import com.revature.service.AccountServiceImpl;
import com.revature.service.CustomerService;
import com.revature.service.CustomerServiceImpl;
import com.revature.util.PageReader;
import org.apache.log4j.Logger;

import java.util.Locale;
import java.util.Scanner;
import com.revature.collection.RevaList;
import com.revature.collection.RevArrayList;

/**
 * BankPresentationImpl handles all user interaction.
 * It begins with welcome() and depending on the input, directs to appropriate menus.
 *
 * When user successfully signs in, a Customer object is instantiated, and called upon
 * throughout this class.
 *
 * When a user invokes signUp, a Customer object is instantiated, and called upon
 * throughout this class.
 */

//TODO: Make private method to take in all those string builder calls that are repeated so many times.

public class BankPresentationImpl implements BankPresentation {

    // codes to change text colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_GREEN = "\u001B[32m";

    // codes to change background colors
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    // common spacing for consistent display
    private static final String nSixTabs = "\n\t\t\t\t\t\t";
    private static final String sixTabs = "\t\t\t\t\t\t";

    // the customer service object used throughout this presentation layer.
    private final CustomerDAO cDao = new CustomerDaoImpl();
    private final CustomerService cService = new CustomerServiceImpl(cDao);

    // the account service object used throughout this presentation layer.
    private final AccountDAO aDao = new AccountDaoImpl();
    private final AccountService aService = new AccountServiceImpl(aDao, cDao);

    // the logger which is configured in BankOnIT.java
    private final Logger logger = Logger.getLogger("BankOnIT");

    // the scanner object use to collect user input
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * welcome() is how a user starts utilizing the application. Directing them to either signUp, or logIn.
     */
    @Override
    public void welcome() {
        System.out.print(ANSI_GREEN);
        String title = PageReader.readPage("src/main/resources/slantTitle");
        System.out.println(title);
        System.out.println(ANSI_RESET);
        System.out.println("\t\t"+"[1] Log In");
        System.out.println("\t\t"+"[2] Sign Up");
        System.out.println("\t\t"+"[x] Exit\n");
        System.out.print("Enter: ");

        String choice = scanner.nextLine();
        switch (choice) {
            case "x":
                System.exit(0);
            case "1":
                logIn();
                break;
            case "2":
                signUp();
                break;
                default:
                    logger.warn("Invalid Input");
                    welcome();
        }
    }

    /**
     * signUp() is used to Create a Customer object in memory,
     * and persist the customer's name and login credentials to a database.
     */
    @Override
    public void signUp(){
        String title = "Bank on IT - Customer Sign Up";
        String line1 = lineMaker("[x] exit", title, "", " ", 100);
        System.out.println(line1);
        String line2 = lineMaker("[z] back", "", "", " ",100);
        System.out.println(line2);
        System.out.print("\n\n\tFirst Name: ");
        String first = scanner.nextLine();
        // using if / else here oppose to switch because there are only two valid options.
        if (first.equals("x")) {
            System.exit(0);
        } else if (first.equals("z")) {
            welcome();
        }
        System.out.print("\tLast Name: ");
        String last = scanner.nextLine();
        if (last.equals("x")) {
            System.exit(0);
        } else if (last.equals("z")) {
            welcome();
        }
        String username = rollUsername();
        System.out.print("\tPassword: ");
        String password = scanner.nextLine();
        if (password.equals("x")) {
            System.exit(0);
        } else if (password.equals("z")) {
            welcome();
        }
        Customer c = new Customer(first, last, username.toLowerCase(Locale.ROOT));
        cService.createCustomer(c, password);
        createAccount(c);
    }

    /**
     * A helper method to recursively ask for a unique username.
     * It will call CustomerService.usernameExists to determine if
     * the username being entered already exists in the database
     * @return The unique username the customer needs to logIn.
     */
    private String rollUsername() {
        System.out.print("\tUsername: ");
        String username = scanner.nextLine();
        if (username.equals("x")) {
            System.exit(0);
        } else if (username.equals("z")) {
            welcome();
        } else if (cService.usernameExists(username)) {
            logger.warn("Username Already Taken");
            rollUsername();
        }
        return username;
    }

    /**
     * When an existing Customer wants to access their data,
     * they use logIn. It calls CustomerService.getCustomer
     * with their username and password, and if there is a
     * matching username / password, a Customer object is
     * instantiated in memory and used throughout this class.
     * When the successfully logIn, they are given the accountSummary.
     * Customer as an instance variable id, which is how all
     * the relevant account information is tied to them.
     */

    @Override
    public void logIn() {
        String title = "Bank on IT - Customer Log In";
        String line1 = lineMaker("[x] exit", title, "", " ", 100);
        System.out.println(line1);
        String line2 = lineMaker("[z] back", "", "", " ",100);
        System.out.println(line2);
        System.out.print("\n\n\tUsername: ");
        String username = scanner.nextLine();
        if (username.equals("x")) {
            System.exit(0);
        } else if (username.equals("z")) {
            welcome();
        }
        System.out.print("\tPassword: ");
        String password = scanner.nextLine();

        Customer customer = cService.getCustomer(username.toLowerCase(Locale.ROOT), password);
        if (customer==null) {
            invalidCredentials();
        } else {
            accountSummary(customer);
            }
        }

    /**
     * If the user enters a username and password that don't match any
     * in the database, they will land here. Given options to signUp
     * or re-enter their login information.
     */
    @Override
    public void invalidCredentials() {
        logger.warn("Invalid Credentials");
        System.out.println("\t\t[1] Re-Enter Login Information");
        System.out.println("\t\t[2] Create User Account");
        System.out.println("\t\t[x] Exit\n");
        System.out.print("Enter: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "x":
                System.exit(0);
            case "z":
                welcome();
                break;
            case "1":
                logIn();
                break;
            case "2":
                signUp();
                break;
            default:
                logger.warn("Invalid Input");
                invalidCredentials();
        }
    }

    /**
     *
     * @param c - The customer who is currently using the application. When they signIn,
     *          a Customer object is instantiated and is used throughout.
     *
     *          This method is used to create a new account.
     *          It calls AccountService.createAccount()
     *          with the Account object that is instantiated
     *          and defined through layers of validated input from user.
     */
    @Override
    public void createAccount(Customer c) {
        Account a = new Account();
        printHeader(c, "Bank on IT - Create New Account", "[h] Account Summary ");
        StringBuilder typeBlock = new StringBuilder(nSixTabs);
        String blockHeader = lineMaker("", "ACCOUNT TYPE", "", " ", 50);
        typeBlock.append(blockHeader);
        typeBlock.append(nSixTabs);
        typeBlock.append(lineMaker("","",""," ", 50));
        typeBlock.append(nSixTabs);
        typeBlock.append(lineMaker("", "[1] Checking", "", " ", 50));
        typeBlock.append(nSixTabs);
        typeBlock.append(lineMaker("", "[2] Savings ", "", " ", 50));
        typeBlock.append(nSixTabs);
        typeBlock.append(lineMaker("","",""," ", 50));
        System.out.println(typeBlock);
        System.out.print(sixTabs);
        typeBlock.append(ANSI_WHITE_BACKGROUND);
        typeBlock.append(ANSI_BLACK);
        System.out.print("Type: ");
        String type = scanner.nextLine();
        switch (type) {
            case "x":
                System.exit(0);
            case "z":
                welcome();
                break;
            case "h":
                accountSummary(c);
                break;
            case "1":
                a.setType((byte)1);
                break;
            case "2":
                a.setType((byte)2);
                break;
            default:
                logger.warn("Invalid Input");
                createAccount(c);
        }
        StringBuilder nicknameBlock = new StringBuilder(nSixTabs);
        String nnBlockHeader = lineMaker("", "ACCOUNT NICKNAME", "", " ", 50);
        nicknameBlock.append(nnBlockHeader);
        System.out.println(nicknameBlock);
        System.out.print(sixTabs);
        System.out.print("Nickname: ");
        String nickname = scanner.nextLine();
        switch (nickname) {
            case "x":
                System.exit(0);
            case "z":
                welcome();
                break;
            case "h":
                accountSummary(c);
                break;
            default:
                a.setNickname(nickname);
        }
        System.out.println(nSixTabs + lineMaker("", "OPENING BALANCE", "", " ", 50));
        System.out.print(sixTabs);
        System.out.print("Balance: ");
        String balance = scanner.nextLine();
        switch (balance) {
            case "x":
                System.exit(0);
            case "z":
                welcome();
                break;
            case "h":
                accountSummary(c);
                break;
            default:
                try {
                    double bal = Double.parseDouble(balance);
                    if (bal <= 0) {
                        logger.warn("You must enter a positive number");
                        createAccount(c);
                    }
                    a.setBalance(bal);
                    aService.createAccount(a, c);
                    accountSummary(c);
                }
                catch (NumberFormatException e) {
                    logger.warn("You must enter a positive number");
                    createAccount(c);
                }
        }
    }

    /**
     * This will call AccountService.getAccounts with the Customer object
     * to instantiate an ArrayList to hold in memory for displaying
     * all relevant data. (Balance / Nickname / Type). The private printAccounts
     * method is then called to display to the user all of their accounts.
     * Each account has a numbered option to invoke accountDetails
     * for that account. Options to show all transactions for all
     * customer's accounts and to create new account are also provided.
     * @param c The Customer object instantiated by logIn or signUp.
     */
    @Override
    public void accountSummary(Customer c) {
        printHeader(c,"Bank on IT - Account Summary", "");
        RevaList<Account> accounts = aService.getAccounts(c);
        printAccounts(accounts, c);
    }

    /**
     *  Helper method to accountSummary to split up accounts according to type
     *  and handle user input. AccountService.getTypeAccounts splits the accounts ArrayList
     *  into checking and savings accounts.
     * @param accounts - ArrayList of all accounts associated to logged in customer
     * @param c - Customer object representing the logged-in user.
     */
    private void printAccounts(RevaList<Account> accounts, Customer c) {
        RevaList<Account> checking = aService.getTypeAccounts(accounts, 1);
        RevaList<Account> savings = aService.getTypeAccounts(accounts, 2);
        int startIdx = 1;
        if (checking.size()>0) {
            typeAccountSummary(checking, "CHECKING", startIdx);
            startIdx += checking.size();
        }
        if (savings.size()>0) {
            typeAccountSummary(savings, "SAVINGS", startIdx);
        }
        int possibleAccountChoices = checking.size() + savings.size();

        RevArrayList<String> validOptions = new RevArrayList<>();
        for (int i=1; i <= possibleAccountChoices; i++) {
            validOptions.add(Integer.toString(i));
        }
        validOptions.add("x");
        validOptions.add("z");
        validOptions.add("c");
        validOptions.add("v");
        validOptions.add("t");

        System.out.print(nSixTabs);
        System.out.println("[c] Create New Account");
        if ((checking.size() + savings.size()) > 0) {
            System.out.print(sixTabs);
            System.out.println("[v] View all Transactions");
        }
        if ((checking.size() + savings.size()) >= 2)
        {
            System.out.print(sixTabs);
            System.out.println("[t] Transfer Between Accounts");
        }
        System.out.print(nSixTabs);
        System.out.print("Enter: ");

        String choice = scanner.nextLine();
        if (validOptions.contains(choice)){
            switch (choice) {
                case "x":
                    System.exit(0);
                case "z":
                    welcome();
                    break;
                case "c":
                    createAccount(c);
                    break;
                case "v":
                    // Call CustomerService.getTransactions here in case they enter something invalid, the recursive call to viewAllTransactions won't require a database query each time
                    RevaList<Transaction> allTransactions = cService.getTransactions(c);
                    viewAllTransactions(c, allTransactions);
                    break;
                case "t":
                    transfer(c, checking, savings);
                    break;
                default:
                    int choiceAsInt = Integer.parseInt(choice);
                    if (choiceAsInt<=checking.size()) {
                        // they chose a checking account
                        accountDetails(c, checking.get(choiceAsInt-1));
                    } else {
                        // they chose a savings account
                        accountDetails(c, savings.get((choiceAsInt-1)- checking.size()));
                    }
                    break;
            }
        } else {
            logger.warn("Invalid Input");
            printAccounts(accounts, c);
        }
    }

    /**
     * Helper method to printAccounts to Create a well formatted block from
     * an ArrayList of Account objects.
     * @param accounts - ArrayList of one type of Account
     * @param type - String denoting savings or checking
     * @param startIdx - int Where to start the numbering for user selection
     */
    private void typeAccountSummary(RevaList<Account> accounts, String type, int startIdx) {
        StringBuilder accountBlock = new StringBuilder(nSixTabs);
        String header = lineMaker("", String.format("%s ACCOUNTS", type), "", " ", 50);
        accountBlock.append(header);
        accountBlock.append("\n");
        accountBlock.append(sixTabs);
        String separator = lineMaker("", "", "", "-", 50);
        accountBlock.append(separator);
        accountBlock.append("\n");
        for(int i = 0; i < accounts.size(); i++) {
            accountBlock.append(sixTabs);
            String leftJust = String.format(" [%d] %s", startIdx, accounts.get(i).getNickname());
            String rightJust = String.format("$%.2f ", accounts.get(i).getBalance());
            String line = lineMaker(leftJust, "", rightJust, " ", 50);
            accountBlock.append(line);
            accountBlock.append("\n");
            startIdx++;
        }
        String lastLine = lineMaker("","","", " ", 50);
        accountBlock.append(sixTabs);
        accountBlock.append(lastLine);
        System.out.println(accountBlock);
    }

    /**
     * When a user select an account from accountSummary, they get here.
     * This displays an account's balance as well as a list of available options.
     * Deposit/Withdrawal/ViewTransactions is always available.
     * Join/Remove/Transfer Ownership/Close Account are only available
     * if the signed in customer is the owner of the account and not just a joined customer.
     * @param c The Customer object that represents the logged-in user.
     * @param a The Account object selected by the user in accountSummary to display details for
     *
     */
    @Override
    public void accountDetails(Customer c, Account a) {
        boolean isOwner = aService.checkOwnerShip(a, c);
        printHeader(c, "Bank on IT - Account Details", "[h] Account Summary ");
        StringBuilder accDetails = new StringBuilder(nSixTabs);
        accDetails.append(nSixTabs);
        accDetails.append(lineMaker("",a.getNickname(), "", " ", 50));
        accDetails.append(nSixTabs);
        accDetails.append(lineMaker("","",""," ",50));
        String bal = String.format("$%.2f", a.getBalance());
        accDetails.append(nSixTabs);
        accDetails.append(lineMaker("Balance: ", "", bal, " ", 50));
        accDetails.append(nSixTabs);
        accDetails.append(lineMaker("","", ""," ",50));
        accDetails.append(nSixTabs);
        accDetails.append(lineMaker("[1] Deposit", "","", " ", 50));
        accDetails.append(nSixTabs);
        accDetails.append(lineMaker( "[2] Withdrawal", "","", " ", 50));
        accDetails.append(nSixTabs);
        accDetails.append(lineMaker( "[3] View Transactions","", " ", " ", 50));
        if (isOwner) {
            accDetails.append(nSixTabs);
            accDetails.append(lineMaker("[4] Join Another Customer", "","",  " ", 50));
            accDetails.append(nSixTabs);
            accDetails.append(lineMaker( "[5] Remove Joined Customer", "","", " ", 50));
            accDetails.append(nSixTabs);
            accDetails.append(lineMaker( "[6] Transfer Ownership", "", "", " ", 50));
            accDetails.append(nSixTabs);
            accDetails.append(lineMaker( "[7] Close Account", "","", " ", 50));
        }
        System.out.println(accDetails);
        accountOptions(c, a);
    }

    /**
     * The helper method called in accountDetails to handle the user's input.
     * Called recursively until input is valid.
     * @param c The Customer object that represents the logged-in user.
     * @param a The Account object selected by the user in accountSummary to display details for
     */
    private void accountOptions(Customer c, Account a) {
        System.out.print(sixTabs);
        System.out.print("Enter: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "x":
                System.exit(0);
            case "z":
                welcome();
                break;
            case "h":
                accountSummary(c);
                break;
            case "1":
                deposit(c, a);
                break;
            case "2":
                withdrawal(c, a);
                break;
            case "3":
                // Call AccountService.getAccountTransactions here so viewTransactions can be called recursively without needed to query database again.
                RevaList<Transaction> transactions = aService.getAccountTransactions(a);
                viewTransactions(c, a, transactions);
                break;
            case "4":
                addCustomerToAccount(a, c);
                break;
            case "5":
                removeCustomerFromAccount(a, c);
                break;
            case "6":
                transferAccountOwner(c, a);
                break;
            case "7":
                deleteAccount(c, a);
                break;
            default:
                logger.warn("Invalid Input");
                accountOptions(c, a);
        }
    }

    /**
     * When a user selects Deposit from accountDetails, they get here
     * AccountService.Deposit is called once valid input is received.
     * If they enter something other than a positive integer, they
     * are taken back to AccountDetails.
     * @param c The Customer object that represents the logged-in user.
     * @param a The Account object selected by the user in accountSummary to display details for
     */
    @Override
    public void deposit(Customer c, Account a) {
        System.out.print(sixTabs);
        System.out.print("Deposit Amount: ");
        String amount = scanner.nextLine();
        try {
            double goodAmount = Double.parseDouble(amount);
            if (goodAmount <= 0) {
                logger.warn("Invalid Input");
                accountDetails(c, a);
            }
            aService.Deposit(c, a, goodAmount);
        } catch (Exception e) {
            logger.warn("Invalid Input");
            accountDetails(c, a);
        }
        accountDetails(c, a);

    }

    /**
     * When a user selects Withdrawal from accountDetails, they get here
     * AccountService.Withdrawal is called once valid input is received.
     * If they enter something other than a positive integer, they
     * are taken back to AccountDetails.
     * @param c The Customer object that represents the logged-in user.
     * @param a The Account object selected by the user in accountSummary to display details for
     */
    @Override
    public void withdrawal(Customer c, Account a) {
        System.out.print(sixTabs);
        System.out.print("Withdrawal Amount: ");
        String amount = scanner.nextLine();
        try {
            double goodAmount = Double.parseDouble(amount);
            if (goodAmount <= 0) {
                logger.warn("Invalid Input");
                accountDetails(c, a);
            }
            boolean success = aService.Withdrawal(c, a, goodAmount);
            if (!success) {
                logger.warn("Could not complete the transaction");
            }
        } catch (Exception e) {
            logger.warn("Invalid Input");
            accountDetails(c, a);
        }
        accountDetails(c, a);
    }

    /**
     * When logged in Customer selects to transfer from AccountSummary, they get here.
     * This ultimately calls AccountService.Transfer after getting valid input
     * for the Account to transfer from, the account to transfer to, and the
     * amount they wish to transfer.
     * @param c The Customer object that represents the logged-in user.
     * @param checking - An ArrayList of the signed in customer's checking accounts ()
     * @param savings - An ArrayList of the signed in customer's savings accounts
     */
    @Override
    public void transfer(Customer c, RevaList<Account> checking, RevaList<Account> savings) {
        Account fromAccount = null;
        Account toAccount = null;
        printHeader(c, "Bank on IT - Transfer", "[h] Account Summary ");
        int startIdx = 1;
        if (checking.size()>0) {
            typeAccountSummary(checking, "CHECKING", startIdx);
            startIdx += checking.size();
        }
        if (savings.size()>0) {
            typeAccountSummary(savings, "SAVINGS", startIdx);
        }
        System.out.print(sixTabs);
        System.out.print("Transfer From: ");
        String fromAccountChoice = scanner.nextLine();
        int possibleAccountChoices = checking.size() + savings.size();
        RevArrayList<String> validOptions = new RevArrayList<>();
        for (int i=1; i <= possibleAccountChoices; i++) {
            validOptions.add(Integer.toString(i));
        }
        validOptions.add("x");
        validOptions.add("z");
        validOptions.add("h");
        // check if valid choice
        if (validOptions.contains(fromAccountChoice)) {
            switch (fromAccountChoice) {
                case "x":
                    System.exit(0);
                case "z":
                    welcome();
                    break;
                case "h":
                    accountSummary(c);
                    break;
                default:
                    int fromAccountAsInt = Integer.parseInt(fromAccountChoice);
                    if (fromAccountAsInt<=checking.size()) {
                        // they chose a checking account
                        fromAccount = checking.get(fromAccountAsInt-1);
                    } else {
                        // they chose a savings account
                        fromAccount = savings.get((fromAccountAsInt-1)- checking.size());
                    }
                    validOptions.remove(fromAccountChoice);
                    break;
            }
        } else {
            logger.warn("Invalid Input");
            transfer(c, checking, savings);
        }
        prettyTransfer(fromAccount, null);
        System.out.print("Transfer To: ");


        String toAccountChoice = scanner.nextLine();
        // check if valid choice
        if (validOptions.contains(toAccountChoice)) { // make private method for this
            switch (toAccountChoice) {
                case "x":
                    System.exit(0);
                case "z":
                    welcome();
                    break;
                case "h":
                    accountSummary(c);
                    break;
                default:
                    int toAccountAsInt = Integer.parseInt(toAccountChoice);
                    if (toAccountAsInt<=checking.size()) {
                        // they chose a checking account
                        toAccount = checking.get(toAccountAsInt-1);
                    } else {
                        // they chose a savings account
                        toAccount = savings.get((toAccountAsInt-1) - checking.size());
                    }
                    break;
            }
        } else {
            logger.warn("Invalid Input");
            transfer(c, checking, savings);
        }

        prettyTransfer(fromAccount, toAccount);
        System.out.print("Amount: ");
        String amount = scanner.nextLine();
        // make sure it's a number
        double goodAmount;
        try {
            goodAmount = Double.parseDouble(amount);
            if (goodAmount > 0) {
                boolean transferSuccessful = aService.Transfer(c, fromAccount, toAccount, Double.parseDouble(amount));
                if (!transferSuccessful) {
                    transfer(c, checking, savings);
                }
                accountSummary(c);
            } else {
                logger.warn("Transfer amount must be entered as a positive number");
                transfer(c, checking, savings);
            }
        } catch (NumberFormatException e) {
            logger.warn("Transfer amount must be entered as a number");
            transfer(c, checking, savings);
        }
    }

    /**
     * Helper method to display a nicely formatted block so user can see which account they've
     * selected to transfer to and from and what the current balance of each is
     * @param fromAccount - Account object representing the account the user selected to transfer from.
     * @param toAccount - Account object representing the account th user selected to transfer to.
     */
    private void prettyTransfer(Account fromAccount, Account toAccount) {
        StringBuilder transferBlock = new StringBuilder("\n\t\t");
        transferBlock.append(lineMaker("", "Make a Transfer", "", " ", 80));
        transferBlock.append("\n\t\t");
        String fromAccountName = "";
        String fromAccountBal = "";
        String toAccountName = "";
        String toAccountBal = "";
        if (fromAccount!=null) {
            fromAccountName = String.format(" From: %s", fromAccount.getNickname());
            fromAccountBal = String.format(" Available Balance: %.2f", fromAccount.getBalance());
        }
        if (toAccount!=null) {
            toAccountName = String.format("To: %s ", toAccount.getNickname());
            toAccountBal = String.format("Balance: %.2f ", toAccount.getBalance());
        }
        transferBlock.append(lineMaker(fromAccountName, "", toAccountName, " ", 80));
        transferBlock.append("\n\t\t");
        transferBlock.append(lineMaker(fromAccountBal, "", toAccountBal, " ", 80));
        transferBlock.append("\n\t\t");
        System.out.print(transferBlock);
    }

    /**
     * When logged in customer selects view transactions for an AccountDetail page, they get here.
     * This will display a nicely formatted block containing each transaction for the selected account.
     * After this the user can only go back to AccountSummary, LogOff, or close the application.
     * @param c Customer object representing the logged in customer.
     * @param a Account object representing the account the logged in customer selected to view transactions of
     * @param transactions - ArrayList of Transaction objects
     */
    @Override
    public void viewTransactions(Customer c, Account a, RevaList<Transaction> transactions) {
        printHeader(c, "Bank On IT - Account Transactions", "[h] Account Summary");
        StringBuilder transactionsBlock = new StringBuilder();
        transactionsBlock.append("\n\t\t");
        double bal = a.getBalance();
        String headRightJust = String.format("Current Balance: $%.2f ", bal);
        transactionsBlock.append(lineMaker(a.getNickname(), "", headRightJust, " ", 80));
        transactionsBlock.append(" Running Balance");
        transactionsBlock.append("\n\t\t");
        transactionsBlock.append(lineMaker("","","","-", 80));
        for(int i = 0; i < transactions.size(); i++) {
            transactionsBlock.append("\n\t\t");
            String leftJust = String.format(" %s - (%s)", transactions.get(i).getTimestamp(), transactions.get(i).getName());
            double amount = transactions.get(i).getAmount();
            String amountString;
            if (amount > 0) {
                // this is a deposit
                amountString = String.format("%.2f ", amount);
            } else {
                // this is a withdrawal
                amountString = String.format("-$%.2f ", -amount);
            }
            String line = lineMaker(leftJust, "", amountString, " ", 80);
            transactionsBlock.append(line);
            transactionsBlock.append(String.format(" $%.2f", bal -= amount));
        }
        System.out.println(transactionsBlock);
        System.out.print("\n\t\t");
        System.out.print("Enter (h/z/x): ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "x":
                System.exit(0);
            case "z":
                welcome();
                break;
            case "h":
                accountSummary(c);
                break;
            default:
                logger.warn("Invalid Input");
                viewTransactions(c, a, transactions);
        }
    }

    /**
     * When a logged in customer selects to view all transactions from the AccountSummary page,
     * they get here. This displays a nicely formatted block of all the transactions in the transactions ArrayList
     * @param c - Customer object representing the logged in customer.
     * @param transactions - ArrayList of Transaction objects associated with any of the user's accounts.
     */
    @Override
    public void viewAllTransactions(Customer c, RevaList<Transaction> transactions) {
        printHeader(c, "Bank On IT - All Transactions", "[h] Account Summary");
        StringBuilder transactionsBlock = new StringBuilder();
        for (int i = 0; i < transactions.size(); i++) {
            transactionsBlock.append("\n\t\t");
            String leftJust = String.format("%s - (%s)    ", transactions.get(i).getTimestamp(), transactions.get(i).getName());
            double amount = transactions.get(i).getAmount();
            String amountString;
            if (amount > 0) {
                // this is a deposit
                amountString = String.format("%.2f", amount);
            } else {
                // this is a withdrawal
                amountString = String.format("-$%.2f", -amount);
            }
            String line = lineMaker(leftJust, transactions.get(i).getAccountNickname(), amountString, " ", 80);
            transactionsBlock.append(line);
        }
        System.out.println(transactionsBlock);
        System.out.print("\n\t\t");
        System.out.print("Enter (h/z/x): ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "x":
                System.exit(0);
            case "z":
                welcome();
                break;
            case "h":
                accountSummary(c);
                break;
            default:
                logger.warn("Invalid Input");
                viewAllTransactions(c, transactions);
        }
    }

    /**
     *  If an account owner wishes to transfer ownership to someone joined on an account,
     *  they get here. AccountService.changeAccountOwner is ultimately called with the validated
     *  parameter the newOwnerId, along with the same parameter passed into this.
     * @param c - Customer object representing the logged in customer.
     * @param a - Account object representing the account the user selected to change ownership of.
     */
    @Override
    public void transferAccountOwner(Customer c, Account a) {
        System.out.print(nSixTabs);
        System.out.print("Enter New Owner Id: ");
        String newOwnerIdStr = scanner.nextLine();
        switch (newOwnerIdStr) {
            case "x":
                System.exit(0);
            case "z":
                welcome();
                break;
            case "h":
                accountSummary(c);
                break;
            default:
                try {
                        int newOwnerId = Integer.parseInt(newOwnerIdStr);
                        if (newOwnerId == c.getId()) {
                            logger.info("That's your user id and you're already the owner.\nEnter a user id of anyone joined to this account to transfer ownership");
                            transferAccountOwner(c, a);
                        }
                        boolean success = aService.changeAccountOwner(a, c, newOwnerId);
                        if (success) {
                            accountDetails(c, a);
                        } else {
                            transferAccountOwner(c, a);
                        }
                    } catch (NumberFormatException e) {
                        logger.warn("You must enter a number");
                        transferAccountOwner(c, a);
                    }
            accountDetails(c, a);
        }
    }

    /**
     * If the logged in customer selects to add a customer to an account
     * from the AccountDetail page, they get here. AccountService.joinCustomer
     * is called with the validated input of the id of the customer to join
     * along with the parameters passed into this.
     * @param c - Customer object representing the logged in customer.
     * @param a - Account object representing the account the user selected to add another customer to.
     */
    @Override
    public void addCustomerToAccount(Account a, Customer c) {
        System.out.print(nSixTabs);
        System.out.print("Customer to Join (Id): ");
        String idToJoinString = scanner.nextLine();
        switch (idToJoinString) {
            case "x":
                System.exit(0);
            case "z":
                welcome();
                break;
            case "h":
                accountSummary(c);
                break;
            default:
                try {
                    int idToJoin = Integer.parseInt(idToJoinString);
                    boolean success = aService.joinCustomer(a, c, idToJoin);
                    if (!success) {
                        addCustomerToAccount(a, c);
                    }
                } catch (NumberFormatException e) {
                    logger.warn("You must enter a number");
                    addCustomerToAccount(a, c);
                }
        }
        accountSummary(c);
    }

    /**
     * If the logged in customer selects to remove a customer from an account
     * from the AccountDetail page, they get here. AccountService.removeJoinedCustomer
     * is called with the validated input of the id of the customer to remove
     * along with the parameters passed into this.
     * @param c - Customer object representing the logged in customer.
     * @param a - Account object representing the account the user selected to remove customer from.
     */
    @Override
    public void removeCustomerFromAccount(Account a, Customer c) {
        // would be kinda cool to print out all the customers who are joined onto the account
        // first last and have them pick a number oppose to having to bring in their customer id
        System.out.print(nSixTabs);
        System.out.print("Customer to Remove (Id): ");
        String idToRemoveString = scanner.nextLine();
        switch (idToRemoveString) {
            case "x":
                System.exit(0);
            case "z":
                welcome();
                break;
            case "h":
                accountSummary(c);
                break;
            default:
            try {
                int idToRemove = Integer.parseInt(idToRemoveString);
                boolean success = aService.removeJoinedCustomer(a, c, idToRemove);
                if (!success) {
                    removeCustomerFromAccount(a, c);
                }
            } catch (NumberFormatException e) {
                logger.warn("You must enter a number");
                removeCustomerFromAccount(a, c);
            }
            accountSummary(c);
        }
    }

    /**
     * When a logged-in customer selects to close an account, they get here.
     * AccountService.deleteAccount is called once user confirms the action.
     * @param c - Customer object representing the logged in customer.
     * @param a - Account object representing the account the user selected to close.
     */
    @Override
    public void deleteAccount(Customer c, Account a) {
        boolean confirmed = rollDeleteConfirm(a.getNickname());
        if (confirmed) {
            aService.deleteAccount(a, c);
        }
        accountSummary(c);
    }

    /**
     * Helper method for deleteAccount, called recursively until user
     * explicitly types 'yes' or 'no' as confirmation.
     * @param nickname - String the nickname given to the account
     * @return boolean value representing the user's confirmation to close an account
     */
    private boolean rollDeleteConfirm(String nickname) {
        System.out.printf("Are you sure you want to close %s? [yes/no]: ", nickname);
        String ans = scanner.nextLine();
        switch (ans) {
            case "yes":
                return true;
            case "no":
                return false;
            default:
                System.out.println("Must enter 'yes' or 'no'.");
                rollDeleteConfirm(nickname);
        }
        return false;
    }


    /**
     * Helper method used throughout the class to display text in a consistent format.
     * @param leftJust - String - The string to left justify.
     * @param centerJust - String - The string to center.
     * @param rightJust - String - The string to right justify.
     * @param filler - String - The single character to space between each element of the line. //TODO: make this char
     * @param lineLen int - the total character length of the line to display
     * @return String - The nicely formatted line of text.
     */
    //TODO: overload this method for all those blank lines
    private static String lineMaker(String leftJust, String centerJust, String rightJust, String filler, int lineLen) {
        // this maybe be cleaner with charArray methods?
        StringBuilder line = new StringBuilder();
        line.append(ANSI_WHITE_BACKGROUND);
        line.append(ANSI_BLACK);
        line.append(leftJust);
        int halfLine = lineLen/2;
        int startCenter = halfLine - centerJust.length()/2;
        int spaceBetween = startCenter - leftJust.length();
        for (int i=0; i < spaceBetween; i++) {
            line.append(filler);
        }
        if (spaceBetween < 0) spaceBetween = 0;
        line.append(centerJust);
        int rightSpaceBetween = lineLen - (leftJust.length() + centerJust.length() + rightJust.length() + spaceBetween);
        for (int i=0; i < rightSpaceBetween; i++) {
            line.append(filler);
        }
        line.append(rightJust);
        line.append(ANSI_RESET);
        return line.toString();
    }

    /**
     * Helper method used throughout to consistently display text to inform user what menu they are in.
     * @param c - Customer object representing the logged in customer.
     * @param title - String - The main title for the display.
     * @param home - String - Provided as "[h]" always except when already in AccountSummary
     */
    private static void printHeader(Customer c, String title, String home) {
        System.out.println("\n\n\n");
        String line1 = lineMaker(" [x] exit", title, home, " ",100);
        System.out.println(line1);
        String name = String.format("%s %s", c.getFirst(), c.getLast());
        String customerId = String.format("Customer Id: %d ", c.getId());
        String line2 = lineMaker(" [z] log out", name, customerId, " ", 100);
        System.out.println(line2);
    }
}
