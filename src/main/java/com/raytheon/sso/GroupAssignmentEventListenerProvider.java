package com.raytheon.sso;

import com.raytheon.sso.conf.AppConfiguration;
import java.util.List;
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
            logger.debug("Received registration event {}", event.toString());
            onRegistrationEvent(event);
        } else {
            logger.trace("Received non-registration event {}", event.toString());
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
    public void onEvent(AdminEvent adminEvent, boolean b) {
        logger.trace("Received admin event {}", adminEvent.toString());
    }


    @Override
    public void close() { }

}
