package com.shattered.game.actor.character.player.component.synchronize.object;

import com.shattered.game.GameObject;
import com.shattered.game.GameWorld;
import com.shattered.account.Account;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.flags.ActorFlagUpdateComponent;
import com.shattered.game.actor.components.flags.FlagType;
import com.shattered.game.actor.object.WorldObject;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.actor.object.component.transform.TransformComponent;
import com.shattered.game.grid.ReplicationGridNode;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.utilities.VariableUtility;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.ProcessComponent;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JTlr Frost 9/9/2019 : 4:43 AM
 */
public class ObjectSynchronizeComponent extends Component {

    /**
     * Represents the Npcs
     */
    @Getter
    @Setter
    private Map<Integer, WorldObject> local;

    /**
     * Represents the Map of appendQueue account
     */
    @Getter
    @Setter
    private Map<Integer, WorldObject> appendQueue;


    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public ObjectSynchronizeComponent(GameObject gameObject) {
        super(gameObject);
        setLocal(new ConcurrentHashMap<>());
        setAppendQueue(new ConcurrentHashMap<>());
    }

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
        checkRegionForAdditional();
    }

    private void checkRegionForAdditional() {
        Player player = (Player) gameObject;
        ReplicationGridNode node = (ReplicationGridNode) GameWorld.getReplicationGrid().getNode(player);
        for (WorldObject obj : node.getNodeObjects().values()) {
            if (obj == null) continue;
            if (!getLocal().containsKey(obj.getClientIndex())) {
                getLocal().put(obj.getClientIndex(), obj);
                getAppendQueue().put(obj.getClientIndex(), obj);
            }
        }

        for (ReplicationGridNode adjacent : node.getAdjacentNodes().values()) {
            if (adjacent == null) continue;
            for (WorldObject adjObj : adjacent.getNodeObjects().values()) {
                if (adjObj == null) continue;
                if (!getLocal().containsKey(adjObj.getClientIndex())) {
                    getLocal().put(adjObj.getClientIndex(), adjObj);
                    getAppendQueue().put(adjObj.getClientIndex(), adjObj);
                }
            }
        }



        /*ReplicationGridNode node = (ReplicationGridNode) GameWorld.getReplicationGrid().getNode(getCharacter());
        for (WorldObject object : node.getNodeObjects().values()) {
            if (object == null) continue;

            if (needsAdded(object)) {
                if (!getLocal().containsKey(object.getClientIndex())) {
                    getLocal().put(object.getClientIndex(), object);
                    getAppendQueue().put(object.getClientIndex(), object);
                }
            }
        }*/
    }

    /**
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {
        checkRegionForAdditional();
    }

    /**
     * Called once per world cycle per each instance.
     */
    @Override
    public void onTick(long deltaTime) {
        checkRegionForAdditional();
        World.ObjectSynchronize.Builder builder = World.ObjectSynchronize.newBuilder();
        for (WorldObject object : getLocal().values()) {
            if (object == null) continue;
            World.LocalObject.Builder objectBuilder = World.LocalObject.newBuilder();
            ActorFlagUpdateComponent flagUpdate = object.component(ActorComponents.FLAG_UPDATE);
            TransformComponent transform = object.component(GameObjectComponents.TRANSFORM_COMPONENT);
            boolean needsAdded = needsAdded(object);
            boolean needsRemoved = needsRemoved(object);

            objectBuilder.setObjectId(object.getId());
            objectBuilder.setObjectIndex(object.getClientIndex());
            objectBuilder.setFlags(flagUpdate.getFlags());

            if (needsAdded || flagUpdate.isFlagged(FlagType.MODEL_BLOCK)) {
                objectBuilder.setObjectName(VariableUtility.formatString(object.getName()));
                objectBuilder.setInteractFlags(object.component(ActorComponents.INTERACTION).getFlags());
            }

            if (needsAdded || flagUpdate.isFlagged(FlagType.TRANSFORM)) {
                objectBuilder.setTransform(World.WorldTransform.newBuilder()
                        .setLocation(World.WorldVector.newBuilder().
                                setX(transform.getLocation().getX()).
                                setY(transform.getLocation().getY()).
                                setZ(transform.getLocation().getZ()).
                                build()).
                                setRotation(World.WorldRotation.newBuilder().setYaw(transform.getRotation().getYaw())).build());
            }

            objectBuilder.setNeedsAdded(needsAdded);
            objectBuilder.setNeedsRemoved(needsRemoved);

            builder.addLocalObject(objectBuilder.build());

            if (needsRemoved)
                getLocal().remove(object.getClientIndex());

            //Removes the Client Index from the Queued if popped.
            getAppendQueue().remove(object.getClientIndex());

        }
        getCharacter().sendMessage(PacketOuterClass.Opcode.SMSG_OBJECT_SYNCHRONIZE, builder.build());
    }

    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {

    }

    /**
     *
     * Check if the Object Needs to be Added
     *
     * @param object
     * @return
     */
    public boolean needsAdded(WorldObject object) {
        return getAppendQueue().containsKey(object.getClientIndex());
    }

    /**
     * Check if the Object Needs to be Removed
     * @param object
     * @return
     */
    public boolean needsRemoved(WorldObject object) {
        return object == null || !object.getState().equals(ActorState.ALIVE);
    }

    /**
     * Gets the Character
     * @return the player
     */
    public Player getCharacter() {
        return (Player) gameObject;
    }
}
