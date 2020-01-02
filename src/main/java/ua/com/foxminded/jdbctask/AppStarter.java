package ua.com.foxminded.jdbctask;

import java.io.IOException;
import java.sql.SQLException;

import ua.com.foxminded.jdbctask.data.DataGenerator;
import ua.com.foxminded.jdbctask.menu.Dialog;

public class AppStarter {

    public static void main(String[] args) throws SQLException, IOException {
        DataGenerator dataGenerator = new DataGenerator();
        dataGenerator.generateData();
        
        Dialog dialog = new Dialog();
        dialog.start();
    }
}
