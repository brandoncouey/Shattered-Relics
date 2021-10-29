package com.shattered.script.api;

import com.shattered.datatable.tables.*;

public abstract class RelicDataTableAPI {

    /**
     * Represents the Item DataTable for the provided item id
     * @param id
     * @return the item data table
     */
    public abstract ItemUDataTable item(int id);

    /**
     * Represents the Item Datatable for the provided item name
     * @param name
     * @return the item data table
     */
    public abstract ItemUDataTable item(String name);


    /**
     * Represents the NPC Data Table for the npc with the provided id
     * @param id
     * @return the object data table
     */
    public abstract NPCUDataTable npc(int id);

    /**
     * Represents the NPC Data Table for the provided npc with the provided name
     * @param name
     * @return the npc data table
     */
    public abstract NPCUDataTable npc(String name);

    /**
     * Represents the Object Data Table for the npc with the provided id
     * @param id
     * @return the object data table
     */
    public abstract ObjectUDataTable obj(int id);

    /**
     * Represents the Object Data Table for the provided object with the provided name
     * @param name
     * @return the object data table
     */
    public abstract ObjectUDataTable obj(String name);

    /**
     * Represents the Buff Data Table for the provided buff with the provided name
     * @param name
     * @return the buff data table
     */
    public abstract BuffUDataTable buff(String name);

    /**
     * Represents the Ability Data Table for the provided ability with the provided name
     * @param name
     * @return the ability data table
     */
    public abstract AbilityUDataTable ability(String name);

    /**
     * Represents the Trades Data Table for the provided trade with the provided name
     * @param name
     * @return the trades data table
     */
    public abstract TradesUDataTable trades(String name);




}
