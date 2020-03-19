package ua.com.foxminded.jdbctask.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import jdbctask.exceptions.DatabaseConnectionException;
import jdbctask.exceptions.NullCredentialsException;

public class DatabaseConnectionGetter {
    private static final String DB_PROPERTIES_PATH = "/config.properties";
    private static final String NO_DB_CONNECTION = "Could not establish database connection.";

    public Connection getConnection() throws DatabaseConnectionException  {
        Connection connection = null;
        String jdbcDriver = null;
        String dbUrl = null;
        String user = null;
        String password = null;
        try (BufferedReader input = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(DB_PROPERTIES_PATH)))) {
            Properties properties = new Properties();
            properties.load(input);
            jdbcDriver = properties.getProperty("jdbcDriver");
            dbUrl = properties.getProperty("dbUrl");
            user = properties.getProperty("dbUser");
            password = properties.getProperty("dbPassword");
        } catch (IOException e) {
            throw new DatabaseConnectionException(NO_DB_CONNECTION);
        }
        boolean credentialsNotNull = (jdbcDriver != null) && (dbUrl != null) && (user != null) && (password != null);
        if (credentialsNotNull) {
            try {
                Class.forName(jdbcDriver);
                connection = DriverManager.getConnection(dbUrl, user, password);
            } catch (Exception e) {
                throw new DatabaseConnectionException(NO_DB_CONNECTION);
            }
        } else {
            throw new NullCredentialsException("One of credentials (jdbcDriver, dbUrl, dbUser, dbPassword) was null.");
        }
        return connection;
    }
}
