package com.shattered.script.api;

import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.object.WorldObject;
import com.shattered.game.actor.object.component.transform.Rotation;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.script.api.impl.ActorVarAPI;
import com.shattered.script.api.impl.WorldAPI;
import com.shattered.script.api.impl.ZoneAPI;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@NonNull
public abstract class RelicObjectAPI extends RelicActorAPI {


    /**
     * Represents the Object referenced for API
     */
    @Getter
    protected final WorldObject object;

    /**
     * Represents the Actor Var API
     */
    public final ActorVarAPI vars;

    /**
     * Represents the Zone API
     * This API is used for any variabled related to the zone
     */
    public final ZoneAPI zone;

    /**
     * Creates a new Object API
     * @param object
     */
    public RelicObjectAPI(WorldObject object) {
        super(object);
        this.object = object;
        this.zone = new ZoneAPI(object);
        this.vars = new ActorVarAPI(object);
    }

    /**
     * Gets the object id
     * @return the object id
     */
    public abstract int getId();

    /**
     * Gets the Name of the World Object
     * @return the object's name
     */
    public abstract String getName();

    /**
     * Gets the current Actor State of this object
     * @return the actor state
     */
    public abstract ActorState getState();

    /**
     * Gets the {@link WorldObject}s Grid Coordinate
     * @return the location
     */
    public abstract GridCoordinate getLocation();

    /**
     * Gets the Object's Rotation
     * @return the rotation
     */
    public abstract Rotation getRotation();

    /**
     * Destroys the object, and sets its state to finished.
     */
    public abstract void destroy();


}
