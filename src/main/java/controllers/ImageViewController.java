package controllers;

import entities.ImageIconPreview;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.stream.Stream;

/**
 * @author (created on 12/4/2017).
 */
public class ImageViewController {

    private final Stage primaryStage;

    public ImageViewController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void initStartView(final VBox vboxPhotoList, Button btn_choose_photo) {

        btn_choose_photo.setOnAction(event -> {
            final File singleFileFromOpenedDialog = getFileChooserForImage(primaryStage);
            if (singleFileFromOpenedDialog != null) {
                // TODO: 12/5/2017 also it can be fetched from internet
                // TODO: 12/5/2017 for a single file it has to be fitted to a screen
                // TODO: 12/5/2017 for a folder

                ImageIconPreview imageIconPreview = new ImageIconPreview(singleFileFromOpenedDialog.toURI().toString());
                vboxPhotoList.getChildren().add(imageIconPreview);
            }
        });

    }

    // TODO: 12/5/2017 mutual method with image and audio
    // TODO: 12/5/2017 it can filter for ALL TOGETHER extensions
    private File getFileChooserForImage(final Stage primaryStage) {
        final FileChooser fileChooser = new FileChooser();
        Stream.of(ImageFilters.values()).forEach(item -> {
            final String fileExt = item.toString();
            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter(fileExt + " files (*." + fileExt + ")", "*." + fileExt));
        });
        fileChooser.setTitle("Choose your images:");
        return fileChooser.showOpenDialog(primaryStage);
    }
}
