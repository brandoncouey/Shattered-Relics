package com.shattered.script.api.impl;

import com.shattered.engine.tasks.DelayedTaskTicker;
import com.shattered.script.ScriptManager;
import com.shattered.script.api.RelicEngineAPI;

public class EngineAPI extends RelicEngineAPI {

    /**
     * Reloads all scripts
     */
    @Override
    public void reload_scripts() {
        ScriptManager.reloadScripts();
    }

    /**
     * Reloads all scripts related to npcs
     */
    @Override
    public void reload_npc_scripts() {

    }

    /**
     * Reloads all scripts related to objects
     */
    @Override
    public void reload_object_scripts() {

    }

    /**
     * Reloads all scripts related to player
     */
    @Override
    public void reload_character_scripts() {

    }

    /**
     * Reloads all scripts related to commands
     */
    @Override
    public void reload_command_scripts() {

    }

    /**
     * Delays a Task
     *
     * @param task
     * @param delay
     */
    @Override
    public void delay_task(Runnable task, int delay) {
        DelayedTaskTicker.delayTask(task, delay);
    }
}
