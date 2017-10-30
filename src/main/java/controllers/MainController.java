package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private ScrollPane scroll_for_music;
    @FXML
    private Button btn_choose_music;
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
        final AudioController audioController = new AudioController(primaryStage);
        audioController.setInitState(btn_pick_folder, btn_choose_music, pane_with_music);
    }

    public void initWebView() {
        tab_with_web.setOnSelectionChanged(action -> {
            WebBrowserController webBrowserController = new WebBrowserController();
            webBrowserController.createSimpleBrowse(pane_with_web);
        });
    }
}
