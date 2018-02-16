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

import controllers.settings.PropertiesHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Util;
import utils.UtilsOpenCV;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class MagicTabController {

    private final static Logger LOGGER = LoggerFactory.getLogger(MagicTabController.class);

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
    private static final CascadeClassifier CASCADE_CLASSIFIER_2 = new CascadeClassifier();
    private int absoluteAreaSize = 0;

    //just for fun
    private int faceCounter = 0;

    //edge detection and hide unappropriated functionality
    private final HBox hboxHidden1;
    private final VBox vboxHidden2;
    private final HBox hboxHidden3;
    private final CheckBox canny;
    private final Slider threshold;
    private final CheckBox dilateErode;
    private final CheckBox inverse;
    private final Button btnActivateCamera;
    private final Button btnOpenCVWriteVideo;
    private Point clickedPoint = new Point(0, 0);
    private Mat oldFrame;

    private VideoWriter writer;
    private final int FOURCC_MJPG = VideoWriter.fourcc('M', 'J', 'P', 'G');

    private static final ConcurrentLinkedQueue<Mat> MAT_CONCURRENT_LINKED_QUEUE = new ConcurrentLinkedQueue<>();

    private CheckBox chkBoxSobel;

    public MagicTabController(final Stage primaryStage, final Button btnOpenCVStartCamera, final CheckBox grayscale,
                              final ComboBox<RecognizingTypeOfDetection> comboBoxForTypeOfDetection,
                              final ComboBox<RecognizingTypeOfClassifier> comboBoxForTypeOfClassifier,
                              final HBox hboxHidden1, final VBox vboxHidden2, final HBox hboxHidden3, final CheckBox canny,
                              final Slider threshold, final CheckBox dilateErode, final CheckBox inverse,
                              final Button btnActivateCamera, final Button btnOpenCVWriteVideo, CheckBox chkBoxSobel) {
        this.primaryStage = primaryStage;
        this.btnOpenCVStartCamera = btnOpenCVStartCamera;
        this.comboBoxForTypeOfDetection = comboBoxForTypeOfDetection;
        this.comboBoxForTypeOfClassifier = comboBoxForTypeOfClassifier;
        this.checkBoxGrayscale = grayscale;

        //edge detection and hide unappropriated functionality
        this.hboxHidden1 = hboxHidden1;
        this.vboxHidden2 = vboxHidden2;
        this.hboxHidden3 = hboxHidden3;
        this.canny = canny;
        this.threshold = threshold;
        this.dilateErode = dilateErode;
        this.inverse = inverse;
        this.btnActivateCamera = btnActivateCamera;

        //write video with OpenCV
        this.btnOpenCVWriteVideo = btnOpenCVWriteVideo;

        this.chkBoxSobel = chkBoxSobel;
    }

    public MagicTabController init() {
        primaryStage.setOnCloseRequest((windowEvent -> setClosed()));
        LOGGER.info("Application settings tab is initialising ...");
        populateComboBoxWithTypeOfDetection();
        populateComboBoxWithTypeofclassifiers();
        btnOpenCVStartCamera.setDisable(true);
        btnOpenCVWriteVideo.setDisable(true);
        hboxHidden1.setDisable(true);
        vboxHidden2.setDisable(true);
        hboxHidden3.setDisable(true);
        threshold.setShowTickLabels(true);
        canny.setOnAction(event -> cannySelected());
        chkBoxSobel.setOnAction(event -> sobelSelected());
        dilateErode.setOnAction(event -> dilateErodeSelected());
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
                    case face:
                    case pedestrian:
                    case plates_rus:
                        hboxHidden1.setDisable(false);
                        vboxHidden2.setDisable(true);
                        hboxHidden3.setDisable(true);
                        btnOpenCVWriteVideo.setDisable(true);
                        break;
                    case edges:
                        hboxHidden1.setDisable(true);
                        vboxHidden2.setDisable(false);
                        hboxHidden3.setDisable(false);
                        btnOpenCVWriteVideo.setDisable(true);
                        break;
                    case write_video:
                        hboxHidden1.setDisable(true);
                        vboxHidden2.setDisable(true);
                        hboxHidden3.setDisable(true);
                        btnOpenCVStartCamera.setDisable(true);
                        break;
                }
            }
        });

        btnActivateCamera.setOnAction(event -> {
            final RecognizingTypeOfDetection typeOfDetection = comboBoxForTypeOfDetection.getValue();
            if (typeOfDetection != null) {
                switch (typeOfDetection) {
                    case face:
                    case plates_rus:
                        doWithClassifiers(typeOfDetection, imageViewForOpenCV);
                        break;
                    case edges:
                        doForEdges(imageViewForOpenCV);
                        break;
                    case write_video:
                        doWritingActions(imageViewForOpenCV);
                        break;
                    case pedestrian:
                        doWithClassifiers(typeOfDetection, imageViewForOpenCV);
                        break;
                }
            }
        });
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

    //=================================================== assign button's behavior =====================================
    /*
    It overrides behaviour for btnOpenCVStartCamera
    */
    private void doWithClassifiers(final RecognizingTypeOfDetection typeOfDetection, ImageView imageViewForOpenCV) {
        final RecognizingTypeOfClassifier typeOfClassifierValue = comboBoxForTypeOfClassifier.getValue();
        if (typeOfClassifierValue != null) {
            final boolean success = retrieveSettingsAndLoadClassifier(typeOfDetection, typeOfClassifierValue);
            if (success) {
                btnOpenCVWriteVideo.setDisable(true);
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

    /*
    It overrides behaviour for btnOpenCVStartCamera
     */
    private void doForEdges(ImageView imageViewForOpenCV) {
        btnOpenCVWriteVideo.setDisable(true);
        btnOpenCVStartCamera.setDisable(false);
        btnOpenCVStartCamera.setOnAction(event -> {
            LOGGER.info("Start detecting edges from a stream captured from camera to a " + imageViewForOpenCV.getId());
            startCameraEdges(imageViewForOpenCV);
        });
    }

    /*
    It overrides behaviour for btnOpenCVWriteVideo
    */
    private void doWritingActions(final ImageView imageViewForOpenCV) {
        final String videoFolderFromProperties = PropertiesHelper.getVideoFolderFromProperties();
        //we can write a video only if folder exists
        if (Util.checkFolderExistence(videoFolderFromProperties)) {
            btnOpenCVStartCamera.setDisable(true);
            //activate the button to write video
            btnOpenCVWriteVideo.setDisable(false);
            btnOpenCVWriteVideo.setOnAction(event -> startWriteAndShow(imageViewForOpenCV, videoFolderFromProperties));
        }

    }

    //=================================                                            =====================================
    //===============================  core logic can be moved to another controller  ==================================
    //=================================                                            =====================================

    private boolean retrieveSettingsAndLoadClassifier(final RecognizingTypeOfDetection typeOfDetectionValue,
                                                      final RecognizingTypeOfClassifier typeOfClassifierValue) {
        boolean result = false;
        //two cases when the video capture can start:
        if (typeOfDetectionValue.equals(RecognizingTypeOfDetection.face)) {
            if (typeOfClassifierValue != null) {
                if (typeOfClassifierValue.equals(RecognizingTypeOfClassifier.haar)) {
                    loadClassifier("trainedNN/opencv/haarcascades/haarcascade_frontalface_alt.xml", CASCADE_CLASSIFIER);
                    loadClassifier("trainedNN/opencv/haarcascades/haarcascade_profileface.xml", CASCADE_CLASSIFIER_2);
                    result = true;
                }
                if (typeOfClassifierValue.equals(RecognizingTypeOfClassifier.lbp)) {
                    loadClassifier("trainedNN/opencv/lbpcascades/lbpcascade_frontalface.xml", CASCADE_CLASSIFIER);
                    result = true;
                }
            }

        }
        if (typeOfDetectionValue.equals(RecognizingTypeOfDetection.plates_rus)) {
            if (typeOfClassifierValue != null) {
                if (typeOfClassifierValue.equals(RecognizingTypeOfClassifier.haar)) {
                    loadClassifier("trainedNN/opencv/haarcascades/haarcascade_russian_plate_number.xml", CASCADE_CLASSIFIER);
                    result = true;
                }
                if (typeOfClassifierValue.equals(RecognizingTypeOfClassifier.lbp)) {
//                loadClassifier("trainedNN/opencv/lbpcascades/lbpcascade_frontalface.xml");
                    LOGGER.warn("Nothing to load!!");
                    result = true;
                }
            }
        }
        if (typeOfDetectionValue.equals(RecognizingTypeOfDetection.pedestrian)) {
            if (typeOfClassifierValue != null) {
                if (typeOfClassifierValue.equals(RecognizingTypeOfClassifier.haar)) {
                    loadClassifier("trainedNN/opencv/haarcascades/haarcascade_fullbody.xml", CASCADE_CLASSIFIER);
                    loadClassifier("trainedNN/opencv/haarcascades/haarcascade_lowerbody.xml", CASCADE_CLASSIFIER_2);
                    result = true;
                }
            }
        }
        return result;
    }

    private void loadClassifier(final String classifierPath, final CascadeClassifier cascadeClassifier) {
        // load the classifier(s)
        boolean load = cascadeClassifier.load(classifierPath);
        if (load) {
            LOGGER.info("Classifier {} loaded", classifierPath);
        } else {
            LOGGER.warn("Classifier {} wasn't loaded", classifierPath);
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
                    final Mat processedFrameWithDetectedFeatures = grabFrame();
                    // convert and show the frame
                    final Image imageToShow = UtilsOpenCV.mat2Image(processedFrameWithDetectedFeatures);
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
            //LOGGER.warn("TODO IT :-)");
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
        if (!CASCADE_CLASSIFIER.empty()) {
            CASCADE_CLASSIFIER.detectMultiScale(grayFrame, faces, 1.1, 2,
                    0 | Objdetect.CASCADE_SCALE_IMAGE, new Size(absoluteAreaSize, absoluteAreaSize), new Size());
        }

        if (!CASCADE_CLASSIFIER_2.empty()) {
            CASCADE_CLASSIFIER_2.detectMultiScale(grayFrame, faces, 1.1, 2,
                    0 | Objdetect.CASCADE_SCALE_IMAGE, new Size(absoluteAreaSize, absoluteAreaSize), new Size());
        }

        // TODO: 2/1/2018 at here is possible to detect something else by using another pre-loaded classifier
        // TODO: 2/8/2018 is it rigth to user faces object for both classifiers? Now I don't know...
        // TODO: 2/1/2018 at here we can also get a frame with face only and then we can detect smile, eyes and so on with no mistakes
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

        if (writer != null) {
            writer.release();
        }

        LOGGER.info("all the resources for camera released.");
    }

    /**
     * Update the {@link ImageView} in the JavaFX main thread
     *  @param view  the {@link ImageView} to update
     * @param image the {@link Image} to show
     */
    private static void updateImageView(final ImageView view, final Image image) {
        UtilsOpenCV.onFXThread(view.imageProperty(), image);
    }

    /**
     * On application close, stop the acquisition from the camera
     */
    private void setClosed() {
        stopAcquisition();
    }


    //==================================================   edge detection  =============================================
    private void startCameraEdges(final ImageView imageViewForOpenCV) {
        // preserve image ratio
        imageViewForOpenCV.setPreserveRatio(true);

        // mouse listener
        imageViewForOpenCV.setOnMouseClicked(e -> {
            LOGGER.info("[" + e.getX() + ", " + e.getY() + "]");
            clickedPoint.x = e.getX();
            clickedPoint.y = e.getY();
        });

        if (!isCameraActive) {

            // start the video capture
            VIDEO_CAPTURE.open(0);

            // is the video stream available?
            if (VIDEO_CAPTURE.isOpened()) {
                isCameraActive = true;

                // grab a frame every 33 ms (30 frames/sec)
                Runnable frameGrabber = () -> {
                    // effectively grab and process a single frame
                    Mat frame = grabFrameEdges();
                    // convert and show the frame
                    Image imageToShow = UtilsOpenCV.mat2Image(frame);
                    updateImageView(imageViewForOpenCV, imageToShow);
                };

                timer = Executors.newSingleThreadScheduledExecutor();
                timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

                // update the button content
                btnOpenCVStartCamera.setText("Stop Camera");
            } else {
                // log the error
                LOGGER.error("Failed to open the camera connection...");
            }
        } else {
            // the camera is not active at this point
            isCameraActive = false;
            // update again the button content
            btnOpenCVStartCamera.setText("Start Camera");
            // enable setting checkboxes
            canny.setDisable(false);
            dilateErode.setDisable(false);

            // stop the timer
            this.stopAcquisition();
        }
    }

    /**
     * Get a frame from the opened video stream (if any)
     *
     * @return the {@link Image} to show
     */
    private Mat grabFrameEdges() {
        Mat frame = new Mat();

        // check if the capture is open
        if (VIDEO_CAPTURE.isOpened()) {
            try {
                // read the current frame
                VIDEO_CAPTURE.read(frame);

                // if the frame is not empty, process it
                if (!frame.empty()) {
                    // handle edge detection
                    if (canny.isSelected()) {
                        frame = doCanny(frame);
                    } else if (chkBoxSobel.isSelected()) {
                        frame = doSobel(frame);
                    }

                    // TODO: 1/25/2018 Add checkbox to swith on functions
                    // foreground detection
                    else if (dilateErode.isSelected()) {
                        // Es. 2.1
//                         frame = doBackgroundRemovalFloodFill(frame);
                        // Es. 2.2
                        frame = doBackgroundRemovalAbsDiff(frame);
                        // Es. 2.3
//                         frame = doBackgroundRemoval(frame);

                    }

                }

            } catch (Exception e) {
                // log the (full) error
                LOGGER.error("Exception in the image elaboration..." + e);
                e.printStackTrace();
            }
        }

        return frame;
    }

    private Mat doBackgroundRemovalAbsDiff(Mat currFrame) {
        Mat greyImage = new Mat();
        Mat foregroundImage = new Mat();

        if (oldFrame == null)
            oldFrame = currFrame;

        Core.absdiff(currFrame, oldFrame, foregroundImage);
        Imgproc.cvtColor(foregroundImage, greyImage, Imgproc.COLOR_BGR2GRAY);

        int thresh_type = Imgproc.THRESH_BINARY_INV;
        if (inverse.isSelected())
            thresh_type = Imgproc.THRESH_BINARY;

        Imgproc.threshold(greyImage, greyImage, 10, 255, thresh_type);
        currFrame.copyTo(foregroundImage, greyImage);

        oldFrame = currFrame;
        return foregroundImage;

    }

    private Mat doBackgroundRemovalFloodFill(Mat frame) {

        Scalar newVal = new Scalar(255, 255, 255);
        Scalar loDiff = new Scalar(50, 50, 50);
        Scalar upDiff = new Scalar(50, 50, 50);
        Point seedPoint = clickedPoint;
        Mat mask = new Mat();
        Rect rect = new Rect();

        // Imgproc.floodFill(frame, mask, seedPoint, newVal);
        Imgproc.floodFill(frame, mask, seedPoint, newVal, rect, loDiff, upDiff, Imgproc.FLOODFILL_FIXED_RANGE);

        return frame;
    }

    /**
     * Perform the operations needed for removing a uniform background
     *
     * @param frame the current frame
     * @return an image with only foreground objects
     */
    private Mat doBackgroundRemoval(Mat frame) {
        // init
        Mat hsvImg = new Mat();
        List<Mat> hsvPlanes = new ArrayList<>();
        Mat thresholdImg = new Mat();

        int thresh_type = Imgproc.THRESH_BINARY_INV;
        if (inverse.isSelected())
            thresh_type = Imgproc.THRESH_BINARY;

        // threshold the image with the average hue value
        hsvImg.create(frame.size(), CvType.CV_8U);
        Imgproc.cvtColor(frame, hsvImg, Imgproc.COLOR_BGR2HSV);
        Core.split(hsvImg, hsvPlanes);

        // get the average hue value of the image
        double threshValue = getHistAverage(hsvImg, hsvPlanes.get(0));

        Imgproc.threshold(hsvPlanes.get(0), thresholdImg, threshValue, 179.0, thresh_type);

        Imgproc.blur(thresholdImg, thresholdImg, new Size(5, 5));

        // dilate to fill gaps, erode to smooth edges
        Imgproc.dilate(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 1);
        Imgproc.erode(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 3);

        Imgproc.threshold(thresholdImg, thresholdImg, threshValue, 179.0, Imgproc.THRESH_BINARY);

        // create the new image
        Mat foreground = new Mat(frame.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        frame.copyTo(foreground, thresholdImg);

        return foreground;
    }

    /**
     * Get the average hue value of the image starting from its Hue channel
     * histogram
     *
     * @param hsvImg    the current frame in HSV
     * @param hueValues the Hue component of the current frame
     * @return the average Hue value
     */
    private double getHistAverage(Mat hsvImg, Mat hueValues) {
        // init
        double average = 0.0;
        Mat hist_hue = new Mat();
        // 0-180: range of Hue values
        MatOfInt histSize = new MatOfInt(180);
        List<Mat> hue = new ArrayList<>();
        hue.add(hueValues);

        // compute the histogram
        Imgproc.calcHist(hue, new MatOfInt(0), new Mat(), hist_hue, histSize, new MatOfFloat(0, 179));

        // get the average Hue value of the image
        // (sum(bin(h)*h))/(image-height*image-width)
        // -----------------
        // equivalent to get the hue of each pixel in the image, add them, and
        // divide for the image size (height and width)
        for (int h = 0; h < 180; h++) {
            // for each bin, get its value and multiply it for the corresponding
            // hue
            average += (hist_hue.get(h, 0)[0] * h);
        }

        // return the average hue of the image
        average = average / hsvImg.size().height / hsvImg.size().width;

        return average;
    }

    /**
     * Apply Canny
     *
     * @param frame the current frame
     * @return an image elaborated with Canny
     */
    private Mat doCanny(Mat frame) {
        // init
        Mat grayImage = new Mat();
        Mat detectedEdges = new Mat();

        // convert to grayscale
        Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);

        // reduce noise with a 3x3 kernel
        Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));

        // canny detector, with ratio of lower:upper threshold of 3:1
        Imgproc.Canny(detectedEdges, detectedEdges, threshold.getValue(), threshold.getValue() * 3);

        // using Canny's output as a mask, display the result
        Mat dest = new Mat();
        frame.copyTo(dest, detectedEdges);

        return dest;
    }

    /**
     * Apply Sobel
     *
     * @param frame the current frame
     * @return an image elaborated with Sobel derivation
     */
    private Mat doSobel(Mat frame) {
        // init
        Mat grayImage = new Mat();
        Mat detectedEdges = new Mat();
        int scale = 1;
        int delta = 0;
        int ddepth = CvType.CV_16S;
        Mat grad_x = new Mat();
        Mat grad_y = new Mat();
        Mat abs_grad_x = new Mat();
        Mat abs_grad_y = new Mat();

        // reduce noise with a 3x3 kernel
        Imgproc.GaussianBlur(frame, frame, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT);

        // convert to grayscale
        Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Gradient X
        // Imgproc.Sobel(grayImage, grad_x, ddepth, 1, 0, 3, scale,
        // this.threshold.getValue(), Core.BORDER_DEFAULT );
        Imgproc.Sobel(grayImage, grad_x, ddepth, 1, 0);
        Core.convertScaleAbs(grad_x, abs_grad_x);

        // Gradient Y
        // Imgproc.Sobel(grayImage, grad_y, ddepth, 0, 1, 3, scale,
        // this.threshold.getValue(), Core.BORDER_DEFAULT );
        Imgproc.Sobel(grayImage, grad_y, ddepth, 0, 1);
        Core.convertScaleAbs(grad_y, abs_grad_y);

        // Total Gradient (approximate)
        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, detectedEdges);
        // Core.addWeighted(grad_x, 0.5, grad_y, 0.5, 0, detectedEdges);

        return detectedEdges;

    }

    /**
     * TODO
     */
    private void cannySelected() {

        if (canny.isSelected()) {
            chkBoxSobel.setSelected(false);
            threshold.setDisable(false);
        }

        if (chkBoxSobel.isSelected()) {
            chkBoxSobel.setSelected(false);
        }

        if (dilateErode.isSelected()) {
            dilateErode.setSelected(false);
            chkBoxSobel.setSelected(false);
            inverse.setDisable(true);
        }

        // now the capture can start
        btnOpenCVStartCamera.setDisable(false);
    }

    /**
     *
     */
    private void sobelSelected() {

        if (canny.isSelected()) {
            canny.setSelected(false);
        }

        if (chkBoxSobel.isSelected()) {
            dilateErode.setSelected(false);
            inverse.setDisable(true);
        }

        if (dilateErode.isSelected()) {
            dilateErode.setSelected(false);
            inverse.setDisable(true);
        }

        // now the capture can start
        btnOpenCVStartCamera.setDisable(false);
    }

    /**
     * TODO
     */
    private void dilateErodeSelected() {
        // check whether the canny checkbox is selected, deselect it and disable
        // its slider
        if (canny.isSelected()) {
            canny.setSelected(false);
            threshold.setDisable(true);
        }

        if (chkBoxSobel.isSelected()) {
            chkBoxSobel.setSelected(false);
        }

        if (dilateErode.isSelected()) {
            inverse.setDisable(false);
            threshold.setDisable(true);
        } else {
            threshold.setDisable(true);
            inverse.setDisable(true);
        }

        // now the capture can start
        btnOpenCVStartCamera.setDisable(false);
    }


    //==================================================   writing a videofile  ========================================

    private void startWriteAndShow(final ImageView imageViewForOpenCV, final String videoFolderFromProperties) {
        // every 33 ms (30 frames/sec)
        // every 66 ms (15 frames/sec)
        final int timeToRefresh = 33;
        final int fps = 30;
        if (!isCameraActive) {
            // start the video capture
            VIDEO_CAPTURE.open(cameraId);
            // is the video stream available?
            if (VIDEO_CAPTURE.isOpened()) {
                isCameraActive = true;
                final Mat frame = new Mat(); //we will reuse this object

                //just once in order to get info about size
                //here is default minimum resolution
                VIDEO_CAPTURE.read(frame);
                final Size frameSize = frame.size();
                final String videoFileName = Util.getVideoFileName(videoFolderFromProperties);
                writer = new VideoWriter(videoFileName, FOURCC_MJPG, fps, frameSize, true);

                final Runnable frameShower = () -> {
                    // Producer grabs a frame, checks it, shows it in a display and put to the thread-safe queue:
                    VIDEO_CAPTURE.read(frame);
                    if (!frame.empty()) {
                        // show the frame
                        final Image imageToShow = UtilsOpenCV.mat2Image(frame);
                        updateImageView(imageViewForOpenCV, imageToShow);
                        //PUT frame TO a thread-safe queue
                        MAT_CONCURRENT_LINKED_QUEUE.offer(frame);
                    }
                };

                //Consumer GETs frame FROM the thread-safe queue and write it
                final Runnable frameWriter = () -> {
                    if (!MAT_CONCURRENT_LINKED_QUEUE.isEmpty()) {
                        final Mat poll = MAT_CONCURRENT_LINKED_QUEUE.poll();
                        writer.write(poll);
                    }
                };

                //we need threads only for producer and consumer
                timer = Executors.newScheduledThreadPool(2);
                timer.scheduleAtFixedRate(frameShower, 0, timeToRefresh, TimeUnit.MILLISECONDS);
                timer.scheduleAtFixedRate(frameWriter, 0, timeToRefresh, TimeUnit.MILLISECONDS);

                // update the button content
                btnOpenCVWriteVideo.setText("Stop writing");
            } else {
                // log the error
                LOGGER.error("Impossible to open the camera connection...");
            }
        } else {
            // the camera is not active at this point
            isCameraActive = false;
            // update again the button content
            btnOpenCVWriteVideo.setText("Start writing");
            // stop the timer
            stopAcquisition();
        }
    }

}
