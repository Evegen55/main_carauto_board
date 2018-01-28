package utils;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.io.File;
import java.time.LocalDateTime;

public class Util {

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
