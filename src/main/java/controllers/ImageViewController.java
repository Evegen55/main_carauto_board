package controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * @author (created on 12/4/2017).
 */
public class ImageViewController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImageViewController.class);

    private final Stage primaryStage;

    public ImageViewController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void initStartView(final VBox vboxPhotoList, final Button btn_choose_photo) {

        //opens image from a folder
        //for a single file it has to be fitted to a screen
        //so the entire list of previews has to be removed
        // TODO: 12/5/2017 also it can be fetched from internet
        btn_choose_photo.setOnAction(event -> {
            final File singleFileFromOpenedDialog = getFileChooserForImage(primaryStage);
            if (singleFileFromOpenedDialog != null) {
                retriveImageFromFileAndFitToBox(vboxPhotoList, singleFileFromOpenedDialog);
            }
        });

        // TODO: 12/5/2017 for a folder
    }

    private void retriveImageFromFileAndFitToBox(final VBox vboxPhotoList, final File singleFileFromOpenedDialog) {
        LOGGER.info("Open image: ", singleFileFromOpenedDialog.toURI());
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(singleFileFromOpenedDialog);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        final ImageView imageView = new ImageView(image);
        vboxPhotoList.getChildren().clear();
        vboxPhotoList.getChildren().add(imageView);
        vboxPhotoList.setAlignment(Pos.CENTER);
        imageView.setPreserveRatio(true);
        if (imageView.getImage().getHeight() > vboxPhotoList.getHeight() ||
                imageView.getImage().getWidth() > vboxPhotoList.getWidth()) {
            imageView.fitHeightProperty().bind(vboxPhotoList.heightProperty());
            imageView.fitWidthProperty().bind(vboxPhotoList.widthProperty());
        }
        LOGGER.info("Image opened");

    }

    // TODO: 12/5/2017 mutual method with image and audio
    // TODO: 12/5/2017 it can filter for ALL TOGETHER extensions
    private File getFileChooserForImage(final Stage primaryStage) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("*" + " files (*." + "*" + ")", "*." + "*"));
        Stream.of(ImageFilters.values()).forEach(item -> {
            final String fileExt = item.toString();
            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter(fileExt + " files (*." + fileExt + ")", "*." + fileExt));
        });
        fileChooser.setTitle("Choose your images:");
        return fileChooser.showOpenDialog(primaryStage);
    }
}
