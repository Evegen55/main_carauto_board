package controllers.mapListeners;

import com.lynden.gmapsfx.MapReadyListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import javafx.scene.control.TextField;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author (created on 11/24/2017).
 */
public class MouseClckForGetCoordListenerImpl implements MapReadyListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(MouseClckForGetCoordListenerImpl.class);

    private GoogleMap map;
    private TextField txt_from;
    private TextField txt_to;

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public void setTxt_from(TextField txt_from) {
        this.txt_from = txt_from;
    }

    public void setTxt_to(TextField txt_to) {
        this.txt_to = txt_to;
    }

    @Override
    public void mapReady() {
        map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {
            LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
            System.out.println("LatLong: lat: " + ll.getLatitude() + " lng: " + ll.getLongitude());
            txt_from.setText(ll.toString());
        });
        LOGGER.info("ui event handler added");
    }


}
