package com.shattered.game.actor.components.flags;

import com.shattered.account.Account;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.component.WorldComponent;
import lombok.Getter;

import java.util.Vector;

/**
 * @author JTlr Frost 10/29/2019 : 9:36 PM
 */
public class ActorFlagUpdateComponent extends WorldComponent {

    /**
     * Represents the List of Flags
     */
    @Getter
    private int flags = 0x0;

    /**
     * Represents a List of Flag Types to Update
     */
    @Getter
    private Vector<FlagType> flagTypes = new Vector<>();

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public ActorFlagUpdateComponent(Object gameObject) {
        super(gameObject);
    }

    /**
     * Checks if a {@link FlagType} has been flagged.
     * @param flagType
     * @return
     */
    public boolean isFlagged(FlagType flagType) {
        return flagTypes.contains(flagType);
    }

    /**
     * Flags a specific {@link FlagType} to be updated
     * @param flagType
     */
    public void flag(FlagType flagType) {
        if (getFlagTypes().contains(flagType)) return;
        getFlagTypes().add(flagType);
        flags = flags | flagType.getFlagId();
    }

    /**
     * Unflags a specific {@link FlagType}
     * @param flagType
     */
    public void unflag(FlagType flagType) {
        if (!getFlagTypes().contains(flagType)) return;
        getFlagTypes().remove(flagType);
        flags = flags & ~flagType.getFlagId();
    }

    /**
     * Clears all the Flags
     */
    public void clearFlags() {
        if (isNPC())
            getNPC().getComponentManager().onClearedFlags();
        else
            if (isPlayer())
                getPlayer().getComponentManager().onClearedFlags();
        getFlagTypes().clear();
        flags = 0x0;
    }

}
