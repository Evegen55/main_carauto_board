package controllers;

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

    }
}
