package controllers.settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
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

    /**
     * It does big job to create right name for style file, then retrieves data from file (.json) as string
     * @return
     */
    public static String doBigMagicToRetrieveStyleForMap() {
        LOGGER.info("Retrieving style for map from settings file ...");
        final StyleHelper styleHelper = new StyleHelper();
        final StyleList styleForMapFromProperties = PropertiesHelper.getStyleForMapFromProperties();
        final String styleString = styleHelper.getStyleForMap(styleForMapFromProperties);
        LOGGER.info("Style for map retrieved from settings file");
        return styleString;
    }

    public static void initSettings(final ComboBox<String> listStylesBox, final ComboBox<String> listLanguages,
                                    final Button btnApplySettings) {
        LOGGER.info("Application settings tab is initialising ...");
        populateComboBoxWithStyles(listStylesBox);
        populateComboBoxWithLanguages(listLanguages);

        // TODO: 11/29/2017 pop-up window or suggest to reload map (routes will be erased if reloaded)
        //it retrieves all settings from all fields and boxes and write them into EXTERNAL file only.
        btnApplySettings.setOnAction(action -> {
            final String styleValueFromComboBox = listStylesBox.getValue();
            final String languagesValueFromComboBox = listLanguages.getValue();
            if (styleValueFromComboBox != null) {
                PropertiesHelper.setProperty(PropertyList.STYLE, styleValueFromComboBox);
            }
            if (languagesValueFromComboBox != null) {
                PropertiesHelper.setProperty(PropertyList.LANGUAGE, languagesValueFromComboBox);
            }
        });

        LOGGER.info("Application settings tab initialised");
    }

    private static void populateComboBoxWithLanguages(ComboBox<String> listLanguages) {
        final ObservableList<String> langList = FXCollections.observableArrayList();
        final LanguageList[] valuesLang = LanguageList.values();
        for (int i = 0; i < valuesLang.length; i++) {
            langList.add(valuesLang[i].toString());
        }
        listLanguages.setItems(langList);
    }

    private static void populateComboBoxWithStyles(ComboBox<String> listStylesBox) {
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

        listStylesBox.setItems(stylesList);
    }


}
