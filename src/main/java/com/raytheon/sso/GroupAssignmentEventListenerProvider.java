package com.raytheon.sso;

import java.util.List;
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

/**
 * This class will receive events from the Keycloak server, and automatically add a user to the correct group
 * based on their email domain.
 */
public class GroupAssignmentEventListenerProvider implements EventListenerProvider {

    private static final String GOV_GROUP_PATH = "GovUser";
    private static final String REALM_NAME = "test-app";
    private static final Logger logger = LoggerFactory.getLogger(GroupAssignmentEventListenerProvider.class);

    private final KeycloakSession session;
    private final RealmProvider model;
//    private final GroupModel govGroup;
    private RealmModel realm;
    private final GroupLookupService lookupService;

    public GroupAssignmentEventListenerProvider(KeycloakSession session, GroupLookupService lookupService) {
        this.session = session;
        this.model = session.realms();
        this.lookupService = lookupService;

        realm = session.realms().getRealm(REALM_NAME);
    }

    /**
     * This method is called when an event is triggered.
     * @param event the event
     */
    @Override
    public void onEvent(Event event) {
        if (event.getType() == EventType.REGISTER) {
            logger.debug("Received registration event {}", eventToString(event));
            onRegistrationEvent(event);
        } else {
            logger.trace("Received non-registration event {}", eventToString(event));
        }
    }

    /**
     * This method will add the groups when a registration event occurs
     * @param event the event
     */
    private void onRegistrationEvent(Event event) {
        RealmModel realm = model.getRealm(event.getRealmId());
        UserModel user = session.users().getUserById(event.getUserId(), realm);


        String userEmail = user.getEmail();
        List<String> groups = lookupService.getGroupsForEmailDomain(userEmail);

        for (String groupName : groups) {
            GroupModel groupModel = KeycloakModelUtils.findGroupByPath(realm, groupName);
            user.joinGroup(groupModel);
        }
    }


    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) { }


    @Override
    public void close() {
        session.close();
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
