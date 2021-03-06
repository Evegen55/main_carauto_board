package controllers.settings;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author (created on 11/29/2017).
 */
class PropertiesHelper2 {

    private final static Logger LOGGER = LoggerFactory.getLogger(PropertiesHelper.class);

    String getnUmodifiebleProperties() {
        LOGGER.info("Retrieve a content of properties from the JAR");
        String content = null;
        try {
            content = IOUtils.toString(this.getClass()
                    .getResourceAsStream("/" + PropertiesHelper.BASIC_PROPERTIES), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
}
