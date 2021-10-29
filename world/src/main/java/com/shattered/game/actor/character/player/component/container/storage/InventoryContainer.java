package com.shattered.game.actor.character.player.component.container.storage;

import com.shattered.database.mysql.MySQLColumn;
import com.shattered.database.mysql.MySQLCommand;
import com.shattered.datatable.tables.ItemUDataTable;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.container.Container;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.character.player.component.container.equipment.EquipmentSlot;
import com.shattered.game.actor.object.item.Item;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.actor.object.item.ItemQuality;
import com.shattered.game.actor.vendor.VendorContainer;
import com.shattered.script.ScriptManager;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author JTlr Frost 10/31/2019 : 5:44 PM
 */
@SuppressWarnings("ALL")
public class InventoryContainer extends Container {


    /**
     * Represents the Current Size of the Inventory
     */
    public static final int DEFAULT_INVENTORY_SIZE = 30;

    /**
     * Represents the default Entry Conditions
     */
    private WhereConditionOption[] updateConditions = new WhereConditionOption[] { new WhereConditionOption("character_id", getPlayer().getId()), null };

    /**
     * Creates a new constructor setting the size of the container
     *
     * @param gameObject
     */
    public InventoryContainer(Object gameObject) {
        super(gameObject, DEFAULT_INVENTORY_SIZE);
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
        sendContainerFullUpdate(Containers.Type.INVENTORY);
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
     * @param itemId
     */
    public void handleItem(int itemId) {
        int lastItemInSlot = getItemInLastSlot(itemId);
        if (lastItemInSlot != -1) {
            onSlot(lastItemInSlot, itemId);
        }
    }

    /**
     *
     * @param slotId
     */
    public void handleItemUse(int slotId) {
        if (get(slotId) == null) return;
        onSlot(slotId, get(slotId).getId());
    }

    /**
     *
     * @param gold
     */
    public void addGold(int gold) {
        getVars().incrementVarInt("coins", gold * 10_000);
        sendDefaultMessage("You receive " + gold + " gold.");
    }

    /**
     *
     * @param silver
     */
    public void addSilver(int silver) {
        getVars().incrementVarInt("coins", silver * 100);
        sendDefaultMessage("You receive " + silver + " silver.");
    }

    /**
     *
     * @param copper
     */
    public void addCopper(int copper) {
        getVars().incrementVarInt("coins", copper);
        sendDefaultMessage("You receive " + copper + " copper.");
    }

    /**
     * Gets the Amount of Gold
     * @return gold
     */
    public int getGold() {
        return getVars().getVarInt("coins") / 10_000;
    }

    /**
     * Gets the Amount of Silver
     * @return silver
     */
    public int getSilver() {
        return (getVars().getVarInt("coins") % 10_000) / 100;
    }

    /**
     * Gets the Amount of Copper
     * @return coins
     */
    public int getCopper() {
        return getVars().getVarInt("coins") % 100;
    }


    @Override
    public String getDatabaseName() {
        return "shatteredrelics";
    }

    @Override
    public String getTableName() {
        return "inventory";
    }

    @Override
    public WhereConditionOption[] getFetchConditions() {
        return new WhereConditionOption[] { new WhereConditionOption("character_id", getPlayer().getId()) };
    }

    @Override
    public WhereConditionOption[] getUpdateConditions() {
        return updateConditions;
    }

    @Override
    public boolean insert() {
        try {
            for (int index = 0; index < getItems().length; index++) {
                List<MySQLColumn> columns = new ArrayList<>();
                Item item = get(index);
                columns.add(new MySQLColumn("slot_id", index));
                columns.add(new MySQLColumn("item_id", item == null ? 0 : item.getId()));
                columns.add(new MySQLColumn("amount", item == null ? 0 : item.getAmount()));
                columns.add(new MySQLColumn("quality", item == null ? 0 : item.getQuality().ordinal()));
                columns.add(new MySQLColumn("character_id", getPlayer().getId()));
                entry(getDatabaseName(), getTableName(), columns, MySQLCommand.INSERT);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update() {
        try {
            for (int index = 0; index < getItems().length; index++) {
                List<MySQLColumn> columns = new ArrayList<>();
                Item item = get(index);
                updateConditions[1] = new WhereConditionOption("slot_id", index);
                columns.add(new MySQLColumn("item_id", item == null ? 0 : item.getId()));
                columns.add(new MySQLColumn("amount", item == null ? 0 : item.getAmount()));
                columns.add(new MySQLColumn("quality", item == null ? 0 : item.getQuality().ordinal()));
                entry(getDatabaseName(), getTableName(), columns, MySQLCommand.UPDATE);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean fetch() {
        ResultSet resultSet = getResults();
        try {
            if (!hasResults()) {
                insert();
                return true;
            }
            while (resultSet.next()) {
                int slotId = resultSet.getInt("slot_id");
                int itemId = resultSet.getInt("item_id");
                int amount = resultSet.getInt("amount");
                ItemQuality quality = ItemQuality.forId(resultSet.getInt("quality"));
                if (itemId > 0 && amount > 0)
                    set(slotId, new Item(itemId, amount, quality));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Adds an Item to the Inventory
     * Sending a DisplayLoot Message
     * @param item
     */
    public void acquireLoot(Item item) {
        addItem(item, true);
    }


    /**
     * Adds an Item with the specified id
     * @param id
     * @return
     */
    public boolean addItem(int id) {
        return addItem(id, 1);
    }


    /**
     * Adds a new Item with the specified id and amount.
     * @param id
     * @param amount
     * @return added
     */
    public boolean addItem(int id, int amount) {
        return addItem(new Item(id, amount));
    }

    /**
     * Adds a new Item with the specified Id, Amount, and Quality
     * @param id
     * @param amount
     * @param quality
     * @return
     */
    public boolean addItem(int id, int amount, ItemQuality quality) {
        return addItem(new Item(id, amount, quality));
    }

    /**
     * Adds a new Item with the specified name
     * @param name
     * @param amount
     * @return
     */
    public boolean addItem(String name) {
        return addItem(name, 1);
    }

    /**
     * Adds a new Item with the specified name
     * @param name
     * @return
     */
    public boolean addItem(String name, int amount) {
        if (ItemUDataTable.forName(name) == null)
            return false;
        return addItem(ItemUDataTable.forName(name).getId(), amount);
    }

    /**
     * Sets a slot item with the specific slot. Must not be a valid slot
     * @param slotId
     * @param item
     * @return added
     */
    public boolean setNullOnlySlot(int slotId, Item item) {
        if (get(slotId) != null) return false;
        if (!isValidSlot(slotId)) return false;
        set(slotId, item);
        sendContainerPartialUpdate(Containers.Type.INVENTORY, slotId);
        return true;
    }

    /**
     * Adds an Item to the Inventory silently
     * @param item
     * @return
     */
    public boolean addItem(Item item) {
        return addItem(item, false);
    }

    /**
     * Adds an Item to the Inventory
     * @param item
     */
    public boolean addItem(Item item, boolean notify) {
        if (item == null) return false;
        //Prevent Coins
        if (item.getId() == 1) {
            getPlayer().component(ActorComponents.VAR).incrementVarInt("coins", item.getAmount());
            return true;
        }
        //Lost in memory later on.
        int total = item.getAmount();

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
                            if (notify) {
                                component(PlayerComponents.WIDGET).sendItemAcquiredNotification(item.getId(), total);
                            }
                            sendContainerFullUpdate(Containers.Type.INVENTORY);
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
                getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Your inventory is full.");
                return false;
            }
            if (toAdd.getAmount() > 0) {
                set(getNextSlotAvailable(), toAdd);
            }
            if (notify) {
                component(PlayerComponents.WIDGET).sendItemAcquiredNotification(item.getId(), total);
            }
            sendContainerFullUpdate(Containers.Type.INVENTORY);
            return true;
        }

        //Item is not stackable
        if (isFull()) {
            getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Your inventory is full.");
            return false;
        }
        if (item.getAmount() > 0) {
            if (!item.getTable().isStackable() && item.getAmount() > 1)
                item.setAmount(1);
            set(getNextSlotAvailable(), item);
        }
        if (notify) {
            component(PlayerComponents.WIDGET).sendItemAcquiredNotification(item.getId(), total);
        }
        sendContainerFullUpdate(Containers.Type.INVENTORY);
        return true;
    }

    /**
     * Adds an Item to the Inventory
     * @param item
     */
    public boolean canAdd(Item item) {
        if (item == null) return false;

        //Lost in memory later on.
        int total = item.getAmount();

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
                            toAdd.setAmount(remaining);
                        }
                    }
                }
            }
            if (isFull()) {
                return false;
            }
            return true;
        }

        //Item is not stackable
        if (isFull()) {
            getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Your inventory is full.");
            return false;
        }
        return true;
    }


    /**
     * Deletes a Specific Item from a Slot
     * @param slotId
     */
    public boolean deleteItemFromSlotCompletely(int slotId) {
        boolean deleted = super.deleteItemFromSlotCompletely(slotId);
        if (deleted)
            sendContainerPartialUpdate(Containers.Type. INVENTORY, slotId);
        return deleted;
    }

    /**
     * Method called when a 'Slot' is used.
     *
     * @param slotId
     * @param itemId
     */
    @Override
    public void onSlot(int slotId, int itemId) {
        try {
            //Ensure it's a valid slot
            if (slotId < 0 || slotId > items.length) {
                System.out.println("Invalid slotId: " + slotId);
                return;
            }

            if (get(slotId) == null) {
                System.out.println("Invalid Item at slot: " + slotId);
                return;
            }

            if (get(slotId).getTable() == null) {
                System.out.println("No valid data table for item:" + itemId + ", at slotId: " + slotId);
                return;
            }

            //Ensure the item is in the right slot.
            if (get(slotId).getId() != itemId) {
                System.out.println("Mismatching client and server item ids at slotId: " + slotId);
                return;
            }

            //Gets the Exact Item from the Slot.
            Item item = get(slotId);

            //TODO i should probably make a fucking handler for this.... aka listener..

            //Represents the player is inside of a trade, and the items he clicks will go into the trade
            if (container(Containers.TRADE).isTrading()) {
                container(Containers.TRADE).addToTrade(slotId, item);
                return;
            }


            //Represents the player is inside a vendor (purchasing / selling items)
            if (getPlayer().getContainerManager().getCurrent() instanceof VendorContainer) {
                VendorContainer vendor = (VendorContainer) getPlayer().getContainerManager().getCurrent();
                if (vendor != null) {

                    if (!vendor.getDataTable().isGeneral()) {
                        //TODO item category for general
                        //return;
                    }
                    vendor.sell(getPlayer(), slotId, item);
                    return;
                }
            }

            //0 Represents None.
            EquipmentSlot equipmentSlot = EquipmentSlot.forId(item.getTable().getEquipmentType());
            if (equipmentSlot != null) {
                if (equipmentSlot != EquipmentSlot.NONE) {
                    getPlayer().container(Containers.EQUIPMENT).equip(slotId, item);
                    return;
                }
            }

            if (!ScriptManager.useItem(getPlayer(), item, slotId))
                 getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("[Unhandled Item] Item Id=" + item.getId() + ", Name=" + item.getName() + ".");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the specified item from the container
     * @param item
     */
    public void deleteItem(Item item) {
        deleteItemById(item.getId(), item.getAmount());
    }

    /**
     * Deletes an item with the specified amount
     * @param name
     * @param amount
     */
    public void deleteItem(String name, int amount) {
        ItemUDataTable table = ItemUDataTable.forName(name);
        if (table == null) return;
        super.remove(new Item(table.getId(), amount));
        sendContainerFullUpdate(Containers.Type.INVENTORY);
    }

    /**
     * Deletes an item with the specified amount
     * @param name
     * @param amount
     */
    public void deleteItemById(int id, int amount) {
        super.remove(new Item(id, amount));
        sendContainerFullUpdate(Containers.Type.INVENTORY);
    }

    /**
     *
     * @param slotId
     * @param amount
     */
    @Override
    public void deleteItem(int slotId, int amount) {
        super.deleteItem(slotId, amount);
        sendContainerPartialUpdate(Containers.Type.INVENTORY, slotId);
    }


    /**
     * Sends an Inventory Item to the specific {@link Actor}
     * @param item
     */
    @Deprecated
    public void sendInventoryItem(Item item) {
        Item given = null;
        if (getActor() instanceof Player) {
            boolean updated = false;
            for (int index = 0; index < items.length; index++) {
                if (items[index] != null)
                    continue;
                if (items[index] == null)
                    items[index] = item;
                given = item;
                updated = true;
                break;
            }
            if (updated) {
                sendContainerFullUpdate(Containers.Type.INVENTORY);
                getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You receive pitch" + given.getAmount() + " of Id=" + given.getId() + ".");
            }
        }
    }



    /**
     * Gets the {@link Actor} of the {@link com.shattered.game.GameObject}
     * @return
     */
    public Actor getActor() {
        return (Actor) gameObject;
    }

    /**
     * Gets the {@link NPC} of the {@link com.shattered.game.GameObject}
     * @return
     */
    public NPC getNPC() {
        return (NPC) gameObject;
    }

    /**
     * Gets the {@link Player} from the {@link com.shattered.game.GameObject}
     * @return
     */
    public Player getPlayer() {
        return (Player) gameObject;
    }

}
