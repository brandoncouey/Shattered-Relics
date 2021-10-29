package com.shattered.game;

import com.shattered.game.actor.ActorList;
import com.shattered.game.actor.ActorType;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.channel.ChannelType;
import com.shattered.game.actor.character.player.component.widget.WidgetEventRepository;
import com.shattered.game.actor.object.WorldObject;
import com.shattered.game.actor.object.component.transform.Rotation;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.game.volume.Volume;
import com.shattered.game.grid.builders.GridNodeBuilder;
import com.shattered.game.grid.drivers.ReplicationGridDriver;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.script.ScriptManager;
import com.shattered.script.impl.ScriptLoader;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JTlr Frost 8/31/2019 : 4:39 AM
 */
public class GameWorld {

    /**
     * Represents the World Construct State
     */
    enum WorldState { LOADING, LOADED }

    /**
     * Represents the World State
     */
    public static WorldState WORLD_STATE = WorldState.LOADING;

    /**
     * Represents the Game World Name
     */
    public static String WORLD_NAME = "Unavailable";

    /**
     * Represents the Game World Type
     */
    public static String WORLD_TYPE = "Normal";

    /**
     * Represents the Game World Location
     */
    public static String WORLD_LOCATION = "United States (East)";

    /**
     * Represents the Maximum Players
     */
    public static final int MAXIMUM_PLAYERS = 4096;

    /**
     * Represents the Maximum NPCs
     */
    public static final int MAXIMUM_NPCS = 100_000;

    /**
     * Represents the Maximum Objects
     */
    public static final int MAXIMUM_OBJECTS = 100_000;

    /**
     * Represents All Players within the World
     */
    @Getter
    private static final ActorList<Player> characters = new ActorList<>(MAXIMUM_PLAYERS, ActorType.CHARACTER);

    /**
     * Represents All NPC's within the World
     */
    @Getter
    private static final ActorList<NPC> npcs = new ActorList<>(MAXIMUM_NPCS, ActorType.NPC);

    /**
     * Represents ALL Object's within the World
     */
    @Getter
    private static final ActorList<WorldObject> objects = new ActorList<>(MAXIMUM_OBJECTS, ActorType.OBJECT);

    /**
     * Represents a Map of Trigger Volumes
     */
    @Getter
    private static final Map<String, Volume> volumes = new HashMap<>();

    /**
     * Represents a map of Location Points
     */
    @Getter
    private static final Map<String, GridCoordinate> locationPoints = new HashMap<>();

    /**
     * Gets the Zone Manager Component
     */
    @Getter
    private static final ReplicationGridDriver replicationGrid = new ReplicationGridDriver(64, 64, 12600);


    /**
     * Initializes the Game World
     */
    public static void initialize(String worldName, String worldLocation, String worldType) {
        WORLD_NAME = worldName;
        WORLD_LOCATION = worldLocation;
        WORLD_TYPE = worldType;

        try {
            WidgetEventRepository.parseRepository();
            ScriptManager.init(false);
            ScriptLoader.init();
            GridNodeBuilder.parseNPCsExports();
            GridNodeBuilder.parseObjectsExports();
            GridNodeBuilder.parseMapVolumeExports();
            GridNodeBuilder.parseLocationPoints();
        } catch (Exception e) {
            e.printStackTrace();
        }
        WORLD_STATE = WorldState.LOADED;

    }

    /**
     * Method called upon Channel Server Connection Initialized
     */
    public static void onChannelConnect() {
        for (Player player : characters) {
            if (player == null) continue;
            player.component(PlayerComponents.SOCIAL_CHANNEL).onChannelConnect();
        }
    }


    /**
     * Sends a system message to all available players
     * @param message
     */
    public static void sendSystemMessage(String message) {
        for (Player player : getCharacters()) {
            if (player == null) continue;
            player.component(PlayerComponents.SOCIAL_CHANNEL).sendMessage(ChannelType.SYSTEM_MESSAGE, message);
        }
    }

    /**
     * Sends a system message to the specified player
     * @param player
     * @param message
     */
    public static void sendSystemMessage(@NonNull Player player, String message) {
        player.component(PlayerComponents.SOCIAL_CHANNEL).sendMessage(ChannelType.SYSTEM_MESSAGE, message);
    }

    /**
     * Spawns a {@link com.shattered.game.actor.emitter.Emitter} at the specified grid coordinate.
     * @param id
     * @param coordinate
     * @param rotation
     */
    public static void spawnEmitterAtLocation(int id, GridCoordinate coordinate, Rotation rotation) {
        for (Player player : getCharacters()) {
            if (player == null) continue;
            World.WorldTransform.Builder transform = World.WorldTransform.newBuilder();
            transform.setLocation(World.WorldVector.newBuilder().
                    setX(coordinate.getX()).
                    setY(coordinate.getY()).
                    setZ(coordinate.getZ()).
                    build());
            player.sendMessage(PacketOuterClass.Opcode.SMSG_SPAWN_EMITTER, World.SpawnEmitter.newBuilder().setEmitterId(id).setTransform(transform.build()).build());
        }
    }


    //Welp spawn projectile is going to be a bit harder than i thought it was going to be.

    /**
     * Gets the Population of the World
     * @return the population of the world
     */
    public static String getPopulation() {
        if (characters.size() >= MAXIMUM_PLAYERS)
            return "Full";
        if (characters.size() >= MAXIMUM_PLAYERS / 1.2)
            return "Very High";
        if (characters.size() >= MAXIMUM_PLAYERS / 1.5)
            return "High";
        if (characters.size() >= MAXIMUM_PLAYERS / 3)
            return "Medium";
        if (characters.size() >= MAXIMUM_PLAYERS / 5)
            return "Low";
        return "Very Low";
    }


    /**
     * Finds an player within the World
     * @param playerName
     * @return player
     */
    public static Player findPlayer(String playerName) {
        synchronized (characters) {
            for (Player player : characters) {
                if (player == null) continue;
                if (playerName.equalsIgnoreCase(player.getPlayerInformation().getName()))
                    return player;
            }
        }
        return null;
    }

    /**
     * Finds a player within the world by it's world zoneId.
     * @param index
     * @return
     */
    public static Player findPlayer(int index) {
        synchronized (characters) {
            for (Player player : getCharacters()) {
                if (player == null) continue;
                if (player.getClientIndex() == index)
                    return player;
            }
        }
        return null;
    }

    /**
     * Finds the Character By UUID
     * @param uuid
     * @return
     */
    public static Player findPlayerByUuid(int uuid) {
        synchronized (characters) {
            for (Player player : getCharacters()) {
                if (player == null) continue;
                if (player.getAccount().getAccountInformation().getAccountId() == uuid)
                    return player;
            }
        }
        return null;
    }

    /**
     * Checks if an CharacterInformation is within the World
     * @param playerName
     * @return the world contains the player
     */
    public static boolean containsPlayer(String playerName) {
        synchronized (characters) {
            for (Player player : characters) {
                if (player == null)
                    continue;
                if (player.getName().equalsIgnoreCase(playerName))
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks if a Player is in the world given their client index
     * @param clientIndex
     * @return the world contains the player
     */
    public static boolean containsPlayer(int clientIndex) {
        synchronized (characters) {
            for (Player player : characters) {
                if (player == null)
                    continue;
                if (player.getClientIndex() == clientIndex)
                    return true;
            }
        }
        return false;
    }

    /**
     * Adds the {@link Player} to the {@code World}
     * @param player
     * @return added
     */
    public static boolean addPlayer(Player player) {
        synchronized (characters) {
            if (characters.contains(player))
                return true;
            characters.add(player);
            //RedisAccountStatusDB.getInstance().getCommands().set(player.getAccount().getAccountInformation().getAccountId(), AccountInformation.OnlineStatus.WORLD.ordinal());
            return true;
        }
    }

    /**
     * Removes The {@link Player} from the {@code World}.
     * @param player
     * @return removed
     */
    public static boolean removePlayer(Player player) {
        synchronized (characters) {
            if (!characters.contains(player))
                return true;
            characters.remove(player);
            //RedisAccountStatusDB.getInstance().getCommands().set(player.getAccount().getAccountInformation().getAccountId(), AccountInformation.OnlineStatus.OFFLINE.ordinal());
            return true;
        }
    }

    /**
     * Finds an NPC within the World
     * @param npcName
     * @return NPC
     */
    public static NPC findNPC(String npcName) {
        for (NPC npc : npcs) {
            if (npc == null) continue;
            if (npcName.equalsIgnoreCase(npc.getName()))
                return npc;
        }
        return null;
    }

    /**
     * Finds a NPC within the World
     * @param clientIndex
     * @return
     */
    public static NPC findNPC(int clientIndex) {
        for (NPC npc : npcs) {
            if (npc == null) continue;
            if (clientIndex == npc.getClientIndex())
                return npc;
        }
        return null;
    }

    /**
     * Checks if an NPC is within the World
     * @param npcName
     * @return
     */
    public static boolean containsNPC(String npcName) {
        for (NPC npc : npcs) {
            if (npc == null)
                continue;
            if (npcName.equalsIgnoreCase(npc.getName()))
                return true;
        }
        return false;
    }

    /**
     * Adds the {@link NPC} to the {@code World}
     * @param npc
     * @return added
     */
    public static void addNPC(NPC npc) {
        synchronized (npcs) {
            npcs.add(npc);
        }
    }

    /**
     * Removes The {@link NPC} from the {@code World}.
     * @param npc
     * @return removed
     */
    public static void removeNPC(NPC npc) {
        synchronized (npcs) {
            npcs.remove(npc);
        }
    }

    /**
     * Finds a Object within the World
     * @param clientIndex
     * @return
     */
    public static WorldObject findObject(int clientIndex) {
        for (WorldObject object : objects) {
            if (object == null) continue;
            if (clientIndex == object.getClientIndex())
                return object;
        }
        return null;
    }

    /**
     * Adds the {@link WorldObject} to the {@code World}
     * @param object
     * @return added
     */
    public static void addObject(WorldObject object) {
        synchronized (objects) {
            objects.add(object);
        }
    }

    /**
     * Removes The {@link WorldObject} from the {@code World}.
     * @param object
     * @return removed
     */
    public static void removeObject(WorldObject object) {
        synchronized (objects) {
            objects.remove(object);
        }
    }

}
