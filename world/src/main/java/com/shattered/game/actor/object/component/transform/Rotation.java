package com.shattered.game.actor.object.component.transform;

import com.shattered.networking.proto.World;
import lombok.Data;

/**
 * @author JTlr Frost 10/24/2019 : 11:14 PM
 */
@Data
public class Rotation {

    /**
     * Represents the X Location
     */
    private float pitch;

    /**
     * Represents the Y Location
     */
    private float roll;

    /**
     * Represents the Z Location
     */
    private float yaw;

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public Rotation(float x, float y, float z) {
        this((int) x, (int) y, (int) z);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public Rotation(double x, double y, double z) {
        this((int) x, (int) y, (int) z);
    }

    /**
     *
     * @param pitch
     * @param roll
     * @param yaw
     */
    public Rotation(int pitch, int roll, int yaw) {
        this.pitch = pitch;
        this.roll = roll;
        this.yaw = yaw;
    }


    /**
     *
     * @param yaw
     */
    public Rotation(int yaw) {
        this(0.f, 0.f, yaw);
    }

    /**
     *
     * @param yaw
     */
    public Rotation(double yaw) {
        this(0.f, 0.f, yaw);
    }

    /**
     * Concvers the rotation to world rotation
     * @return world rotation
     */
    public World.WorldRotation toWorldRotation() {
        return World.WorldRotation.newBuilder().setRoll(getRoll()).setYaw(getYaw()).setPitch(getPitch()).build();
    }

}
