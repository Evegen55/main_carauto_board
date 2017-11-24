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

    private LatLong latLongOrigin;
    private LatLong latLongDestination;

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public void setTxt_from(TextField txt_from) {
        this.txt_from = txt_from;
    }

    public void setTxt_to(TextField txt_to) {
        this.txt_to = txt_to;
    }

    public LatLong getLatLongOrigin() {
        return latLongOrigin;
    }

    public LatLong getLatLongDestination() {
        return latLongDestination;
    }

    @Override
    public void mapReady() {
        map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {
            latLongOrigin = new LatLong((JSObject) obj.getMember("latLng"));
            txt_from.setText(latLongOrigin.toString());
            latLongDestination = new LatLong((JSObject) obj.getMember("latLng"));
            txt_to.setText(latLongDestination.toString());
        });
        LOGGER.info("UI eventhandler added");
    }


}
