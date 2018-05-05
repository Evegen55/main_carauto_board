package controllers;

import com.github.sarxos.webcam.Webcam;
import entities.WebCamInfo;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

import static app.CarControlBoard.EXECUTOR_SERVICE;


/**
 * This is controller for WebCamPreview FXML.
 *
 * @author Rakesh Bhatt (rakeshbhatt10)
 * @author <a href="mailto:i.dolende@gmail.com">Evgenii_Lartcev</a>
 */
public class WebCamPreviewController implements Initializable {

    private final static Logger LOGGER = LoggerFactory.getLogger(WebCamPreviewController.class);

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
        final ObservableList<WebCamInfo> options = FXCollections.observableArrayList();
        int webCamCounter = 0;
        for (Webcam webcam : Webcam.getWebcams()) {
            final WebCamInfo webCamInfo = new WebCamInfo(webCamCounter, webcam.getName());
            options.add(webCamInfo);
            webCamCounter++;
        }
        cbCameraOptions.setItems(options);
        cbCameraOptions.setPromptText(cameraListPromptText);
        cbCameraOptions.getSelectionModel().selectedItemProperty().addListener((arg01, arg11, arg2) -> {
            if (arg2 != null) {
                LOGGER.info("WebCam Index: " + arg2.webCamIndex + ": WebCam Name:" + arg2.webCamName);
                initializeWebCam(arg2.webCamIndex);
            }
        });
        Platform.runLater(this::setImageViewSize);

        btnStartCamera.setOnAction(this::startCamera);
        btnStopCamera.setOnAction(this::stopCamera);
        btnDisposeCamera.setOnAction(this::disposeCamera);

    }

    private void setImageViewSize() {
        imgWebCamCapturedImage.setPreserveRatio(true);
    }

    private void initializeWebCam(final int webCamIndex) {

        EXECUTOR_SERVICE.submit(() -> {
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
        });

        fpBottomPane.setDisable(false);
        btnStartCamera.setDisable(true);
    }

    private void startWebCamStream() {

        stopCamera = false;
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                while (!stopCamera) {
                    try {
                        if ((grabbedImage = selWebCam.getImage()) != null) {

                            Platform.runLater(() -> {
                                final Image mainiamge = SwingFXUtils.toFXImage(grabbedImage, null);
                                imageProperty.set(mainiamge);
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
        EXECUTOR_SERVICE.submit(th);
//        th.start();
        imgWebCamCapturedImage.imageProperty().bind(imageProperty);

    }

    private void closeCamera() {
        if (selWebCam != null) {
            selWebCam.close();
        }
    }

    private void stopCamera(ActionEvent event) {
        stopCamera = true;
        btnStartCamera.setDisable(false);
        btnStopCamera.setDisable(true);
    }

    private void startCamera(ActionEvent event) {
        stopCamera = false;
        startWebCamStream();
        btnStartCamera.setDisable(true);
        btnStopCamera.setDisable(false);
    }

    private void disposeCamera(ActionEvent event) {
        stopCamera = true;
        closeCamera();
        btnStopCamera.setDisable(true);
        btnStartCamera.setDisable(true);
    }
}
