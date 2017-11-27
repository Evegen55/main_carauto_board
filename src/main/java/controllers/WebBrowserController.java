package controllers;

import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @author (created on 10/23/2017).
 */
public class WebBrowserController {

    private static final String PREDEFINED_URL_CNN = "http://edition.cnn.com/";

    public Pane createSimpleBrowse(final Pane pane) {
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        webEngine.load(PREDEFINED_URL_CNN);
        browser.setMaxWidth(pane.getWidth());
        browser.setMinHeight(pane.getHeight());
        pane.getChildren().add(browser);
        return pane;
    }
}
