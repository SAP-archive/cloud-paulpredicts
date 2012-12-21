package com.sap.pto.util.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.pto.util.configuration.ConfigSectionParam.Type;

public class ConfigUtil {
    private static final Logger confLogger = LoggerFactory.getLogger("audit.configuration");
    private static final String TEMP_GROUP = "temp_runtime";

    private static Map<String, Properties> allProperties = new HashMap<String, Properties>();
    private static List<Fallback> fallbacks = Arrays.asList(Fallback.DB, Fallback.SYSTEM, Fallback.FILE);
    private static List<ConfigSection> configSections = new ArrayList<ConfigSection>();
    private static ConfigAdapter dbAdapter;

    public enum Fallback {
        DB, FILE, SYSTEM
    }

    public static void setDBAdapter(ConfigAdapter dbAdapter) {
        ConfigUtil.dbAdapter = dbAdapter;
    }

    public synchronized static String getProperty(String group, String key) {
        Properties properties = getProperties(group);

        if (properties != null) {
            return properties.getProperty(key);
        } else {
            return null;
        }
    }

    public synchronized static Boolean getBooleanProperty(String group, String key) {
        return Boolean.parseBoolean(getProperty(group, key));
    }

    public synchronized static int getIntProperty(String group, String key) {
        return Integer.parseInt(getProperty(group, key));
    }

    public synchronized static Properties getProperties(String group) {
        // load properties on demand
        if (!allProperties.containsKey(group)) {
            for (Fallback fallback : fallbacks) {
                confLogger.info("Loading property group '" + group + "' from " + fallback.name());

                Properties properties = new Properties();

                switch (fallback) {
                case DB:
                    properties = loadFromDatabase(group);
                    break;
                case FILE:
                    properties = loadFromPropertiesFile(group);
                    break;
                case SYSTEM:
                    properties = loadFromSystemProperties(group);
                    break;
                default:
                    // confLogger.info(" Unknown fallback: " + fallback);
                }

                confLogger.info(" " + properties.size() + " properties loaded");
                saveProperties(group, properties);
            }
        }

        if (allProperties.containsKey(group)) {
            return allProperties.get(group);
        } else {
            return null;
        }
    }

    public static void setProperties(String group, Properties properties) {
        allProperties.put(group, properties);
    }

    public static void setProperty(String group, String key, String value) {
        Properties existing = allProperties.get(group);
        if (existing == null) {
            existing = getProperties(group);
            if (existing == null) {
                existing = new Properties();
            }
        }
        existing.put(key, value);
        allProperties.put(group, existing);
    }

    private static Properties loadFromPropertiesFile(String group) {
        Properties properties = new Properties();
        int fileCounter = 1;

        String file = group + ".properties";
        InputStream stream = null;
        do {
            stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);

            if (stream != null) {
                try {
                    properties.load(stream);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read configuration from resource at '" + file + "'.", e);
                } finally {
                    IOUtils.closeQuietly(stream);
                }
            }

            fileCounter += 1;
            file = group + "." + fileCounter + ".properties";
        } while (stream != null);

        return properties;
    }

    private static Properties loadFromSystemProperties(String group) {
        Properties groupProperties = new Properties();
        Properties properties = System.getProperties();

        String prefix = group + ".";

        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                String value = properties.getProperty(key);

                groupProperties.put(key.substring(prefix.length()), value);
            }
        }

        return groupProperties;
    }

    private static Properties loadFromDatabase(String group) {
        if (dbAdapter != null) {
            return dbAdapter.getByGroup(group);
        } else {
            confLogger.error("Cannot load configuration from database. DB Adapter is not set.");
            return new Properties();
        }
    }

    private static void saveProperties(String group, Properties properties) {
        if (properties.size() == 0) {
            return;
        }

        if (allProperties.containsKey(group)) {
            Properties existingProperties = allProperties.get(group);

            // save only new ones to obey fallback chain
            for (String key : properties.stringPropertyNames()) {
                if (!existingProperties.containsKey(key)) {
                    existingProperties.put(key, properties.getProperty(key));
                }
            }
        } else {
            allProperties.put(group, properties);
        }
    }

    public static void setTempProperty(String key, String value) {
        Properties properties = new Properties();
        if (allProperties.containsKey(TEMP_GROUP)) {
            properties = allProperties.get(TEMP_GROUP);
        }
        properties.put(key, value);
        allProperties.put(TEMP_GROUP, properties);
    }

    public static String getTempProperty(String key) {
        return getProperty(TEMP_GROUP, key);
    }

    public static List<Fallback> getFallbacks() {
        return fallbacks;
    }

    public static void setFallbacks(List<Fallback> newFallbacks) {
        fallbacks = newFallbacks;
    }

    public static void reload() {
        confLogger.info("Reloading configuration");
        allProperties.clear();
    }

    public static Map<String, Properties> getAllProperties() {
        return allProperties;
    }

    public static List<ConfigSection> getConfigSections() {
        return getConfigSections(false);
    }

    public static List<ConfigSection> getConfigSections(boolean fillCurrentValues) {
        for (ConfigSection section : configSections) {
            for (ConfigSectionParam param : section.getParameters()) {
                if (fillCurrentValues && !param.getType().equals(Type.PASSWORD)) {
                    param.setCurrentValue(getProperty(section.getKey(), param.getKey()));
                } else {
                    param.setCurrentValue(null);
                }
            }
        }
        return configSections;
    }

    public static void setConfigSections(List<ConfigSection> configSections) {
        ConfigUtil.configSections = configSections;
    }

    public static void addConfigSection(ConfigSection config) {
        configSections.add(config);
    }

    public static void clearConfigSections() {
        configSections.clear();
    }

    /**
     * Saves the configuration into the database.
     */
    public static boolean saveConfiguration(ConfigSection section) {
        if (dbAdapter == null) {
            confLogger.error("Cannot save configuration. DB Adapter is not set.");
            return false;
        }

        for (ConfigSectionParam param : section.getParameters()) {
            dbAdapter.save(section.getKey(), param.getKey(), param.getCurrentValue());
        }

        return true;
    }
}
