package entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * @author (created on 12/4/2017).
 */
public class ImageIconPreview extends Pane {

    private Image image;
    private ImageView imageView;

    public ImageIconPreview(final String url) {
        this.image = new Image(url);
        this.imageView = new ImageView(image);
        this.getChildren().add(imageView);
    }
}
