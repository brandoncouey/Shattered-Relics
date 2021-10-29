package com.shattered.game.grid;

import com.shattered.game.actor.object.component.transform.Vector3;
import com.shattered.networking.proto.World;

public class GridCoordinate extends Vector3 {



    /**
     * Creates a new Grid Coordinate with the given x, y and z.
     * @param x
     * @param y
     * @param z
     */
    public GridCoordinate(float x, float y, float z) {
        super(x, y, z);
    }

    /**
     * Creates a new Grid Coordinate with the given x and y.
     * @param x
     * @param y
     */
    public GridCoordinate(int x, int y) {
        super(x, y, 0);
    }

    /**
     * Creates a new Grid Coordinate with the given x and y.
     * @param x
     * @param y
     * @param z
     */
    public GridCoordinate(int x, int y, int z) {
        super(x, y, z);
    }

    /**
     * Converts the grid coordinate to world vector
     * @return world vector
     */
    public World.WorldVector toWorldVector() {
        return World.WorldVector.newBuilder().setX(getX()).setY(getY()).setZ(getZ()).build();
    }


}
