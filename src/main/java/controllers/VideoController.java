package controllers;

import javafx.scene.control.Button;
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

    public void seiInitState(final Pane pane, final Button buttonToOpen,
                             Button btn_play_video, Button btn_stop_video) {
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
}
