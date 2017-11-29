package controllers.settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * @author (created on 11/28/2017).
 */
public class ApplicationSettingsController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ApplicationSettingsController.class);

    public static void initSettings(final ComboBox<String> listStyles) {
        LOGGER.info("Application settings tab is initialising ...");

        //prior Java8 - simple and fast for small amount of data
        final ObservableList<String> stylesList = FXCollections.observableArrayList();
        final StyleList[] values = StyleList.values();
        for (int i = 0; i < values.length; i++) {
            stylesList.add(values[i].toString());
        }

        //Java8 - hard to write but can be fast for big amount of data
        final ObservableList<String> styleLists = Arrays.stream(StyleList.values())
                .map(Enum::toString)
                .collect(collectingAndThen(toList(), FXCollections::observableArrayList));

        listStyles.setItems(stylesList);
        // TODO: 11/29/2017 add an action to write preferred style to a text file

        LOGGER.info("Application settings tab initialised");
    }

    public static String styleForMap() {
        final StyleHelper styleHelper = new StyleHelper();
        final String styleString = styleHelper.getStyleForMap(StyleList.RETRO);
        // TODO: 11/29/2017 add an action to read style theme from a text file
        return styleString;
    }
}
