package com.shattered.game.actor.character.player.component.channel;

/**
 * @author JTlr Frost 10/27/2019 : 6:00 PM
 */
public enum ChannelType {

    /**
     * Represents the 'System Chat'
     */
    SYSTEM_MESSAGE,

    /**
     * Represents the 'General Chat'
     */
    GENERAL,

    /**
     * Represents the 'Trade Chat'
     */
    TRADE,

    /**
     * Represents the 'Local Defence' Container
     */
    LOCAL_DEFENSE,

    /**
     * Represents the 'Party' Chat
     */
    PARTY,

    /**
     * Represents the 'Local' Chat
     */
    LOCAL,

    /**
     * Used for 'Local Info' Chat
     */
    INFO,

    /**
     * Represents the 'Whisper' Chat
     */
    WHISPER,

    /**
     * Represents the 'Yell' Chat
     */
    YELL,

    /**
     * Represents the 'Guild' Chat
     */
    GUILD,

    /**
     * Represents the Combat Log
     */
    COMBAT
    
    
    ;

    /**
     * Gets the Channel Type for ID.
     * @param typeId
     * @return
     */
    public static ChannelType forId(int typeId) {
        for (ChannelType type : ChannelType.values()) {
            if (type.ordinal() == typeId)
                return type;
        }
        return null;
    }
    
}
