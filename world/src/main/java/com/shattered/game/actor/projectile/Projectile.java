package com.shattered.game.actor.projectile;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.object.component.transform.Rotation;
import com.shattered.game.actor.projectile.component.ProjectileComponentManager;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.utilities.VariableUtility;
import com.shattered.utilities.ecs.ProcessInterval;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * @author JTlr Frost 11/6/2019 : 3:08 PM
 */
@EqualsAndHashCode(callSuper = true)
public class Projectile extends Actor {


    /**
     * Represents the amount of one second cycles before getting destroyed
     */
    private static final int CYCLE_LIFE = 5;

    /**
     * Represents the Actor that sent the projectile
     */
    private final Actor owner;

    /**
     * Represents the Actor that it's going to
     */
    private final Actor target;

    /**
     * Represents the Projectiles UUID
     */
    private final int uuid;

    /**
     * Represents the timestamp of when it was created
     */
    private final long createdTime;

    /**
     * Represents the current life cycle of this projectile
     */
    private int cycle;

    /**
     * Creates a new projectile with the specified id, location and rotation
     * @param id
     * @param location
     * @param rotation
     * @param owner
     */
    public Projectile(int id, long createdTime, @NonNull GridCoordinate location, @NonNull Rotation rotation, @NonNull Actor owner, @NonNull Actor target) {//TODO should we assign an AbilityScript to this to call onHit from there?
        super(id);
        this.owner = owner;
        this.target = target;
        this.uuid = VariableUtility.random(0, Integer.MAX_VALUE);
        this.createdTime = createdTime;
        setComponentManager(new ProjectileComponentManager(this));
        addComponents();
        getComponentManager().onStart();
        setState(ActorState.ALIVE);
    }

    /**
     * Represents the Name of the Projectile
     *
     *
     * @return
     */
    @Override
    public String getName() {
        if (UDataTableRepository.getProjectileDataTable().containsKey(id))
            return UDataTableRepository.getProjectileDataTable().get(id).getName();
        return "Unavailable";
    }


    /**
     * Ticks every 30ms and 1000ms
     * @param deltaTime
     */
    @Override
    public void onTick(long deltaTime) {


        //TODO convert from deltaTime

        /*if (interval.equals(ProcessInterval.HALF_SECOND)) {


        }


        if (interval.equals(ProcessInterval.SECOND)) {
            if (cycle++ >= CYCLE_LIFE)
                onFinish();
        }*/

    }

    /**
     * Called upon projectile's end of life cycle
     */
    @Override
    public void onFinish() {
        setState(ActorState.FINISHED);
    }
}
