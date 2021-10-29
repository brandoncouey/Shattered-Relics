package com.shattered.script.api;

public abstract class RelicEngineAPI {


    /**
     * Reloads all scripts
     */
    public abstract void reload_scripts();

    /**
     * Reloads all scripts related to npcs
     */
    public abstract void reload_npc_scripts();

    /**
     * Reloads all scripts related to objects
     */
    public abstract void reload_object_scripts();

    /**
     * Reloads all scripts related to player
     */
    public abstract void reload_character_scripts();

    /**
     * Reloads all scripts related to commands
     */
    public abstract void reload_command_scripts();

    /**
     * Delays a Task
     * @param task
     * @param delay
     */
    public abstract void delay_task(Runnable task, int delay);

}
