package com.raytheon.sso;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A specialization of {@link GroupLookupService} that loads properties from a {@code .properties} file or {@link Properties} object
 */
public class PropertiesFileGroupLookupService implements GroupLookupService {


    private static final String PROPERTIES_FILE_NAME = "group-lookup.properties";
    private static final String EMAIL_REGEX = "^(.+)@(.+)\\.(.+)$";
    private static final Logger LOG = LoggerFactory.getLogger(GroupLookupService.class);

    private Properties config;

    public PropertiesFileGroupLookupService() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            config = new Properties();
            config.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read configuration", e);
        }
    }

    /**
     * This constructor allows clients to specify a properties file instead of reading the file. This is useful for unit-testing.
     * @param config the properties config.
     */
    public PropertiesFileGroupLookupService(Properties config) {
        this.config = config;
    }


    @Override
    public List<String> getGroupsForEmailDomain(String email) {
        if (!email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Illegal email address " + email);
        }

        int atChar = email.indexOf('@');
        String domain = email.substring(atChar + 1);

        String rawGroups = config.getProperty(domain);
        if (rawGroups == null) {
            return Collections.emptyList();
        }

        List<String> groups = Arrays.asList(rawGroups.split(","));
        groups.replaceAll(String::trim);

        return groups;
    }

    @Override
    public List<String> getGroupsForEmailTld(String email) {
        if (!email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Illegal email address " + email);
        }

        int atIdx = email.indexOf('@');
        String domain = email.substring(atIdx + 1);
        int dotIdx = domain.indexOf('.');
        String tld = domain.substring(dotIdx);

        String rawGroups = config.getProperty(tld);
        if (rawGroups == null) {
            return Collections.emptyList();
        }

        List<String> groups = Arrays.asList(rawGroups.split(","));
        groups.replaceAll(String::trim);

        return groups;
    }

}
