package controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class AudioController {

    private final static Logger LOGGER = LoggerFactory.getLogger(AudioController.class);

    public void pickListFileInsideFolderWithMaps(final Stage primaryStage, final Button button,
                                                 final Pane pane, final Label lbl_with_music) {
        button.setOnAction(action -> {
            final File openDialogFile = getFileChooserForMusic(primaryStage);
            if (openDialogFile != null) {
                try {
                    readAudioFile(openDialogFile, pane, lbl_with_music);
                } catch (IOException e1) {
                    LOGGER.error("Second exception");
                    e1.printStackTrace();
                }
            } else {
                pickAndReadAudioFilesFromFolder(primaryStage);
            }
        });

    }

    private static File getFileChooserForMusic(final Stage primaryStage) {
        final FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter mp3Filter =
                new FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.mp3");
        FileChooser.ExtensionFilter m4aFilter =
                new FileChooser.ExtensionFilter("M4A files (*.m4a)", "*.m4a");
        fileChooser.getExtensionFilters().addAll(mp3Filter, m4aFilter);
        fileChooser.setTitle("Choose your music:");
        return fileChooser.showOpenDialog(primaryStage);
    }

    private void readAudioFile(final File openDialogFile, final Pane pane, Label lbl_with_music) throws IOException {
        String openDialogFilePath = openDialogFile.getPath();
        LOGGER.info("now opening: " + openDialogFilePath);
        String baseName = FilenameUtils.getBaseName(openDialogFilePath);
        lbl_with_music.setText(baseName);
        final String path = openDialogFile.toURI().toASCIIString();
        final Media mediaSound = new Media(path);
        LOGGER.info("mediaSound opened " + mediaSound.getSource());
        final MediaPlayer mediaPlayer = new MediaPlayer(mediaSound);

        // TODO: 29.10.2017 process it
        AudioEqualizer mediaPlayerAudioEqualizer = mediaPlayer.getAudioEqualizer();

        final MediaView mediaView = new MediaView(mediaPlayer);
        pane.getChildren().add(mediaView);
        mediaPlayer.play();
    }

    private void pickAndReadAudioFilesFromFolder(final Stage primaryStage) {

    }

}
