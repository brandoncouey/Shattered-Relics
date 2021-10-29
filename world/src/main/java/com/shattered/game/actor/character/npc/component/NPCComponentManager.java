package com.shattered.game.actor.character.npc.component;

import com.shattered.game.actor.Actor;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.component.WorldComponent;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.ComponentManager;

/**
 * @author JTlr Frost 9/2/2019 : 9:30 PM
 */
public class NPCComponentManager extends ComponentManager {
    
    
    /**
     * @param object
     */
    public NPCComponentManager(Object object) {
        super(object);
    }

    public void onClearedFlags() {
        for (Component components : getComponents().values()) {
            if (components == null) continue;
            if (components instanceof WorldComponent) {
                WorldComponent worldComponent = (WorldComponent) components;
                worldComponent.onClearedFlags();
            }
        }
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
