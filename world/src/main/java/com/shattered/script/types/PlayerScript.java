package com.shattered.script.types;

import com.shattered.script.api.*;
import com.shattered.script.api.impl.*;
import com.shattered.utilities.ecs.ProcessInterval;

public abstract class PlayerScript {


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


    public void on_awake(PlayerAPI api) {

    }

    public void on_world_awake(PlayerAPI api) {

    }

    public void on_update(PlayerAPI api, long deltaTime) {

    }

    public void on_death(PlayerAPI api, RelicCharacterAPI source) {

    }

    public void on_finished(PlayerAPI api) {

    }


}
