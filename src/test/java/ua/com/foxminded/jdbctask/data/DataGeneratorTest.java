package ua.com.foxminded.jdbctask.data;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

class DataGeneratorTest {
    private static final DatabaseConnectionGetter dbConnGetter = new DatabaseConnectionGetter();
    private static final DataGenerator dataGenerator = new DataGenerator();

    @Test
    void testGenerateData_tablesCreated()
            throws ClassNotFoundException, NullCredentialsException, SQLException, IOException {
        Connection connection = dbConnGetter.getConnection();
        dataGenerator.generateData();
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
