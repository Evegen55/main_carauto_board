package controllers.settings;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author (created on 11/29/2017).
 */
public class PropertiesHelper {

    private final static Logger LOGGER = LoggerFactory.getLogger(PropertiesHelper.class);

    private static final Parameters PARAMETERS = new Parameters();

    private static final FileBasedConfigurationBuilder<FileBasedConfiguration> FILE_BASED_CONFIGURATION_BUILDER =
            new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                    .configure(PARAMETERS.properties()
                            .setFileName("settings.properties"));

    public static StyleList getStyleForMapFromProperties() {
        StyleList style = null;
        try {
            Configuration config = FILE_BASED_CONFIGURATION_BUILDER.getConfiguration();
            String styleName = config.getString("style");
            style = StyleList.valueOf(styleName.toUpperCase());
        } catch (ConfigurationException cex) {
            LOGGER.info("\n" + cex.getCause());
        }
        return style;
    }


}
