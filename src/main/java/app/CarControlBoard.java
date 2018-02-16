/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * <p>
 * Copyright (C) 2017 - 2017 Evgenii Lartcev (https://github.com/Evegen55/) and contributors
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Evgenii Lartcev
 * @created on 10/20/2017.
 * <p>
 * Here is the main application *
 */

package app;

import controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class CarControlBoard extends Application {

    private final static Logger LOGGER = LoggerFactory.getLogger(CarControlBoard.class);

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private boolean openCV_loaded = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        LOGGER.info("Start loading application ...\n");
        primaryStage.setTitle("Car control panel");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/base_form.fxml"));
        // set the main controller as root controller
        final MainController mainController = new MainController(primaryStage);
        loader.setController(mainController);
        final Parent parent = loader.load();
        final Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.show();

        //do the stuff
        mainController.initMap();
        mainController.initControls();
        mainController.initMusic();
        mainController.initWebView();
        mainController.initVideo();
        mainController.initWebcams();
        mainController.initApplicationSettings();
        mainController.initPhotoTab();
        mainController.initPhoneTab();

        try {
            //conflict with mainController.initWebcams() just because it uses it own version of an opencv lib?
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            openCV_loaded = true;
            LOGGER.info("Application loaded OpenCV library successfully");
        } catch (Throwable e) {
            LOGGER.warn("Application can't load OpenCV library");
            showPopUpWarnWindow();
        }

        if (openCV_loaded) {
            mainController.initOpenCVTab();
        }

        LOGGER.info("Main application window has been loaded successfully\n");

    }

    private void showPopUpWarnWindow() {
        LOGGER.info("Show warning dialog");
        final Alert alert = new Alert(Alert.AlertType.WARNING, "Application can't load OpenCV library. " +
                "Try install OpenCV and start application as shown in README.md. " +
                "If you press \"NO\" button the app will be stopped. Otherwise, it starts without OpenCV " +
                "but with loss appropriate functionality. " +
                "Do you want to proceed with NO OpenCV?",
                ButtonType.YES, ButtonType.NO);

        final Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES) {
            LOGGER.info("User chose YES");
        } else {
            LOGGER.info("User chose to stop application. Good, good user.");
            stop();
        }
    }

    @Override
    public void stop() {
        EXECUTOR_SERVICE.shutdownNow();
        // TODO: 1/12/2018 release all resources
        System.exit(0);
    }

}
