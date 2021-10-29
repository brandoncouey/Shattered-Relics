package com.shattered.game.actor.character.player.component.managers;

import com.shattered.game.GameObject;
import com.shattered.account.Account;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.grid.ReplicationGridNode;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.PriorityComponent;
import com.shattered.utilities.ecs.ProcessComponent;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JTlr Frost 9/7/2019 : 7:40 PM
 */
@ProcessComponent
public class WorldLevelManagerComponent extends Component {

    /**
     * Represents the World has been Loaded
     */
    @Getter
    @Setter
    private boolean worldLevelLoaded;

    /**
     * Represents World Loaded
     */
    @Getter
    @Setter
    private boolean worldLoaded;

    /**
     * Represents the 
     */
    @Getter
    @Setter
    private Map<Integer, ReplicationGridNode> zoneMap;

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public WorldLevelManagerComponent(GameObject gameObject) {
        super(gameObject);
        setZoneMap(new ConcurrentHashMap<>());
    }

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
        
    }

    /**
     * Initializes Once the 'ChannelLine' Has been Awakened.
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
        if (isWorldLevelLoaded() && !isWorldLoaded())
            sendWorldReady();
    }

    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {
        
    }

    /**
     * Sends World is Ready
     */
    public void sendWorldReady() {
        setWorldLoaded(true);
        getCharacter().sendMessage(PacketOuterClass.Opcode.SMSG_WORLD_READY, PacketOuterClass.EmptyPayload.newBuilder().build());
    }

    /**
     * Gets the Character
     * @return
     */
    public Player getCharacter() {
        return (Player) gameObject;
    }

    /**
     * Database name.
     */
    @Override
    public String getDatabaseName() {
        return null;
    }

    /**
     * Table name.
     */
    @Override
    public String getTableName() {
        return null;
    }
}
