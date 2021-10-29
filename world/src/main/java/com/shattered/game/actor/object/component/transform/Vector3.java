package com.shattered.game.actor.object.component.transform;

import com.shattered.game.GameObject;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.volume.Volume;
import com.shattered.networking.proto.World;
import com.shattered.utilities.math.MathUtilities;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * @author JTlr Frost 10/24/2019 : 11:14 PM
 */
@Data
public class Vector3 {


    /**
     * Represents the X Location
     */
    private float x;

    /**
     * Represents the Y Location
     */
    private float y;

    /**
     * Represents the Z Location
     */
    private float z;

    /**
     * Creates a new Vector3D with the given points.
     * @param x
     * @param y
     * @param z
     */
    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates a new Vector3D with the given points.
     * @param x
     * @param y
     * @param z
     */
    public Vector3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creatres a new Vector3 casting doubles to floats.
     * @param x
     * @param y
     * @param z
     */
    public Vector3(double x, double y, double z) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }

    /**
     * Gets the X into a Integer Type
     * @return
     */
    public float getX() {
        return x;
    }

    /**
     * Gets the Y into a Integer Type.
     * @return
     */
    public float getY() {
        return y;
    }

    /**
     * Gets the Z into a Integer Type.
     * @return
     */
    public float getZ() {
        return z;
    }

    /**
     * Checks if the current location is inside of a volume no matter what the Z (Height is).
     * @param volume
     * @return current vector is within the volume's extent ignoring the Z (height)
     */
    public boolean isInsideVolumeIgnoreZ(Volume volume) {
        float maxX = volume.getOrigin().getX() + volume.getExtent().getX();
        float minX = volume.getOrigin().getX() - volume.getExtent().getX();

        float maxY = volume.getOrigin().getY() + volume.getExtent().getY();
        float minY = volume.getOrigin().getY() - volume.getExtent().getY();

        return (x <= maxX && x >= minX) && (y <= maxY &&  y >= minY);
    }

    /**
     * Checks if the current location is inside of a volume no matter what the Z (Height is).
     * @param volume
     * @return the location is within the volume's extent, ignoring the Z (Height)
     */
    public static boolean isInsideVolumeIgnoreZ(Vector3 location, Volume volume) {
        float maxX = volume.getOrigin().getX() + volume.getExtent().getX();
        float minX = volume.getOrigin().getX() - volume.getExtent().getX();

        float maxY = volume.getOrigin().getY() + volume.getExtent().getY();
        float minY = volume.getOrigin().getY() - volume.getExtent().getY();

        return (location.getX() <= maxX && location.getX() >= minX) && (location.getY() <= maxY &&  location.getY() >= minY);
    }

    /**
     * Checks if the current location is inside of a Volume.
     * @param volume
     * @return current vector is within a volume's extent.
     */
    public boolean isInsideVolume(Volume volume) {
        float maxX = volume.getOrigin().getX() + volume.getExtent().getX();
        float minX = volume.getOrigin().getX() - volume.getExtent().getX();

        float maxY = volume.getOrigin().getY() + volume.getExtent().getY();
        float minY = volume.getOrigin().getY() - volume.getExtent().getY();

        float maxZ = volume.getOrigin().getZ() + volume.getExtent().getZ();
        float minZ = volume.getOrigin().getZ() - volume.getExtent().getZ();

        return (x <= maxX && x >= minX) && (y <= maxY &&  y >= minY) && (z <= maxZ && z >= minZ);
    }

    /**
     * Checks if the specified location is inside of a volume.
     * @param location
     * @param volume
     * @return bounds inside the volume
     */
    public static boolean isInsideVolume(Vector3 location, Volume volume) {
        float maxX = volume.getOrigin().getX() + volume.getExtent().getX();
        float minX = volume.getOrigin().getX() - volume.getExtent().getX();

        float maxY = volume.getOrigin().getY() + volume.getExtent().getY();
        float minY = volume.getOrigin().getY() - volume.getExtent().getY();

        float maxZ = volume.getOrigin().getZ() + volume.getExtent().getZ();
        float minZ = volume.getOrigin().getZ() - volume.getExtent().getZ();

        return (location.getX() <= maxX && location.getX() >= minX) && (location.getY() <= maxY &&  location.getY() >= minY) && (location.getZ() <= maxZ && location.getZ() >= minZ);
    }


    /** Gets the distance between two locations. The Result is UNITS.
     * @param gameObject
     * @return the distance between this vector and the specified game object's vector
     */
    public int distanceTo(@NonNull GameObject gameObject) {
        return distanceTo(gameObject.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation());
    }

    /**
     * Checks if this vector and the specified game object is within the specified units
     * @param gameObject
     * @param units
     * @return is within the units
     */
    public boolean isWithinUnits(@NonNull GameObject gameObject, int units) {
        return distanceTo(gameObject.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation()) <= units;
    }

    /**
     * Checks if this vector and the specified vector is within the specified units
     * @param target
     * @param units
     * @return is within the units
     */
    public boolean isWithinUnits(Vector3 target, int units) {
        return distanceTo(target) <= units;
    }

    /** Gets the distance between two locations. The Result is UNITS.
     * @param target
     * @return the distance to the target in ue4 units
     */
    public int distanceTo(Vector3 target) {
        return (int) Math.sqrt(MathUtilities.square(target.x - x) + MathUtilities.square(target.y - y) + MathUtilities.square(target.z - z));
    }

    /** Gets the distance between two locations. The Result is UNITS.
     * @param target
     * @return the distance to the target in ue4 units
     */
    public int distanceToXY(Vector3 target) {
        return (int) Math.sqrt(MathUtilities.square(target.x - x) + MathUtilities.square(target.y - y));
    }

    /** Gets the distance between two locations. The Result is UNITS.
     * @param origin
     * @param target
     * @return the distance in units to the target vector.
     */
    public static double distanceTo(Vector3 origin, Vector3 target) {
        return Math.sqrt(MathUtilities.square(target.x-origin.x) + MathUtilities.square(target.y-origin.y) + MathUtilities.square(target.z-origin.z));
    }

    /**
     * Gets the Direction to a Target in degrees
     * @param target
     * @return the direction in degrees
     */
    public float directionTo(Vector3 target) {
        Vector3D direction = new Vector3D(target.getX() - x, target.getY() - y, target.getZ() - z).normalize();
        return (float) MathUtilities.getAngle(direction.getX(), direction.getY());
    }

    /**
     * Gets the Direction to a Target in degrees
     * @param target
     * @return the direction in degrees
     */
    public static float directionTo(Vector3 current, Vector3 target) {
        Vector3D direction = new Vector3D(target.getX() - current.getX(), target.getY() - current.getY(), target.getZ() - current.getZ()).normalize();
        return (float) MathUtilities.getAngle(direction.getX(), direction.getY());
    }


    /** Compute the dot-product of two vectors.
     * first vector is the instance of this Vector3
     * @param value second vector
     * @return the dot product instance.v2
     */
    public double dotProduct(Vector3 value) {
        return Vector3D.dotProduct(toVector3D(), value.toVector3D());
    }

    /** Compute the dot-product of two vectors.
     * @param v1 first vector
     * @param v2 second vector
     * @return the dot product v1.v2
     */
    public static double dotProduct(Vector3 v1, Vector3 v2) {
        return Vector3D.dotProduct(v1.toVector3D(), v2.toVector3D());
    }


    /**
     * Converts the Current {@link Vector3} to a {@link Vector3D} object.
     * @return Vector3 to Vector3D conversion.
     */
    public Vector3D toVector3D() {
        return new Vector3D(x, y, z);
    }

    /**
     * Converts the Current {@link Vector3} to a {@link Vector3D} object.
     * @return Vector3 to Vector3D conversion.
     */
    public Vector2D toVector2D() {
        return new Vector2D(x, y);
    }

    /**
     * Takes the current vector 3 and sets the z to 0
     * @return the flattened vector location
     */
    public Vector3 flatten() {
        return new Vector3(x, y, 0);
    }

    /**
     * Gets the length of the vector
     * @return the length
     */
    public double length() {
        return Math.sqrt((x * x) + (y * y) + (z * z));
    }

    /**
     * Normalizes the specific vector
     * @return the current vector normalized
     */
    public Vector3 normalize() {
        double len = length();
        if (len == 0.0) {
            return new Vector3(0.f, 0.f, 0.f);
        }
        return new Vector3(x / len, y / len, z / len);
    }

    /**
     * Converts the current vector to degrees of rotation
     * @return the degrees
     */
    public float rotation() {
        double rotation = Math.atan2(y, x);
        return (float) Math.toDegrees(rotation);
    }

    /**
     * Converts the object to a Proto DTO
     * @return the DTO
     */
    public World.WorldVector toProto() {
        return World.WorldVector.newBuilder().setX(x).setY(y).setZ(z).build();
    }

}
