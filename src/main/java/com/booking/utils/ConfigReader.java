package com.booking.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    // Properties object to hold key-value configurations
    private static Properties properties;

    // Static block ensures configuration files load exactly once when the framework starts
    static {
        try {
            // Read environment flag from system variables. Defaults to "qa" if null.
            String environment = System.getProperty("env", "qa").toLowerCase().trim();
            String configFilePath = "src/test/resources/" + environment + ".properties";

            FileInputStream fileInputStream = new FileInputStream(configFilePath);
            properties = new Properties();
            properties.load(fileInputStream);
            fileInputStream.close();

        } catch (IOException e) {
            throw new RuntimeException("Framework Initialization Failed: Could not load properties file. " + e.getMessage());
        }
    }

    /**
     * Retrieves the string configuration value corresponding to the specified key.
     * @param key The property name configured inside your .properties files
     * @return The string configuration value, or null if key does not exist
     */
    public static String getProperty(String key) {
        if (properties == null) {
            throw new IllegalStateException("Properties matrix was not initialized correctly.");
        }
        String value = properties.getProperty(key);
        return (value != null) ? value.trim() : null;
    }

    /**
     * Overloaded helper utility to fetch integer parameters directly (like timeouts or wait limits).
     * @param key The property name configured inside your .properties files
     * @return The parsed integer value, or a provided fallback default value
     */
    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        return (value != null) ? Integer.parseInt(value) : defaultValue;
    }
}
