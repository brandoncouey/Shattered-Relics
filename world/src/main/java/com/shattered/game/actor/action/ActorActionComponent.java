package com.shattered.game.actor.action;

import com.shattered.account.Account;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.component.WorldComponent;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.WorldProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.script.types.ActionScript;
import com.shattered.utilities.ecs.ProcessInterval;
import com.shattered.utilities.ecs.ProcessComponent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

//TODO we need to bind this to a interval of 1000ms ticks
@ProcessComponent(interval = 1.f)
public class ActorActionComponent extends WorldComponent {

    /**
     * Represents the Current Action
     */
    @Getter
    @Setter
    private ActionScript action;

    /**
     * Prevents the on_tick from being called for this duration
     */
    @Getter
    @Setter
    private int delay;

    @Getter
    @Setter
    private long startTime;


    //TODO maybe NPC Object Action?

    //TODO maybe Player Object Action?

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public ActorActionComponent(Object gameObject) {
        super(gameObject);
    }

    /**
     * Used for component identifcation, left as-is.
     */
    public ActorActionComponent() {
        super(null);
    }

    @Override
    public void onStart() {
        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_REQUEST_STOP_ACTION, new WorldProtoListener<PacketOuterClass.EmptyPayload>() {

            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(PacketOuterClass.EmptyPayload message, Player player) {
                player.component(ActorComponents.ACTION).stopAction();
            }
        }, PacketOuterClass.EmptyPayload.getDefaultInstance());

    }

    /**
     * Starts a new {@link ActionScript} for the Actor
     * @param actorActionScript
     */
    public void startAction(@NonNull ActionScript actorActionScript) {
        if (isDoingAction()) return;
        if (actorActionScript.can_start()) {
            setStartTime(System.currentTimeMillis());
            setAction(actorActionScript);
            getAction().on_start();
        }
    }

    /**
     * Called every 30ms currently
     * TODO we must update this to every 1000ms
     */
    @Override
    public void onTick(long deltaTime) {

        //Ensures the current action is valid
        if (!isDoingAction()) return;

        //Ensures the actor is alive to continue
        if (!getActor().getState().equals(ActorState.ALIVE)) {
            stopAction();
            return;
        }

        //Cancels the action.
        if (getCharacter().component(ActorComponents.MOVEMENT).getLastTimeMoved() > getStartTime()) {
            stopAction();
            return;
        }

        //Ensures they have the required reqs to do this action.
        if (!getAction().can_start()) {
            stopAction();
            return;
        }

        //Overrides the wait delay
        if (delay > 0) {
            delay--;
            return;
        }

        if (!isDoingAction())
            return;

        int delay = getAction().on_tick();

        if (delay == -1) {
            stopAction();
            return;
        }

        //Append the current await delay to the on_tick delay
        this.delay += delay;
    }

    /**
     * Stops the Character from doing an Action
     */
    public void stopAction() {
        if (!isDoingAction()) return;
        getAction().on_finished();
        setAction(null);
    }


    /**
     * Checks if the actor is doing an action
     * @return performing an action
     */
    public boolean isDoingAction() {
        return action != null;
    }
}
