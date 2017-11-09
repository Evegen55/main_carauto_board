package controllers;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.DirectionsPane;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.service.directions.DirectionStatus;
import com.lynden.gmapsfx.service.directions.DirectionsLeg;
import com.lynden.gmapsfx.service.directions.DirectionsRenderer;
import com.lynden.gmapsfx.service.directions.DirectionsRequest;
import com.lynden.gmapsfx.service.directions.DirectionsResult;
import com.lynden.gmapsfx.service.directions.DirectionsService;
import com.lynden.gmapsfx.service.directions.DirectionsServiceCallback;
import com.lynden.gmapsfx.service.directions.TravelModes;
import com.lynden.gmapsfx.service.geocoding.GeocoderStatus;
import com.lynden.gmapsfx.service.geocoding.GeocodingResult;
import com.lynden.gmapsfx.service.geocoding.GeocodingService;
import com.lynden.gmapsfx.service.geocoding.GeocodingServiceCallback;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.FlowPane;
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
            content = IOUtils.toString(this.getClass().getResourceAsStream("/css/grayMap.json"), "UTF-8");
        } catch (IOException e) {
            LOGGER.error(e.getCause().toString());
        }
        return content;
    }

    /**
     * @param tab
     * @param btn_clear_directions
     * @param btn_show_directions
     * @param btn_find_path
     * @param btn_clear_path
     */
    public void createSimpleMap(final Tab tab, final FlowPane flowPane,
                                final Button btn_clear_directions, final Button btn_show_directions,
                                final Button btn_find_path, final Button btn_clear_path) {
        //generates google map with some defaults and put it into top pane
        mapComponent = new GoogleMapView("/html/maps.html");
        mapComponent.addMapInializedListener(this);
        initControls(btn_clear_directions, btn_show_directions, btn_find_path, btn_clear_path);
        mapComponent.getChildren().add(flowPane);
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

    private void initControls(final Button btn_clear_directions, final Button btn_show_directions,
                              final Button btn_find_path, final Button btn_clear_path) {
        btn_clear_directions.setOnAction(action -> {
            // TODO: 10/31/2017 research it
//            directionsRenderer.clearDirections();
            directionsRenderer.getJSObject().eval("hideDirections()");
        });

        btn_show_directions.setOnAction(action -> {
            // TODO: 10/31/2017 research it
//            directionsRenderer.clearDirections();
            directionsRenderer.getJSObject().eval("showDirections()");
        });

        btn_find_path.setOnAction(action -> {
            // TODO: 11/9/2017
        });

        btn_clear_path.setOnAction(action -> {
            // TODO: 11/9/2017
        });
    }
}
