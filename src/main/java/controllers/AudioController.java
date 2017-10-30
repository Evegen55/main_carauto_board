package controllers;

import entities.AudioItem;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class AudioController {

    private final static Logger LOGGER = LoggerFactory.getLogger(AudioController.class);
    private final Stage primaryStage;
    private MediaPlayer mediaPlayer;

    public AudioController(final Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    /**
     * It reads and plays a single audio file
     * @param buttonToOpen
     * @param pane
     */
    public void setInitialStateForSingleAudioItem(final Button buttonToOpen, final Pane pane) {
        buttonToOpen.setOnAction(action -> {
            final File singleFileFromOpenedDialog = getFileChooserForMusic(primaryStage);
            if (singleFileFromOpenedDialog != null) {
                try {
                    readAndPlayAudioFile(singleFileFromOpenedDialog, pane);
                } catch (IOException e1) {
                    LOGGER.error("Second exception");
                    e1.printStackTrace();
                }
            }
        });
    }

    private void readAndPlayAudioFile(final File singleFileFromOpenedDialog, final Pane pane) throws IOException {
        final AudioItem audioItem = new AudioItem();
        String openDialogFilePath = singleFileFromOpenedDialog.getPath();
        LOGGER.info("now opening: " + openDialogFilePath);
        String baseName = FilenameUtils.getName(openDialogFilePath);
        audioItem.getLabel_for_name().setText(baseName);

        final String path = singleFileFromOpenedDialog.toURI().toASCIIString();
        final Media mediaSound = new Media(path);
        LOGGER.info("mediaSound opened " + mediaSound.getSource());
        mediaPlayer = new MediaPlayer(mediaSound);
        final MediaView mediaView = new MediaView(mediaPlayer);
        audioItem.getChildren().add(mediaView);
        pane.getChildren().add(audioItem);

        audioItem.getStop().setOnAction(action -> mediaPlayer.stop());
        audioItem.getPlay().setOnAction(action -> {
            audioItem.getLabel_for_time().setText(String.valueOf(mediaSound.getDuration().toMinutes()));
            mediaPlayer.play();
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

    private void pickAndReadAudioFilesFromFolder(final Stage primaryStage) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Find a folder with audio files");
        final File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            final File[] listFiles = selectedDirectory.listFiles();
            if (listFiles != null) {
                LOGGER.info("Found " + listFiles.length + " files");
                Arrays.stream(listFiles).forEach(file -> {
                    LOGGER.info("Trying to load from the file: " + file.getPath());
                    // TODO: 29.10.2017
                });
            }
        }
    }

}
