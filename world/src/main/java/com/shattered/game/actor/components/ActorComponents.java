package com.shattered.game.actor.components;

import com.shattered.game.actor.action.ActorActionComponent;
import com.shattered.game.actor.components.animation.ActorAnimSequenceComponent;
import com.shattered.game.actor.components.flags.ActorFlagUpdateComponent;
import com.shattered.game.actor.components.interaction.ActorInteractionFlaggerComponent;
import com.shattered.game.actor.components.movement.ActorMovementComponent;
import com.shattered.game.actor.components.variable.ActorTransVariableComponent;
import com.shattered.game.actor.components.variable.ActorVariableComponent;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.Components;

import java.util.function.Supplier;

/**
 * @author JTlr Frost 10/29/2019 : 9:27 PM
 */
public class ActorComponents <T extends Component> extends Components {


    /**
     * Represents the Updating Flag Updates
     */
    public static final Components<ActorFlagUpdateComponent> FLAG_UPDATE = new Components<>(() -> new ActorFlagUpdateComponent(null));

    /**
     * Represents the Movement Component
     */
    public static final Components<ActorMovementComponent> MOVEMENT = new Components<>(() -> new ActorMovementComponent(null));

    /**
     * Represents the Animation Component
     */
    public static final Components<ActorAnimSequenceComponent> ANIMATION = new Components<>(() -> new ActorAnimSequenceComponent(null));

    /**
     * Represents Actor Variable Component
     */
    public static final Components<ActorVariableComponent> VAR = new Components<>(() -> new ActorVariableComponent(null));

    /**
     * Represents Actor Trans Variable Component (Transient)
     * These do not save, and are session based.
     */
    public static final Components<ActorTransVariableComponent> TRANS_VAR = new Components<>(() -> new ActorTransVariableComponent(null));

    /**
     * Represents Actor Interaction Component
     * This is used for handling actor interaction settings.
     */
    public static final Components<ActorInteractionFlaggerComponent> INTERACTION = new Components<>(() -> new ActorInteractionFlaggerComponent(null));

    /**
     * Represents the Actor Action Component
     * This is used for handling actions that are a continuation on-going process. i.e fishing.
     */
    public static final Components<ActorActionComponent> ACTION = new Components<>(() -> new ActorActionComponent(null));

    /**
     * @param supplier
     */
    public ActorComponents(Supplier supplier) {
        super(supplier);
    }
}
