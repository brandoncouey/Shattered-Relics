package com.shattered.script.types;

import com.shattered.script.api.RelicEngineAPI;
import com.shattered.script.api.RelicMathAPI;
import com.shattered.script.api.RelicUtilityAPI;
import com.shattered.script.api.RelicWorldAPI;
import com.shattered.script.api.impl.*;

public abstract class CommandScript {

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

    /**
     * Gets the Command Name for Execution
     * @return
     */
    public abstract String name();

    /**
     * Method Called upon executing of the Command
     * @return
     */
    public abstract boolean on_execute(PlayerAPI api, String[] arguments);

}
