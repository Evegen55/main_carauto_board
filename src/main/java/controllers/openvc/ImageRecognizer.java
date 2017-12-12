package controllers.openvc;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
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
    private VideoCapture capture = new VideoCapture();
    // a flag to change the button behavior
    private boolean cameraActive = false;
    // the id of the camera to be used
    private static int cameraId = 0;

    public ImageRecognizer(final Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showSimpleCamera(final ImageView imageViewForOpenCV, Button btnOpenCVStartCamera) {
        LOGGER.info("Start showing a stream captured from camera");
        primaryStage.setOnCloseRequest((windowEvent -> setClosed()));
        btnOpenCVStartCamera.setOnAction(event -> {
            startCamera(imageViewForOpenCV, btnOpenCVStartCamera);
        });

    }

    private void startCamera(final ImageView imageViewForOpenCV, final Button btnOpenCVStartCamera) {
        if (!cameraActive) {
            // start the video capture
            capture.open(cameraId);

            // is the video stream available?
            if (capture.isOpened()) {
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
        if (capture.isOpened()) {
            try {
                // read the current frame
                capture.read(frame);

                // if the frame is not empty, process it
                if (!frame.empty()) {
                    Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
                }

            } catch (Exception e) {
                // log the error
                LOGGER.error("Exception during the image elaboration: " + e);
            }
        }

        return frame;
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

        if (capture.isOpened()) {
            // release the camera
            capture.release();
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
