package controllers.openvc;

import org.junit.Ignore;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class WriteVideoControllerTest {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    @Ignore
    public void writeFromCameraToFolder() {
        VideoCapture camera = new VideoCapture(0);
        WriteVideoController writeVideoController = new WriteVideoController(camera, 1280, 720);
        writeVideoController.writeFromCameraToFolder("../video1", Float.POSITIVE_INFINITY, 15);
    }

    @Test
    public void writeFromCameraToFolderShort() throws IOException {
        VideoCapture camera = new VideoCapture(0);
        WriteVideoController writeVideoController = new WriteVideoController(camera, 1280, 720);
        writeVideoController.writeFromCameraToFolder("../video1", 10, 15);
        String videoFileName = writeVideoController.getSavedFile();
        Files.delete(Paths.get(videoFileName));
    }
}