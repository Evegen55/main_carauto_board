package controllers;

import entities.AudioItem;
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
    private Button btn_clear_directions;

    //elements with controls
    @FXML
    private AnchorPane myPaneWithControls;
    @FXML
    private Slider gen_temperature_slider;
    @FXML
    private Label lbl_with_temperature;

    //elements with music
    @FXML
    private Tab tab_with_music;
    @FXML
    private Pane pane_with_music;
    @FXML
    private Button btn_choose_music;
//    @FXML
//    private Label lbl_with_music;
//    @FXML
//    private Button btn_stop_music;
//    @FXML
//    private Button btn_play_music;
    private AudioItem audioItem = new AudioItem();
    @FXML
    private Button btn_pick_folder;

    //elements with web
    @FXML
    private Pane pane_with_web;
    @FXML
    private Tab tab_with_web;

    public MainController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showMap() {
        final GmapfxController gmapfxController = new GmapfxController();
        gmapfxController.createSimpleMap(tab_with_map);
        gmapfxController.initButtons(btn_clear_directions);
    }

    public void initialiseControls() {
        final ClimateControlController climateControlController = new ClimateControlController(gen_temperature_slider);
        climateControlController.createInitForControls(myPaneWithControls, lbl_with_temperature);
    }

    public void initializeMusic() {
        AudioController audioController = new AudioController(primaryStage);
        // TODO: 10/30/2017 move it into audio controller to create a list of items when a folder is uploaded
        pane_with_music.getChildren().add(audioItem);
        audioController
                .setInitialState(btn_choose_music, audioItem.getStop(), audioItem.getPlay(), btn_pick_folder,
                        pane_with_music, audioItem.getLabel_for_name());
    }

    public void initWebView() {
        tab_with_web.setOnSelectionChanged(action -> {
            WebBrowserController webBrowserController = new WebBrowserController();
            webBrowserController.createSimpleBrowse(pane_with_web);
        });


    }
}
