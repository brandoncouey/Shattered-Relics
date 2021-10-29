package com.shattered.script.types;


import com.shattered.script.api.RelicEngineAPI;
import com.shattered.script.api.RelicMathAPI;
import com.shattered.script.api.RelicUtilityAPI;
import com.shattered.script.api.RelicWorldAPI;
import com.shattered.script.api.impl.*;
import com.shattered.utilities.ecs.ProcessInterval;

public abstract class ObjectScript  {


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
     * Represents the List Obejcts that utilize this script
     * @return an array of object names
     */
    public String[] for_objects() {
        return null;
    }

    /**
     * Represents the 'single' object that utilizes this script
     * @return the single object that uses this script
     */
    public String forobject() {
        return null;
    }


    public void on_awake(ObjectAPI api) {

    }

    public void on_world_awake(ObjectAPI api) {

    }

    public void on_update(ObjectAPI api, long deltaTime) {

    }

    public void on_finished(ObjectAPI api) {

    }

    public void on_normal_interact(PlayerAPI character, ObjectAPI obj) {

    }

    public void on_shift_interact(PlayerAPI character, ObjectAPI obj) {

    }

    public void on_cntrl_interact(PlayerAPI character, ObjectAPI obj) {

    }

}
