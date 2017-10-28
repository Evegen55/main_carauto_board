package controllers;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class AudioController {

    private final static Logger LOGGER = LoggerFactory.getLogger(AudioController.class);

    private final static String fileProtocol = "file:////";

    public void pickListFileInsideFolderWithMaps(final Stage primaryStage,
                                                 final Button button, final Pane pane) {
        button.setOnAction(action -> {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose your music:");
            final File openDialogFile = fileChooser.showOpenDialog(primaryStage);
            if (openDialogFile != null) {
                final String openDialogFilePath = openDialogFile.getPath();
                LOGGER.info(openDialogFilePath);
                try {
                    readAudioFile(openDialogFile, pane);
                } catch (IOException e1) {
                    LOGGER.error("Second exception");
                    e1.printStackTrace();
                }
            } else {
                pickAndReadAudioFilesFromFolder(primaryStage);
            }
        });

    }

    private void readAudioFile(final File openDialogFilePath, final Pane pane) throws IOException {
        String path = openDialogFilePath.toURI().getPath();
        // TODO: 28.10.2017 can open files with no spaces only
        final Media mediaSound = new Media(fileProtocol + path);
        LOGGER.info("mediaSound opened " + mediaSound.getSource());
        final MediaPlayer mediaPlayer = new MediaPlayer(mediaSound);
        AudioEqualizer mediaPlayerAudioEqualizer = mediaPlayer.getAudioEqualizer();

        final MediaView mediaView = new MediaView(mediaPlayer);
        pane.getChildren().add(mediaView);
        mediaPlayer.play();
    }

    private void pickAndReadAudioFilesFromFolder(final Stage primaryStage) {

    }

}
