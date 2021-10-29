package com.shattered.game.volume;

import com.shattered.game.actor.object.component.transform.Vector3;
import lombok.Data;
import lombok.Getter;

@Data
public class Volume {

    /**
     * Represents the Origin of the Volume
     */
    @Getter
    private final Vector3 origin;

    /**
     * Represents the Extent of the Volume
     */
    @Getter
    private final Vector3 extent;


    /**
     * Creates a new Volume
     * @param origin
     * @param extent
     */
    public Volume(Vector3 origin, Vector3 extent) {
        this.origin = origin;
        this.extent = extent;
    }

}
