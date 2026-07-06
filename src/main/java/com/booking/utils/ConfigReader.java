package com.booking.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static final String ENV_PREFIX = "BOOKING_";
    private static final Properties properties = loadProperties();

    private static Properties loadProperties() {
        String environment = System.getProperty("env", "qa").toLowerCase().trim();
        String configFilePath = "src/test/resources/" + environment + ".properties";

        try (FileInputStream fileInputStream = new FileInputStream(configFilePath)) {
            Properties props = new Properties();
            props.load(fileInputStream);
            return props;
        } catch (IOException e) {
            throw new RuntimeException(
                "Framework Initialization Failed: Could not load properties file: " + configFilePath, e);
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
        String bookingKey = ENV_PREFIX + key.toUpperCase();

        String value = firstNonBlank(
            System.getenv(bookingKey),
            System.getProperty(bookingKey),
            System.getProperty(key),
            properties.getProperty(key)
        );

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

    private static String firstNonBlank(String... candidates) {
        for (String candidate : candidates) {
            if (candidate != null && !candidate.isBlank()) {
                return candidate;
            }
        }
        return null;
    }
}
