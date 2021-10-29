package com.shattered.game.actor.object.component;

import com.shattered.game.actor.object.component.transform.TransformComponent;
import com.shattered.game.actor.object.component.zone.ActorZoneComponent;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.Components;

import java.util.function.Supplier;

/**
 * @author JTlr Frost 10/24/2019 : 11:12 PM
 */
public class GameObjectComponents<T extends Component> extends Components {
    
    
    /**
     * @param supplier
     */
    public GameObjectComponents(Supplier supplier) {
        super(supplier);
    }


    /**
     * Represents the World Manager Component
     */
    public static final Components<TransformComponent> TRANSFORM_COMPONENT = new Components<>(() -> new TransformComponent(null));

    /**
     * Represents the Zone Component
     */
    public static final Components<ActorZoneComponent> ZONE_COMPONENT = new Components<>(() -> new ActorZoneComponent(null));

}
