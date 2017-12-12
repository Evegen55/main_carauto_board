package controllers.openvc;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.UtilsOpenCV;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ImageRecognizer {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImageRecognizer.class);

    private final Stage primaryStage;

    // a timer for acquiring the video stream
    private ScheduledExecutorService timer;
    // the OpenCV object that realizes the video capture
    private static final VideoCapture VIDEO_CAPTURE = new VideoCapture();
    private final Button btnOpenCVStartCamera;
    // a flag to change the button behavior
    private boolean cameraActive = false;
    // the id of the camera to be used
    private static int cameraId = 0;

    //face recognition
    private final CheckBox checkBoxGrayscale;
    private final CheckBox checkBoxhaarClassifier;
    private final CheckBox checkBoxlbpClassifier;
    // face cascade classifier
    private static final CascadeClassifier FACE_CASCADE = new CascadeClassifier();
    private int absoluteFaceSize = 0;

    public ImageRecognizer(final Stage primaryStage, Button btnOpenCVStartCamera, final CheckBox checkBoxGrayscale,
                           final CheckBox checkBoxhaarClassifier, final CheckBox checkBoxlbpClassifier) {
        this.primaryStage = primaryStage;
        this.btnOpenCVStartCamera = btnOpenCVStartCamera;
        this.checkBoxGrayscale = checkBoxGrayscale;
        this.checkBoxhaarClassifier = checkBoxhaarClassifier;
        this.checkBoxlbpClassifier = checkBoxlbpClassifier;
    }

    public void showSimpleCamera(final ImageView imageViewForOpenCV) {
        LOGGER.info("Start showing a stream captured from camera");
        primaryStage.setOnCloseRequest((windowEvent -> setClosed()));

        checkBoxhaarClassifier.setOnAction(event -> {
                    if (checkBoxlbpClassifier.isSelected())
                        checkBoxlbpClassifier.setSelected(false);
                    checkboxSelection("trainedNN/opencv/haarcascades/haarcascade_frontalface_alt.xml");
                }
        );

        checkBoxlbpClassifier.setOnAction(event -> {
            if (checkBoxhaarClassifier.isSelected())
                checkBoxhaarClassifier.setSelected(false);
            checkboxSelection("trainedNN/opencv//lbpcascades/lbpcascade_frontalface.xml");
        });

        imageViewForOpenCV.setPreserveRatio(true);
        btnOpenCVStartCamera.setOnAction(event -> {
            startCamera(imageViewForOpenCV, btnOpenCVStartCamera);
        });

    }

    private void checkboxSelection(final String classifierPath) {
        // load the classifier(s)
        boolean load = FACE_CASCADE.load(classifierPath);
        if(load) {
            LOGGER.info("Classifier loaded");
        } else {
            LOGGER.warn("Classifier wasn't loaded");
        }
        // now the video capture can start
        btnOpenCVStartCamera.setDisable(false);
    }

    private void startCamera(final ImageView imageViewForOpenCV, final Button btnOpenCVStartCamera) {
        if (!cameraActive) {

            // disable setting checkboxes
            checkBoxhaarClassifier.setDisable(true);
            checkBoxlbpClassifier.setDisable(true);

            // start the video capture
            VIDEO_CAPTURE.open(cameraId);

            // is the video stream available?
            if (VIDEO_CAPTURE.isOpened()) {
                cameraActive = true;

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
            cameraActive = false;
            // update again the button content
            btnOpenCVStartCamera.setText("Start Camera");

            // enable classifiers checkboxes
            checkBoxhaarClassifier.setDisable(false);
            checkBoxlbpClassifier.setDisable(false);

            // stop the timer
            stopAcquisition();
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
    private void detectAndDisplay(final Mat frame, boolean grayIsAlreadySelected) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        if (grayIsAlreadySelected) {
            LOGGER.warn("TODO IT :-)");
        } else {
            // convert the frame in gray scale
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        }

        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (20% of the frame height, in our case)
        if (absoluteFaceSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0) {
                absoluteFaceSize = Math.round(height * 0.2f);
            }
        }

        // detect faces
        FACE_CASCADE.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());

        // each rectangle in faces is a face: draw them!
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
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
