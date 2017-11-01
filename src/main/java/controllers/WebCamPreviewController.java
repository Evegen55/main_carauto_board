package controllers;

import com.github.sarxos.webcam.Webcam;
import entities.WebCamInfo;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * This is controller for WebCamPreview FXML.
 *
 * @author Rakesh Bhatt (rakeshbhatt10)
 */
public class WebCamPreviewController implements Initializable {

    private Button btnStartCamera;
    private Button btnStopCamera;
    private Button btnDisposeCamera;
    private ComboBox<WebCamInfo> cbCameraOptions;
    private Pane bpWebCamPaneHolder;
    private FlowPane fpBottomPane;
    private ImageView imgWebCamCapturedImage;

    public WebCamPreviewController(final Button btnStartCamera, final Button btnStopCamera, final Button btnDisposeCamera,
                                   final ComboBox<WebCamInfo> cbCameraOptions, final Pane bpWebCamPaneHolder,
                                   final FlowPane fpBottomPane, final ImageView imgWebCamCapturedImage) {
        this.btnStartCamera = btnStartCamera;
        this.btnStopCamera = btnStopCamera;
        this.btnDisposeCamera = btnDisposeCamera;
        this.cbCameraOptions = cbCameraOptions;
        this.bpWebCamPaneHolder = bpWebCamPaneHolder;
        this.fpBottomPane = fpBottomPane;
        this.imgWebCamCapturedImage = imgWebCamCapturedImage;
    }

    private BufferedImage grabbedImage;
    private Webcam selWebCam = null;
    private boolean stopCamera = false;
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();

    private String cameraListPromptText = "Choose Camera";

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        fpBottomPane.setDisable(true);
        ObservableList<WebCamInfo> options = FXCollections.observableArrayList();
        int webCamCounter = 0;
        for (Webcam webcam : Webcam.getWebcams()) {
            WebCamInfo webCamInfo = new WebCamInfo();
            webCamInfo.setWebCamIndex(webCamCounter);
            webCamInfo.setWebCamName(webcam.getName());
            options.add(webCamInfo);
            webCamCounter++;
        }
        cbCameraOptions.setItems(options);
        cbCameraOptions.setPromptText(cameraListPromptText);
        cbCameraOptions.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<WebCamInfo>() {

            @Override
            public void changed(ObservableValue<? extends WebCamInfo> arg0, WebCamInfo arg1, WebCamInfo arg2) {
                if (arg2 != null) {
                    System.out.println("WebCam Index: " + arg2.getWebCamIndex() + ": WebCam Name:" + arg2.getWebCamName());
                    initializeWebCam(arg2.getWebCamIndex());
                }
            }
        });
        Platform.runLater(this::setImageViewSize);

        btnStartCamera.setOnAction(this::startCamera);
        btnStopCamera.setOnAction(this::stopCamera);
        btnDisposeCamera.setOnAction(this::disposeCamera);

    }

    protected void setImageViewSize() {
//        double height = bpWebCamPaneHolder.getHeight();
//        double width = bpWebCamPaneHolder.getWidth();
//        imgWebCamCapturedImage.setFitHeight(height);
//        imgWebCamCapturedImage.setFitWidth(width);
//        imgWebCamCapturedImage.prefHeight(height);
//        imgWebCamCapturedImage.prefWidth(width);
        imgWebCamCapturedImage.setPreserveRatio(true);

    }

    protected void initializeWebCam(final int webCamIndex) {

        Task<Void> webCamIntilizer = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                if (selWebCam == null) {
                    selWebCam = Webcam.getWebcams().get(webCamIndex);
                    selWebCam.open();
                } else {
                    closeCamera();
                    selWebCam = Webcam.getWebcams().get(webCamIndex);
                    selWebCam.open();
                }
                startWebCamStream();
                return null;
            }

        };

        new Thread(webCamIntilizer).start();
        fpBottomPane.setDisable(false);
        btnStartCamera.setDisable(true);
    }

    protected void startWebCamStream() {

        stopCamera = false;
        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                while (!stopCamera) {
                    try {
                        if ((grabbedImage = selWebCam.getImage()) != null) {

                            Platform.runLater(new Runnable() {

                                @Override
                                public void run() {
                                    final Image mainiamge = SwingFXUtils
                                            .toFXImage(grabbedImage, null);
                                    imageProperty.set(mainiamge);
                                }
                            });

                            grabbedImage.flush();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        imgWebCamCapturedImage.imageProperty().bind(imageProperty);

    }

    private void closeCamera() {
        if (selWebCam != null) {
            selWebCam.close();
        }
    }

    public void stopCamera(ActionEvent event) {
        stopCamera = true;
        btnStartCamera.setDisable(false);
        btnStopCamera.setDisable(true);
    }

    public void startCamera(ActionEvent event) {
        stopCamera = false;
        startWebCamStream();
        btnStartCamera.setDisable(true);
        btnStopCamera.setDisable(false);
    }

    public void disposeCamera(ActionEvent event) {
        stopCamera = true;
        closeCamera();
        btnStopCamera.setDisable(true);
        btnStartCamera.setDisable(true);
    }
}
