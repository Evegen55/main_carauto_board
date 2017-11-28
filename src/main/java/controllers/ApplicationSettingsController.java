package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.StyleList;

/**
 * @author (created on 11/28/2017).
 */
public class ApplicationSettingsController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ApplicationSettingsController.class);

    public static void initSettings(final ComboBox<String> listStyles) {
        LOGGER.info("Application settings tab is initialising ...");

        final ObservableList<String> stylesList = FXCollections.observableArrayList();
        final StyleList[] values = StyleList.values();
        for (int i = 0; i < values.length; i++) {
            stylesList.add(values[i].toString());
        }
        listStyles.setItems(stylesList);


    }
}
