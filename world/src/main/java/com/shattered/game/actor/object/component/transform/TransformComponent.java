package com.shattered.game.actor.object.component.transform;

import com.shattered.game.GameObject;
import com.shattered.game.GameWorld;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.flags.FlagType;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.component.WorldComponent;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.game.grid.ReplicationGridNode;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.WorldProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import lombok.Getter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * @author JTlr Frost 10/24/2019 : 11:13 PM
 */
public class TransformComponent extends WorldComponent {


    /**
     * Represent's the Game Object's Location
     */
    @Getter private GridCoordinate location = new GridCoordinate(0, 0, 0);

    /**
     * Represents the Game Object's Rotation
     */
    @Getter private Rotation rotation = new Rotation(0, 0,0 );


    /**
     * Creates a new constructor
     *
     * @param gameObject
     */
    public TransformComponent(GameObject gameObject) {
        super(gameObject);
        if (isPlayer()) {
           this.location = new GridCoordinate(-5828, -1024, 193);
        }
    }

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
        if (getActor() instanceof Player) {
            ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_TRANSFORM_UPDATE, new WorldProtoListener<World.CharacterMovementInput>() {

                /**
                 * @param message
                 * @param player
                 */
                @Override
                public void handle(World.CharacterMovementInput message, Player player) {
                    player.component(ActorComponents.MOVEMENT).handleTransform(message);
                }
            }, World.CharacterMovementInput.getDefaultInstance());
        }
    }

    /**
     * Converts the transform to WorldTransform
     * @return the WorldTransform
     */
    public World.WorldTransform toWorldTransform() {
        return World.WorldTransform.newBuilder().setLocation(location.toWorldVector()).setRotation(rotation.toWorldRotation()).build();
    }

    /**
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {

    }

    /**
     * Called once per world cycle per each instance.
     */
    @Override
    public void onTick(long deltaTime) {

    }

    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {
        
    }

    /**
     * Gets the Node the Current Actor is in.
     * @return
     */
    public ReplicationGridNode getNode() {
        return (ReplicationGridNode) GameWorld.getReplicationGrid().getNode(getActor());
    }

    /**
     * Sets the Transform.
     * @param location
     * @param rotation
     */
    public void setTransform(GridCoordinate location, Rotation rotation) {
        this.location = location;
        this.rotation = rotation;
    }

    /**
     * Sets the Location of the {@link Actor}
     * @param location
     */
    public void setLocation(GridCoordinate location) {
        this.location = location;
    }

    /**
     * Sets the Location of the {@link Actor}
     * @param location
     */
    public void setLocation(Vector3 location) {
        this.location = new GridCoordinate(location.getX(), location.getY(), location.getZ());
    }

    /**
     * Sets the Location of the {@link Actor}
     * @param location
     */
    public void setLocation(Vector3D location) {
        this.location = new GridCoordinate((float) location.getX(), (float) location.getY(), (float) location.getZ());
    }

    /**
     * Sets the Rotation nof the {@link Actor}
     * @param rotation
     */
    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }


    /**
     * Gets the Actor from the {@link Object}
     * @return
     */
    public Actor getActor() { return (Actor) gameObject; }

    /**
     * Gets the Character from the {@link GameObject}
     * @return
     */
    public Player getPlayer() { return (Player) gameObject; }
}
