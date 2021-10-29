package com.shattered.script.impl;

import com.shattered.script.ScriptManager;

public class ScriptLoader {


    public static void init() {
        ScriptManager.getNpcCombatScripts().put("frilled lizzard", new FrilledLizzardCombatScript());
    }
}
