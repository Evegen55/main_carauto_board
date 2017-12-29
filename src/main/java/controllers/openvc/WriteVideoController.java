package controllers.openvc;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDate;

public class WriteVideoController {

    private final static Logger LOGGER = LoggerFactory.getLogger(WriteVideoController.class);

    private final int FOURCC_MJPG = VideoWriter.fourcc('M', 'J', 'P', 'G');
    private static final String VIDEO_FILE_NAME_EXTENSION = ".avi";
    private static final String VIDEO_FILE_NAME_ROOT = "video";
    private static final String FILE_SEPARATOR = File.separator;

    private final VideoCapture camera;
    private VideoWriter writer;
    private final int width;
    private final int heigth;

    private String savedFile;

    public WriteVideoController(int cameraIndex, int width, int heigth) {
        this.camera = new VideoCapture(cameraIndex);
        this.width = width;
        this.heigth = heigth;
        this.camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, width);
        this.camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, heigth);
    }

    public WriteVideoController(VideoCapture camera, int width, int heigth) {
        this.camera = camera;
        this.width = width;
        this.heigth = heigth;
        this.camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, this.width);
        this.camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, this.heigth);
    }

    public String getSavedFile() {
        return savedFile;
    }

    public VideoWriter getWriter() {
        return writer;
    }

    /**
     * @param folder
     * @param amountOfFrames can be Float.POSITIVE_INFINITY in case of unknown writing time
     * @param fps
     */
    public void writeFromCameraToFolder(final String folder, final float amountOfFrames, final int fps) {
        final String videoFileName = getVideoFileName(folder);
        LOGGER.info("videoFileName: " + videoFileName);
        writer = new VideoWriter(videoFileName, FOURCC_MJPG, fps, new Size(width, heigth), true);
        if (!camera.isOpened()) {
            System.out.println("Error");
        } else {
            int index = 0;
            Mat frame = new Mat();
            while (true) {
                if (camera.read(frame)) {
                    LOGGER.info("Captured Frame Width " + frame.width() + " Height " + frame.height());
                    writer.write(frame);
                    try {
                        Thread.currentThread().sleep(66);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    index++;
                }
                if (index > amountOfFrames) {
                    break;
                }
                frame.release();
            }
        }
        releaseResources();
    }

    public void releaseResources() {
        writer.release();
        camera.release();
    }

    private String getVideoFileName(final String folder) {
        savedFile = new StringBuilder().append(folder)
                .append(FILE_SEPARATOR).append(VIDEO_FILE_NAME_ROOT)
                .append("_")
                .append(LocalDate.now()) // TODO: 12/29/2017 add time with no :
                .append("_")
                .append(VIDEO_FILE_NAME_EXTENSION).toString();
        return savedFile;
    }

}