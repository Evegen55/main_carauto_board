package controllers;

import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author (created on 11/21/2017).
 */
public class FacebookController {

    private final static Logger LOGGER = LoggerFactory.getLogger(FacebookController.class);

    private Tab tab;

    public FacebookController(final Tab tab_with_facebook) {
        tab = tab_with_facebook;
    }

    public void initLoginFlow() {
        LOGGER.info("Start initialization for a login page for facebook");
    }
}
