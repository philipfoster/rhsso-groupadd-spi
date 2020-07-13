package com.raytheon.sso;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

class GroupAssignmentEventListenerProviderTest {


    @Test
    public void testManualApproval() {

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

        provider.onEvent(inputEvent);

        // validate that the user account is disabled
        verify(userModel, times(1)).setEnabled(false);

        // verify that user is not added to any groups
        verify(userModel,  times(0)).joinGroup(any());

    }

}