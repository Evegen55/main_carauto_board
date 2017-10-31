package entities;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;


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

    private static Image imagePlay = new Image("http://icons.iconarchive.com/icons/hopstarter/soft-scraps/48/Button-Play-icon.png");
    private static Image imageStop = new Image("http://icons.iconarchive.com/icons/icons-land/play-stop-pause/48/Stop-Normal-icon.png");
    private static final double RADIUS_IMAGE_VIEW_BUTTON = Math.sqrt(Math.pow(50.0 / 2, 2) * 2);

    public AudioItem() {
        super();
        super.setPrefHeight(80.0);
        super.setPrefWidth(661.0);

        final ImageView imageViewPlayButton = new ImageView(imagePlay);
        imageViewPlayButton.setFitHeight(50.0);
        imageViewPlayButton.setFitWidth(50.0);
        imageViewPlayButton.setPreserveRatio(true);
        imageViewPlayButton.setPickOnBounds(true);

        final ImageView imageViewStopButton = new ImageView(imageStop);
        imageViewStopButton.setFitHeight(50.0);
        imageViewStopButton.setFitWidth(50.0);
        imageViewStopButton.setPreserveRatio(true);
        imageViewStopButton.setPickOnBounds(true);

        play.setLayoutX(14.0);
        play.setLayoutY(8.0);
        play.setPrefHeight(64.0);
        play.setPrefWidth(64.0);
        play.setMnemonicParsing(false);
        play.setGraphic(imageViewPlayButton);
        play.setShape(new Circle(RADIUS_IMAGE_VIEW_BUTTON));

        label_for_name.setLayoutX(84.0);
        label_for_name.setLayoutY(14.0);
        label_for_name.setPrefWidth(493.0);

        slider.setLayoutX(78.0);
        slider.setLayoutY(33.0);
        slider.setPrefWidth(504.0);
        slider.setPrefHeight(14.0);

        label_for_time.setLayoutX(302.0);
        label_for_time.setLayoutY(47.0);
        label_for_time.setPrefHeight(25.0);
        label_for_time.setPrefWidth(57.0);

        stop.setLayoutX(584.0);
        stop.setLayoutY(8.0);
        stop.setPrefHeight(64.0);
        stop.setPrefWidth(64.0);
        stop.setMnemonicParsing(false);
        stop.setGraphic(imageViewStopButton);
        stop.setShape(new Circle(RADIUS_IMAGE_VIEW_BUTTON));

        this.getChildren().addAll(play, stop, label_for_name, label_for_time, slider);
    }

    public AudioItem(double layOutX, double layOutY) {
        this();
        super.setLayoutX(layOutX);
        super.setLayoutY(layOutY);
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
