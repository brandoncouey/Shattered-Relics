package com.shattered.game.actor.character.player.component.container.equipment;

import com.shattered.account.Account;
import com.shattered.database.mysql.MySQLColumn;
import com.shattered.database.mysql.MySQLCommand;
import com.shattered.datatable.tables.AnimSequenceUDataTable;
import com.shattered.datatable.tables.ItemUDataTable;
import com.shattered.engine.tasks.DelayedTaskTicker;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.container.storage.InventoryContainer;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.container.Container;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.flags.FlagType;
import com.shattered.game.actor.object.item.Item;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.WorldProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import lombok.Getter;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JTlr Frost 11/2/2019 : 9:20 PM
 */
public class EquipmentContainer extends Container {


    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public EquipmentContainer(Object gameObject) {
        super(gameObject, EquipmentSlot.values().length);
    }

    /**
     * Represents the Current Tool In Use
     * - If no tool is in use, it will return null. This is for animation overriding for (fishing, etc)
     */
    @Getter
    public Item currentTool;

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_REQUEST_WEAPON_SWAP, new WorldProtoListener<PacketOuterClass.EmptyPayload>() {

            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(PacketOuterClass.EmptyPayload message, Player player) {
                player.container(Containers.EQUIPMENT).onClassSwitch();

                DelayedTaskTicker.delayTask(() -> {
                    //player.container(Containers.ABILITY_BOOK).onClassSwitch();
                    player.component(PlayerComponents.ACTION_BAR).onClassSwitch();
                }, 500.f);

            }
        }, PacketOuterClass.EmptyPayload.getDefaultInstance());
    }


    /**
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {
        sendUpdatedEquipmentSlots();
        Item weapon = get(EquipmentSlot.MAIN_HAND.ordinal());
        getPlayer().component(PlayerComponents.MODEL_BLOCK).getModel().setBlendId(ItemUDataTable.getBlendPoseForWeapon(weapon));
    }

    public void onClassSwitch() {
        Item oldMainHand = get(EquipmentSlot.MAIN_HAND.ordinal());
        Item oldOffhand = get(EquipmentSlot.OFF_HAND.ordinal());
        Item oldSpecMainhand = get(EquipmentSlot.OFFSPEC_MAIN_HAND.ordinal());
        Item oldSpecOffhand = get(EquipmentSlot.OFFSPEC_OFF_HAND.ordinal());
        //Grimoire Switching
        Item oldGrimoire1 = get(EquipmentSlot.GRIMOIRE_1.ordinal());
        Item oldGrimoire2 = get(EquipmentSlot.GRIMOIRE_2.ordinal());
        set(EquipmentSlot.MAIN_HAND.ordinal(), oldSpecMainhand);
        set(EquipmentSlot.OFFSPEC_MAIN_HAND.ordinal(), oldMainHand);
        set(EquipmentSlot.OFF_HAND.ordinal(), oldSpecOffhand);
        set(EquipmentSlot.OFFSPEC_OFF_HAND.ordinal(), oldOffhand);

        set(EquipmentSlot.GRIMOIRE_1.ordinal(), oldGrimoire2);
        set(EquipmentSlot.GRIMOIRE_2.ordinal(), oldGrimoire1);
        sendContainerPartialUpdate(Containers.Type.EQUIPMENT, EquipmentSlot.MAIN_HAND.ordinal());
        sendContainerPartialUpdate(Containers.Type.EQUIPMENT, EquipmentSlot.OFF_HAND.ordinal());
        sendContainerPartialUpdate(Containers.Type.EQUIPMENT, EquipmentSlot.OFFSPEC_MAIN_HAND.ordinal());
        sendContainerPartialUpdate(Containers.Type.EQUIPMENT, EquipmentSlot.OFFSPEC_OFF_HAND.ordinal());

        sendContainerPartialUpdate(Containers.Type.EQUIPMENT, EquipmentSlot.GRIMOIRE_1.ordinal());
        sendContainerPartialUpdate(Containers.Type.EQUIPMENT, EquipmentSlot.GRIMOIRE_2.ordinal());

        Item weapon = get(EquipmentSlot.MAIN_HAND.ordinal());

        if (weapon.getName().toLowerCase().contains("bow")) {
            getPlayer().component(ActorComponents.ANIMATION).playAnimSequence("EquipBow");
        } else {
            getPlayer().component(ActorComponents.ANIMATION).playAnimSequence("EquipTwohand");
        }

        getPlayer().component(PlayerComponents.MODEL_BLOCK).getModel().setBlendId(ItemUDataTable.getBlendPoseForWeapon(weapon));
        component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);

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

    @Override
    public String getDatabaseName() {
        return "shatteredrelics";
    }

    @Override
    public String getTableName() {
        return "equipment";
    }

    @Override
    public WhereConditionOption[] getFetchConditions() {
        return new WhereConditionOption[] { new WhereConditionOption("character_id", getPlayer().getId()) };
    }

    @Override
    public WhereConditionOption[] getUpdateConditions() {
        return new WhereConditionOption[] { new WhereConditionOption("character_id", getPlayer().getId()) };
    }

    @Override
    public boolean insert() {
        try {
            List<MySQLColumn> columns = new ArrayList<>();
            columns.add(new MySQLColumn("character_id", getPlayer().getId()));
            for (int index = 1; index < EquipmentSlot.values().length; index++) {
                columns.add(new MySQLColumn(EquipmentSlot.values()[index].name().toLowerCase() + "_slot", get(index) == null ? 0 : get(index).getId()));
            }
            entry(getDatabaseName(), getTableName(), columns, MySQLCommand.INSERT);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update() {
        try {
            List<MySQLColumn> columns = new ArrayList<>();
            for (int index = 1; index < EquipmentSlot.values().length; index++) {
                columns.add(new MySQLColumn(EquipmentSlot.values()[index].name().toLowerCase() + "_slot", get(index) == null ? 0 : get(index).getId()));
            }
            entry(getDatabaseName(), getTableName(), columns, MySQLCommand.UPDATE);
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
            if (resultSet.next()) {
                for (int index = 1; index < EquipmentSlot.values().length; index++) {
                    EquipmentSlot slot = EquipmentSlot.values()[index];
                    int id = resultSet.getInt(slot.name().toLowerCase() + "_slot");
                    set(slot.ordinal(), id < 1 ? null : new Item(id, 1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Gets the Item for the specified Equipment Slot
     * @param slot
     * @return
     */
    public Item getItemForSlot(EquipmentSlot slot) {
        if (slot.ordinal() == 0) return null;
        if (get(slot.ordinal()) == null)
            return null;
        return get(slot.ordinal());
    }

    /**
     * Gets the name of the current Mainhand
     * @return the main hand weapon name
     */
    public String getMainHandName() {
        if (get(EquipmentSlot.MAIN_HAND.ordinal()) == null)
            return null;
        return get(EquipmentSlot.MAIN_HAND.ordinal()).getName();
    }

    /**
     * Gets the current offhand weapon name
     * @return the offhand weapon name
     */
    public String getOffhandName() {
        if (get(EquipmentSlot.OFF_HAND.ordinal()) == null)
            return null;
        return get(EquipmentSlot.OFF_HAND.ordinal()).getName();
    }

    /**
     * Checks if the player has offspec weapons
     * @return has offspec weapons
     */
    public boolean hasOffspecWeapons() {
        return get(EquipmentSlot.OFFSPEC_MAIN_HAND.ordinal()) != null || get(EquipmentSlot.OFFSPEC_OFF_HAND.ordinal()) != null;
    }

    /**
     * Checks if the current item name is in either offhand or main hand slot
     * @param name
     * @return is in the slot
     */
    public boolean isItemInHand(String name) {
        if (getMainHandName() != null)
            if (getMainHandName().equalsIgnoreCase(name))
                return true;

        if (getOffhandName() != null)
            if (getOffhandName().equalsIgnoreCase(name))
                return true;
        return false;
    }

    /**
     * Checks if the weapon name contains a piece of text
     * @param name
     * @return does contain
     */
    public boolean doesItemInHandsContain(String name) {
        if (getMainHandName() != null)
            if (getMainHandName().toLowerCase().contains(name.toLowerCase()))
                return true;

        if (getOffhandName() != null)
            if (getOffhandName().toLowerCase().contains(name.toLowerCase()))
                return true;
        return false;
    }

    public void equip(int fromSlotId, int toSlotId, Item item) {
        if (item == null) {
            getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You cannot equip that item.");
            return;
        }

        if (item.getTable() == null) {
            getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You cannot equip that item.");
            return;
        }

        InventoryContainer inventory = getPlayer().container(Containers.INVENTORY);
        if (inventory == null) {
            getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You cannot equip that item.");
            return;
        }

        //This is the specific slot for item id.
        EquipmentSlot slot = EquipmentSlot.forId(item.getTable().getEquipmentType());
        if (slot == null) {
            getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You cannot equip that item.");
            return;
        }

        if (slot == EquipmentSlot.NONE) {
            getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You cannot equip that item.");//testing message
            return;
        }

        EquipmentSlot toSlot = EquipmentSlot.forId(toSlotId);
        Item currentSlot = get(toSlotId);

        boolean isOffspecMainhand = (slot == EquipmentSlot.MAIN_HAND && toSlot == EquipmentSlot.OFFSPEC_MAIN_HAND);
        boolean isOffspecOffhand = (slot == EquipmentSlot.OFFSPEC_OFF_HAND && toSlot == EquipmentSlot.OFFSPEC_OFF_HAND);

        if (toSlotId != EquipmentSlot.GRIMOIRE_1.ordinal() && toSlotId != EquipmentSlot.GRIMOIRE_2.ordinal())
            if (!toSlot.equals(slot) && !isOffspecMainhand && !isOffspecOffhand)
                return;


        //If there is no equipment in the current slot
        if (currentSlot == null) {
            set(toSlotId, item);
            inventory.deleteItemFromSlotCompletely(fromSlotId);
            sendContainerPartialUpdate(Containers.Type.EQUIPMENT, toSlotId);
            if (toSlot == EquipmentSlot.MAIN_HAND)
                getPlayer().component(PlayerComponents.MODEL_BLOCK).getModel().setBlendId(item.getTable().getBlendPoseForWeapon());
            component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);

            //Grimoires - these have to be updated last
            if (toSlot == EquipmentSlot.GRIMOIRE_1 || toSlot == EquipmentSlot.GRIMOIRE_2) {
                container(Containers.ABILITY_BOOK).onClassSwitch();
                component(PlayerComponents.ACTION_BAR).onClassSwitch();
            }
            return;
        } else {
            //check for inventory space
            //TODO check bag space for arrows or stackables.
            if (inventory.isFull()) {
                getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Your inventory is currently full.");
                return;
            }
            if (toSlot == EquipmentSlot.MAIN_HAND)
                getPlayer().component(PlayerComponents.MODEL_BLOCK).getModel().setBlendId(item.getTable().getBlendPoseForWeapon());

            inventory.deleteItemFromSlotCompletely(fromSlotId);
            inventory.addItem(get(toSlotId));
            set(toSlotId, item);
            sendContainerPartialUpdate(Containers.Type.EQUIPMENT, toSlotId);


        }
        component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
    }

    /**
     *
     * @param fromSlotId
     * @param item
     */
    public void equip(int fromSlotId, Item item) {

        if (item == null) {
            getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You cannot equip that item.");
            return;
        }

        if (item.getTable() == null) {
            getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You cannot equip that item.");
            return;
        }

        InventoryContainer inventory = getPlayer().container(Containers.INVENTORY);
        if (inventory == null) {
            getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You cannot equip that item.");
            return;
        }

        //This is the specific slot for item id.
        EquipmentSlot slot = EquipmentSlot.forId(item.getTable().getEquipmentType());
        if (slot == null) {
            getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You cannot equip that item.");
            return;
        }

        if (slot == EquipmentSlot.NONE) {
            getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You cannot equip that item.");//testing message
            return;
        }

        if (slot == EquipmentSlot.GRIMOIRE_1 || slot == EquipmentSlot.GRIMOIRE_2) {
            if (get(EquipmentSlot.GRIMOIRE_1.ordinal()) == null) {
                inventory.deleteItemFromSlotCompletely(fromSlotId);
                inventory.addItem(get(EquipmentSlot.GRIMOIRE_1.ordinal()));
                set(EquipmentSlot.GRIMOIRE_1.ordinal(), item);
                sendContainerPartialUpdate(Containers.Type.EQUIPMENT, EquipmentSlot.GRIMOIRE_1.ordinal());
                getPlayer().container(Containers.ABILITY_BOOK).sendContainerFullUpdate(1);
                component(PlayerComponents.ACTION_BAR).onClassSwitch();
                return;
            }
            if (get(EquipmentSlot.GRIMOIRE_2.ordinal()) == null) {
                inventory.deleteItemFromSlotCompletely(fromSlotId);
                inventory.addItem(get(EquipmentSlot.GRIMOIRE_2.ordinal()));
                set(EquipmentSlot.GRIMOIRE_2.ordinal(), item);
                sendContainerPartialUpdate(Containers.Type.EQUIPMENT, EquipmentSlot.GRIMOIRE_2.ordinal());
                getPlayer().container(Containers.ABILITY_BOOK).sendContainerFullUpdate(2);
                component(PlayerComponents.ACTION_BAR).onClassSwitch();
                return;
            }
            if (get(EquipmentSlot.GRIMOIRE_1.ordinal()) != null && get(EquipmentSlot.GRIMOIRE_2.ordinal()) != null) {
                if (inventory.isFull()) {
                    getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Your inventory is currently full.");
                    return;
                }
                inventory.deleteItemFromSlotCompletely(fromSlotId);
                inventory.addItem(get(EquipmentSlot.GRIMOIRE_1.ordinal()));
                set(EquipmentSlot.GRIMOIRE_1.ordinal(), item);
                sendContainerPartialUpdate(Containers.Type.EQUIPMENT, EquipmentSlot.GRIMOIRE_1.ordinal());
                getPlayer().container(Containers.ABILITY_BOOK).sendContainerFullUpdate(1);
                component(PlayerComponents.ACTION_BAR).onClassSwitch();
                return;
            }
        }

        Item currentSlot = get(slot.ordinal());

        //If there is no equipment in the current slot
        if (currentSlot == null) {
            set(slot.ordinal(), item);
            inventory.deleteItemFromSlotCompletely(fromSlotId);
            sendContainerPartialUpdate(Containers.Type.EQUIPMENT, slot.ordinal());
            if (slot == EquipmentSlot.MAIN_HAND)
                getPlayer().component(PlayerComponents.MODEL_BLOCK).getModel().setBlendId(item.getTable().getBlendPoseForWeapon());
            component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
            return;
        } else {
            //check for inventory space
            //TODO check bag space for arrows or stackables.
            if (inventory.isFull()) {
                getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Your inventory is currently full.");
                return;
            }
            if (slot == EquipmentSlot.MAIN_HAND)
                getPlayer().component(PlayerComponents.MODEL_BLOCK).getModel().setBlendId(item.getTable().getBlendPoseForWeapon());
            inventory.deleteItemFromSlotCompletely(fromSlotId);
            inventory.addItem(get(slot.ordinal()));
            set(slot.ordinal(), item);
            sendContainerPartialUpdate(Containers.Type.EQUIPMENT, slot.ordinal());
        }
        component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);

    }


    /**
     *
     * @param slotId
     * @param itemId
     */
    public void unequip(int slotId, int itemId) {
        if (container(Containers.INVENTORY).addItem(itemId)) {

            set(slotId, null);
            sendContainerPartialUpdate(Containers.Type.EQUIPMENT, slotId);
            if (!hasWeapon())
                getPlayer().component(PlayerComponents.MODEL_BLOCK).getModel().setBlendId(AnimSequenceUDataTable.BlendPoses.NORMAL.ordinal());
            component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);

            //Grimoires - these have to be updated last
            if (slotId == EquipmentSlot.GRIMOIRE_1.ordinal() || slotId == EquipmentSlot.GRIMOIRE_2.ordinal()) {
                container(Containers.ABILITY_BOOK).onClassSwitch();
                component(PlayerComponents.ACTION_BAR).onClassSwitch();
            }
        }
    }

    /**
     *
     * @param fromSlotId
     * @param itemId
     * @param containerId
     * @param toSlotId
     */
    public void unequipToWidget(int fromSlotId, int itemId, int containerId, int toSlotId) {
        Item equipment = get(fromSlotId);
        if (equipment == null || itemId < 1) return;

        boolean added = false;
        switch (Containers.Type.forId(containerId)) {
            case INVENTORY:
                added = container(Containers.INVENTORY).addItem(equipment);
                break;
            case BANK:
                added = container(Containers.BANK).addItem(equipment);
                break;
        }

        if (added) {
            set(fromSlotId, null);
            getPlayer().component(PlayerComponents.MODEL_BLOCK).getModel().setBlendId(AnimSequenceUDataTable.BlendPoses.NORMAL.ordinal());
            sendContainerPartialUpdate(Containers.Type.EQUIPMENT, fromSlotId);
            component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);

            //Grimoires - these have to be updated last
            if (fromSlotId == EquipmentSlot.GRIMOIRE_1.ordinal() || fromSlotId == EquipmentSlot.GRIMOIRE_2.ordinal()) {
                container(Containers.ABILITY_BOOK).onClassSwitch();
                component(PlayerComponents.ACTION_BAR).onClassSwitch();
            }
        }
    }

    /**
     *
     * @param fromSlotId
     * @param toSlotId
     * @param itemId
     * @return
     */
    @Override
    public boolean switchSlots(int fromSlotId, int toSlotId, int itemId) {
      if ((fromSlotId == EquipmentSlot.GRIMOIRE_2.ordinal() && toSlotId == EquipmentSlot.GRIMOIRE_1.ordinal()) || (fromSlotId == EquipmentSlot.GRIMOIRE_1.ordinal() && toSlotId == EquipmentSlot.GRIMOIRE_2.ordinal())) {
          Item grim1 = get(EquipmentSlot.GRIMOIRE_1.ordinal());
          Item grim2 = get(EquipmentSlot.GRIMOIRE_2.ordinal());
          set(EquipmentSlot.GRIMOIRE_1.ordinal(), grim2);
          set(EquipmentSlot.GRIMOIRE_2.ordinal(), grim1);
          sendContainerPartialUpdate(Containers.Type.EQUIPMENT, toSlotId);
          sendContainerPartialUpdate(Containers.Type.EQUIPMENT, fromSlotId);
          container(Containers.ABILITY_BOOK).onClassSwitch();
          component(PlayerComponents.ACTION_BAR).onClassSwitch();
          return true;
      }
       return false;
    }


    /**
     * Sets the current tool to the specific item
     *      and sets the blend model
     * @param currentTool
     */
    public void setCurrentTool(Item currentTool) {
        this.currentTool = currentTool;
        getPlayer().component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
        if (currentTool == null) {
            getPlayer().component(PlayerComponents.MODEL_BLOCK).getModel().setBlendId(getEquipmentItemIdForSlot(EquipmentSlot.MAIN_HAND));
            return;
        }
        getPlayer().component(PlayerComponents.MODEL_BLOCK).getModel().setBlendId(currentTool.getTable().getBlendPoseForWeapon());
    }

    /**
     * Sends all equipment slots with their updated slot ids
     */
    public void sendUpdatedEquipmentSlots() {
        sendContainerFullUpdate(Containers.Type.EQUIPMENT);
    }

    /**
     *
     * @param slotId
     * @param itemId
     */
    public void sendUpdatedEquipmentSlot(int slotId, int itemId) {
        Item item = get(slotId);
        if (item != null) {
            World.UpdateItemContainerSlot.Builder equipmentSlot = World.UpdateItemContainerSlot.newBuilder();
            equipmentSlot.setContainerId(Containers.Type.EQUIPMENT.ordinal());
            equipmentSlot.setItemSlot(World.ItemSlot.newBuilder().setId(itemId).setAmount(item.getAmount()).build());
            getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_ITEM_CONTAINER_SLOT, equipmentSlot.build());
            getPlayer().component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
        }
    }

    /**
     * Checks if the character has a weapon or not
     * @return has a weapon
     */
    public boolean hasWeapon() {
        return getEquipmentItemIdForSlot(EquipmentSlot.MAIN_HAND) > 1 || getEquipmentItemIdForSlot(EquipmentSlot.OFF_HAND) > 1;
    }

    /**
     * Gets the Item Id for Equipment Slot
     * @param slot
     * @return
     */
    public int getEquipmentItemIdForSlot(EquipmentSlot slot) {
        return (get(slot.ordinal()) == null ? 0 : get(slot.ordinal()).getId());
    }

    /**
     * Gets the current main weapon for sync
     * @return
     */
    public int getCurrentMainWeaponForSync() {

        if (getPlayer().component(CharacterComponents.COMBAT).isDisarmed())
            return 0;


        //Current Tool gets chosen first
        if (currentTool != null)
            return currentTool.getId();

        return getEquipmentItemIdForSlot(EquipmentSlot.MAIN_HAND);
    }

    /**
     * Gets the current offhand weapon for sync
     * @return
     */
    public int getCurrentOffhandWeaponForSync() {
        if (getPlayer().component(CharacterComponents.COMBAT).isDisarmed()) return 0;
        //Current Tool gets chosen first
        if (currentTool != null)
            return 0;
        return getEquipmentItemIdForSlot(EquipmentSlot.OFF_HAND);
    }

    /**
     * Gets the Equipment {@link Item{ for Equipment Slot.}
     * @param slot
     * @return
     */
    public Item getEquipmentFromSlot(EquipmentSlot slot) {
        return get(slot.ordinal());
    }

    /**
     * Gets the Character of {@link com.shattered.game.GameObject}
     * @return
     */
    public Player getPlayer() {
        return (Player) gameObject;
    }

    /**
     * Method called when a 'Slot' is used.
     *
     * @param slotId
     * @param itemId
     */
    @Override
    public void onSlot(int slotId, int itemId) {
        unequip(slotId, itemId);
    }

    /**
     * Gets the amount of accuracy on all of the gear.
     * @return the total accuracy
     */
    public int getAccuracy() {
        int accuracy = 0;
        for (Item item : items) {
            if (item == null) continue;
            accuracy += item.getTable().getAccuracy();
        }
        return accuracy;
    }

    /**
     * Gets the amount of stamina on all of the gear.
     * @return the total stamina
     */
    public int getStamina() {
        int stamina = 0;
        for (Item item : items) {
            if (item == null) continue;
            stamina += item.getTable().getStamina();
        }
        return stamina;
    }

    /**
     * Gets the amount of stamina on all of the gear.
     * @return the total stamina
     */
    public int getStrength() {
        int strength = 0;
        for (Item item : items) {
            if (item == null) continue;
            strength += item.getTable().getStrength();
        }
        return strength;
    }

    /**
     * Gets the amount of speed  on all of the gear.
     * @return the total speed
     */
    public int getSpeed() {
        int speed = 0;
        for (Item item : items) {
            if (item == null) continue;
            speed += item.getTable().getSpeed();
        }
        return speed;
    }
    /**
     * Gets the amount of  on all of the gear.
     * @return the total
     */
    public int getCriticalStrikeChance() {
        int crit = 0;
        for (Item item : items) {
            if (item == null) continue;
            crit += item.getTable().getCriticalStrike();
        }
        return crit;
    }

    /**
     * Gets the amount of pvp power on all of the gear.
     * @return the total pvp power
     */
    public int getPvpPower() {
        int power = 0;
        for (Item item : items) {
            if (item == null) continue;
            power += item.getTable().getPvpPower();
        }
        return power;
    }

    /**
     * Gets the amount of pvp resistance on all of the gear.
     * @return the total pvp resistance
     */
    public int getPvpResistance() {
        int resistance = 0;
        for (Item item : items) {
            if (item == null) continue;
            resistance += item.getTable().getPvpResistance();
        }
        return resistance;
    }

    /**
     * Gets the amount of melee resistance on all of the gear.
     * @return the total melee resistance
     */
    public int getMeleeResistance() {
        int melee = 0;
        for (Item item : items) {
            if (item == null) continue;
            melee += item.getTable().getMeleeResistance();
        }
        return melee;
    }

    /**
     * Gets the amount of archery resistance on all of the gear.
     * @return the total archery resistance
     */
    public int getArcheryResistance() {
        int arrow = 0;
        for (Item item : items) {
            if (item == null) continue;
            arrow += item.getTable().getArrowResistance();
        }
        return arrow;
    }

    /**
     * Gets the amount of magic resistance on all of the gear.
     * @return the total magic resistance
     */
    public int getMagicResistance() {
        int magic = 0;
        for (Item item : items) {
            if (item == null) continue;
            magic += item.getTable().getMagicResistance();
        }
        return magic;
    }
}
