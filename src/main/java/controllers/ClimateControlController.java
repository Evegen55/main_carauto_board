package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

    public void createInitForControls(final AnchorPane myPaneWithControls) {
        LOGGER.info(slider.getOrientation().toString());

        // Adding Listener to value property.
        slider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, //
                                Number oldValue, Number newValue) {
                LOGGER.info("New value: " + newValue);
            }
        });

    }
}
