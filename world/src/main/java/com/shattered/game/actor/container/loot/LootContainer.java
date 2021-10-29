package com.shattered.game.actor.container.loot;

import com.shattered.game.actor.Actor;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.container.Container;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.actor.object.item.Item;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.utilities.VariableUtility;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class LootContainer extends Container {

    /**
     * Represents the Current Size of the Inventory
     */
    public static final int MAXIMUM_LOOTABLE_ITEMS = 16;


    /**
     * Represents a list of potential looters
     */
    @Getter
    @Setter
    private List<Player> looters = new ArrayList<>();

    /**
     * Represents the current looter of this actor
     */
    @Getter
    @Setter
    private Player looter;

    //Used for Identification
    public LootContainer() {
        super(null, 0);
    }

    /**
     * Creates a new constructor setting the capacity of the container
     *
     * @param gameObject
     */
    public LootContainer(Object gameObject) {
        super(gameObject, MAXIMUM_LOOTABLE_ITEMS);
    }

    /**
     * Method called upon onstart of the npc
     */
    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onDeath(Actor source) {
        super.onDeath(source);
        if (isNPC()) {
            if (getNPC().getDataTable().getPossibleLoot().isEmpty()) return;

            for (PossibleLoot loot : getNPC().getDataTable().getPossibleLoot()) {
                float chance = new Random().nextFloat() * 100;
                if (chance <= loot.getDropChance())
                    add(new Item(loot.getItem(), VariableUtility.random(loot.getMinAmount(), loot.getMaxAmount())));
            }
        }
        if (source instanceof Player)
            getLooters().add((Player) source);

        if (hasLoot()) {
            getLooters().stream().filter(Objects::nonNull).forEach(l -> {
                l.component(ActorComponents.TRANS_VAR).sendNPCTVarBool(getActor().getClientIndex(), "lootable", true);
            });
            getTVars().setVarBool("lootable", true);
        }
    }

    /**
     * Opens loot for the player
     * @param player
     */
    public void openLoot(Player player) {
        if (isBusy()) {
            player.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage(looter.getName() + " is busy looting this.");
            return;
        }
        if (hasLoot()) {
            setLooter(player);
            sendFullUpdate(player);
            player.component(PlayerComponents.WIDGET).showWidget("loot");
            player.getContainerManager().setCurrent(this);
        }
    }

    /**
     *
     * @param player
     */
    public void sendFullUpdate(Player player) {
       sendContainerFullUpdate(player, Containers.Type.LOOT);
    }

    /**
     * Attempts to loot all items for the player
     */
    public void lootAll() {
        for (int index = 0; index < getItems().length; index++) {
            Item item = get(index);
            if (item != null) {
                if (getLooter() != null) {
                    if (getLooter().container(Containers.INVENTORY).addItem(item)) {
                        set(index, null);
                        if (hasLoot())
                            sendFullUpdate(getLooter());
                        else
                            onLooted();
                    }
                }
            }
        }
    }

    /**
     * Method called when a 'Slot' is used.
     *
     * @param slotId
     * @param itemId
     */
    @Override
    public void onSlot(int slotId, int itemId) {
        Item item = get(slotId);
        if (item != null) {
            if (getLooter() != null) {
                if (getLooter().container(Containers.INVENTORY).addItem(item)) {
                    set(slotId, null);
                    if (hasLoot())
                        sendFullUpdate(getLooter());
                     else
                        onLooted();

                }
            }
        }
    }

    /**
     * Method called when initializing being hidden.
     */
    @Override
    public void hide() {
        if (getLooter() != null)
            sendHide(getLooter(), "loot");
        setLooter(null);
    }

    /**
     * Method called when there is nothing else to loot.
     */
    public void onLooted() {
        for (Player looter : getLooters()) {
            if (looter == null) continue;
            looter.component(ActorComponents.TRANS_VAR).sendNPCTVarBool(getActor().getClientIndex(), "lootable", false);
        }
        getTVars().setVarBool("lootable", false);
        getLooters().clear();
        hide();
    }

    /**
     * Checks if there is loot remaining
     * @return the loot
     */
    public boolean hasLoot() {
        return getNumOfItems() > 0;
    }


    /**
     * Checks if this actor is lootable for the player attempting to loot.
     * @param player
     * @return lootable
     */
    public boolean isLootable(Player player) {
        boolean isDead = getActor().getState().equals(ActorState.DEAD);
        boolean isLooter = getLooters().contains(player);
        boolean inDistance = isWithinDistance(player, 185);
        return isDead && isLooter && inDistance;
    }

    /**
     * Checks if someone is looting this actor currently
     * @return currently being looted
     */
    public boolean isBusy() {
        if (looter == null) return false;
        if (!looter.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().isWithinUnits(getActor(), 185))
            looter = null;
        //TODO add more checks to ensure they're actually looting this actor.
        return looter != null;
    }
}
