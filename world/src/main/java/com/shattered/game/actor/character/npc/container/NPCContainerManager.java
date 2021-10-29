package com.shattered.game.actor.character.npc.container;

import com.shattered.game.actor.Actor;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.component.WorldComponent;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.ComponentManager;

/**
 * Represents the NPC Container Manager
 */
public class NPCContainerManager extends ComponentManager {


    /**
     * Creates a new Container Manager for the {@link NPC}
     * @param npc
     */
    public NPCContainerManager(NPC npc) {
        super(npc);
    }

    /**
     * Called Upon Onstart of the NPC
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Method Called upon the {@link NPC} dying
     */
    public void onDeath(Actor source) {
        for (Component components : getComponents().values()) {
            if (components == null) continue;
            if (components instanceof WorldComponent) {
                WorldComponent worldComponent = (WorldComponent) components;
                worldComponent.onDeath(source);
            }
        }
    }
}
