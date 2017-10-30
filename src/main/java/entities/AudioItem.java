package entities;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;


/**
 * An object which is created dynamique
 *
 * @author (created on 10/30/2017).
 */
public class AudioItem extends AnchorPane {

    private final Button play = new Button();
    private final Button pause = new Button();
    private final Button stop = new Button();

    private final Slider slider = new Slider();
    private final Label label_for_name = new Label();
    private final Label label_for_time = new Label();

    public AudioItem() {
        super();
        stop.setLayoutX(14.0);
        stop.setLayoutY(49.0);
        stop.setMnemonicParsing(false);
        stop.setText("stop audio");

        play.setLayoutX(14.0);
        play.setLayoutY(15.0);
        play.setMnemonicParsing(false);
        play.setText("play audio");

        label_for_name.setLayoutX(95.0);
        label_for_name.setLayoutY(19.0);
        label_for_name.setPrefWidth(409.0);

        label_for_time.setLayoutX(657.0);
        label_for_time.setLayoutY(4.0);
        label_for_time.setPrefWidth(47.0);
        slider.setPrefHeight(72.0);

        slider.setLayoutX(95.0);
        slider.setLayoutY(55.0);
        slider.setPrefWidth(553.0);
        slider.setPrefHeight(14.0);

        this.getChildren().addAll(play, stop, label_for_name, label_for_time, slider);
    }

    public Button getPlay() {
        return play;
    }

    public Button getPause() {
        return pause;
    }

    public Button getStop() {
        return stop;
    }

    public Slider getSlider() {
        return slider;
    }

    public Label getLabel_for_name() {
        return label_for_name;
    }

    public Label getLabel_for_time() {
        return label_for_time;
    }

    /**
     * @return modifiable list of children.
     */
    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

}
