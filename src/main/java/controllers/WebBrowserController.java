package controllers;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author (created on 10/23/2017).
 */
public class WebBrowserController {

    private final static Logger LOGGER = LoggerFactory.getLogger(WebBrowserController.class);
    private static final String PREDEFINED_URL_CNN = "http://edition.cnn.com/";

    /**
     * @param pane
     * @param backButton
     * @param forwardButton
     */
    public static void createBrowser(final Pane pane, final Button backButton, Button forwardButton) {
        LOGGER.info("Start initialization for a web page");
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        webEngine.load(PREDEFINED_URL_CNN);
        browser.setMaxWidth(pane.getWidth());
        browser.setMinHeight(pane.getHeight());
        pane.getChildren().add(browser);

        initControlsFor(webEngine, backButton, forwardButton);
        LOGGER.info("Initialization for a web page is done");
    }

    private static void initControlsFor(final WebEngine webEngine, final Button backButton, final Button forwardButton) {
        backButton.setOnAction(e -> {
            if (webEngine.getHistory().getCurrentIndex() > 0) {
                webEngine.getHistory().go(-1);
            }
        });
        forwardButton.setOnAction(e -> {
            if (webEngine.getHistory().getCurrentIndex() > 0) {
                webEngine.getHistory().go(1);
            }
        });
    }
}
