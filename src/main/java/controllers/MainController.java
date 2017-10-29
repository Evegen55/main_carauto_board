package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author (created on 10/23/2017).
 */
public class MainController {

    private final Stage primaryStage;

    //elements with map
    @FXML
    private AnchorPane myPaneWithMapsAndOtherFeatures;
    @FXML
    private Tab tab_with_map;
    @FXML
    private AnchorPane pane_with_map;

    //elements with controls
    @FXML
    private AnchorPane myPaneWithControls;
    @FXML
    private Slider gen_temperature_slider;

    //elements with music
    @FXML
    private Tab tab_with_music;
    @FXML
    private Pane pane_with_music;
    @FXML
    private Button btn_choose_music;
    @FXML
    private Label lbl_with_music;
    @FXML
    private Button btn_stop_music;
    @FXML
    private Button btn_play_music;
    @FXML
    private Button btn_pick_folder;

    public MainController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showMap() {
        final GmapfxController gmapfxController = new GmapfxController();
        gmapfxController.createSimpleMap(pane_with_map);
    }

    public void initialiseControls() {
        final ClimateControlController climateControlController = new ClimateControlController(gen_temperature_slider);
        climateControlController.createInitForControls(myPaneWithControls);
    }

    public void initializeMusic() {
        AudioController audioController = new AudioController();
        audioController
                .setInitialState(primaryStage, btn_choose_music, btn_stop_music, btn_play_music, btn_pick_folder,
                        pane_with_music, lbl_with_music);
    }
}
