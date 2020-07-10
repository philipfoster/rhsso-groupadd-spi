package com.raytheon.sso.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfiguration {

    private static final String PROPERTIES_FILE_NAME = "config.properties";
    private static final String UNLISTED_APPROVAL_PROP = "unlisted_manual_approval";
    private Properties config;

    public AppConfiguration() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            config = new Properties();
            config.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read configuration", e);
        }
    }

    public AppConfiguration(Properties config) {
        this.config = config;
    }

    /**
     * If this method returns {@code true}, any user with an email domain that is not in the whitelist will have their account locked until manually
     * approved by a system administrator. If this method returns {@code false}, then this feature will be disable.d
     */
    public boolean unlistedRequiresManualApproval() {
        String rawValue = config.getProperty(UNLISTED_APPROVAL_PROP);
        return Boolean.parseBoolean(rawValue);
    }
}
