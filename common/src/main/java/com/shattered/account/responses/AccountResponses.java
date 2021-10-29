package com.shattered.account.responses;

/**
 * @author JTlr Frost 9/7/2019 : 3:36 AM
 */
public enum AccountResponses {

    /**
     * Represents the Successful Login Response
     */
    SUCCESSFUL,

    /**
     * Represents Unavailable System
     */
    ERR_SYSTEM_UNAVAILABLE,

    /**
     * Represents Error Loading Account
     */
    ERR_LOADING_ACCOUNT,

    /**
     * Represents Bad Session Request
     */
    ERR_MALFORMED_SESSION_REQUEST,

    /**
     * Represents Incorrect Build
     */
    ERR_INVALID_BUILD,

    /**
     * Represents Mac Address is Banned
     */
    ERR_MAC_INACCESSIBLE,

    /**
     * Represents Invalid Information Response
     */
    ACCOUNT_INVALID_INFORMATION,

    /**
     * Represents the 'Account not found' response
     */
    ACCOUNT_NOT_FOUND,

    /**
     * Represents Account Banned
     */
    ACCOUNT_INACCESSIBLE,

    /**
     * Represents a Locked Account
     */
    ACCOUNT_LOCKED,

    /**
     * Represents Active Session
     */
    ACCOUNT_ACTIVE_SESSION,

    /**
     * Represents Unable to find Character
     */
    UNABLE_LOAD_CHARACTER,

    /**
     * Represents the Server is currently Full
     */
    SERVER_FULL,

    /**
     * Represents a discord access token is invalid
     */
    INVALID_ACCESS_TOKEN,

    /**
     * Represents your discord account is not linked
     */
    DISCORD_NOT_LINKED,

    ;

    /**
     * Gets the Type for ID.
     * @param responseId
     * @return
     */
    public static AccountResponses forId(int responseId) {
        for (AccountResponses response : AccountResponses.values()) {
            if (response.ordinal() == responseId)
                return response;
        }
        return null;
    }

}
