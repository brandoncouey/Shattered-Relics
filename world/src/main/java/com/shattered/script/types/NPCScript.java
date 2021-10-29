package com.shattered.script.types;

import com.shattered.script.api.*;
import com.shattered.script.api.impl.*;
import com.shattered.utilities.ecs.ProcessInterval;

public abstract class NPCScript {

    /**
     * Represents the Game World API
     */
    public final RelicWorldAPI world = new WorldAPI();

    /**
     * Represents the Game World API
     */
    public final RelicEngineAPI engine = new EngineAPI();

    /**
     * Represents the Data Table API
     * This API is used for grabbing selected information throughout all our data tables
     * i.e item, npc, object information
     */
    public final DataTableAPI tables = new DataTableAPI();

    /**
     * Represents the Math API
     */
    public final RelicMathAPI math = new MathAPI();

    /**
     * Represents the Utility API
     */
    public final RelicUtilityAPI utility = new UtilityAPI();

    public String[] for_npcs() {
        return null;
    }

    public String fornpc() {
        return null;
    }

    /**
     * Registers the NPC to a Vendor
     * @return the vendor name
     */
    public String register_to_vendor() { return null; }


    public void on_awake(NpcAPI api) {

    }

    public void on_world_awake(NpcAPI api) {

    }

    public void on_update(NpcAPI api, long deltaTime) {

    }

    public void on_finished(NpcAPI api) {

    }

    public void on_death(NpcAPI api, RelicCharacterAPI source) {

    }

    public void on_normal_interact(PlayerAPI character, NpcAPI npc) {

    }

    public void on_shift_interact(PlayerAPI character, NpcAPI npc) {

    }

    public void on_cntrl_interact(PlayerAPI character, NpcAPI npc) {

    }

}
