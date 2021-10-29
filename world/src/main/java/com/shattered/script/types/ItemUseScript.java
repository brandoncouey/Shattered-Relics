package com.shattered.script.types;

import com.shattered.game.actor.object.item.Item;
import com.shattered.script.api.RelicEngineAPI;
import com.shattered.script.api.RelicMathAPI;
import com.shattered.script.api.RelicUtilityAPI;
import com.shattered.script.api.RelicWorldAPI;
import com.shattered.script.api.impl.*;

public abstract class ItemUseScript {

    /**
     * Represents the Game World API
     */
    public final RelicWorldAPI world = new WorldAPI();

    /**
     * Represents the Data Table API
     * This API is used for grabbing selected information throughout all our data tables
     * i.e item, npc, object information
     */
    public final DataTableAPI tables = new DataTableAPI();

    /**
     * Represents the Game World API
     */
    public final RelicEngineAPI engine = new EngineAPI();

    /**
     * Represents the Math API
     */
    public final RelicMathAPI math = new MathAPI();

    /**
     * Represents the Utility API
     */
    public final RelicUtilityAPI utility = new UtilityAPI();

    public String[] for_items() {
        return null;
    }

    public String foritem() {
        return null;
    }


    /**
     * Method called when a item is processed for use
     * @param character
     * @param item
     * @param slotId
     */
    public void on_use(PlayerAPI character, Item item, int slotId) {

    }

}
