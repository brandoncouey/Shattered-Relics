package com.shattered.game.actor.character.player.component.quest;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuestRequirements {

    /**
     * Represents Integer Var
     */
    private Map<String, Integer> ivars = new HashMap<>();

    /**
     * Represents Boolean Vars
     */
    private Map<String, Boolean> bvars = new HashMap<>();

    /**
     * Represents String Vars
     */
    private Map<String, String> svars = new HashMap<>();

    public void vars(String var, int value) {
        ivars.put(var, value);
    }

    public void vars(String var, boolean value) {
        bvars.put(var, value);
    }

    public void vars(String var, String value) {
        svars.put(var, value);
    }

}
