package com.shattered.game.volume.trigger;

import com.shattered.game.actor.object.component.transform.Vector3;
import com.shattered.game.volume.Volume;

public class TriggerVolume extends Volume {

    /**
     * Creates a new Trigger Volume
     *
     * @param origin
     * @param extent
     */
    public TriggerVolume(Vector3 origin, Vector3 extent) {
        super(origin, extent);
    }
}
