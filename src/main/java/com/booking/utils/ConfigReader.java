package com.booking.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    // Properties object to hold key-value configurations
    private static final Properties properties;

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
     * <p>
     * Resolution order:
     * <ol>
     *   <li>Environment variable prefixed with BOOKING_ (e.g. BOOKING_USERNAME)</li>
     *   <li>Java system property prefixed with BOOKING_ (e.g. -DBOOKING_USERNAME=...)</li>
     *   <li>Java system property (e.g. -Dusername=...)</li>
     *   <li>Value from the loaded properties file</li>
     * </ol>
     *
     * @param key The property name configured inside your .properties files
     * @return The string configuration value, or null if key does not exist
     */
    public static String getProperty(String key) {
        if (properties == null) {
            throw new IllegalStateException("Properties matrix was not initialized correctly.");
        }

        String bookingKey = "BOOKING_" + key.toUpperCase();

        // 1. Environment variable with BOOKING_ prefix
        String envValue = System.getenv(bookingKey);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue.trim();
        }

        // 2. Java system property with BOOKING_ prefix
        String bookingSystemValue = System.getProperty(bookingKey);
        if (bookingSystemValue != null && !bookingSystemValue.isEmpty()) {
            return bookingSystemValue.trim();
        }

        // 3. Java system property
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isEmpty()) {
            return systemValue.trim();
        }

        // 4. Properties file fallback
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
