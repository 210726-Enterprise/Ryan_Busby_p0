package com.revature.util;

import java.sql.*;

/**
 * Class to instantiate a connection to the database whenever needed
 */

public class ConnectionFactory {

    private static final String DB_HOST=System.getenv("DB_HOST");
    private static final String DB_USER=System.getenv("DB_USER");
    private static final String DB_PASS=System.getenv("DB_PASS");
    private static final String DB_URL = String.format("jdbc:postgresql://%s:5432/bankonit?currentSchema=bank",
            DB_HOST
    );

    private static Connection connection;

    /**
     * Get the connection to the database
     * @return Connection
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException{
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        return connection;
    }
}

