package com.raytheon.sso;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import com.raytheon.sso.conf.AppConfiguration;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.keycloak.events.Event;
import org.keycloak.events.EventType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GroupAssignmentEventListenerProviderTest {

    private static final Logger logger = LoggerFactory.getLogger(GroupAssignmentEventListenerProvider.class);


    @Test
    public void testManualApprovalEnabledNoGroups() {

        // Setup mocked objects
        KeycloakSession session = mock(KeycloakSession.class);
        GroupLookupService lookupService = mock(GroupLookupService.class);
        AppConfiguration config = mock(AppConfiguration.class);
        RealmProvider model = mock(RealmProvider.class);
        RealmModel realmModel = mock(RealmModel.class);
        UserModel userModel = mock(UserModel.class);
        UserProvider sessionUsers = mock(UserProvider.class);

        when(config.unlistedRequiresManualApproval()).thenReturn(true);
        when(session.realms()).thenReturn(model);
        when(session.users()).thenReturn(sessionUsers);

        // Setup return values for internal calls
        when(model.getRealm(anyString())).thenReturn(realmModel);
        when(sessionUsers.getUserById(anyString(), any())).thenReturn(userModel);
        when(userModel.getEmail()).thenReturn("john.doe@website.com");
        when(lookupService.getGroupsForEmailDomain(anyString())).thenReturn(new ArrayList<>());
        when(lookupService.getGroupsForEmailTld(anyString())).thenReturn(new ArrayList<>());

        // create object to test
        GroupAssignmentEventListenerProvider provider = new GroupAssignmentEventListenerProvider(session, lookupService, config);
        Event inputEvent = new Event();
        inputEvent.setType(EventType.REGISTER);
        inputEvent.setRealmId("abc");
        inputEvent.setUserId("123");

        // invoke method to test
        provider.onEvent(inputEvent);

        // validate that the user account is disabled
        verify(userModel, times(1)).setEnabled(false);

        // verify that user is not added to any groups
        verify(userModel, times(0)).joinGroup(any());

    }

    @Test
    public void testManualApprovalDisabledNoGroups() {

        // Setup mocked objects
        KeycloakSession session = mock(KeycloakSession.class);
        GroupLookupService lookupService = mock(GroupLookupService.class);
        AppConfiguration config = mock(AppConfiguration.class);
        RealmProvider model = mock(RealmProvider.class);
        RealmModel realmModel = mock(RealmModel.class);
        UserModel userModel = mock(UserModel.class);
        UserProvider sessionUsers = mock(UserProvider.class);

        when(config.unlistedRequiresManualApproval()).thenReturn(false);
        when(session.realms()).thenReturn(model);
        when(session.users()).thenReturn(sessionUsers);


        // Setup return values for internal calls
        when(model.getRealm(anyString())).thenReturn(realmModel);
        when(sessionUsers.getUserById(anyString(), any())).thenReturn(userModel);
        when(userModel.getEmail()).thenReturn("john.doe@website.com");
        when(lookupService.getGroupsForEmailDomain(anyString())).thenReturn(new ArrayList<>());
        when(lookupService.getGroupsForEmailTld(anyString())).thenReturn(new ArrayList<>());


        // create object to test
        GroupAssignmentEventListenerProvider provider = new GroupAssignmentEventListenerProvider(session, lookupService, config);
        Event inputEvent = new Event();
        inputEvent.setType(EventType.REGISTER);
        inputEvent.setRealmId("abc");
        inputEvent.setUserId("123");

        // invoke method to test
        provider.onEvent(inputEvent);

        /// validate that the user account is not disabled
        verify(userModel, times(0)).setEnabled(anyBoolean());

        // verify that user is not added to any groups
        verify(userModel,  times(0)).joinGroup(any());

    }


    @Test
    public void testGroupsAddedAndMergedVerificationEnabled() {

        // Setup mocked objects
        KeycloakSession session = mock(KeycloakSession.class);
        GroupLookupService lookupService = mock(GroupLookupService.class);
        AppConfiguration config = mock(AppConfiguration.class);
        RealmProvider model = mock(RealmProvider.class);
        RealmModel realmModel = mock(RealmModel.class);
        UserModel userModel = mock(UserModel.class);
        UserProvider sessionUsers = mock(UserProvider.class);

        when(config.unlistedRequiresManualApproval()).thenReturn(true);
        when(session.realms()).thenReturn(model);
        when(session.users()).thenReturn(sessionUsers);


        // Setup return values for internal calls
        when(model.getRealm(anyString())).thenReturn(realmModel);
        when(sessionUsers.getUserById(anyString(), any())).thenReturn(userModel);
        when(userModel.getEmail()).thenReturn("john.doe@website.com");
        when(lookupService.getGroupsForEmailDomain(anyString())).thenReturn(Lists.newArrayList("Group1"));
        when(lookupService.getGroupsForEmailTld(anyString())).thenReturn(Lists.newArrayList("Group1", "Group2"));


        // create object to test
        GroupAssignmentEventListenerProvider provider = new GroupAssignmentEventListenerProvider(session, lookupService, config);
        Event inputEvent = new Event();
        inputEvent.setType(EventType.REGISTER);
        inputEvent.setRealmId("abc");
        inputEvent.setUserId("123");

        // invoke method to test
        provider.onEvent(inputEvent);

        // validate that the user account is not disabled
        verify(userModel, times(0)).setEnabled(anyBoolean());

        // verify that user is added to 2 groups
        verify(userModel, times(2)).joinGroup(any());

    }


    @Test
    public void testGroupsAddedAndMergedVerificationDisabled() {

        // Setup mocked objects
        KeycloakSession session = mock(KeycloakSession.class);
        GroupLookupService lookupService = mock(GroupLookupService.class);
        AppConfiguration config = mock(AppConfiguration.class);
        RealmProvider model = mock(RealmProvider.class);
        RealmModel realmModel = mock(RealmModel.class);
        UserModel userModel = mock(UserModel.class);
        UserProvider sessionUsers = mock(UserProvider.class);

        when(config.unlistedRequiresManualApproval()).thenReturn(false);
        when(session.realms()).thenReturn(model);
        when(session.users()).thenReturn(sessionUsers);


        // Setup return values for internal calls
        when(model.getRealm(anyString())).thenReturn(realmModel);
        when(sessionUsers.getUserById(anyString(), any())).thenReturn(userModel);
        when(userModel.getEmail()).thenReturn("john.doe@website.com");
        when(lookupService.getGroupsForEmailDomain(anyString())).thenReturn(Lists.newArrayList("Group1"));
        when(lookupService.getGroupsForEmailTld(anyString())).thenReturn(Lists.newArrayList("Group1", "Group2"));


        // create object to test
        GroupAssignmentEventListenerProvider provider = new GroupAssignmentEventListenerProvider(session, lookupService, config);
        Event inputEvent = new Event();
        inputEvent.setType(EventType.REGISTER);
        inputEvent.setRealmId("abc");
        inputEvent.setUserId("123");

        // invoke method to test
        provider.onEvent(inputEvent);

        // validate that the user account is not disabled
        verify(userModel, times(0)).setEnabled(anyBoolean());

        // verify that user is added to 2 groups
        verify(userModel, times(2)).joinGroup(any());

    }


}