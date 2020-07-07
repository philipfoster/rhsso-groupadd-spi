package com.raytheon.sso;

import org.keycloak.Config.Scope;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;


/**
 * This class is used by the server to get a {@link GroupAssignmentEventListenerProvider} instance.
 * It is registered by the {@code src\main\resources\META-INF\services\org.keycloak.events.EventListenerProviderFactory} file
 */
public class GroupAssignmentEventListenerProviderFactory implements EventListenerProviderFactory {

    private static final String EVENT_LISTENER_NAME = "group-assignment-event-listener";

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new GroupAssignmentEventListenerProvider(keycloakSession);
    }

    @Override
    public void init(Scope scope) { }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) { }

    @Override
    public void close() { }

    /**
     * Defines the event listener name for the server.
     */
    @Override
    public String getId() {
        return EVENT_LISTENER_NAME;
    }

    @Override
    public int order() {
        return 0;
    }
}
