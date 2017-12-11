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
        super();
        setMinHeight(150);
        setMinWidth(150);
        this.image = new Image(url);
        this.imageView = new ImageView(image);
        this.imageView.setPreserveRatio(true);
        this.getChildren().add(imageView);
        imageView.fitHeightProperty().bind(this.heightProperty());
        imageView.fitWidthProperty().bind(this.widthProperty());
    }
}
