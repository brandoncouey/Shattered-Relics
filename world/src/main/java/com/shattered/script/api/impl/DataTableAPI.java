package com.shattered.script.api.impl;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.datatable.tables.*;
import com.shattered.script.api.RelicDataTableAPI;

public class DataTableAPI extends RelicDataTableAPI {
    
    /**
     * Represents the Item DataTable for the provided item id
     *
     * @param id
     * @return the item data table
     */
    @Override
    public ItemUDataTable item(int id) {
        return UDataTableRepository.getItemDataTable().get(id);
    }

    /**
     * Represents the Item Datatable for the provided item name
     *
     * @param name
     * @return the item data table
     */
    @Override
    public ItemUDataTable item(String name) {
        return ItemUDataTable.forName(name);
    }

    /**
     * Represents the NPC Data Table for the npc with the provided id
     *
     * @param id
     * @return the object data table
     */
    @Override
    public NPCUDataTable npc(int id) {
        return UDataTableRepository.getNpcDataTable().get(id);
    }

    /**
     * Represents the NPC Data Table for the provided npc with the provided name
     *
     * @param name
     * @return the npc data table
     */
    @Override
    public NPCUDataTable npc(String name) {
        return NPCUDataTable.forName(name);
    }

    /**
     * Represents the Object Data Table for the npc with the provided id
     *
     * @param id
     * @return the object data table
     */
    @Override
    public ObjectUDataTable obj(int id) {
        return UDataTableRepository.getObjectDataTable().get(id);
    }

    /**
     * Represents the Object Data Table for the provided npc with the provided name
     *
     * @param name
     * @return the object data table
     */
    @Override
    public ObjectUDataTable obj(String name) {
        return ObjectUDataTable.forName(name);
    }

    /**
     * Represents the Buff Data Table for the provided buff with the provided name
     *
     * @param name
     * @return the buff data table
     */
    @Override
    public BuffUDataTable buff(String name) {
        return BuffUDataTable.forName(name);
    }

    /**
     * Represents the Ability Data Table for the provided ability with the provided name
     *
     * @param name
     * @return the ability data table
     */
    @Override
    public AbilityUDataTable ability(String name) {
        return AbilityUDataTable.forName(name);
    }

    /**
     * Represents the Trades Data Table for the provided trade with the provided name
     *
     * @param name
     * @return the trades data table
     */
    @Override
    public TradesUDataTable trades(String name) {
        return TradesUDataTable.forTrade(name);
    }
}
