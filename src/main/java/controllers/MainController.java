package controllers;

import controllers.settings.ApplicationSettingsController;
import entities.WebCamInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
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
    private TabPane tabPaneWithMap;
    @FXML
    private Tab tab_with_map;
    @FXML
    private Pane paneWithMap;
    @FXML
    private FlowPane flowpaneWithMapButtons;
    @FXML
    private Button btn_clear_directions;
    @FXML
    private Button btn_show_directions;
    @FXML
    private Button btn_find_path;
    @FXML
    private Button btn_clear_path;
    @FXML
    private Pane path_choice_pane;
    @FXML
    private Button btn_get_coords_find_path;
    @FXML
    private TextField txt_from;
    @FXML
    private TextField txt_to;

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
//    @FXML
//    private Tab tab_with_web;
//    @FXML
//    private AnchorPane anchor_pane_web;
    @FXML
    private Pane pane_with_web;
    @FXML
    private Button backButton;
    @FXML
    private Button forwardButton;

    //video
    @FXML
    private Pane top_pane_for_video;
    @FXML
    private Button btn_choose_video;
    @FXML
    private Button btn_play_video;
    @FXML
    private Button btn_stop_video;
    @FXML
    private Button btn_pause_video;
    @FXML
    private Slider time_slider;
    @FXML
    private Label time_lbl;
    @FXML
    private Slider volume_audio_slider;


    //webcam control 0
    @FXML
    private Button btnStartCamera;
    @FXML
    private Button btnStopCamera;
    @FXML
    private Button btnDisposeCamera;
    @FXML
    private ComboBox<WebCamInfo> cbCameraOptions;
    @FXML
    private Pane bpWebCamPaneHolder;
    @FXML
    private FlowPane fpBottomPane;
    @FXML
    private ImageView imgWebCamCapturedImage;

    //webcam control 1
    @FXML
    private Button btnStartCamera1;
    @FXML
    private Button btnStopCamera1;
    @FXML
    private Button btnDisposeCamera1;
    @FXML
    private ComboBox<WebCamInfo> cbCameraOptions1;
    @FXML
    private Pane bpWebCamPaneHolder1;
    @FXML
    private FlowPane fpBottomPane1;
    @FXML
    private ImageView imgWebCamCapturedImage1;

    //settings
    @FXML
    private ComboBox<String> listStyles;

    public MainController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void initMap() {
        final GmapfxController gmapfxController = new GmapfxController();
        gmapfxController.createSimpleMap(tab_with_map, flowpaneWithMapButtons,
                btn_clear_directions, btn_show_directions, btn_find_path, btn_clear_path, path_choice_pane,
                btn_get_coords_find_path, txt_from, txt_to);
    }

    public void initControls() {
        final ClimateControlController climateControlController = new ClimateControlController(gen_temperature_slider);
        climateControlController.createInitForControls(myPaneWithControls, lbl_with_temperature);
    }

    public void initMusic() {
        final AudioController audioController = new AudioController(primaryStage);
        audioController.setInitState(btn_pick_folder, btn_choose_music, pane_with_music);
    }

    public void initWebView() {
//        WebBrowserController webBrowserController = new WebBrowserController();
        WebBrowserController.createBrowser(pane_with_web, backButton, forwardButton);
    }

    public void initVideo() {
        final VideoController videoController = new VideoController(primaryStage);
        videoController.seiInitState(top_pane_for_video, btn_choose_video, btn_play_video, btn_stop_video,
                btn_pause_video, time_slider, volume_audio_slider, time_lbl);
    }

    public void initWebcams() {
        WebCamPreviewController webCamPreviewController =
                new WebCamPreviewController(btnStartCamera, btnStopCamera, btnDisposeCamera,
                        cbCameraOptions, bpWebCamPaneHolder, fpBottomPane, imgWebCamCapturedImage);
        webCamPreviewController.initialize(null, null); //just because fxml loader is already used

        WebCamPreviewController webCamPreviewController1 =
                new WebCamPreviewController(btnStartCamera1, btnStopCamera1, btnDisposeCamera1,
                        cbCameraOptions1, bpWebCamPaneHolder1, fpBottomPane1, imgWebCamCapturedImage1);
        webCamPreviewController1.initialize(null, null); //just because fxml loader is already used
    }

    public void initApplicationSettings() {
        ApplicationSettingsController.initSettings(listStyles);
    }
}
