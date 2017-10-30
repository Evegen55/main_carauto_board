package controllers;

import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @author (created on 10/23/2017).
 */
public class WebBrowserController {

    public void createSimpleBrowse(final Pane pane) {
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        String url = "https://www.google.com/";
        webEngine.load(url);
        browser.setMaxWidth(pane.getWidth());
        browser.setMinHeight(pane.getHeight());
        pane.getChildren().add(browser);
    }
}
