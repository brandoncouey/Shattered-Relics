package com.shattered.game.actor.character.player.component.container.abilitybook;

import com.shattered.account.Account;
import com.shattered.datatable.UDataTableRepository;
import com.shattered.datatable.tables.AbilityUDataTable;
import com.shattered.game.actor.ability.Ability;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.container.Container;
import com.shattered.game.actor.container.Containers;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.utilities.VariableUtility;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;

public class AbilityBookContainer extends Container {

    /**
     * Represents the default Entry Conditions
     */
    private WhereConditionOption[] updateConditions = new WhereConditionOption[] { new WhereConditionOption("character_id", getPlayer().getId()), null };



    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public AbilityBookContainer(Object gameObject) {
        super(gameObject, UDataTableRepository.getAbilityDataTable().size());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onWorldAwake() {
        super.onWorldAwake();
        for (int index = 4; index < UDataTableRepository.getAbilityDataTable().size(); index++) {
            AbilityUDataTable ability = AbilityUDataTable.forId(index);
            if (ability != null && !ability.getName().contains("Unknown"))
                 add(ability.getName(), true);
        }
        sendUpdateFull();
    }

    /**
     * Method called when a 'Slot' is used.
     *
     * @param slotId
     * @param id
     */
    @Override
    public void onSlot(int slotId, int id) {
        //Don't want them to be able to use abilities with it open
    }

    /**
     * Sends the Data for the Action Bar
     */
    public void sendUpdateFull() {
        if (getActor() instanceof Player) {
            sendContainerFullUpdate(1);
            sendContainerFullUpdate(2);
        }
    }

    public void onClassSwitch() {
        sendContainerFullUpdate(1);
        sendContainerFullUpdate(2);
    }

    /**
     * Sends ALl items within inventory to the client.
     */
    public void sendContainerFullUpdate(int bookId) {
        if (getActor() instanceof Player) {
            World.UpdateItemContainerFull.Builder addItems = World.UpdateItemContainerFull.newBuilder();
            addItems.setContainerId((Containers.Type.GRIMOIRE_BOOK.ordinal() - 1) + bookId);
            for (int index = 0; index < items.length; index++) {
                World.ItemSlot.Builder itemBuilder = World.ItemSlot.newBuilder();
                Ability ability = (Ability) get(index);
                itemBuilder.setId(ability == null ? 0 : ability.getId() << 4 | 2);
                itemBuilder.setAmount(1);
                addItems.addItemSlot(itemBuilder);
            }
            getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_ITEM_CONTAINER_FULL, addItems.build());
        }
    }


    /**
     * Updates a shortcut index with the specified index, and new shortcut.
     * @param slotId
     */
    public void sendUpdatePartial(int slotId) {
        if (getActor() instanceof Player) {
            Ability ability = (Ability) get(slotId);
            World.UpdateItemContainerSlot.Builder builder = World.UpdateItemContainerSlot.newBuilder();
            builder.setContainerId(Containers.Type.GRIMOIRE_BOOK.ordinal());
            builder.setSlotId(slotId);
            builder.setItemSlot(World.ItemSlot.newBuilder().setId(ability == null ? 0 : ability.getId() << 4 | 2).setAmount(1).build());
            getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_ITEM_CONTAINER_SLOT, builder.build());
        }
    }

    /**
     *
     * @param slotId
     * @param bookId
     */
    public void sendUpdatePartial(int slotId, int bookId) {
        if (getActor() instanceof Player) {
            Ability ability = (Ability) get(slotId);
            World.UpdateItemContainerSlot.Builder builder = World.UpdateItemContainerSlot.newBuilder();
            builder.setContainerId(Containers.Type.GRIMOIRE_BOOK.ordinal() + bookId - 1);
            builder.setSlotId(slotId);
            builder.setItemSlot(World.ItemSlot.newBuilder().setId(ability == null ? 0 : ability.getId() << 4 | 2).setAmount(1).build());
            getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_ITEM_CONTAINER_SLOT, builder.build());
        }
    }

    /**
     *
     * @param abilityName
     * @param notify
     * @return
     */
    public boolean add(String abilityName, boolean notify) {
        Ability ability = new Ability(abilityName);
        if (ability == null) {
            System.out.println(abilityName);
            return false;
        }
        set(getNextSlotAvailable(), ability);
        if (notify) {
            getPlayer().component(PlayerComponents.WIDGET).sendLearnedNotification(abilityName, false);
        }
        return true;
    }


    @Override
    public String getDatabaseName() {
        return "shatteredrelics";
    }

    @Override
    public String getTableName() {
        return "spellbook";
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
        /*try {
            for (int index = 0; index < getItems().length; index++) {
                List<MySQLColumn> columns = new ArrayList<>();
                Item ability = get(index);
                columns.add(new MySQLColumn("slot_id", index));
                columns.add(new MySQLColumn("ability_id", ability == null ? 0 : ability.getId()));
                columns.add(new MySQLColumn("character_id", getPlayer().getId()));
                entry(getDatabaseName(), getTableName(), columns, MySQLCommand.INSERT);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return false;
    }

    @Override
    public boolean update() {
        /*try {
            for (int index = 0; index < getItems().length; index++) {
                List<MySQLColumn> columns = new ArrayList<>();
                Item item = get(index);
                updateConditions[1] = new WhereConditionOption("slot_id", index);
                columns.add(new MySQLColumn("ability_id", item == null ? 0 : item.getId()));
                entry(getDatabaseName(), getTableName(), columns, MySQLCommand.UPDATE);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return false;
    }

    @Override
    public boolean fetch() {
        /*ResultSet resultSet = getResults();
        try {
            if (!hasResults()) {
                insert();
                return true;
            }
            while (resultSet.next()) {
                int slotId = resultSet.getInt("slot_id");
                int abilityId = resultSet.getInt("ability_id");
                //set(slotId, abilityId);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        return true;
    }
}
