/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * <p>
 * Copyright (C) 2017 - 2017 Evgenii Lartcev (https://github.com/Evegen55/) and contributors
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Evgenii Lartcev
 * @created on 12/13/2017
 */

package controllers;

import entities.AudioItem;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
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

import static utils.Util.formatDurationForMedia;
import static utils.Util.updateValuesForSliderDependsOnPlayer;

public final class AudioController {

    private final static Logger LOGGER = LoggerFactory.getLogger(AudioController.class);
    private final Stage primaryStage;

    private static final String MP3 = "mp3";
    private static final String M4A = "m4A";

    //one for all sounds
    private boolean alreadyPlaying = false;

    public AudioController(final Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Here is the main method after invoking constructor
     * @param btn_pick_folder
     * @param btn_choose_music
     * @param vboxPlaylist
     */
    public void setInitState(final Button btn_pick_folder, final Button btn_choose_music, final VBox vboxPlaylist) {
        setInitialStateForSingleAudioItem(btn_choose_music, vboxPlaylist);
        setInitialStateForListOfAudioItems(btn_pick_folder, vboxPlaylist);
    }

    /**
     * It reads and plays a single audio file
     *
     * @param buttonToOpen
     */
    private void setInitialStateForSingleAudioItem(final Button buttonToOpen, final VBox vboxPlaylist) {
        buttonToOpen.setOnAction(action -> {
            final File singleFileFromOpenedDialog = getFileChooserForMusic(primaryStage);
            if (singleFileFromOpenedDialog != null) {
                try {
                    readFileAndSetInitialStateForAudioItem(singleFileFromOpenedDialog, vboxPlaylist);
                } catch (IOException e1) {
                    LOGGER.error("Second exception");
                    e1.printStackTrace();
                }
            }
        });
    }

    private void readFileAndSetInitialStateForAudioItem(final File singleFileFromOpenedDialog, final VBox vboxPlaylist)
            throws IOException {
        final AudioItem audioItem = new AudioItem();
        final String openDialogFilePath = singleFileFromOpenedDialog.getPath();
        LOGGER.info("Now is opening: " + openDialogFilePath);
        final String baseName = FilenameUtils.getName(openDialogFilePath);
        final String path = singleFileFromOpenedDialog.toURI().toASCIIString();
        final Media mediaSound = new Media(path);
        LOGGER.info("MediaSound " + mediaSound.getSource() + " opened");
        final MediaPlayer mediaPlayer = new MediaPlayer(mediaSound);
        final MediaView mediaView = new MediaView(mediaPlayer);

        setupTimeSliderDependsOnMediaPlayer(audioItem.getSlider(), audioItem.getLabel_for_time(), mediaPlayer);

        audioItem.getLabel_for_name().setText(baseName);
        audioItem.getChildren().addAll(mediaView);

        audioItem.getStop().setOnAction(action -> {
            mediaPlayer.stop();
            alreadyPlaying = false;
        });

        audioItem.getPlay().setOnAction(action -> {
            if (!alreadyPlaying) {
                final String formatted = formatDurationForMedia(mediaSound.getDuration());
                audioItem.getLabel_for_time().setText(formatted);
                mediaPlayer.play();
                alreadyPlaying = true;
            }
        });

        vboxPlaylist.getChildren().add(audioItem);

    }

    private void setupTimeSliderDependsOnMediaPlayer(final Slider slider, final Label label_for_time, final MediaPlayer mediaPlayer) {
        slider.valueProperty().addListener(event -> {
            if (slider.isValueChanging() || slider.isPressed()) {
                mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(slider.getValue() / 100));
            }
        });
        mediaPlayer.currentTimeProperty().addListener(event -> updateValuesForSliderDependsOnPlayer(slider, label_for_time, mediaPlayer));
    }

    // TODO: 12/5/2017 mutual method with image and audio
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

    private void setInitialStateForListOfAudioItems(final Button btn_pick_folder, final VBox vboxPlaylist) {
        btn_pick_folder.setOnAction(action -> {
            pickAndReadAudioFilesFromFolder(primaryStage, vboxPlaylist);
        });
    }

    // TODO: 11/30/2017 it would be able to read from folders with writing delimiter to a playlist
    private void pickAndReadAudioFilesFromFolder(final Stage primaryStage, final VBox vboxPlaylist) {
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
                                readFileAndSetInitialStateForAudioItem(file, vboxPlaylist);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }
        }
    }

}
