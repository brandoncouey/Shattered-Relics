package com.shattered.game.actor.container;

import com.shattered.account.Account;
import com.shattered.datatable.tables.ItemUDataTable;
import com.shattered.game.actor.ability.Ability;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.object.item.Item;
import com.shattered.game.actor.vendor.VendorContainer;
import com.shattered.game.component.WorldComponent;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author JTlr Frost 11/1/2019 : 7:01 PM
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class Container extends WorldComponent {

    /**
     * Represents the Type of the Container
     */
    public enum Type {

        ITEM,

        ABILITY,

        MIXED
    }

    /**
     * Represents the List of Items
     */
    @Getter
    protected Item[] items;

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public Container(Object gameObject, int capacity) {
        super(gameObject);
        setItems(new Item[capacity]);
    }

    /**
     * Method called when a 'Slot' is used.
     * @param slotId
     * @param itemId
     */
    public abstract void onSlot(int slotId, int itemId);

    /**
     * This is used for hiding the container
     */
    public void hide() {

    }

    /**
     * Gets the Desired Item by Slot Id
     * @param slotId
     * @return
     */
    public Item get(int slotId) {
        if (items == null) return null;
        if (slotId < 0 || slotId > items.length) return null;
        return items[slotId];
    }

    /**
     * Sets the Slot to the specified {@link Item}
     * @param slotId
     * @param item
     */
    public void set(int slotId, Item item) {
        if (slotId < 0 || slotId > items.length) return;
        items[slotId] = item;
    }

    /**
     * Checks if the slot id is a valid slot for this container
     * @param slotId
     * @return valid
     */
    public boolean isValidSlot(int slotId) {
        return slotId > 0 && slotId < items.length;
    }

    /**
     * Checks if the slot is a valid slot id and is empty
     * @param slotId
     * @return valid and empty
     */
    public boolean isValidEmptySlot(int slotId) {
        return isValidSlot(slotId) && get(slotId) == null;
    }

    /**
     * Gets the Number of Remaining Slots
     * @return
     */
    public int getAvailableSlots() {
        if (items == null) return -1;
        int remaining = 0;
        for (int slot = 0; slot < items.length; slot++) {
            if (items[slot] == null) {
                remaining++;
                continue;
            }
            if (items[slot].getId() == 0)//Checks if the id is =
                remaining++;
        }
        return remaining;
    }

    /**
     * Gets the Number of items within the container
     * @return the total
     */
    public int getNumOfItems() {
        if (items == null) return -1;
        int total = 0;
        for (Item item : getItems()) {
            if (item != null)
                total++;
        }
        return total;
    }

    /**
     * Checks if the Container is Full
     * @return
     */
    public boolean isFull() {
        return getAvailableSlots() == 0;
    }


    /**
     * Gets the Next Slot that is Available
     * @return next slot id available
     */
    public int getNextSlotAvailable() {
        if (items == null) return -1;
        for (int slot = 0; slot < items.length; slot++) {
            if (items[slot] == null)
                return slot;
            if (items[slot].getId() == 0)//Incase a slot is valid, but invalid item.
                return slot;
        }
        return -1;
    }

    /**
     * Gets the Next available spot from index -> end.
     * @param skipToIndex
     * @return next available slot id
     */
    public int getNextSlotAvailable(int skipToIndex) {
        if (items == null) return -1;
        for (int slot = skipToIndex; slot < items.length; slot++) {
            if (items[slot] == null)
                return slot;
            if (items[slot].getId() == 0)
                return slot;
        }
        return -1;
    }

    /**
     * Gets the Last {@link Item} in the slots.
     * @param itemId
     * @return
     */
    public int getItemInLastSlot(int itemId) {
        if (items == null) return -1;
        for (int slot = slot = items.length - 1; slot >= 0; slot--) {
            if (items[slot] == null) continue;
            if (itemId == items[slot].getId())
                return slot;
        }
        return -1;
    }

    /**
     * Adds an Item to the Container
     * @param item
     * @return
     */
    public boolean add(Item item) {
        if (item == null) return false;

        //Check for a item that could be the same, and append the stack.
        if (item.getTable().isStackable()) {

            //We need to make a copy because 1 stack could almost be full.
            Item toAdd = item;

            for (int slot = 0; slot < items.length; slot++) {
                if (get(slot) == null) continue;

                if (toAdd.getAmount() < 1) break;

                //Found the a item that is similar
                if (get(slot).getId() == item.getId()) {

                    //Enesures that there are in fact left amount to add.
                    if (toAdd.getAmount() > 0) {

                        //If the slot can add the entire amount...
                        if ((get(slot).getAmount() + toAdd.getAmount()) <= item.getTable().getMaxStack()) {
                            get(slot).setAmount(get(slot).getAmount() + toAdd.getAmount());
                            toAdd.setAmount(0);
                            return true;
                        }

                        //If the slot cannot add the entire specific amount.
                        if (get(slot).getAmount() + toAdd.getAmount() > item.getTable().getMaxStack()) {
                            int remaining = (get(slot).getAmount() + toAdd.getAmount()) - item.getTable().getMaxStack();
                            get(slot).setAmount(item.getTable().getMaxStack());
                            toAdd.setAmount(remaining);
                        }
                    }
                }
            }
            if (isFull()) {
                return false;
            }
            if (toAdd.getAmount() > 0) {
                set(getNextSlotAvailable(), toAdd);
            }
            return true;
        }

        //Item is not stackable
        if (isFull()) {
            return false;
        }
        if (item.getAmount() > 0) {
            set(getNextSlotAvailable(), item);
        }
        return true;
    }

    /**
     * Reorders the container
     */
    public void reorderContainer() {
        Item[] reordered = new Item[VendorContainer.MAXIMUM_SLOTS - 1];
        int index = 0;
        for (Item item : getItems()) {
            if (item == null) continue;
            reordered[index] = item;
            index++;
        }
        setItems(reordered);
    }

    /**
     * Removes the item at the given {@code slot}.
     *
     * @param item
     *            the slot to remove from.
     * @return the removed {@link Item} instance if present otherwise
     *         {@code null}.
     */
    public int remove(Item item) {
        int removed = 0, toRemove = item.getAmount();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                if (items[i].getId() == item.getId()) {
                    int amount = items[i].getAmount();
                    if (amount > toRemove) {
                        removed += toRemove;
                        amount -= toRemove;
                        toRemove = 0;
                        items[i] = new Item(items[i].getId(), amount);
                        return removed;
                    } else {
                        removed += amount;
                        toRemove -= amount;
                        items[i] = null;
                    }
                }
            }
        }
        return removed;
    }

    /**
     * Deletes an Item from the Container
     * @param slotId
     * @param amount
     */
    public void deleteItem(int slotId, int amount) {
        if (slotId > items.length || slotId < 0) return;
        if ((get(slotId).getAmount() - amount)  <= 0) {
            deleteItemFromSlotCompletely(slotId);
            return;
        }
        get(slotId).setAmount(get(slotId).getAmount() - amount);
    }

    /**
     * Deletes a Specific Item from a Slot
     * @param slotId
     */
    public boolean deleteItemFromSlotCompletely(int slotId) {
        if (slotId > items.length || slotId < 0) return false;
        set(slotId, null);
        return true;
    }

    /**
     * Gets the Last {@link Item} in the slots.
     * @param item
     * @return
     */
    public int getItemInLastSlot(Item item) {
        if (items == null) return -1;
        for (int slot = slot = items.length - 1; slot >= 0; slot--) {
            if (items[slot] == null) continue;
            if (item.getId() == items[slot].getId())
                return slot;
        }
        return -1;
    }

    /**
     * Gets the Item Slot by ItemId
     * @param itemId
     * @return
     */
    public int getItemSlot(int itemId) {
        return getItemSlot(new Item(itemId));
    }

    /**
     * Gets the Item Slot by the {@link Item} object.
     *
     * @param item
     * @return
     */
    public int getItemSlot(Item item) {
        if (items == null) return -1;
        for (int slot = 0; slot < items.length; slot++) {
            if (items[slot] != null) {
                if (items[slot].getId() == item.getId())
                    return slot;
            }
        }
        return -1;
    }

    /**
     * CHecks if container contains a {@link Item} with the default amount of 1
     * @param name
     * @return container does contact the item
     */
    public boolean contains(String name) {
        ItemUDataTable table = ItemUDataTable.forName(name);
        if (table == null)
            return false;
        return contains(table.getId(), 1);
    }

    /**
     * Checks if container contains a {@link Item} with the default value of 1
     *
     * @param item
     * @return contains inventory
     */
    public boolean contains(Item item) {
        if (items == null) return false;
        for (int slot = 0; slot < items.length; slot++) {
            if (items[slot] == null) continue;
            if (items[slot] == item) return true;
        }
        return false;
    }

    /**
     * Checks if the inventory contains the specified quantity of the item
     * @param id
     * @param quantity
     * @return contains the amount of item
     */
    public boolean contains(int id, int quantity) {
        if (items == null) return false;
        int amount = 0;
        for (int slot = 0; slot < items.length; slot++) {
            if (items[slot] == null) continue;
            if (items[slot].getId() == id)
                amount += items[slot].getAmount();
            if (amount >= quantity) return true;
        }
        return false;
    }

    /**
     *
     * @param fromSlotId
     * @param toSlotId
     * @param itemId
     * @return
     */
    public boolean switchSlots(int fromSlotId, int toSlotId, int itemId) {
        return switchSlots(fromSlotId, toSlotId, get(fromSlotId));//TODO useItemId but its not being transmitted properly from client.
    }

    /**
     * Switches an {@link Item} from one slot to another
     *
     * @param fromSlotId
     * @param toSlotId
     * @param fromItemSlot
     * @return successfully switched
     */
    public boolean switchSlots(int fromSlotId, int toSlotId, Item fromItemSlot) {
        //Ensures the slot trying to change is valid
        if (get(fromSlotId) == null) return false;

        if (toSlotId < 0 || toSlotId > items.length) return false;

        //Don't trust client input of itemId, ensure that the one is right.
        //Also helper methods convert the itemid to amount, with invalid amount, so do NOT remove this.
        if (get(fromSlotId).getId() != fromItemSlot.getId()) return false;

        //Gets the actual item + amount from the current slot
        fromItemSlot  = get(fromSlotId);

        //Gets the toSlot item to ensure if there is an item or not, we can replace with the old slot.
        Item toItemSlot = get(toSlotId);

        //If the TO slot is null, it will set the old as null.
        if (toItemSlot == null) {
            set(toSlotId, fromItemSlot);
            set(fromSlotId, null);
            return true;
        }

        //if the TO slot contains an item.
        if (toItemSlot != null) {
            set(toSlotId, fromItemSlot);
            set(fromSlotId, toItemSlot);
            return true;
        }

        return false;
    }

    /**
     * Gets a Copy of the current {@link Item} array
     * @return
     */
    public Item[] getCopy() {
        Item[] duplicate = new Item[items.length];
        System.arraycopy(items, 0, duplicate, 0, duplicate.length);
        return duplicate;
    }

    /**
     * Clears the Item Container
     */
    public void clear() {
        for (int index = 0; index < getItems().length; index++) {
            set(index, null);
        }
    }

    /**
     * Sends ALl items within inventory to the client.
     */
    public void sendContainerFullUpdate(Player player, Containers.Type type) {
            World.UpdateItemContainerFull.Builder addItems = World.UpdateItemContainerFull.newBuilder();
            addItems.setContainerId(type.ordinal());
            for (int index = 0; index < items.length; index++) {
                World.ItemSlot.Builder itemBuilder = World.ItemSlot.newBuilder();
                int itemId = 0;
                int amount = 0;
                if (items[index] != null) {
                    itemId = items[index].getId();
                    amount = items[index].getAmount();
                    if (amount < 0) amount = 0;
                }
                itemBuilder.setId(itemId);
                itemBuilder.setAmount(amount);
                addItems.addItemSlot(itemBuilder);
            }
            player.sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_ITEM_CONTAINER_FULL, addItems.build());
    }

    /**
     * Sends ALl items within inventory to the client.
     */
    public void sendContainerFullUpdate(Containers.Type type) {
        if (getActor() instanceof Player) {
            World.UpdateItemContainerFull.Builder addItems = World.UpdateItemContainerFull.newBuilder();
            addItems.setContainerId(type.ordinal());
            for (int index = 0; index < items.length; index++) {
                World.ItemSlot.Builder itemBuilder = World.ItemSlot.newBuilder();
                int itemId = 0;
                int amount = 0;
                if (items[index] != null) {
                    itemId = items[index].getId();
                    amount = items[index].getAmount();
                    if (amount < 0) amount = 0;
                }
                itemBuilder.setId(itemId);
                itemBuilder.setAmount(amount);
                addItems.addItemSlot(itemBuilder);
            }
            getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_ITEM_CONTAINER_FULL, addItems.build());
        }
    }

    /**
     *
     * @param slotId
     */
    public void sendContainerPartialUpdate(Player player, Containers.Type type, int slotId) {
            World.UpdateItemContainerSlot.Builder addItem = World.UpdateItemContainerSlot.newBuilder();
            addItem.setContainerId(type.ordinal());
            addItem.setSlotId(slotId);
            World.ItemSlot.Builder itemBuilder = World.ItemSlot.newBuilder();
            int itemId = 0;
            int amount = 0;
            Item item = get(slotId);
            if (item != null) {
                itemId = item.getId();
                amount = item.getAmount();
                if (amount < 0) amount = 0;
            }
            itemBuilder.setId(itemId);
            itemBuilder.setAmount(amount);
            addItem.setItemSlot(itemBuilder);
            addItem.setItemSlot(World.ItemSlot.newBuilder().setId(itemId).setAmount(amount).build());
            player.sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_ITEM_CONTAINER_SLOT, addItem.build());
    }

    /**
     *
     * @param slotId
     */
    public void sendContainerPartialUpdate(Containers.Type type, int slotId) {
        if (getActor() instanceof Player) {
            World.UpdateItemContainerSlot.Builder addItem = World.UpdateItemContainerSlot.newBuilder();
            addItem.setContainerId(type.ordinal());
            addItem.setSlotId(slotId);
            World.ItemSlot.Builder itemBuilder = World.ItemSlot.newBuilder();
            int itemId = 0;
            int amount = 0;
            Item item = get(slotId);
            if (item != null) {
                itemId = item.getId();
                amount = item.getAmount();
                if (amount < 0) amount = 0;
            }
            itemBuilder.setId(itemId);
            itemBuilder.setAmount(amount);
            addItem.setItemSlot(itemBuilder);
            addItem.setItemSlot(World.ItemSlot.newBuilder().setId(itemId).setAmount(amount).build());
            getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_ITEM_CONTAINER_SLOT, addItem.build());

        }
    }

    /**
     * Sends the Hide Widget to the player
     * @param player
     * @param container
     */
    public void sendHide(Player player, String container) {
        player.sendMessage(PacketOuterClass.Opcode.SMSG_HIDE_WIDGET, World.StructWidget.newBuilder().setWidgetName(container).build());
    }

    /**
     * Sends the Hide Widget to the player
     * @param container
     */
    public void sendHide(String container) {
        if (getCharacter() instanceof Player)
            getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_HIDE_WIDGET, World.StructWidget.newBuilder().setWidgetName(container).build());
    }

    /**
     * Checks if the current slot id is an ability
     * @param slotId
     * @return is ability
     */
    public boolean isAbility(int slotId) {
        if (items != null) {
            if (get(slotId) != null)
                return get(slotId) instanceof Ability;
        }
        return false;
    }

}
