package utils;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class Util {

    private final static Logger LOGGER = LoggerFactory.getLogger(Util.class);

    private static final String VIDEO_FILE_NAME_EXTENSION = ".avi";
    private static final String VIDEO_FILE_NAME_ROOT = "video";
    private static final String FILE_SEPARATOR = File.separator;

    public static String formatDurationForMedia(final Duration currentTime) {
        return DurationFormatUtils.formatDuration((long) currentTime.toMillis(), "H:mm:ss", true);
    }

    public static void updateValuesForSliderDependsOnPlayer(final Slider time_slider, final Label time_lbl, final MediaPlayer mediaPlayer) {
        Platform.runLater(() -> {
            final Duration currentTime = mediaPlayer.getCurrentTime();
            double curr = currentTime.toMillis();
            double total = mediaPlayer.getTotalDuration().toMillis();
            final double vol = curr / total * 100;
            time_slider.setValue(vol);
            final String format = formatDurationForMedia(currentTime);
            time_lbl.setText(format);
        });
    }

    /*
    it checks a folder. If it doesn't exist - method creates one.
     */
    public static boolean checkFolderExistence(final String videoFolderFromProperties) {
        final Path path = Paths.get(videoFolderFromProperties);
        if (!Files.exists(path)) {
            LOGGER.warn("The folder for storing video DOESN'T exists and will be created");
            try {
                Files.createDirectory(path); //we can return the full path if it needs
                return true;
            } catch (IOException e) {
                LOGGER.error("Something went wrong" + e);
                e.printStackTrace();
            }
        } else {
            LOGGER.info("The folder for storing video EXISTS");
            return true;
        }
        return false;
    }

    public static String getVideoFileName(final String videoFolderFromProperties) {
        final String timeMark = LocalDateTime.now().toString();
        final String timeMarkprepared = timeMark.replace(':', '-');
        return new StringBuilder().append(videoFolderFromProperties)
                .append(FILE_SEPARATOR).append(VIDEO_FILE_NAME_ROOT)
                .append("_")
                .append(timeMarkprepared)
                .append("_")
                .append(VIDEO_FILE_NAME_EXTENSION).toString();
    }
}
