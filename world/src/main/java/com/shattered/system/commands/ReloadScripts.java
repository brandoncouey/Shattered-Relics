package com.shattered.system.commands;

import com.shattered.script.ScriptManager;
import com.shattered.system.SystemCommand;

public class ReloadScripts implements SystemCommand {
    /**
     * @return the Syntaxs
     */
    @Override
    public String[] getSyntax() {
        return new String[] {"reloadscripts"};
    }

    /**
     * @param args
     * @return
     */
    @Override
    public boolean execute(String... args) {
        ScriptManager.reloadScripts();
        return true;
    }
}
