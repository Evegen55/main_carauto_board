/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * <p>
 * Copyright (C) 2017 - 2017 Evgenii Lartcev (https://github.com/Evegen55/) and contributors
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Evgenii Lartcev
 * @created on 11/28/2017
 */

package controllers.settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public final class ApplicationSettingsController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ApplicationSettingsController.class);

    /**
     * It does big job to create right name for style file, then retrieves data from file (.json) as string
     *
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

    public static void initSettings(final ComboBox<StyleList> listStylesBox, final ComboBox<LanguageList> listLanguages,
                                    TextField txtFieldPathToVideo, final Button btnApplySettings) {
        LOGGER.info("Application settings tab is initialising ...");
        populateComboBoxWithStyles(listStylesBox);
        populateComboBoxWithLanguages(listLanguages);

        // TODO: 11/29/2017 pop-up window or suggest to reload map (routes will be erased if reloaded)
        //it retrieves all settings from all fields and boxes and write them into EXTERNAL file only.
        btnApplySettings.setOnAction(action -> {

            final String styleValueFromComboBox = listStylesBox.getValue().toString(); // TODO: 12/29/2017 check NULL listStylesBox.getValue()
            final String languagesValueFromComboBox = listLanguages.getValue().toString(); // TODO: 12/29/2017 check NULL listLanguages.getValue()
            final String pathToVideoFolder = txtFieldPathToVideo.getText();

            if (styleValueFromComboBox != null) {
                PropertiesHelper.setProperty(PropertyList.STYLE, styleValueFromComboBox);
            }
            if (languagesValueFromComboBox != null) {
                PropertiesHelper.setProperty(PropertyList.LANGUAGE, languagesValueFromComboBox);
            }
            if (pathToVideoFolder != null) {
                PropertiesHelper.setProperty(PropertyList.VIDEO_FOLDER, pathToVideoFolder);
            }
        });

        LOGGER.info("Application settings tab initialised");
    }

    private static void populateComboBoxWithLanguages(ComboBox<LanguageList> listLanguages) {
        final ObservableList<LanguageList> langList = FXCollections.observableArrayList();
        final LanguageList[] valuesLang = LanguageList.values();
        for (int i = 0; i < valuesLang.length; i++) {
            langList.add(valuesLang[i]);
        }
        listLanguages.setItems(langList);
    }

    private static void populateComboBoxWithStyles(ComboBox<StyleList> listStylesBox) {
        //prior Java8 - simple and fast for small amount of data
        final ObservableList<StyleList> stylesList = FXCollections.observableArrayList();
        final StyleList[] values = StyleList.values();
        for (int i = 0; i < values.length; i++) {
            stylesList.add(values[i]);
        }

        //Java8 - hard to write but can be fast for big amount of data
        final ObservableList<String> styleLists = Arrays.stream(StyleList.values())
                .map(Enum::toString)
                .collect(collectingAndThen(toList(), FXCollections::observableArrayList));

        listStylesBox.setItems(stylesList);
    }


}
