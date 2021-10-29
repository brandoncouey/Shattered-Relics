package com.shattered.game.actor.object;

/**
 * @author JTlrBrad
 */
public enum ObjectActionType {

    NONE,

    INSPECT,

    OPEN,

    CLOSE,

    CHECK,

    LOOT,

    GATHER,

    DESTROY

    ;

    /**
     * Used for parsing a action name to the type.
     * @param actionName
     * @return
     */
    public static ObjectActionType forName(String actionName) {
        for (ObjectActionType type : ObjectActionType.values()) {
            if (type.name().equalsIgnoreCase(actionName))
                return type;
        }
        return null;
    }

}
