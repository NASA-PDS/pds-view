package gov.nasa.pds.citool.commandline.options;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class ToolsPropertiesConfiguration extends PropertiesConfiguration {
    public ToolsPropertiesConfiguration(File file) throws ConfigurationException {
        super(file);
    }

    public boolean containsKey(ConfigKey configKey) {
        return containsKey(configKey.getKey());
    }

    public List<String> getList(ConfigKey configKey) {
        return getList(configKey.getKey());
    }

    public int getInt(ConfigKey configKey) {
        return getInt(configKey.getKey());
    }

    public String getString(ConfigKey configKey) {
        return getString(configKey.getKey());
    }

    public Boolean getBoolean(ConfigKey configKey) {
        return getBoolean(configKey.getKey());
    }

}
