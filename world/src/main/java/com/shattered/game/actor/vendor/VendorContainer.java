package com.shattered.game.actor.vendor;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.datatable.tables.VendorUDataTable;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.container.Container;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.object.item.Item;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.script.types.NPCScript;
import com.shattered.system.SystemLogger;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author JTlr Brad
 */
public class VendorContainer extends Container {


    /**
     * Represents the Maximum Number of Slots the Vendor can have.
     */
    public static final int MAXIMUM_SLOTS = 30;

    /**
     * Represents a List of all the Players viewing this Vendor
     */
    @Getter
    private final List<Player> viewers = new CopyOnWriteArrayList<>();

    /**
     * Used for Containers Identifier
     */
    public VendorContainer() {
        super(null, MAXIMUM_SLOTS);
    }

    /**
     * Creates a new Vendor Container Component for the specified {@link Actor}
     * @param actor
     */
    public VendorContainer(Actor actor) {
        super(actor, MAXIMUM_SLOTS);
    }

    @Override
    public void onStart() {
        if (getDataTable() != null) {
            for (int index = 0; index < getDataTable().getStock().length; index++) {
                set(index, getDataTable().getStock()[index]);
            }
        }
    }

    /**
     * Adds a {@link Player} as a viewer of the vendor.
     * @param player
     */
    public void addViewer(final Player player) {
        if (!getViewers().contains(player))
            getViewers().add(player);

        sendVendorFullUpdate(player);
        player.getContainerManager().setCurrent(this);
    }

    /**
     * Method called when a 'Slot' is used.
     *
     * @param slotId
     * @param itemId
     */
    @Override
    public void onSlot(int slotId, int itemId) {
        //TODO right now we just use the 'buy button' need to be more generic for once and use right click buy like WoW.
    }

    /**
     * Removes a viewer from the vendor
     * @param viewer
     */
    public void removeViewer(Player viewer) {
        viewer.getContainerManager().setCurrent(null);
        getViewers().remove(viewer);
        sendHide(viewer, "vendor");
    }

    @Override
    public void hide() {
        sendHide("vendor");
    }

    /**
     * Purchases an {@link Item} from the vendor
     * @param player
     * @param slotId
     * @param quantity
     */
    public void purchase(Player player, int slotId, int quantity) {

        //Ensures the slot isn't past our container limit.
        if (slotId > MAXIMUM_SLOTS)
            return;

        //Ensures the slot attempted isn't out of our index bounds.
        if (slotId > getItems().length)
            return;

        Item stock = get(slotId);

        //Ensures the Slot attempting to purchase from has a valid item
        if (stock == null)
            return;

        //Checks to ensure that there are more items in stock of the type.
        if (stock.getAmount() < 1) {
            player.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("This item is currently out of stock.");
            return;
        }

        //Sets the stock to the maximum amount.
        if (quantity > stock.getAmount())
            quantity = stock.getAmount();

        //Checks to ensure the player has enough coins to purchase the stock.
        if ((stock.getTable().getCost() * quantity) > player.component(ActorComponents.VAR).getVarInt("coins"))  {
            player.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You do not have enough coins to purchase that many.");
            return;
        }

        //Checks if purchasing the set quantity will be purchasing all of them.
        boolean isOver = (stock.getAmount() - quantity) < 1;

        deleteItem(slotId, quantity);
        player.container(Containers.INVENTORY).addItem(stock.getId(), quantity);
        player.playSoundEffect("drop coins");
        player.component(ActorComponents.VAR).decrementVarInt("coins", (stock.getTable().getCost() * quantity));
        player.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You have purchased x" + quantity + " " + stock.getName() + "(s).");
    }

    /**
     * Deletes an item from the slot from the vendor
     * @param slotId
     * @param amount
     */
    @Override
    public void deleteItem(int slotId, int amount) {
        if (slotId > items.length || slotId < 0) return;
        Item item = get(slotId);
        if (item == null) return;
        if (item.getTable().isStackable()) {
            if ((item.getAmount() - amount) < 1)
                deleteItemFromSlotCompletely(slotId);
            else
                item.setAmount(item.getAmount() - amount);
            updateViewers(slotId);
            return;
        }
        deleteItemFromSlotCompletely(slotId);
        updateViewers(slotId);
    }

    /**
     * Deletes an item from the vendor slot completely and updates the entire vendor.
     * @param slotId
     */
    @Override
    public boolean deleteItemFromSlotCompletely(int slotId) {
        boolean deleted = super.deleteItemFromSlotCompletely(slotId);
        if (deleted) {
            reorderContainer();
            updateViewers();
        }
        return deleted;
    }

    /**
     * Sells an {@link Item} to the vendor.
     *
     * @param player
     * @param slotId"
     * @param product
     */
    public void sell(Player player, int slotId, Item product) {
        reorderContainer();
        int amount = (product.getTable().getCost() * product.getAmount());
        player.playSoundEffect("pickup coins");
        player.component(ActorComponents.VAR).incrementVarInt("coins", amount);
        boolean added = add(product);
        player.container(Containers.INVENTORY).deleteItemFromSlotCompletely(slotId);
        player.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You sell your " + product.getName() + " for " + amount + " coins.");
        updateViewers();
    }

    /**
     * Updates all of the vendor viewers with a full update.
     */
    public void updateViewers() {
        for (Player player : getViewers()) {
            if (player == null) continue;
            sendVendorFullUpdate(player);
        }
    }


    /**
     * Updates all of the viewers of the changes of the vendor with a partial update
     * @param slotId
     */
    public void updateViewers(int slotId) {
        for (Player player : getViewers()) {
            if (player == null) continue;
            sendVendorPartialUpdate(player, slotId);
        }
    }

    /**
     * Sends all of the vendors items for the specified player.
     */
    public void sendVendorFullUpdate(final Player player) {
        World.UpdateItemContainerFull.Builder addItems = World.UpdateItemContainerFull.newBuilder();
        addItems.setContainerId(Containers.Type.VENDOR.ordinal());
        for (int index = 0; index < items.length; index++) {
            World.ItemSlot.Builder itemBuilder = World.ItemSlot.newBuilder();
            int itemId = 0;
            int amount = 0;
            int cost = 0;
            Item item = get(index);
            if (item != null) {
                itemId = item.getId();
                amount = item.getAmount();
                if (item.getTable() != null)
                    cost = item.getTable().getCost();//TODO regional costs not static costs
                if (amount < 0) amount = 0;
            }
            itemBuilder.setId(itemId);
            itemBuilder.setAmount(amount);
            itemBuilder.setCost(cost);
            addItems.addItemSlot(itemBuilder);
        }
        player.sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_ITEM_CONTAINER_FULL, addItems.build());
    }

    /**
     * Updates the Vendor's items for the specified slot id for the specified player.
     * @param slotId
     */
    public void sendVendorPartialUpdate(final Player player, int slotId) {
        World.UpdateItemContainerSlot.Builder addItem = World.UpdateItemContainerSlot.newBuilder();
        addItem.setContainerId(Containers.Type.VENDOR.ordinal());
        addItem.setSlotId(slotId);
        World.ItemSlot.Builder itemBuilder = World.ItemSlot.newBuilder();
        int itemId = 0;
        int amount = 0;
        int cost = 0;
        Item item = get(slotId);
        if (item != null) {
            itemId = item.getId();
            amount = item.getAmount();
            if (item.getTable() != null)
                cost = item.getTable().getCost();//TODO regional costs not static costs
            if (amount < 0) amount = 0;
        }
        itemBuilder.setId(itemId);
        itemBuilder.setAmount(amount);
        itemBuilder.setCost(cost);
        addItem.setItemSlot(itemBuilder);
        addItem.setItemSlot(World.ItemSlot.newBuilder().setId(itemId).setAmount(amount).setCost(cost).build());
        player.sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_ITEM_CONTAINER_SLOT, addItem.build());
    }

    /**
     * Gets the Data Table for the current Vendor
     * @return the Data Table
     */
    public VendorUDataTable getDataTable() {
        NPCScript script = getNPC().getScript();
        if (script == null) return null;
        if (script.register_to_vendor() == null) return null;
        if (UDataTableRepository.getVendorDataTable().get(script.register_to_vendor().toLowerCase()) == null) {
            SystemLogger.sendSystemErrMessage("NPC Vendor is unable to register, A vendor name mismatch has occured `" + script.register_to_vendor().toLowerCase() + "` under the NPC Script for " + getNPC().getName() + "!");
            return null;
        }
        return UDataTableRepository.getVendorDataTable().get(script.register_to_vendor().toLowerCase());
    }



}
