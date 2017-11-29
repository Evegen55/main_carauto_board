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

    private static final String styleRetro = "/css/byGoogleDemoRetro.json";
    private static final String styleNight = "/css/byGoogleDemoNight.json";
    private static final String styleGray = "/css/grayMap.json";

    public String getStyleForMap(StyleList retro) {
        String content = null;
        try {
            switch (retro) {
                case RETRO:
                    content = IOUtils.toString(this.getClass().getResourceAsStream(styleRetro), "UTF-8");
                    LOGGER.info("Use " + styleRetro + " as stylesheet");
                    break;
                case GRAY:
                    content = IOUtils.toString(this.getClass().getResourceAsStream(styleGray), "UTF-8");
                    LOGGER.info("Use " + styleGray + " as stylesheet");
                    break;
                case NIGHT:
                    content = IOUtils.toString(this.getClass().getResourceAsStream(styleNight), "UTF-8");
                    LOGGER.info("Use " + styleNight + " as stylesheet");
                    break;
            }
        } catch (IOException e) {
            LOGGER.error(e.getCause().toString());
        }
        return content;
    }
}
