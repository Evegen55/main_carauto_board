package controllers;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.directions.*;
import com.lynden.gmapsfx.service.geocoding.GeocoderStatus;
import com.lynden.gmapsfx.service.geocoding.GeocodingResult;
import com.lynden.gmapsfx.service.geocoding.GeocodingService;
import com.lynden.gmapsfx.service.geocoding.GeocodingServiceCallback;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author (created on 10/20/2017).
 */
public class GmapfxController implements MapComponentInitializedListener, DirectionsServiceCallback, GeocodingServiceCallback {

    private final static Logger LOGGER = LoggerFactory.getLogger(GmapfxController.class);

    private GoogleMapView mapComponent;
    private GoogleMap map;
    private DirectionsPane directionsPane;
    private DirectionsRenderer directionsRenderer;
    private DirectionsService directionsService;


    private String styleString = getStyleForMap();

    private String getStyleForMap() {
        String content = null;
        try {
            content = IOUtils.toString(this.getClass()
                    .getResourceAsStream("/css/grayMap.json"), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * @param tab
     */
    public void createSimpleMap(final Tab tab) {
        //generates google map with some defaults and put it into top pane
        mapComponent = new GoogleMapView("/html/maps.html");
        mapComponent.addMapInializedListener(this);
        tab.setContent(mapComponent);
    }

    @Override
    public void mapInitialized() {
        LatLong center = new LatLong(34.0219, -118.4814);
        MapOptions options = new MapOptions()
                .center(center)
                .mapType(MapTypeIdEnum.ROADMAP)
                //maybe set false
                .mapTypeControl(true)
                .overviewMapControl(false)
                .panControl(true)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoom(8)
                .zoomControl(true)
                .styleString(styleString); // TODO: 28.10.2017 add the theme to a menu
        //it returns control for created map
        map = mapComponent.createMap(options);

        // TODO: 28.10.2017 gets it from mouse
        String addressOrigin = "Los Angeles";
        String addressDestination = "Santa Barbara";
        DirectionsRequest directionsRequest = new DirectionsRequest(
                addressOrigin,
                addressDestination,
                TravelModes.DRIVING);

        directionsPane = mapComponent.getDirec();
        directionsService = new DirectionsService();
        directionsRenderer = new DirectionsRenderer(true, map, directionsPane);
        directionsService.getRoute(directionsRequest, this, directionsRenderer);
    }

    @Override
    public void directionsReceived(DirectionsResult results, DirectionStatus status) {
        if (status.equals(DirectionStatus.OK)) {
            mapComponent.getMap().showDirectionsPane();
            LOGGER.info("Directions was found");
            DirectionsResult e = results;
            GeocodingService gs = new GeocodingService();
            LOGGER.info("SIZE ROUTES: " + e.getRoutes().size() + "\n" + "ORIGIN: " + e.getRoutes().get(0).getLegs().get(0).getStartLocation());
//            gs.reverseGeocode(e.getRoutes().get(0).getLegs().get(0).getStartLocation().getLatitude(), e.getRoutes().get(0).getLegs().get(0).getStartLocation().getLongitude(), this);
            LOGGER.info("LEGS SIZE: " + e.getRoutes().get(0).getLegs().size());
            LOGGER.info("WAYPOINTS " + e.getGeocodedWaypoints().size());
            double d = 0;
            for (DirectionsLeg g : e.getRoutes().get(0).getLegs()) {
                d += g.getDistance().getValue();
                System.out.println("DISTANCE " + g.getDistance().getValue());
            }
            try {
                LOGGER.info("Distance total = " + e.getRoutes().get(0).getLegs().get(0).getDistance().getText());
            } catch (Exception ex) {
                LOGGER.error("ERROR: " + ex.getMessage());
            }

        }
    }

    @Override
    public void geocodedResultsReceived(GeocodingResult[] results, GeocoderStatus status) {
        if (status.equals(GeocoderStatus.OK)) {
            for (GeocodingResult e : results) {
                LOGGER.info(e.getVariableName());
                LOGGER.info("GEOCODE: " + e.getFormattedAddress() + "\n" + e.toString());
            }

        }
    }

    public void initButtons(final Button btn_clear_directions) {
        btn_clear_directions.setOnAction(action -> {
        });
    }
}
