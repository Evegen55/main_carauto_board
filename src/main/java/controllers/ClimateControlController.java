package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;

/**
 * @author (created on 10/23/2017).
 */
public class ClimateControlController {

    private Slider slider;

    public ClimateControlController(Slider slider) {
        this.slider = slider;
    }

    public void createInitForControls(final AnchorPane myPaneWithControls) {
        System.out.println(slider.getOrientation());

        // Adding Listener to value property.
        slider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, //
                                Number oldValue, Number newValue) {
                System.out.println("New value: " + newValue);
            }
        });

    }
}
