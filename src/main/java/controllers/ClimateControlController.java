package controllers;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author (created on 10/23/2017).
 */
public class ClimateControlController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClimateControlController.class);

    private Slider slider;

    public ClimateControlController(Slider slider) {
        this.slider = slider;
    }

    public void createInitForControls(final AnchorPane myPaneWithControls, final Label lbl_with_temperature) {
        lbl_with_temperature.setText("Initial temperature: " + slider.getValue());
        // Adding Listener to value property.
        //
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // TODO: 10/30/2017 smth
            lbl_with_temperature.setText("New temperature: " + newValue);
        });

    }
}
