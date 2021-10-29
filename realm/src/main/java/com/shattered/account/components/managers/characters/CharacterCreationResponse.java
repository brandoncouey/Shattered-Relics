package com.shattered.account.components.managers.characters;

/**
 * @author JTlr Frost 1/19/2020 : 6:17 PM
 */
public enum CharacterCreationResponse {

    //Represents successful creation
    SUCCESSFUL,

    //Represents not enough account.
    INSUFFICIENT_CHARACTERS,

    //Represents tif the name is currently taken
    NAME_IN_USE,

    //Represents the Character Name being inappropriate.
    INAPPROPRIATE_LANGUAGE,

    //Represents System Unavailable
    SYSTEM_UNAVAILABLE,
}
