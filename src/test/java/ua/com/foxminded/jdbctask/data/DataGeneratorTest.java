package ua.com.foxminded.jdbctask.data;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import ua.com.foxminded.jdbctask.exceptions.DataGenerationException;
import ua.com.foxminded.jdbctask.exceptions.DatabaseConnectionException;

class DataGeneratorTest {
    public static final String TEST_DB_PROPERTIES_PATH = "/test_config.properties";
    private static final DatabaseConnectionGetter dbConnGetter = new DatabaseConnectionGetter();
    private static final DataGenerator dataGenerator = new DataGenerator();
    
    @Test
    void testGenerateData_tablesCreated() throws DatabaseConnectionException, SQLException, DataGenerationException {
        Connection connection = dbConnGetter.getConnection(TEST_DB_PROPERTIES_PATH);
        dataGenerator.generateData(TEST_DB_PROPERTIES_PATH);
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[] { "TABLE" });

        resultSet.next();
        String expected = "courses";
        String actual = resultSet.getString("TABLE_NAME");
        assertEquals(expected, actual);

        resultSet.next();
        expected = "groups";
        actual = resultSet.getString("TABLE_NAME");
        assertEquals(expected, actual);

        resultSet.next();
        expected = "students";
        actual = resultSet.getString("TABLE_NAME");
        assertEquals(expected, actual);

        resultSet.next();
        expected = "students_courses";
        actual = resultSet.getString("TABLE_NAME");
        assertEquals(expected, actual);
        
        connection.close();
    }

}
