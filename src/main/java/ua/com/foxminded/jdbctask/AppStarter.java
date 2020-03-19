package ua.com.foxminded.jdbctask;

import ua.com.foxminded.jdbctask.data.DataGenerator;
import ua.com.foxminded.jdbctask.menu.Dialog;

public class AppStarter {

    public static void main(String[] args) {
        DataGenerator dataGenerator = new DataGenerator();
        String dataGenerationResult = dataGenerator.generateData();

        Dialog dialog = new Dialog();
        dialog.start(dataGenerationResult);
    }
}
