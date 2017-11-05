package utils;

import javafx.util.Duration;
import org.apache.commons.lang3.time.DurationFormatUtils;

public class Util {

    public static String formatDurationForMedia(final Duration currentTime) {
        return DurationFormatUtils.formatDuration((long) currentTime.toMillis(), "H:mm:ss", true);
    }
}
