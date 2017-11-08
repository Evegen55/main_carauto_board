package utils;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.commons.lang3.time.DurationFormatUtils;

public class Util {

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
}
