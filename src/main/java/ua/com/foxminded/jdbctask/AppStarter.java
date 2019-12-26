package ua.com.foxminded.jdbctask;

import java.io.IOException;
import java.sql.SQLException;

import ua.com.foxminded.jdbctask.data.DataGenerator;

public class AppStarter {

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        DataGenerator dataGenerator = new DataGenerator();
            dataGenerator.generateData();
    }
}
