package com.shattered.account.components.friend;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author JTlr Frost 2/1/2020 : 5:23 PM
 */
@RequiredArgsConstructor
public class FriendInformation {

    /**
     * Represents their account uuid
     */
    @Getter
    private final int uuid;

    /**
     * Represents their server connection uuid
     */
    @Getter
    private final String connectionUuid;

    /**
     * Represents their name
     */
    @Getter
    private final String name;

    /**
     * Represents the Location they're at.
     */
    @Getter
    private final String location;

    /**
     * Represents the Server Name they're currently onl
     */
    @Getter
    private final String serverName;

}
