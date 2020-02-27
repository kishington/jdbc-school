package ua.com.foxminded.jdbctask.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionGetter {
    private static final String DB_PROPERTIES_PATH = "src/main/resources/config.properties";
    
    public Connection getConnection() throws IOException, SQLException, ClassNotFoundException {
        Connection connection = null;
        String jdbcDriver = null;
        String dbUrl = null;
        String user = null;
        String password = null;
        try (InputStream input = new FileInputStream(DB_PROPERTIES_PATH)) {
            Properties properties = new Properties();
            properties.load(input);
            jdbcDriver = properties.getProperty("jdbcDriver");
            dbUrl = properties.getProperty("dbUrl");
            user = properties.getProperty("dbUser");
            password = properties.getProperty("dbPassword");
        }
        boolean credentialsNotNull = (jdbcDriver != null) && (dbUrl != null) && (user != null) && (password != null);
        if (credentialsNotNull) {
            Class.forName(jdbcDriver);    
            connection = DriverManager.getConnection(dbUrl, user, password);
        } else {
            throw new NullPointerException();
        }
        return connection;
    }
}
