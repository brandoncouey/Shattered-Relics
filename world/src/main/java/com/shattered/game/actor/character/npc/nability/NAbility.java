package com.shattered.game.actor.character.npc.nability;

import com.shattered.game.actor.Actor;
import com.shattered.game.actor.character.Character;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.script.types.AbilityScript;
import lombok.Getter;

public class NAbility extends NPC {

    /**
     * Represents the owner of the Ability Script
     */
    @Getter
    private final Character owner;

    /**
     * Represents the ability script that is associated with this NPC that is an ability
     */
    @Getter
    private final AbilityScript abilityScript;

    /**
     * Represents the life cycle of this actor in seconds
     * -1 = infinite
     */
    @Getter
    public int lifeCycle = -1;

    /**
     * Creates and Spawns a new NPC with the provided id, and grid coordinate.
     *
     * @param id
     * @param coordinate
     */
    public NAbility(int id, GridCoordinate coordinate, Character owner, AbilityScript abilityScript) {
        super(id, coordinate);
        this.owner = owner;
        this.abilityScript = abilityScript;
    }

    @Override
    public void onAwake() {
        super.onAwake();
    }

    @Override
    public void onDeath(Actor source) {
        super.onDeath(source);
    }

    @Override
    public void onTick(long deltaTime) {
        super.onTick(deltaTime);


        /*if (interval == ProcessInterval.DEFAULT) return;

        abilityScript.on_tick(new PlayerAPI((Player) owner), interval);

        if (interval == ProcessInterval.SECOND) {
            if (lifeCycle == 0) {
                GameWorld.removeNPC(this);
                setState(ActorState.FINISHED);
                return;
            }
            if (lifeCycle > 1)
                lifeCycle--;
        }*/
    }
}
