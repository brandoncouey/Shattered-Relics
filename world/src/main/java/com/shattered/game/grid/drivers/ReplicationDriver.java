package com.shattered.game.grid.drivers;

import com.shattered.game.actor.Actor;
import com.shattered.game.grid.ReplicationNode;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public abstract class ReplicationDriver{

    /**
     * Represents the X Size of the Map
     * This number is pulled from the client.
     */
    private final int sizeX;

    /**
     * Represents the Y Size of the Map
     * This number is pulled from the client.
     */
    private final int sizeY;

    /**
     * Represents the TileSize of the Map
     * This number is pulled from the client.
     */
    private final int tileSize;

    /**
     * Gets the ReplicationNode the Actor is currently in.
     * @param actor
     * @return the replication Node
     */
    public abstract ReplicationNode getNode(Actor actor);


}
