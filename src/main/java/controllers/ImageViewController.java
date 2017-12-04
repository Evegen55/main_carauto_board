package controllers;

import entities.ImageIconPreview;
import javafx.scene.layout.VBox; /**
 * @author (created on 12/4/2017).
 */
public class ImageViewController {

    public void initStartView(final VBox vboxPhotoList) {
        String url="http://icons.iconarchive.com/icons/jj-maxer/merry-christmas/128/home-icon.png";
        ImageIconPreview imageIconPreview1 = new ImageIconPreview(url);
        ImageIconPreview imageIconPreview2 = new ImageIconPreview(url);
        ImageIconPreview imageIconPreview3 = new ImageIconPreview(url);
        vboxPhotoList.getChildren().addAll(imageIconPreview1, imageIconPreview2, imageIconPreview3);
    }
}
