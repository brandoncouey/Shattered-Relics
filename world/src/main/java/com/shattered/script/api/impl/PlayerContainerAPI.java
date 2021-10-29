package com.shattered.script.api.impl;

import com.shattered.datatable.tables.ItemUDataTable;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.container.equipment.EquipmentSlot;
import com.shattered.game.actor.character.player.component.synchronize.npc.NPCSynchronizeComponent;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.flags.FlagType;
import com.shattered.game.actor.components.variable.ActorVariableComponent;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.object.item.Item;
import com.shattered.game.actor.object.item.ItemQuality;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.script.api.RelicPlayerContainerAPI;
import com.shattered.system.SystemLogger;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class PlayerContainerAPI extends RelicPlayerContainerAPI {

    /**
     * Creates the implementation of the Container API.
     * @param player
     */
    public PlayerContainerAPI(Player player) {
        super(player);
    }


    /**
     * Adds the specified amount of coins to your coin pouch
     *
     * @param amount
     */
    @Override
    public void coins_add(int amount) {
        player.component(ActorComponents.VAR).incrementVarInt("coins", amount);
        player.playSoundEffect("pickup coins");
    }

    /**
     * Removes the specified amount of coins from your coin pouch
     *
     * @param amount
     */
    @Override
    public void coins_remove(int amount) {
        player.component(ActorComponents.VAR).decrementVarInt("coins", amount);
        player.playSoundEffect("drop coins");
    }

    /**
     * Sets the current amount of coins to the specified amount
     *
     * @param amount
     */
    @Override
    public void coins_set(int amount) {
        ActorVariableComponent vars = player.component(ActorComponents.VAR);
        int old = vars.getVarInt("coins");
        vars.setVarInt("coins", amount);
        player.playSoundEffect((old >= vars.getVarInt("coins") ? "drop" : "pickup") + " coins");
    }

    /**
     * Opens a vendor with the specified vendor name
     *
     * @param npc
     * @param name
     */
    @Override
    public void vendor_open(NpcAPI npc, String name) {
        NPCSynchronizeComponent sync = player.component(PlayerComponents.NPC_SYNCHRONIZE);
        if (sync.isNPCNear(npc.getIndex())) {
            player.component(PlayerComponents.WIDGET).showWidget("vendor");
            player.component(PlayerComponents.WIDGET).showWidget("inventory");
            npc.getNpc().container(Containers.VENDOR).addViewer(player);
        }
    }

    /**
     * Adds an item with the specified name and amount is set to 1.
     *
     * @param name
     */
    @Override
    public void inv_add_item(String name) {
        inv_add_item(name, 1);
    }

    /**
     * @param name
     * @see #inv_add_item(String), additionally sends a notification for acquiring an item
     */
    @Override
    public void acquire_item(String name) {
        inv_add_item(name);
        if (ItemUDataTable.forName(name) != null) {
            player.sendMessage(PacketOuterClass.Opcode.SMSG_ITEM_REWARD_NOTIFICATION, World.AcquiredNotification.newBuilder().setItemId(ItemUDataTable.forName(name).getId()).setAmount(1).build());
        }
    }

    /**
     * Check if the specified item is in the inventory
     *
     * @param item
     * @return has item in inventory
     */
    @Override
    public boolean inv_has_item(Item item) {
        return player.container(Containers.INVENTORY).contains(item);
    }

    /**
     * Checks if the specified item is in your inventory with a quantity of 1.
     *
     * @param name
     * @return in inventory
     */
    @Override
    public boolean inv_has_item(String name) {
        ItemUDataTable result = ItemUDataTable.forName(name);
        return result != null && player.container(Containers.INVENTORY).contains(result.getId(), 1);
    }

    /**
     * Checks if the specified item is in your inventory with the specified quantity
     *
     * @param name
     * @param quantity
     * @return amount of in inventory
     */
    @Override
    public boolean inv_has_item(String name, int quantity) {
        ItemUDataTable result = ItemUDataTable.forName(name);
        return result != null && player.container(Containers.INVENTORY).contains(result.getId(), quantity);
    }

    /**
     * Adds an item with the specified name and the default amount set to 1 with the specified item quality
     *
     * @param name
     * @param quality
     */
    @Override
    public void inv_add_item(String name, ItemQuality quality) {
        if (ItemUDataTable.forName(name) != null) {
            inv_add_item(ItemUDataTable.forName(name).getId(), 1, quality);
        }
    }

    /**
     * @param name
     * @param quality
     * @see #inv_add_item(String, ItemQuality), additionally sends a notification for acquiring an item
     */
    @Override
    public void acquire_item(String name, ItemQuality quality) {
        inv_add_item(name, quality);
        if (ItemUDataTable.forName(name) != null) {
            player.sendMessage(PacketOuterClass.Opcode.SMSG_ITEM_REWARD_NOTIFICATION, World.AcquiredNotification.newBuilder().setItemId(ItemUDataTable.forName(name).getId()).setAmount(1).build());
        }
    }

    /**
     * Adds the specified item to the inventory
     *
     * @param item
     */
    @Override
    public void inv_add_item(Item item) {
        player.container(Containers.INVENTORY).addItem(item);
    }

    /**
     * Adds an item with the specified item name, and the amount to add.
     *
     * @param name
     * @param amount
     */
    @Override
    public void inv_add_item(String name, int amount) {
        if (ItemUDataTable.forName(name) != null) {
            inv_add_item(ItemUDataTable.forName(name).getId(), amount);
        }
    }

    /**
     * Adds an item with the specified name, amount and the quality of the item
     *
     * @param name
     * @param amount
     * @param quality
     */
    @Override
    public void inv_add_item(String name, int amount, ItemQuality quality) {
        if (ItemUDataTable.forName(name) != null) {
            inv_add_item(ItemUDataTable.forName(name).getId(), amount, quality);
        }
    }

    /**
     * @param name
     * @param amount
     * @param quality
     * @see #inv_add_item(String, int, ItemQuality), additionally sends a notification for acquiring an item
     */
    @Override
    public void acquire_item(String name, int amount, ItemQuality quality) {
        inv_add_item(name, amount, quality);
        if (ItemUDataTable.forName(name) != null) {
            player.sendMessage(PacketOuterClass.Opcode.SMSG_ITEM_REWARD_NOTIFICATION, World.AcquiredNotification.newBuilder().setItemId(ItemUDataTable.forName(name).getId()).setAmount(amount).build());
        }
    }

    /**
     * Adds an item with the specified item id, and the default amount to 1.
     *
     * @param id
     */
    @Override
    public void inv_add_item(int id) {
        inv_add_item(id, 1);
    }

    /**
     * Adds an item with the specified item id, and the default amount set to 1 with the specified item quality
     *
     * @param id
     * @param quality
     */
    @Override
    public void inv_add_item(int id, ItemQuality quality) {
        player.container(Containers.INVENTORY).addItem(id, 1, quality);
    }

    /**
     * Adds an item with the specified item id, and the specified amount.
     *
     * @param id
     * @param amount
     */
    @Override
    public void inv_add_item(int id, int amount) {
        player.container(Containers.INVENTORY).addItem(id, amount);
    }

    /**
     * Adds an item with the specified item id, amount and the item quality
     *
     * @param id
     * @param amount
     * @param quality
     */
    @Override
    public void inv_add_item(int id, int amount, ItemQuality quality) {
        player.container(Containers.INVENTORY).addItem(id, amount, quality);
    }

    /**
     * Deletes an item with the specified item name and default quantity of 1
     *
     * @param name
     */
    @Override
    public void inv_delete_item(String name) {
        inv_delete_item(name, 1);
    }

    /**
     * Deletes an item with the specified item name and amount.
     *
     * @param name
     * @param amount
     */
    @Override
    public void inv_delete_item(String name, int amount) {
        player.container(Containers.INVENTORY).deleteItem(name, amount);
    }

    /**
     * Deletes an item with the specified item id and default quantity of 1
     *
     * @param id
     */
    @Override
    public void inv_delete_item(int id) {
        inv_delete_item(id, 1);
    }

    /**
     * Deletes an item with the specified item id and amount.
     *
     * @param id
     * @param amount
     */
    @Override
    public void inv_delete_item(int id, int amount) {
        player.container(Containers.INVENTORY).deleteItemById(id, amount);
    }

    /**
     * Deletes the item from the inventory
     *
     * @param item
     */
    @Override
    public void inv_delete_item(Item item) {
        player.container(Containers.INVENTORY).deleteItem(item);
    }

    /**
     * Checks if the inventory is completely full
     *
     * @return full
     */
    @Override
    public boolean inv_is_full() {
        return player.container(Containers.INVENTORY).isFull();
    }

    /**
     * Gets the amount of remaining free slots, does not include stacked
     *
     * @return amount of free, unused slots.
     */
    @Override
    public int inv_free_slots() {
        return player.container(Containers.INVENTORY).getAvailableSlots();
    }

    /**
     * Checks if the inventory has room for an item, Including stacked items
     *
     * @param name
     * @param quantity
     * @return has room for including stacked item.
     */
    @Override
    public boolean inv_can_add(String name, int quantity) {
        ItemUDataTable item = ItemUDataTable.forName(name);
        if (item == null) return false;
        return player.container(Containers.INVENTORY).canAdd(new Item(item.getId(), quantity));
    }


    /**
     * Checks if the item in either mainhand or offhand contains a string of text
     *
     * @param name
     * @return contains the name
     */
    @Override
    public boolean equip_hands_contains(String name) {
        return player.container(Containers.EQUIPMENT).doesItemInHandsContain(name);
    }

    /**
     * Checks if a certain item is equipped non restrictive by a certain slot
     *
     * @param name
     * @return
     */
    @Override
    public boolean equip_has_item(String name) {
        return player.container(Containers.EQUIPMENT).contains(name);
    }

    /**
     * Checks if a specified item is within the certain equipment slot
     *
     * @param name
     * @param slot
     * @return
     */
    @Override
    public boolean equip_in_slot(String name, EquipmentSlot slot) {
        Item item = player.container(Containers.EQUIPMENT).get(slot.ordinal());
        if (item == null) return false;
        return item.getName().toLowerCase().replace("_", " ").equalsIgnoreCase(name);
    }

    /**
     * Checks if the specified equipment slot is currently in use
     *
     * @param slot
     * @return
     */
    @Override
    public boolean equip_slot_in_use(EquipmentSlot slot) {
        return player.container(Containers.EQUIPMENT).get(slot.ordinal()) != null;
    }

    @Override
    public void equip_tool(String toolName) {
        ItemUDataTable tool = ItemUDataTable.forName(toolName);
        if (tool == null) {
            SystemLogger.sendSystemErrMessage("Unable to equip tool by the name: " + toolName +", NULL data table.");
            return;
        }
        player.container(Containers.EQUIPMENT).setCurrentTool(new Item(tool.getId(), 1));
    }

    @Override
    public void remove_equiped_tool() {
        player.container(Containers.EQUIPMENT).setCurrentTool(null);
    }

    /**
     * Checks if the specified item name is in the main hand of offhand slot
     *
     * @param name
     * @return is in the slot
     */
    @Override
    public boolean equip_in_hands(String name) {
        return player.container(Containers.EQUIPMENT).isItemInHand(name);
    }
}
