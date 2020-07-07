package com.raytheon.sso;

import java.util.Map;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GroupAssignmentEventListenerProvider implements EventListenerProvider {

    private static final String GOV_GROUP_PATH = "GovUser";
    private static final String REALM_NAME = "test-app";
    private static final Logger logger = LoggerFactory.getLogger(GroupAssignmentEventListenerProvider.class);

    private final KeycloakSession session;
    private final RealmProvider model;
    private final GroupModel govGroup;

    public GroupAssignmentEventListenerProvider(KeycloakSession session) {
        this.session = session;
        this.model = session.realms();

        RealmModel realm = session.realms().getRealm(REALM_NAME);
        this.govGroup = KeycloakModelUtils.findGroupByPath(realm, GOV_GROUP_PATH);
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType() == EventType.REGISTER) {
            logger.debug("Received registration event {}", eventToString(event));
            onRegistrationEvent(event);
        } else {
            logger.trace("Received non-registration event {}", eventToString(event));
        }
    }

    private void onRegistrationEvent(Event event) {
        RealmModel realm = model.getRealm(event.getRealmId());
        UserModel user = session.users().getUserById(event.getUserId(), realm);


        String userEmail = user.getEmail();
        if (userEmail.endsWith(".gov") || userEmail.endsWith(".mil")) {
            logger.info("Granting gov group access to new user {}", user.getEmail());
            user.joinGroup(govGroup);
        }
    }


    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
    }


    @Override
    public void close() {
    }


    private String eventToString(Event event) {
        StringBuilder sb = new StringBuilder();

        sb.append("type=");
        sb.append(event.getType());
        sb.append(", realmId=");
        sb.append(event.getRealmId());
        sb.append(", clientId=");
        sb.append(event.getClientId());
        sb.append(", userId=");
        sb.append(event.getUserId());
        sb.append(", ipAddress=");
        sb.append(event.getIpAddress());

        if (event.getError() != null) {
            sb.append(", error=");
            sb.append(event.getError());
        }

        if (event.getDetails() != null) {
            for (Map.Entry<String, String> e : event.getDetails().entrySet()) {
                sb.append(", ");
                sb.append(e.getKey());
                if (e.getValue() == null || e.getValue().indexOf(' ') == -1) {
                    sb.append("=");
                    sb.append(e.getValue());
                } else {
                    sb.append("='");
                    sb.append(e.getValue());
                    sb.append("'");
                }
            }
        }

        return sb.toString();
    }
}
