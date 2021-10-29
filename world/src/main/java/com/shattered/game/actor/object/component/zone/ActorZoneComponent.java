package com.shattered.game.actor.object.component.zone;

import com.shattered.database.mysql.MySQLColumn;
import com.shattered.database.mysql.MySQLCommand;
import com.shattered.game.GameObject;
import com.shattered.game.GameWorld;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.channel.ChannelComponents;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.flags.FlagType;
import com.shattered.game.actor.components.movement.MovementFlags;
import com.shattered.game.actor.object.WorldObject;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.component.WorldComponent;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.game.volume.Volume;
import com.shattered.game.grid.ReplicationGridNode;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.utilities.ecs.ProcessComponent;
import lombok.Getter;
import lombok.Setter;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author JTlr Frost 10/25/2019 : 4:49 PM
 */
@ProcessComponent
public class ActorZoneComponent extends WorldComponent {


    /**
     * Represents the Last Zone Id
     */
    @Getter
    @Setter
    private ReplicationGridNode previousCell;

    /**
     * Represents the last zone name
     */
    @Getter
    @Setter
    private String lastZoneName = "Unavailable";

    /**
     * Represents the Current Zone Name.
     */
    @Getter
    private String zoneName = "Unavailable";

    /**
     * Creates a new constructor
     *
     * @param gameObject
     */
    public ActorZoneComponent(GameObject gameObject) {
        super(gameObject);
    }

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
    }

    /**
     * Refreshes the Zone for this Actor
     */
    public void updateZone() {

        Actor actor = (Actor) gameObject;
        if (actor instanceof WorldObject || actor instanceof NPC || actor instanceof Player) {

            if (actor.getState().equals(ActorState.FINISHED)) {
                getNode().onRemoved(actor);
            }

            if (getPreviousCell() != null) {
                if (getPreviousCell() != getNode()) {
                    getPreviousCell().onRemoved(actor);
                    getNode().onAdd(actor);
                    setPreviousCell(getNode());
                }
            } else {
                getNode().onAdd(actor);
                setPreviousCell(getNode());
            }
        }
    }

    /**
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {
        updateZone();
    }

    /**
     * Called once per world cycle per each instance.
     */
    @Override
    public void onTick(long deltaTime) {

        updateZone();
        if (isPlayer()) {
            //TODO loop through volumes in area.
            for (Map.Entry<String, Volume> entrySet : GameWorld.getVolumes().entrySet()) {
                if (entrySet == null) continue;
                if (getPlayer().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().isInsideVolumeIgnoreZ(entrySet.getValue())) {
                    String mapName = entrySet.getKey().replace("_", " ");

                    // set if null
                    if (getLastZoneName() == null) {
                        setLastZoneName(mapName);
                        setZoneName(mapName);
                        sendMessage(PacketOuterClass.Opcode.SMSG_ENTERED_AREA_NOTIFICATION, World.AreaNotification.newBuilder().setZoneName(mapName).build());
                        getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You have entered " + mapName);
                        return;
                    }

                    //When the player has entered a new area.
                    if (!mapName.equalsIgnoreCase(getZoneName())) {
                        setLastZoneName(getZoneName());
                        setZoneName(mapName);
                        sendMessage(PacketOuterClass.Opcode.SMSG_ENTERED_AREA_NOTIFICATION, World.AreaNotification.newBuilder().setZoneName(mapName).build());
                        getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You have entered " + mapName);
                        return;
                    }
                }
            }
        }
    }

    /**
     *
     * @param zoneName
     */
    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;

        if (isPlayer()) {
            getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.FRIENDS_CHANNEL).sendUpdatedOnlineStatus();
        }
    }


    /**
     * Teleports the actor to a specifc location
     * @param coordinate
     */
    public void teleport(GridCoordinate coordinate) {
        getActor().component(GameObjectComponents.TRANSFORM_COMPONENT).setLocation(coordinate);
        getActor().component(ActorComponents.FLAG_UPDATE).flag(FlagType.TRANSFORM);
        getActor().component(ActorComponents.MOVEMENT).flag(MovementFlags.TELEPORT);
    }

    @Override
    public String getDatabaseName() {
        return "shatteredrelics";
    }

    @Override
    public String getTableName() {
        return "zone";
    }

    @Override
    public WhereConditionOption[] getFetchConditions() {
        return new WhereConditionOption[] { new WhereConditionOption("characterId", getPlayer().getId()) };
    }

    @Override
    public WhereConditionOption[] getUpdateConditions() {
        return new WhereConditionOption[] { new WhereConditionOption("characterId", getPlayer().getId()) };
    }

    @Override
    public boolean insert() {
        try {
            List<MySQLColumn> columns = new ArrayList<>();
            columns.add(new MySQLColumn("characterId", getPlayer().getId()));
            columns.add(new MySQLColumn("zone_name", getZoneName()));
            columns.add(new MySQLColumn("location_x", getPlayer().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().getX()));
            columns.add(new MySQLColumn("location_y", getPlayer().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().getY()));
            columns.add(new MySQLColumn("location_z", getPlayer().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().getZ()));
            columns.add(new MySQLColumn("rotation_yaw", getPlayer().component(GameObjectComponents.TRANSFORM_COMPONENT).getRotation().getYaw()));
            entry(getDatabaseName(), getTableName(), columns, MySQLCommand.INSERT);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update() {
        try {
            List<MySQLColumn> columns = new ArrayList<>();
            columns.add(new MySQLColumn("zone_name", getZoneName()));
            columns.add(new MySQLColumn("location_x", getPlayer().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().getX()));
            columns.add(new MySQLColumn("location_y", getPlayer().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().getY()));
            columns.add(new MySQLColumn("location_z", getPlayer().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().getZ()));
            columns.add(new MySQLColumn("rotation_yaw", getPlayer().component(GameObjectComponents.TRANSFORM_COMPONENT).getRotation().getYaw()));
            entry(getDatabaseName(), getTableName(), columns, MySQLCommand.UPDATE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean fetch() {
        ResultSet results = getResults();
        try {
            if (results.next()) {
                setZoneName(results.getString("zone_name"));
                getPlayer().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().setX(results.getInt("location_x"));
                getPlayer().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().setY(results.getInt("location_y"));
                getPlayer().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().setZ(results.getInt("location_z"));
                getPlayer().component(GameObjectComponents.TRANSFORM_COMPONENT).getRotation().setYaw(results.getInt("rotation_yaw"));
            } else {
                insert();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {
        updateZone();
    }

    /**
     * Gets the Current Zone
     * @return their current node
     */
    public ReplicationGridNode getNode() {
        return (ReplicationGridNode) GameWorld.getReplicationGrid().getNode(getActor());
    }

    /**
     * Casts the Game Object ot a World Object
     * @return
     */
    public WorldObject getWorldObject() {
        return (WorldObject) gameObject;
    }

    /**
     * Casts and gets the Game Object to a NPC
     * @return
     */
    public NPC getNpc() {
        return (NPC) gameObject;
    }

    /**
     * Casts the Game Object to a Character
     * @return
     */
    public Player getPlayer() {
        return (Player) gameObject;
    }

}
