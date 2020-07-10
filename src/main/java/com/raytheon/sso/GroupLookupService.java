package com.raytheon.sso;

import java.util.List;

/**
 * A GroupLookupService will look up SSO groups that a user should be added to based off of account details
 */
public interface GroupLookupService {

    /**
     * Get a list of groups for the requested email domain.
     * @param email the email address to lookup groups for
     * @return A list of SSO groups that the user should be added to
     */
    List<String> getGroupsForEmailDomain(String email);

    /**
     * Get a list of groups for the requested email TLD (Top level domain, such as .com, or .gov).
     * @param email the email address to lookup groups for
     * @return A list of SSO groups that the user should be added to
     */
    List<String> getGroupsForEmailTld(String email);

}
