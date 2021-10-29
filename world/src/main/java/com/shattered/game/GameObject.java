package com.shattered.game;

import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.actor.object.component.transform.TransformComponent;
import com.shattered.game.actor.object.component.zone.ActorZoneComponent;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.ComponentManager;
import com.shattered.utilities.ecs.Components;
import com.shattered.utilities.ecs.ProcessInterval;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost 7/27/18 : 6:17 PM
 */
public abstract class GameObject {

    /**
     * Represents the Client Index
     */
    @Getter
    @Setter
    protected int id;

    /**
     * Represents the CharacterInformation ContainerManager
     */
    @Getter
    @Setter
    protected ComponentManager componentManager;

    /**
     *  Initializes the 'Containers' of the 'Game Object'
     * @param id
     */
    public GameObject(int id) {
        setId(id);
    }

    /**
     * Represents the Name of the Game Object
     * @return
     */
    public abstract String getName();

    /**
     * Represents when the 'Game Object' has Started
     */
    public abstract void onAwake();

    /**
     * Represents When the 'Game Object' is finished
     */
    public abstract void onFinish();

    /**
     * Adds the Components
     */
    public void addComponents() {
        getComponentManager().attatch(GameObjectComponents.TRANSFORM_COMPONENT, new TransformComponent(this));
        getComponentManager().attatch(GameObjectComponents.ZONE_COMPONENT, new ActorZoneComponent(this));
    }

    /**
     * Represents the 'Cycle' Method Call
     */
    public void onTick(long deltaTime) {
    }

    /**
     * Gets a piece of Game Object Component
     * @param components
     */
    public <T extends Component> T component(Components<T> components) {
        return getComponentManager().get(components);
    }



}
