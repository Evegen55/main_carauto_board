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
 * @author (created on 11/29/2017).
 */
public class PropertiesHelper {

    private final static Logger LOGGER = LoggerFactory.getLogger(PropertiesHelper.class);

    private static final Parameters PARAMETERS = new Parameters();

    static final String basicProperties = "settings.properties";

    private static final File PROPERTY_FILE = createAnExternalFile();

    private static File createAnExternalFile() {
        LOGGER.info("Creating an external file with settings if id does NOT exist");
        final File settingsModifieble = new File(basicProperties);
        if(!settingsModifieble.exists()) {
            final PropertiesHelper2 propertiesHelper2 = new PropertiesHelper2();
            try {
                FileWriter fileWriter = new FileWriter(settingsModifieble);
                fileWriter.write(propertiesHelper2.getnUmodifiebleProperties());
                fileWriter.flush();
                fileWriter.close();
                LOGGER.info("An external file with settings has been created successfully");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info("An external file with settings exists already");
        return settingsModifieble;
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
            Configuration config = FILE_BASED_CONFIGURATION_BUILDER.getConfiguration();
            String styleName = config.getString("style");
            style = StyleList.valueOf(styleName.toUpperCase());
        } catch (ConfigurationException cex) {
            LOGGER.info("\n" + cex.getCause());
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
            LOGGER.info("\n" + cex.getCause());
            cex.printStackTrace();
        }
    }


}
