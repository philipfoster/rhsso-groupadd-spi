package com.raytheon.sso.conf;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import org.junit.jupiter.api.Test;

class AppConfigurationTest {

    @Test
    void testFileLoaded() {
        AppConfiguration conf = new AppConfiguration();
        assertTrue(conf.unlistedRequiresManualApproval());
    }

    @Test
    void testPropertiesLoaded() {
        Properties props = new Properties();
        props.put("unlisted_manual_approval", false);

        AppConfiguration conf = new AppConfiguration(props);
        assertFalse(conf.unlistedRequiresManualApproval());
    }

}