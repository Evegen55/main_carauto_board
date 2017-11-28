package controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author (created on 11/28/2017).
 */
public class ApplicationSettingsController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ApplicationSettingsController.class);

    public static void initSettings() {
        LOGGER.info("Application settings tab is initialising ...");
    }
}
