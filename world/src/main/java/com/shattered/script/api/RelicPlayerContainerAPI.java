package com.shattered.script.api;

import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.container.equipment.EquipmentSlot;
import com.shattered.game.actor.object.item.Item;
import com.shattered.game.actor.object.item.ItemQuality;
import com.shattered.script.api.impl.NpcAPI;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 *
 */
@NonNull
@RequiredArgsConstructor
public abstract class RelicPlayerContainerAPI {

    /**
     * Represents the Character for the Quest API
     */
    @Getter
    protected final Player player;

    /**
     * Adds the specified amount of coins to your coin pouch
     * @param amount
     */
    public abstract void coins_add(int amount);

    /**
     * Removes the specified amount of coins from your coin pouch
     * @param amount
     */
    public abstract void coins_remove(int amount);

    /**
     * Sets the current amount of coins to the specified amount
     * @param amount
     */
    public abstract void coins_set(int amount);

    /**
     * Opens a vendor with the specified vendor name
     * @param npc
     * @param name
     */
    public abstract void vendor_open(NpcAPI npc, String name);

    /**
     * Adds an item with the specified name and amount is set to 1.
     * @param name
     */
    public abstract void inv_add_item(String name);

    /**
     * @see #inv_add_item(String), additionally sends a notification for acquiring an item
     * @param name
     */
    public abstract void acquire_item(String name);

    /**
     * Check if the specified item is in the inventory
     * @param item
     * @return has item in inventory
     */
    public abstract boolean inv_has_item(Item item);

    /**
     * Checks if the specified item is in your inventory with a quantity of 1.
     * @param name
     * @return in inventory
     */
    public abstract boolean inv_has_item(String name);

    /**
     * Checks if the specified item is in your inventory with the specified quantity
     * @param name
     * @param quantity
     * @return amount of in inventory
     */
    public abstract boolean inv_has_item(String name, int quantity);

    /**
     * Adds an item with the specified name and the default amount set to 1 with the specified item quality
     * @param name
     * @param quality
     */
    public abstract void inv_add_item(String name, ItemQuality quality);

    /**
     * @see #inv_add_item(String, ItemQuality), additionally sends a notification for acquiring an item
     * @param name
     * @param quality
     */
    public abstract void acquire_item(String name, ItemQuality quality);

    /**
     * Adds the specified item to the inventory
     * @param item
     */
    public abstract void inv_add_item(Item item);

    /**
     * Adds an item with the specified item name, and the amount to add.
     * @param name
     * @param amount
     */
    public abstract void inv_add_item(String name, int amount);

    /**
     * Adds an item with the specified name, amount and the quality of the item
     * @param name
     * @param amount
     * @param quality
     */
    public abstract void inv_add_item(String name, int amount, ItemQuality quality);

    /**
     * @see #inv_add_item(String, int, ItemQuality), additionally sends a notification for acquiring an item
     * @param name
     * @param amount
     * @param quality
     */
    public abstract void acquire_item(String name, int amount, ItemQuality quality);

    /**
     * Adds an item with the specified item id, and the default amount to 1.
     * @param id
     */
    public abstract void inv_add_item(int id);

    /**
     * Adds an item with the specified item id, and the default amount set to 1 with the specified item quality
     * @param id
     * @param quality
     */
    public abstract void inv_add_item(int id, ItemQuality quality);

    /**
     * Adds an item with the specified item id, and the specified amount.
     * @param id
     * @param amount
     */
    public abstract void inv_add_item(int id, int amount);

    /**
     * Adds an item with the specified item id, amount and the item quality
     * @param id
     * @param amount
     * @param quality
     */
    public abstract void inv_add_item(int id, int amount, ItemQuality quality);

    /**
     * Deletes an item with the specified item name and default quantity of 1
     * @param name
     */
    public abstract void inv_delete_item(String name);

    /**
     * Deletes an item with the specified item name and amount.
     * @param name
     * @param amount
     */
    public abstract void inv_delete_item(String name, int amount);

    /**
     * Deletes an item with the specified item id and default quantity of 1
     * @param id
     */
    public abstract void inv_delete_item(int id);

    /**
     * Deletes an item with the specified item id and amount.
     * @param id
     * @param amount
     */
    public abstract void inv_delete_item(int id, int amount);

    /**
     * Deletes the item from the inventory
     * @param item
     */
    public abstract void inv_delete_item(Item item);

    /**
     * Checks if the inventory is completely full
     * @return full
     */
    public abstract boolean inv_is_full();

    /**
     * Gets the amount of remaining free slots, does not include stacked
     * @return amount of free, unused slots.
     */
    public abstract int inv_free_slots();

    /**
     * Checks if the inventory has room for an item, Including stacked items
     * @param name
     * @param quantity
     * @return has room for including stacked item.
     */
    public abstract boolean inv_can_add(String name, int quantity);

    /**
     * Checks if the specified item name is in the main hand of offhand slot
     * @param name
     * @return is in the slot
     */
    public abstract boolean equip_in_hands(String name);

    /**
     * Checks if the item in either mainhand or offhand contains a string of text
     * @param name
     * @return contains the name
     */
    public abstract boolean equip_hands_contains(String name);

    /**
     * Checks if a certain item is equipped non restrictive by a certain slot
     * @param name
     * @return
     */
    public abstract boolean equip_has_item(String name);

    /**
     * Checks if a specified item is within the certain equipment slot
     * @param name
     * @param slot
     * @return
     */
    public abstract boolean equip_in_slot(String name, EquipmentSlot slot);

    /**
     * Checks if the specified equipment slot is currently in use
     * @param slot
     * @return
     */
    public abstract boolean equip_slot_in_use(EquipmentSlot slot);

    /**
     * Equips the current tool
     * @param toolName
     */
    public abstract void equip_tool(String toolName);

    /**
     * Removes the current tool from being equipped
     */
    public abstract void remove_equiped_tool();



}
