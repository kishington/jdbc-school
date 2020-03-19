package ua.com.foxminded.jdbctask;

import jdbctask.exceptions.DataGenerationException;
import ua.com.foxminded.jdbctask.data.DataGenerator;
import ua.com.foxminded.jdbctask.menu.Dialog;

public class AppStarter {

    public static void main(String[] args) throws DataGenerationException {
        DataGenerator dataGenerator = new DataGenerator();
        dataGenerator.generateData();

        Dialog dialog = new Dialog();
        dialog.start();
    }
}
