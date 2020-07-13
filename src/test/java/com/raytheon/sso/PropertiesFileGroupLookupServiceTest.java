package com.raytheon.sso;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class PropertiesFileGroupLookupServiceTest {

    private static final String[] TYPE_HOLDER = new String[0];

    @Test
    void getGroupsForEmail() {
        Properties props = new Properties();
        props.put("website.gov", "Group1 ");
        props.put("website.mil", " Group1,  Group2 ");

        GroupLookupService svc = new PropertiesFileGroupLookupService(props);
        List<String> govDomain = svc.getGroupsForEmailDomain("john.doe@website.gov");
        List<String> milDomain = svc.getGroupsForEmailDomain("john.doe@website.mil");
        List<String> unlistedDomain = svc.getGroupsForEmailDomain("john.doe@gmail.com");

        assertArrayEquals(new String[] {"Group1"}, govDomain.toArray(TYPE_HOLDER));
        assertArrayEquals(new String[] {"Group1", "Group2"}, milDomain.toArray(TYPE_HOLDER));
        assertArrayEquals(new String[0], unlistedDomain.toArray(TYPE_HOLDER));
        assertThrows(IllegalArgumentException.class, () -> svc.getGroupsForEmailDomain("not an email address"));
    }

    @Test
    void getGroupsForTld() {
        Properties props = new Properties();
        props.put(".gov", "Group1 ");

        GroupLookupService svc = new PropertiesFileGroupLookupService(props);
        List<String> govDomain = svc.getGroupsForEmailTld("john.doe@website.gov");

        assertArrayEquals(new String[] {"Group1"}, govDomain.toArray(TYPE_HOLDER));
        assertThrows(IllegalArgumentException.class, () -> svc.getGroupsForEmailTld("not an email address"));

    }

    @Test
    void fileLoad() {
        GroupLookupService svc = new PropertiesFileGroupLookupService();
        List<String> govDomain = svc.getGroupsForEmailDomain("john.doe@website.gov");

        assertArrayEquals(new String[] {"GovGroup"}, govDomain.toArray(TYPE_HOLDER));
    }
}