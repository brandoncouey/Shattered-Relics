package com.shattered.game.actor.character.player.component;

import com.shattered.game.actor.Actor;
import com.shattered.game.component.WorldComponent;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.ComponentManager;

/**
 * @author JTlr Frost 9/2/2019 : 9:30 PM
 */
public class PlayerComponentManager extends ComponentManager {
    
    
    /**
     * @param object
     */
    public PlayerComponentManager(Object object) {
        super(object);
    }


    /**
     * Method called once flags have been cleared.
     */
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
     * Method Called upon the {@link Character} is dying
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
