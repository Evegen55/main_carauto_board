package controllers.settings;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author (created on 11/23/2017).
 */
public class StyleHelper {

    private final static Logger LOGGER = LoggerFactory.getLogger(StyleHelper.class);

    private static final String STYLE_RETRO = "/css/byGoogleDemoRetro.json";
    private static final String STYLE_NIGHT = "/css/byGoogleDemoNight.json";
    private static final String STYLE_GRAY = "/css/grayMap.json";

    public String getStyleForMap(StyleList styleList) {
        String content = null;
        try {
            switch (styleList) {
                case RETRO:
                    content = IOUtils.toString(this.getClass().getResourceAsStream(STYLE_RETRO), "UTF-8");
                    LOGGER.info("Use " + STYLE_RETRO + " as stylesheet for maps");
                    break;
                case GRAY:
                    content = IOUtils.toString(this.getClass().getResourceAsStream(STYLE_GRAY), "UTF-8");
                    LOGGER.info("Use " + STYLE_GRAY + " as stylesheet");
                    break;
                case NIGHT:
                    content = IOUtils.toString(this.getClass().getResourceAsStream(STYLE_NIGHT), "UTF-8");
                    LOGGER.info("Use " + STYLE_NIGHT + " as stylesheet");
                    break;
            }
        } catch (IOException e) {
            LOGGER.error(e.getCause().toString());
        }
        return content;
    }
}
