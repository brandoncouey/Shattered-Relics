package com.shattered.game.actor.components.interaction;

import com.shattered.account.Account;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.flags.FlagType;
import com.shattered.game.component.WorldComponent;
import lombok.Getter;
import lombok.Setter;

import java.util.Vector;

public class ActorInteractionFlaggerComponent extends WorldComponent {

    /**
     * Represents the List of Flags
     */
    @Getter
    @Setter
    private int flags = 0x0;

    /**
     * Represents a List of Flag Types to Update
     */
    @Getter
    @Setter
    private Vector<InteractionFlags> flagTypes = new Vector<>();

    /**
     * Represents a List of Flag Types to Update
     */
    @Getter
    @Setter
    private Vector<InteractionFlags> spawnFlags = new Vector<>();

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public ActorInteractionFlaggerComponent(Object gameObject) {
        super(gameObject);
    }

    /**
     * Checks if a {@link FlagType} has been flagged.
     * @param flagType
     * @return
     */
    public boolean isFlagged(InteractionFlags flagType) {
        return flagTypes.contains(flagType);
    }

    /**
     * Toggles a flag depending on the
     * @param flagType
     * @param toFlag
     */
    public void toggle(InteractionFlags flagType, boolean toFlag) {
        if (toFlag)
            flag(flagType);
        else
            unflag(flagType);
    }

    /**
     * Flags a specific {@link FlagType} to be updated
     * @param flagType
     */
    public void flag(InteractionFlags flagType) {
        getActor().component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
        if (getFlagTypes().contains(flagType)) return;
        getFlagTypes().add(flagType);
        flags = flags | flagType.getFlag();
    }

    /**
     * Unflags a specific {@link FlagType}
     * @param flagType
     */
    public void unflag(InteractionFlags flagType) {
        getActor().component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
        if (!getFlagTypes().contains(flagType)) return;
        getFlagTypes().remove(flagType);
        flags = flags & ~flagType.getFlag();
    }

    /**
     * Clears all the Flags
     */
    public void clearFlags() {
        if (getFlagTypes().size() != 0)
            getFlagTypes().clear();
        flags = 0x0;
    }

    /**
     * Filters the needed flags for the player.
     * @param player
     */
    public int filter(Player player) {
        int fFlags = flags;
        if (isPlayer()) {

            fFlags = addIf(fFlags, InteractionFlags.ATTACKABLE,
                    getPlayer().component(ActorComponents.TRANS_VAR).getVarInt("dueled_puuid") == player.getClientIndex());

            fFlags = removeIf(fFlags, InteractionFlags.ATTACKABLE,
                    getPlayer().component(ActorComponents.TRANS_VAR).getVarInt("dueled_puuid") != player.getClientIndex());

        }
        return fFlags;
    }

    /**
     * Removes the flag type if any of the checks return false
     * @param fFlags
     * @param flag
     * @param checks
     * @return the new modified flags.
     */
    public int removeIf(int fFlags, InteractionFlags flag, boolean... checks) {
        int rFlags = fFlags;
        for (boolean c : checks) {
            if (c == false)
                return rFlags = fFlags & ~flag.getFlag();
        }
        return rFlags;
    }

    /**
     * Adds the flag if any of the checks return true
     * @param fFlags
     * @param flag
     * @param checks
     * @return the new modified flags.
     */
    public int addIf(int fFlags, InteractionFlags flag, boolean... checks) {
        int aFlags = fFlags;
        for (boolean c : checks) {
            if (c == true)
                return aFlags = fFlags | flag.getFlag();
        }
        return aFlags;
    }

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {

    }

    /**
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {

    }

    /**
     * Called once per world cycle per each instance.
     */
    @Override
    public void onTick(long deltaTime) {

    }

    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {

    }

    /**
     *
     * @param source
     */
    @Override
    public void onDeath(Actor source) {
        super.onDeath(source);
        if (isNPC()) {
            if (getNPC().getDataTable().isSkinnable()) {
                flag(InteractionFlags.CAN_TALK_TO);
            }
        }
    }
}
