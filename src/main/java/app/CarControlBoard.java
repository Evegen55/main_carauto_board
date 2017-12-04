/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2017 - 2017 Evgenii Lartcev (https://github.com/Evegen55/) and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Evgenii Lartcev
 * @created on 10/20/2017.
 *
 * Here is the main application *
 */

package app;

import controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CarControlBoard extends Application {

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
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
    }

    @Override
    public void stop() throws Exception {
        EXECUTOR_SERVICE.shutdownNow();
        System.exit(0);
    }

}
