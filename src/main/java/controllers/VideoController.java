package controllers;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author (created on 10/31/2017).
 */
public class VideoController {

    private final static Logger LOGGER = LoggerFactory.getLogger(VideoController.class);
    private static final String MP4 = "mp4";
    private final Stage primaryStage;

    private MediaPlayer mediaPlayer;

    public VideoController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void seiInitState(final Pane pane, final Button buttonToOpen, final Button btn_play_video,
                             final Button btn_stop_video, final Button btn_pause_video, final Slider time_slider,
                             final Slider volume_audio_slider, Label time_lbl) {
        buttonToOpen.setOnAction(action -> {
            final File singleFileFromOpenedDialog = getFileChooserForVideo(primaryStage);
            if (singleFileFromOpenedDialog != null) {
                try {
                    readFileAndSetInitialStateForVideoItem(singleFileFromOpenedDialog, pane);
                } catch (IOException e1) {
                    LOGGER.error("Second exception");
                    e1.printStackTrace();
                }
            }
        });

        setupControlButtons(btn_play_video, btn_stop_video, btn_pause_video);
        setupTimeSliderDependsOnMediaPlayer(time_slider, time_lbl);
        setupVolumeSliderDependsOnMediaPlayer(volume_audio_slider);

    }

    private void readFileAndSetInitialStateForVideoItem(final File singleFileFromOpenedDialog, final Pane pane)
            throws IOException {
        final String openDialogFilePath = singleFileFromOpenedDialog.getPath();
        LOGGER.info("now opening: " + openDialogFilePath);
        final String path = singleFileFromOpenedDialog.toURI().toASCIIString();
        final Media mediaVideo = new Media(path);
        LOGGER.info("mediaVideo opened " + mediaVideo.getSource());

        mediaPlayer = new MediaPlayer(mediaVideo);
        final MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setPreserveRatio(true);
        mediaView.setFitHeight(pane.getHeight());
        mediaView.setFitWidth(pane.getWidth());
        pane.getChildren().add(mediaView);
    }

    private File getFileChooserForVideo(final Stage primaryStage) {
        final FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter mp4Filter =
                new FileChooser.ExtensionFilter(MP4 + " files (*." + "mp4" + ")", "*." + MP4);
        fileChooser.getExtensionFilters().add(mp4Filter);
        fileChooser.setTitle("Choose your video:");
        return fileChooser.showOpenDialog(primaryStage);
    }


    private void setupControlButtons(final Button btn_play_video, final Button btn_stop_video,
                                     final Button btn_pause_video) {
        btn_play_video.setOnAction(action -> {
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
        });

        btn_stop_video.setOnAction(action -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });

        btn_pause_video.setOnAction(action -> {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        });
    }

    private void setupTimeSliderDependsOnMediaPlayer(final Slider time_slider, final Label time_lbl) {
        time_slider.valueProperty().addListener(event -> {
            if (time_slider.isPressed()) {
                mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(time_slider.getValue() / 100));
            }
        });
        // TODO: 05.11.2017 why it doesn't work??
        if (mediaPlayer != null) {
            mediaPlayer.currentTimeProperty().addListener(event -> updateValues(time_slider, time_lbl));
        }
    }

    private void updateValues(final Slider time_slider, final Label time_lbl) {
        Platform.runLater(() -> {
            double curr = mediaPlayer.getCurrentTime().toMillis();
            double total = mediaPlayer.getTotalDuration().toMillis();
            System.out.println(curr);
            final double vol = curr / total * 100;
            time_slider.setValue(vol);
            time_lbl.setText("abc" + vol);
        });
    }

    private void setupVolumeSliderDependsOnMediaPlayer(final Slider volume_audio) {
        if (mediaPlayer != null) {
            volume_audio.valueProperty().addListener(event -> {
                if (volume_audio.isPressed()) {
                    mediaPlayer.setVolume(volume_audio.getValue() / 100);
                }
            });
        }
    }
}
