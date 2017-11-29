package controllers.settings;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The order of fields and methods is important
 *
 * External file is used to memorise settings.
 * So when application starts at first time, the external file doesn't exist.
 * Application creates file and populate it with default settings (retrieved from
 * main/java/resources file. Then for the next time it always checks existence
 * of a file. If file exists - application works with external file.
 * All this stuff just because an application CAN'T change a file inside .jar
 *
 * @author (created on 11/29/2017).
 */
public class PropertiesHelper {

    private final static Logger LOGGER = LoggerFactory.getLogger(PropertiesHelper.class);

    private static final Parameters PARAMETERS = new Parameters();

    static final String BASIC_PROPERTIES = "settings.properties";

    private static final File PROPERTY_FILE = createAnExternalFileorUseExisted();

    private static File createAnExternalFileorUseExisted() {
        LOGGER.info("Creating an external file with settings if id does NOT exist");
        final File settingsModifiable = new File(BASIC_PROPERTIES);
        if(!settingsModifiable.exists()) {
            final PropertiesHelper2 propertiesHelper2 = new PropertiesHelper2();
            try {
                final FileWriter fileWriter = new FileWriter(settingsModifiable);
                fileWriter.write(propertiesHelper2.getnUmodifiebleProperties());
                fileWriter.flush();
                fileWriter.close();
                LOGGER.info("An external file with settings has been created successfully");
            } catch (IOException e) {
                LOGGER.error("Smth went wrong");
                e.printStackTrace();
            }
        }
        LOGGER.info("An external file with settings exists already");
        return settingsModifiable;
    }

    private static final FileBasedConfigurationBuilder<FileBasedConfiguration> FILE_BASED_CONFIGURATION_BUILDER =
            new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                    .configure(PARAMETERS.properties()
                            .setFileName(PROPERTY_FILE.getPath()));

    /**
     *
     * @return
     */
    public static StyleList getStyleForMapFromProperties() {
        StyleList style = null;
        try {
            final Configuration config = FILE_BASED_CONFIGURATION_BUILDER.getConfiguration();
            final String styleName = config.getString("style");
            style = StyleList.valueOf(styleName.toUpperCase());
        } catch (ConfigurationException cex) {
            LOGGER.error("\n" + cex.getCause());
            cex.printStackTrace();
        }
        return style;
    }

    /**
     *
     * @param style
     */
    public static void setStyleForMapIntoProperties(final String style) {
        try {
            Configuration config = FILE_BASED_CONFIGURATION_BUILDER.getConfiguration();
            config.setProperty("style", style);
            FILE_BASED_CONFIGURATION_BUILDER.save();
        } catch (ConfigurationException cex) {
            LOGGER.error("\n" + cex.getCause());
            cex.printStackTrace();
        }
    }


}
