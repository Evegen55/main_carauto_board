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

    private static final String MP3 = "mp3";
    private static final String M4A = "m4A";

    private static double layUotX = 0.0;
    private static double layUotY = 0.0;
    private static double bias = 80.0;

    public AudioController(final Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // TODO: 10/30/2017 list of audio items from folder to a scroll pane

    /**
     * @param btn_pick_folder
     * @param btn_choose_music
     * @param pane_with_music
     */
    public void setInitState(final Button btn_pick_folder, final Button btn_choose_music, final Pane pane_with_music) {
        setInitialStateForSingleAudioItem(btn_choose_music, pane_with_music);
        setInitialStateForListOfAudioItems(btn_pick_folder, pane_with_music);
    }

    /**
     * It reads and plays a single audio file
     *
     * @param buttonToOpen
     * @param pane
     */
    private void setInitialStateForSingleAudioItem(final Button buttonToOpen, final Pane pane) {
        buttonToOpen.setOnAction(action -> {
            final File singleFileFromOpenedDialog = getFileChooserForMusic(primaryStage);
            if (singleFileFromOpenedDialog != null) {
                try {
                    readFileAndSetInitialStateForAudioItem(singleFileFromOpenedDialog, pane);
                } catch (IOException e1) {
                    LOGGER.error("Second exception");
                    e1.printStackTrace();
                }
            }
        });
    }

    private void readFileAndSetInitialStateForAudioItem(final File singleFileFromOpenedDialog, final Pane pane)
            throws IOException {
        final AudioItem audioItem = new AudioItem(layUotX, layUotY);
        layUotX = audioItem.getLayoutX();
        layUotY = audioItem.getLayoutY() + bias;
        final String openDialogFilePath = singleFileFromOpenedDialog.getPath();
        LOGGER.info("now opening: " + openDialogFilePath);
        final String baseName = FilenameUtils.getName(openDialogFilePath);
        final String path = singleFileFromOpenedDialog.toURI().toASCIIString();
        final Media mediaSound = new Media(path);
        LOGGER.info("mediaSound opened " + mediaSound.getSource());
        final MediaPlayer mediaPlayer = new MediaPlayer(mediaSound);
        final MediaView mediaView = new MediaView(mediaPlayer);

        audioItem.getLabel_for_name().setText(baseName);
        audioItem.getChildren().addAll(mediaView);
        pane.getChildren().add(audioItem);

        audioItem.getStop().setOnAction(action -> mediaPlayer.stop());
        audioItem.getPlay().setOnAction(action -> {
            checkStopForOtherAudioItems();
            audioItem.getLabel_for_time().setText(String.valueOf(mediaSound.getDuration().toMinutes()));
            mediaPlayer.play();
        });

    }

    private void checkStopForOtherAudioItems() {
        // TODO: 10/31/2017 add logic to stop other audio items
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

    private void setInitialStateForListOfAudioItems(final Button btn_pick_folder, final Pane pane_with_music) {
        btn_pick_folder.setOnAction(action -> {
            pickAndReadAudioFilesFromFolder(primaryStage, pane_with_music);
        });
    }

    private void pickAndReadAudioFilesFromFolder(final Stage primaryStage, final Pane pane) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Find a folder with audio files");
        final File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            final File[] listFiles = selectedDirectory.listFiles();
            if (listFiles != null) {
                LOGGER.info("Found " + listFiles.length + " files");
                Arrays.stream(listFiles)
                        .filter(file -> !file.isDirectory())
                        .filter(file -> {
                            final String extension = FilenameUtils.getExtension(file.getPath());
                            return extension.equals(MP3) || extension.equals(M4A);
                        })
                        .forEach(file -> {
                            LOGGER.info("Trying to load from the file: " + file.getPath());
                            try {
                                readFileAndSetInitialStateForAudioItem(file, pane);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }
        }
    }

}
