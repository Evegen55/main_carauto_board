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
 * @created on 12/12/2017
 */

package controllers.openvc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.UtilsOpenCV;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ImageRecognizer {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImageRecognizer.class);

    private final Stage primaryStage;

    // a timer for acquiring the video stream
    private ScheduledExecutorService timer;
    // the OpenCV object that realizes the video capture
    private static final VideoCapture VIDEO_CAPTURE = new VideoCapture();
    private final Button btnOpenCVStartCamera;
    // a flag to change the button behavior
    private boolean isCameraActive = false;
    // the id of the camera to be used
    private static int cameraId = 0;

    //recognition
    private final CheckBox checkBoxGrayscale;
    private ComboBox<RecognizingTypeOfDetection> comboBoxForTypeOfDetection;
    private ComboBox<RecognizingTypeOfClassifier> comboBoxForTypeOfClassifier;

    //cascade classifier
    private static final CascadeClassifier CASCADE_CLASSIFIER = new CascadeClassifier();
    private int absoluteAreaSize = 0;

    //just for fun
    private int faceCounter = 0;

    //edge detection and hide unappropriated functionality
    private final HBox hboxHidden1;
    private final HBox hboxHidden2;
    private final HBox hboxHidden3;
    private final CheckBox canny;
    private final Slider threshold;
    private final CheckBox dilateErode;
    private final CheckBox inverse;
    private final Button btnActivateCamera;

    public ImageRecognizer(final Stage primaryStage, final Button btnOpenCVStartCamera, final CheckBox grayscale,
                           final ComboBox<RecognizingTypeOfDetection> comboBoxForTypeOfDetection,
                           final ComboBox<RecognizingTypeOfClassifier> comboBoxForTypeOfClassifier,
                           final HBox hboxHidden1, final HBox hboxHidden2, final HBox hboxHidden3, final CheckBox canny,
                           final Slider threshold, final CheckBox dilateErode, final CheckBox inverse, Button btnActivateCamera) {
        this.primaryStage = primaryStage;
        this.btnOpenCVStartCamera = btnOpenCVStartCamera;
        this.comboBoxForTypeOfDetection = comboBoxForTypeOfDetection;
        this.comboBoxForTypeOfClassifier = comboBoxForTypeOfClassifier;
        this.checkBoxGrayscale = grayscale;

        //edge detection and hide unappropriated functionality
        this.hboxHidden1 = hboxHidden1;
        this.hboxHidden2 = hboxHidden2;
        this.hboxHidden3 = hboxHidden3;
        this.canny = canny;
        this.threshold = threshold;
        this.dilateErode = dilateErode;
        this.inverse = inverse;
        this.btnActivateCamera = btnActivateCamera;
    }

    public ImageRecognizer init() {
        primaryStage.setOnCloseRequest((windowEvent -> setClosed()));
        LOGGER.info("Application settings tab is initialising ...");
        populateComboBoxWithTypeOfDetection();
        populateComboBoxWithTypeofclassifiers();
        btnOpenCVStartCamera.setDisable(true);
        hboxHidden1.setDisable(true);
        hboxHidden2.setDisable(true);
        hboxHidden3.setDisable(true);
        LOGGER.info("Application settings tab initialised");
        return this;
    }

    //first action from combo box, second - from button
    public void showSimpleCameraInto(final ImageView imageViewForOpenCV) {
        imageViewForOpenCV.setPreserveRatio(true);

        comboBoxForTypeOfDetection.setOnAction(event -> {
            final RecognizingTypeOfDetection typeOfDetection = comboBoxForTypeOfDetection.getValue();
            if (typeOfDetection != null) {
                LOGGER.info("Type of detection: " + typeOfDetection);
                switch (typeOfDetection) {
                    case face: hboxHidden1.setDisable(false); hboxHidden2.setDisable(true); hboxHidden3.setDisable(true); break;
                    case plates_rus: hboxHidden1.setDisable(false); hboxHidden2.setDisable(true); hboxHidden3.setDisable(true); break;
                    case edges: hboxHidden1.setDisable(true); hboxHidden2.setDisable(false); hboxHidden3.setDisable(false); break;
                }
            }
        });

        btnActivateCamera.setOnAction(event -> {
            final RecognizingTypeOfDetection typeOfDetection = comboBoxForTypeOfDetection.getValue();
            if (typeOfDetection != null) {
                switch (typeOfDetection) {
                    case face: doWithClassifiers(typeOfDetection, imageViewForOpenCV); break;
                    case plates_rus: doWithClassifiers(typeOfDetection, imageViewForOpenCV); break;
                    case edges: doForEdges(typeOfDetection, imageViewForOpenCV); break;
                }
            }
        });

    }

    private void doForEdges(final RecognizingTypeOfDetection typeOfDetection, ImageView imageViewForOpenCV) {
        // TODO: 12/15/2017  
    }

    private void doWithClassifiers(final RecognizingTypeOfDetection typeOfDetection, ImageView imageViewForOpenCV) {
        final RecognizingTypeOfClassifier typeOfClassifierValue = comboBoxForTypeOfClassifier.getValue();
        if (typeOfClassifierValue != null) {
            final boolean success = retrieveSettingsAndLoadClassifier(typeOfDetection, typeOfClassifierValue);
            if (success) {
                btnOpenCVStartCamera.setDisable(false);
                btnOpenCVStartCamera.setOnAction(event1 -> {
                    LOGGER.info("Start showing a stream captured from camera to a " + imageViewForOpenCV.getId());
                    startCamera(imageViewForOpenCV);
                });
            } else {
                LOGGER.warn("Settings weren't setup");
            }
        }
    }

    private void populateComboBoxWithTypeofclassifiers() {
        final ObservableList<RecognizingTypeOfClassifier> detections = FXCollections.observableArrayList();
        final RecognizingTypeOfClassifier[] values = RecognizingTypeOfClassifier.values();
        detections.addAll(Arrays.asList(values));
        comboBoxForTypeOfClassifier.setItems(detections);
    }

    private void populateComboBoxWithTypeOfDetection() {
        final ObservableList<RecognizingTypeOfDetection> detections = FXCollections.observableArrayList();
        final RecognizingTypeOfDetection[] values = RecognizingTypeOfDetection.values();
        detections.addAll(Arrays.asList(values));
        comboBoxForTypeOfDetection.setItems(detections);
    }

    private boolean retrieveSettingsAndLoadClassifier(final RecognizingTypeOfDetection typeOfDetectionValue,
                                                      final RecognizingTypeOfClassifier typeOfClassifierValue) {
        boolean result = false;
        //two cases when the video capture can start:
        if (typeOfDetectionValue.equals(RecognizingTypeOfDetection.face)) {
            if (typeOfClassifierValue != null) {
                if (typeOfClassifierValue.equals(RecognizingTypeOfClassifier.haar)) {
                    loadClassifier("trainedNN/opencv/haarcascades/haarcascade_frontalface_alt.xml");
                    result = true;
                }
                if (typeOfClassifierValue.equals(RecognizingTypeOfClassifier.lbp)) {
                    loadClassifier("trainedNN/opencv/lbpcascades/lbpcascade_frontalface.xml");
                    result = true;
                }
            }

        }
        if (typeOfDetectionValue.equals(RecognizingTypeOfDetection.plates_rus)) {
            if (typeOfClassifierValue != null) {
                if (typeOfClassifierValue.equals(RecognizingTypeOfClassifier.haar)) {
                    loadClassifier("trainedNN/opencv/haarcascades/haarcascade_russian_plate_number.xml");
                    result = true;
                }
                if (typeOfClassifierValue.equals(RecognizingTypeOfClassifier.lbp)) {
//                loadClassifier("trainedNN/opencv/lbpcascades/lbpcascade_frontalface.xml");
                    LOGGER.warn("Nothing to load!!");
                    result = true;
                }
            }
        }
        return result;
    }

    private void loadClassifier(final String classifierPath) {
        // load the classifier(s)
        boolean load = CASCADE_CLASSIFIER.load(classifierPath);
        if (load) {
            LOGGER.info("Classifier loaded");
        } else {
            LOGGER.warn("Classifier wasn't loaded");
        }
    }

    private void startCamera(final ImageView imageViewForOpenCV) {
        if (!isCameraActive) {
            // start the video capture
            VIDEO_CAPTURE.open(cameraId);

            // is the video stream available?
            if (VIDEO_CAPTURE.isOpened()) {
                isCameraActive = true;

                // grab a frame every 33 ms (30 frames/sec)
                Runnable frameGrabber = () -> {
                    // effectively grab and process a single frame
                    final Mat frame = grabFrame();
                    // convert and show the frame
                    final Image imageToShow = UtilsOpenCV.mat2Image(frame);
                    updateImageView(imageViewForOpenCV, imageToShow);
                };

                timer = Executors.newSingleThreadScheduledExecutor();
                timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

                // update the button content
                btnOpenCVStartCamera.setText("Stop Camera");
            } else {
                // log the error
                LOGGER.error("Impossible to open the camera connection...");
            }
        } else {
            // the camera is not active at this point
            isCameraActive = false;
            // update again the button content
            btnOpenCVStartCamera.setText("Start Camera");

            // stop the timer
            stopAcquisition();

            //print number of faces
            LOGGER.info("The maximum number of detected faces is: " + faceCounter);
        }
    }

    /**
     * Get a frame from the opened video stream (if any)
     *
     * @return the {@link Mat} to show
     */
    private Mat grabFrame() {
        // init everything
        final Mat frame = new Mat();

        // check if the capture is open
        if (VIDEO_CAPTURE.isOpened()) {
            try {
                // read the current frame
                VIDEO_CAPTURE.read(frame);

                // if the frame is not empty, process it
                if (!frame.empty()) {
                    if (checkBoxGrayscale != null && checkBoxGrayscale.isSelected()) {
                        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
                        detectAndDisplay(frame, true);
                    } else {
                        detectAndDisplay(frame, false);
                    }
                }
            } catch (Exception e) {
                // log the error
                LOGGER.error("Exception during the image elaboration: " + e);
            }
        }

        return frame;
    }

    // TODO: 12/12/2017 recognise faces with gray scale stream
    /*
    Detect faces at each frame
     */
    private void detectAndDisplay(final Mat frame, boolean grayIsAlreadySelected) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        if (grayIsAlreadySelected) {
            LOGGER.warn("TODO IT :-)");
        } else {
            // convert the frame in gray scale to ANOTHER frame
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        }

        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (20% of the frame height, in our case)
        if (absoluteAreaSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0) {
                absoluteAreaSize = Math.round(height * 0.2f);
            }
        }

        // detect faces
        /*
        The detectMultiScale function detects objects of different sizes in the input image.
        The detected objects are returned as a list of rectangles. The parameters are:
            image Matrix of the type CV_8U containing an image where objects are detected.
            objects Vector of rectangles where each rectangle contains the detected object.
            scaleFactor Parameter specifying how much the image size is reduced at each image scale.
            minNeighbors Parameter specifying how many neighbors each candidate rectangle should have to retain it.
            flags Parameter with the same meaning for an old cascade as in the function cvHaarDetectObjects. It is not used for a new cascade.
            minSize Minimum possible object size. Objects smaller than that are ignored.
            maxSize Maximum possible object size. Objects larger than that are ignored.
        So the result of the detection is going to be in the objects parameter or in our case faces.
         */
        CASCADE_CLASSIFIER.detectMultiScale(grayFrame, faces, 1.1, 2,
                0 | Objdetect.CASCADE_SCALE_IMAGE, new Size(absoluteAreaSize, absoluteAreaSize), new Size());

        /*
        each rectangle in faces is a face: draw them!
        Let’s put this result in an array of rects and draw them on the frame, by doing so we can display the detected face are.
        As you can see we selected the color green with a transparent background: Scalar(0, 255, 0, 255).
        .tl() and .br() stand for top-left and bottom-right and they represents the two opposite vertexes.
        The last parameter just set the thickness of the rectangle’s border.
         */
        final Rect[] facesArray = faces.toArray();
        countFaces(facesArray.length);
        for (Rect aFacesArray : facesArray)
            Imgproc.rectangle(frame, aFacesArray.tl(), aFacesArray.br(), new Scalar(0, 255, 0, 255), 3);
    }

    private void countFaces(final int length) {
        if (length > faceCounter) {
            faceCounter = length;
        }
    }

    /**
     * Stop the acquisition from the camera and release all the resources
     */
    private void stopAcquisition() {
        LOGGER.info("Stopping the acquisition from the camera and release all the resources ...");
        if (timer != null && !timer.isShutdown()) {
            try {
                // stop the timer
                timer.shutdown();
                timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // log any exception
                LOGGER.error("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }

        if (VIDEO_CAPTURE.isOpened()) {
            // release the camera
            VIDEO_CAPTURE.release();
        }

        LOGGER.info("all the resources for camera released.");
    }

    /**
     * Update the {@link ImageView} in the JavaFX main thread
     *
     * @param view  the {@link ImageView} to update
     * @param image the {@link Image} to show
     */
    private void updateImageView(final ImageView view, final Image image) {
        UtilsOpenCV.onFXThread(view.imageProperty(), image);
    }

    /**
     * On application close, stop the acquisition from the camera
     */
    private void setClosed() {
        stopAcquisition();
    }
}
