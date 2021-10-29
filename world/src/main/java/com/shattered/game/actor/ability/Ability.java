package com.shattered.game.actor.ability;

import com.shattered.datatable.tables.AbilityUDataTable;
import com.shattered.game.actor.object.item.Item;

public class Ability extends Item {

    /**
     *
     * @param name
     */
    public Ability(String name) {
        this(AbilityUDataTable.forId(name));
    }

    /**
     * Creates a new Item with the provided id with a set amount of 1.
     *
     * @param id
     */
    public Ability(int id) {
        super(id);
    }

    public AbilityUDataTable getDataTable() {
        return AbilityUDataTable.forId(id);
    }
}
