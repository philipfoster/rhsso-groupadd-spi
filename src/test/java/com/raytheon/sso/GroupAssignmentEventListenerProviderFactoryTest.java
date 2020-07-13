package com.raytheon.sso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.raytheon.sso.conf.AppConfiguration;
import org.junit.jupiter.api.Test;
import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

class GroupAssignmentEventListenerProviderFactoryTest {

    @Test
    void testOrder() {
        GroupLookupService service = mock(GroupLookupService.class);
        AppConfiguration conf = mock(AppConfiguration.class);
        Scope scope = mock(Scope.class);
        KeycloakSessionFactory sessionFactory = mock(KeycloakSessionFactory.class);

        GroupAssignmentEventListenerProviderFactory factory = new GroupAssignmentEventListenerProviderFactory(service, conf);
        factory.init(scope);
        factory.postInit(sessionFactory);
        assertEquals(0, factory.order());
        factory.close();
    }

    @Test
    void testGetId() {
        GroupLookupService service = mock(GroupLookupService.class);
        AppConfiguration conf = mock(AppConfiguration.class);
        Scope scope = mock(Scope.class);
        KeycloakSessionFactory sessionFactory = mock(KeycloakSessionFactory.class);

        GroupAssignmentEventListenerProviderFactory factory = new GroupAssignmentEventListenerProviderFactory(service, conf);
        factory.init(scope);
        factory.postInit(sessionFactory);
        assertEquals("group-assignment-event-listener", factory.getId());
        factory.close();
    }

    @Test
    void testCreate() {
        GroupLookupService service = mock(GroupLookupService.class);
        AppConfiguration conf = mock(AppConfiguration.class);
        Scope scope = mock(Scope.class);
        KeycloakSessionFactory sessionFactory = mock(KeycloakSessionFactory.class);
        KeycloakSession session = mock(KeycloakSession.class);

        GroupAssignmentEventListenerProviderFactory factory = new GroupAssignmentEventListenerProviderFactory(service, conf);
        factory.init(scope);
        factory.postInit(sessionFactory);
        assertNotNull(factory.create(session));
        factory.close();
    }

    @Test
    void testNormalConstructor() {
        GroupAssignmentEventListenerProviderFactory factory = new GroupAssignmentEventListenerProviderFactory();
        assertNotNull(factory);
    }
}