package com.raytheon.sso;

import com.raytheon.sso.conf.AppConfiguration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

    private static final Logger logger = LoggerFactory.getLogger(GroupAssignmentEventListenerProvider.class);

    private final KeycloakSession session;
    private final RealmProvider model;
    private final GroupLookupService lookupService;
    private final AppConfiguration config;

    public GroupAssignmentEventListenerProvider(KeycloakSession session, GroupLookupService lookupService, AppConfiguration config) {
        this.session = session;
        this.model = session.realms();
        this.lookupService = lookupService;
        this.config = config;
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
        List<String> domainGroups = lookupService.getGroupsForEmailDomain(userEmail);
        List<String> tldGroups = lookupService.getGroupsForEmailTld(userEmail);
        
        List<String> allGroups = Stream.concat(domainGroups.stream(), tldGroups.stream())
            .distinct()
            .collect(Collectors.toList());

        if (allGroups.isEmpty() && config.unlistedRequiresManualApproval()) {
            // If the manual approval feature is enabled, and the user is not a member of any groups, disable their account.
            // TODO: Find some way to alert a system admin that a user needs to be approved (?)
            user.setEnabled(false);
        } else {
            for (String groupName : allGroups) {
                logger.info("Adding user {} ({}) to group {} per configuration", user.getId(), userEmail, groupName);
                GroupModel groupModel = KeycloakModelUtils.findGroupByPath(realm, groupName);
                user.joinGroup(groupModel);
            }
        }
    }


    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) { }


    @Override
    public void close() { }


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
