package com.shattered.game.actor.character.player.component.actionbar;

import com.shattered.account.Account;
import com.shattered.database.mysql.MySQLColumn;
import com.shattered.database.mysql.MySQLCommand;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.combat.ClassTypes;
import com.shattered.game.actor.character.player.component.combat.PlayerCombatComponent;
import com.shattered.game.actor.character.player.component.container.equipment.EquipmentSlot;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.component.WorldComponent;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import lombok.Getter;
import lombok.Setter;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerActionBarComponent extends WorldComponent {


    /**
     * Represents the Maximum number of shortcuts for an action bar.
     */
    public static final int MAX_SHORTCUTS = 7;

    /**
     * Represents the current ability bars
     * Key = ClassName
     * Key (default_bar) = No Class
     */
    @Getter
    private final Map<ClassTypes, Map<Integer, Shortcut>> abilityBars = new ConcurrentHashMap<>();

    /**
     * Represents the Current Class Type
     */
    @Getter
    @Setter
    private ClassTypes classType = ClassTypes.NONE;

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public PlayerActionBarComponent(Object gameObject) {
        super(gameObject);
        //Auto fills the objects
        Arrays.stream(ClassTypes.values()).forEach(type -> abilityBars.put(type, new ConcurrentHashMap<>()));
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
        onClassSwitch();
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
     * Changes the Actionbar
     * @param type
     */
    public void setActionBar(ClassTypes type) {
        if (type == null)
            type = ClassTypes.NONE;
        setClassType(type);
        sendUpdateFull();
    }

    public void onClassSwitch() {
        setActionBar(ClassTypes.forGrimoire(container(Containers.EQUIPMENT).getItemForSlot(EquipmentSlot.GRIMOIRE_1)));
    }

    /**
     * Sets the Current Bar Index
     * @param index
     * @param shortcut
     */
    public void setShortcut(int index, Shortcut shortcut) {
        if (index < 0 || index > MAX_SHORTCUTS)
            return;

        if (shortcut == null) {
            getAbilityBars().get(getClassType()).remove(index);
            sendUpdatePartial(index);
            return;
        }
        abilityBars.get(getClassType()).put(index, shortcut);
        sendUpdatePartial(index);

        //Sends the ability having a cooldown
        if (shortcut.getType() == ShortcutType.ABILITY) {
            if (!component(CharacterComponents.COMBAT).getCooldowns().containsKey(shortcut.getId())) return;
            long remaining = component(CharacterComponents.COMBAT).getCooldowns().get(shortcut.getId());
            if (remaining > 0)
                sendMessage(PacketOuterClass.Opcode.SMSG_DISPLAY_COOLDOWN_TIMER, World.AbilityCooldown.newBuilder().setAbilityId(shortcut.getId()).setDuration(remaining).build());
        }

    }

    /**
     *
     * @param index
     * @return
     */
    public Shortcut getShortcut(int index) {
        return abilityBars.get(getClassType()).get(index);
    }

    /**
     * Sends ALl items within action bar to the client.
     */
    public void sendUpdateFull() {
        if (getActor() instanceof Player) {
            World.UpdateItemContainerFull.Builder addItems = World.UpdateItemContainerFull.newBuilder();
            addItems.setContainerId(Containers.Type.ACTION_BAR.ordinal());
            for (int index = 0; index < MAX_SHORTCUTS; index++) {
                Shortcut shortcut = getShortcut(index);
                World.ItemSlot.Builder abilityBuilder = World.ItemSlot.newBuilder();
                abilityBuilder.setId(shortcut == null ? 0 : shortcut.getId() << 4 | shortcut.getType().ordinal() + 1).setAmount(shortcut == null ? 0 : 1);
                addItems.addItemSlot(abilityBuilder.build());
            }
            getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_ITEM_CONTAINER_FULL, addItems.build());
            for (int index = 0; index < MAX_SHORTCUTS; index++) {
                Shortcut shortcut = getShortcut(index);

                //Sends the ability having a cooldown
                if (shortcut != null) {
                    if (shortcut.getType() == ShortcutType.ABILITY) {
                        if (!component(CharacterComponents.COMBAT).getCooldowns().containsKey(shortcut.getId())) continue;
                        long remaining = component(CharacterComponents.COMBAT).getCooldowns().get(shortcut.getId());
                        if (remaining > 0)
                            sendMessage(PacketOuterClass.Opcode.SMSG_DISPLAY_COOLDOWN_TIMER, World.AbilityCooldown.newBuilder().setAbilityId(shortcut.getId()).setDuration(remaining).build());
                    }
                }
            }
        }
    }


    /**
     * Updates a shortcut index with the specified index, and new shortcut.
     * @param slotId
     */
    public void sendUpdatePartial(int slotId) {
        if (getActor() instanceof Player) {
            Shortcut shortcut = getShortcut(slotId);
            World.UpdateItemContainerSlot.Builder builder = World.UpdateItemContainerSlot.newBuilder();
            builder.setContainerId(Containers.Type.ACTION_BAR.ordinal());
            builder.setSlotId(slotId);
            builder.setItemSlot(World.ItemSlot.newBuilder().setId(shortcut == null ? 0 : shortcut.getId() << 4 | shortcut.getType().ordinal() + 1).setAmount(1).build());
            getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_ITEM_CONTAINER_SLOT, builder.build());
        }
    }

    /**
     * Switches one action bar slot to the next.
     * @param fromSlotId
     * @param toSlotId
     * @param itemId
     */
    public void switchSlots(int fromSlotId, int toSlotId, int itemId) {
        Shortcut shortcut = getShortcut(fromSlotId);
        Shortcut toShortcut = getShortcut(toSlotId);
        if (shortcut == null)
            return;

        if (fromSlotId == toSlotId) return;

        if (shortcut != null && toShortcut != null) {
            setShortcut(toSlotId, shortcut);
            setShortcut(fromSlotId, toShortcut);
            return;
        }
        setShortcut(toSlotId, shortcut);
        setShortcut(fromSlotId, null);
    }

    /**
     *
     * @param slotId
     * @param id
     */
    public void onSlot(int slotId, int id) {
        if (slotId < 0 || slotId > MAX_SHORTCUTS) return;
        if (getShortcut(slotId) == null) return;
        Shortcut shortcut = getShortcut(slotId);
        if (id != shortcut.getId()) return;

        switch (shortcut.getType()) {

            case ITEM: {
                container(Containers.INVENTORY).handleItem(shortcut.getId());
                break;
            }

            default:
                getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Unhandled shortcut type!");
                break;

        }
    }

    public void onSlot(int slotId, int id, float pitch) {
        if (slotId < 0 || slotId > MAX_SHORTCUTS) return;
        if (getShortcut(slotId) == null) return;
        Shortcut shortcut = getShortcut(slotId);
        if (id != shortcut.getId()) return;

        switch (shortcut.getType()) {

            case ABILITY: {
                PlayerCombatComponent combat = (PlayerCombatComponent) getPlayer().component(CharacterComponents.COMBAT);
                combat.onAbilityRequest(id, pitch);
                break;
            }

            default:
                getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Unhandled shortcut type!");
                break;

        }
    }


    /**
     * Gets the SQL Database Name
     * @return
     */
    @Override
    public String getDatabaseName() {
        return "shatteredrelics";
    }

    /**
     * Gets the SQL Table Name
     * @return
     */
    @Override
    public String getTableName() {
        return "action_bar";
    }

    /**
     * Gets the Fetch Conditions for SQL Table
     * @return
     */
    @Override
    public WhereConditionOption[] getFetchConditions() {
        return new WhereConditionOption[] { new WhereConditionOption("character_id", getPlayer().getId()) };
    }

    /**
     * Gets the Entry Conditions for SQL Table
     * @return
     */
    @Override
    public WhereConditionOption[] getUpdateConditions() {
        return new WhereConditionOption[] { new WhereConditionOption("character_id", getPlayer().getId()) };
    }

    @Override
    public boolean insert() {
        try {
            List<MySQLColumn> columns = new ArrayList<>();
            columns.add(new MySQLColumn("character_id", getPlayer().getId()));
            for (Map.Entry<ClassTypes, Map<Integer, Shortcut>> bars : abilityBars.entrySet()) {
                StringBuilder items = new StringBuilder();
                for (int index = 0; index < MAX_SHORTCUTS; index++) {

                    if (!bars.getValue().containsKey(index)) {
                        items.append("0:0");
                    } else {
                        items.append(bars.getValue().get(index).getId() + ":" + bars.getValue().get(index).getType().ordinal());
                    }
                }
                columns.add(new MySQLColumn(bars.getKey().name().toLowerCase() + "_bar", items.toString()));
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

            for (Map.Entry<ClassTypes, Map<Integer, Shortcut>> bars : abilityBars.entrySet()) {
                StringBuilder items = new StringBuilder();
                for (int index = 0; index < MAX_SHORTCUTS; index++) {

                    if (!bars.getValue().containsKey(index)) {
                        items.append("0:0");
                    } else {
                        items.append(bars.getValue().get(index).getId() + ":" + bars.getValue().get(index).getType().ordinal());
                    }

                    if (index != MAX_SHORTCUTS)
                        items.append(",");

                }
                columns.add(new MySQLColumn(bars.getKey().name().toLowerCase() + "_bar", items.toString()));
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
            if (resultSet.next()) {
                int slot;
               for (ClassTypes classType : ClassTypes.values()) {
                   slot = 0;
                   String column = resultSet.getString(classType.name().toLowerCase() + "_bar");
                   String[] slots = column.split(",");
                   if (slot > MAX_SHORTCUTS) continue;
                   for (String x : slots) {
                       String[] result = x.split(":");
                       int id = Integer.parseInt(result[0]);
                       int type = Integer.parseInt(result[1]);
                       if (id != 0)
                           getAbilityBars().get(classType).put(slot, new Shortcut(id, ShortcutType.values()[type]));
                       slot++;
                   }
               }
                return true;
            } else {
                insert();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

}
