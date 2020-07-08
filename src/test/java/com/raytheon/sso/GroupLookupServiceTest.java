package com.raytheon.sso;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class GroupLookupServiceTest {

    private static final String[] TYPE_HOLDER = new String[0];

    @Test
    void getGroupsForEmail() {
        Properties props = new Properties();
        props.put("website.gov", "Group1 ");
        props.put("website.mil", " Group1,  Group2 ");

        GroupLookupService svc = new PropertiesFileGroupLookupService(props);
        List<String> govDomain = svc.getGroupsForEmailDomain("john.doe@website.gov");
        List<String> milDomain = svc.getGroupsForEmailDomain("john.doe@website.mil");

        assertArrayEquals(new String[] {"Group1"}, govDomain.toArray(TYPE_HOLDER));
        assertArrayEquals(new String[] {"Group1", "Group2"}, milDomain.toArray(TYPE_HOLDER));
        assertThrows(IllegalArgumentException.class, () -> svc.getGroupsForEmailDomain("not an email address"));
    }
}