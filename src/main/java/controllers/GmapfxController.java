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
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.StyleHelper;
import utils.StyleList;

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

    // TODO: 11/23/2017 move it into global settings
    private StyleHelper styleHelper = new StyleHelper();
    private String styleString = styleHelper.getStyleForMap(StyleList.RETRO);

    /**
     * @param tab
     * @param btn_clear_directions
     * @param btn_show_directions
     * @param btn_find_path
     * @param btn_clear_path
     * @param path_choice_pane
     * @param btn_get_coords_find_path
     * @param txt_from
     * @param txt_to
     */
    public void createSimpleMap(final Tab tab, final FlowPane flowPane, final Button btn_clear_directions,
                                final Button btn_show_directions, final Button btn_find_path,
                                final Button btn_clear_path, final Pane path_choice_pane,
                                final Button btn_get_coords_find_path, final TextField txt_from,
                                final TextField txt_to) {
        //generates google map with some defaults and put it into top pane
        mapComponent = new GoogleMapView("/html/maps.html");
        mapComponent.addMapInializedListener(this);

        initControls(btn_clear_directions, btn_show_directions, btn_find_path, btn_clear_path, path_choice_pane,
                btn_get_coords_find_path, txt_from, txt_to);
        mapComponent.getChildren().addAll(flowPane, path_choice_pane);

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
                              final Button btn_find_path, final Button btn_clear_path, final Pane path_choice_pane,
                              final Button btn_get_coords_find_path, final TextField txt_from, final TextField txt_to) {
        //first thing to do
        path_choice_pane.setVisible(false);
        btn_get_coords_find_path.setVisible(false);

        btn_clear_directions.setOnAction(action -> {
//            directionsRenderer.getJSObject().eval("hideDirections()");
            //or - in order to avoid check dir renderer for null
            map.hideDirectionsPane();
        });

        btn_show_directions.setOnAction(action -> {
            map.showDirectionsPane();
        });

        //clear directions
        //clear route
        //clear texts - text fields always with ""
        //show pane with path choice
        btn_find_path.setOnAction(action -> {
            clearPath();
            txt_from.clear();
            txt_to.clear();
            path_choice_pane.setVisible(true);
            btn_get_coords_find_path.setVisible(true);

            //text fields are always not null but "" because we invoked clear()
            btn_get_coords_find_path.setOnAction(btn_action ->
                    getCoordinatesCalculatePathShowDirectionsAndHidePanel(path_choice_pane, txt_from, txt_to, btn_get_coords_find_path));
        });

        btn_clear_path.setOnAction(action -> clearPath());
    }

    private void clearPath() {
        if (directionsRenderer != null) {
            directionsRenderer.clearDirections();
        }
        map.hideDirectionsPane();
    }

    // TODO: 11/23/2017 pass Travel mode from UI
    private void getCoordinatesCalculatePathShowDirectionsAndHidePanel(final Pane path_choice_pane,
                                                                       final TextField txt_from, final TextField txt_to,
                                                                       final Button btn_get_coords_find_path) {
        final String addressOrigin = txt_from.getText();
        final String addressDestination = txt_to.getText();

        final DirectionsRequest directionsRequest =
                new DirectionsRequest(addressOrigin, addressDestination, TravelModes.DRIVING);

        directionsPane = mapComponent.getDirec(); // TODO: 11/10/2017 IT HAS TO BE CLEARED!
        directionsService = new DirectionsService();
        directionsRenderer = new DirectionsRenderer(true, map, directionsPane);
        directionsService.getRoute(directionsRequest, this, directionsRenderer);

        if (!addressOrigin.equals("") || !addressDestination.equals("")) {
            path_choice_pane.setVisible(false);
            btn_get_coords_find_path.setVisible(false);
        }
    }
}
