/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * <p>
 * Copyright (C) 2017 - 2017 Evgenii Lartcev (https://github.com/Evegen55/) and contributors
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright
 * notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Requires next JVM params in order to use full capabilities:
 * <p>
 * Win:
 * <p>
 * -Djava.library.path=d:\path\to-build-with\opencv\build\java\x64
 * <p>
 * or
 * <p>
 * Linux
 * <p>
 * -Djava.library.path=/usr/local/share/OpenCV/java
 * <p>
 * for Linux Ubuntu 16.04.04 there is a precompiled library
 * <p>
 * -Djava.library.path=local-maven-repo/libopencv_java341.so
 * <p>
 * JavaFX:
 * <p>
 * -Dprism.verbose=true
 */

public final class CarControlBoard extends Application {

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private final static Logger LOGGER = LoggerFactory.getLogger(CarControlBoard.class);

    private boolean openCV_loaded = false;

    private Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws IOException {
        LOGGER.info("Start loading application ...\n");
        window = primaryStage;
        window.setTitle("Car control panel");


        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/base_form.fxml"));
        // set the main controller as root controller
        final MainController mainController = new MainController(window);
        loader.setController(mainController);
        final Parent parent = loader.load();
        final Scene scene = new Scene(parent);

        window.setScene(scene); //here is the key to set new scene by loading it from another fxml configuration
        // so it also key to avoid using table with tabs instead of different windows
        window.show();

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
            LOGGER.info("Application loaded OpenCV {} library successfully", Core.VERSION);
            LOGGER.debug(Core.getBuildInformation());
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
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        final Optional<ButtonType> result = alert.showAndWait();
        if (ButtonType.YES == result.get()) {
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
