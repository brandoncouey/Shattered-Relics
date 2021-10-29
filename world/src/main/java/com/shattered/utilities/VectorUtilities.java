package com.shattered.utilities;

import com.shattered.game.actor.object.component.transform.Vector3;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class VectorUtilities {

    /**
     *
     * @param v1
     * @param v2
     * @return
     */
    public static Vector3D multiply(Vector3D v1, Vector3D v2) {
        return new Vector3D(v1.getX() * v2.getX(), v1.getY() * v2.getY(), v1.getZ() * v2.getZ());
    }

    public static Vector3D multiply(Vector3D v1, double amount) {
        return new Vector3D(v1.getX() * amount, v1.getY() * amount, v1.getZ() * amount);
    }

    public static Vector3 multiply(Vector3 v1, double amount) {
        return new Vector3(v1.getX() * amount, v1.getY() * amount, v1.getZ() * amount);
    }

    /**
     *
     * @param v1
     * @param v2
     * @return
     */
    public static Vector3D add(Vector3D v1, Vector3D v2) {
        return new Vector3D(v1.getX() + v2.getX(), v1.getY() + v2.getY(), v1.getZ() + v2.getZ());
    }

    public static Vector3D subtract(Vector3D v1, Vector3D v2) {
        return new Vector3D(v1.getX() - v2.getX(), v1.getY() - v2.getY(), v1.getZ() - v2.getZ());
    }

    public static Vector3 add(Vector3 v1, Vector3 v2) {
        return new Vector3(v1.getX() + v2.getX(), v1.getY() + v2.getY(), v1.getZ() + v2.getZ());
    }

    public static Vector3 subtract(Vector3 v1, Vector3 v2) {
        return new Vector3(v1.getX() - v2.getX(), v1.getY() - v2.getY(), v1.getZ() - v2.getZ());
    }

    public static boolean isZero(Vector3D v1) {
        return v1.getX() == 0 && v1.getY() == 0 && v1.getZ() == 0;
    }

}
