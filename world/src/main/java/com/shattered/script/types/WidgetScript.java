package com.shattered.script.types;

import com.shattered.script.api.RelicEngineAPI;
import com.shattered.script.api.RelicMathAPI;
import com.shattered.script.api.RelicUtilityAPI;
import com.shattered.script.api.RelicWorldAPI;
import com.shattered.script.api.impl.*;

import java.util.List;

public abstract class WidgetScript {

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

    /**
     * Represents the widget that utilizes this script
     * @return the widget name
     */
    public abstract String forwidget();

    /**
     * Method called upon the widget getting clicked with the specified itemId and buttonId
     * @param player
     * @param parameters
     * @param buttonId
     */
    public abstract void on_clicked_widget(PlayerAPI player, List<Integer> parameters, int buttonId);
}
