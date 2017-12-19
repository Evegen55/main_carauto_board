package controllers.openvc;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

public class WriteVideoController {

    // TODO: 12/19/2017
    public void writeToFile(Mat frame_input, String folder) {

    }

    public static void main(String... args) throws InterruptedException {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture camera = new VideoCapture(0);

        VideoWriter writer = new VideoWriter("D:/5_TEMP/test.avi", VideoWriter.fourcc('M', 'J', 'P', 'G'), 15, new Size(1280, 720), true);

        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 1280);
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 720);

        if(!camera.isOpened()){
            System.out.println("Error");
        }
        else {
            int index = 0;
            Mat frame = new Mat();

            while(true){
                if (camera.read(frame)){
                    System.out.println("Captured Frame Width " + frame.width() + " Height " + frame.height());
                    writer.write(frame);
                    Thread.currentThread().sleep(66);
                    index++;
                }

                if (index > 200) {
                    break;
                }

                frame.release();
            }
        }
        writer.release();
        camera.release();
    }
}
