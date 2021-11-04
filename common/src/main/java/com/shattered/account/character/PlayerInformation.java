package com.shattered.account.character;

import com.shattered.networking.proto.Realm;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author JTlr Frost 9/5/2019 : 9:18 PM
 */
@Data
@RequiredArgsConstructor
public class PlayerInformation {

    /**
     * Represents the DB Id.
     */
    private final int id;

    /**
     * Represents the Name of the Player
     */
    private final String name;

    /**
     * Represents the last location of the Player
     */
    private String mapName;

    /**
     * Represents the location of the Player
     */
    private final String location;

    /**
     * Represents the Model Information
     */
    private Realm.CharacterInformation.Builder modelInformation;
}
