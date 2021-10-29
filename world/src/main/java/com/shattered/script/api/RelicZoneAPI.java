package com.shattered.script.api;

import com.shattered.game.actor.Actor;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.actor.object.component.transform.Rotation;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.script.api.impl.PlayerAPI;
import com.shattered.script.api.impl.NpcAPI;
import com.shattered.script.api.impl.ObjectAPI;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@NonNull
@RequiredArgsConstructor
public abstract class RelicZoneAPI {

    /**
     * Represents the Actor this is used for.
     */
    @Getter
    protected final Actor actor;

    /**
     * Gets the Location of the Character
     * @return the location
     */
    public abstract GridCoordinate getLocation();

    /**
     * Gets the rotation of the Character
     * @return the rotation
     */
    public abstract Rotation getRotation();

    /**
     * Checks if the player has successfully loaded in
     * @return has awaken in the world (onWorldAwake)
     */
    public abstract boolean getLoaded();

    /**
     * Gets the current zone name
     * @return the current zone
     */
    public abstract String getName();

    /**
     * Gets the previous zone name
     * @return the previous zone name
     */
    public abstract String getLastName();

    /**
     * Returns a List of all Local Players that are within your node, and all 8 adjacent nodes.
     * @return the list of local players.
     */
    public abstract List<PlayerAPI> getPlayers();

    /**
     * Returns a List of all Local NPCs within your area.
     * @return the local npcs
     */
    public abstract List<NpcAPI> getNpcs();

    /**
     * Returns a List of all Local World Objects within your area.
     * @return the local objects
     */
    public abstract List<ObjectAPI> getObjects();

    /**
     * Returns the distance to a specified actor
     * @param actor
     * @return the distance in units
     */
    public abstract int distance_to(RelicActorAPI actor);

    /**
     * Gets the distance between you and a coordinate
     * @param coordinate
     * @return
     */
    public abstract int distance_to(GridCoordinate coordinate);

    /**
     * CHecks if you're within a specified distance to an actor
     * @param actor
     * @param units
     * @return is within distance
     */
    public abstract boolean is_within_distance(RelicActorAPI actor, int units);

    /**
     * Checks if you're within a certain distance of a specified coordinate
     * @param coordinate
     * @param units
     * @return is within distance
     */
    public abstract boolean is_within_distance(GridCoordinate coordinate, int units);

    /**
     * Teleports the player to the specific location
     * @param coordinate
     */
    public abstract void teleport(GridCoordinate coordinate);

    /**
     * Teleports the player to a specific coordinate and loads a new zone (continent)
     * @param coordinate
     * @param zone
     */
    public abstract void teleport_to_zone(GridCoordinate coordinate, String zone);

    /**
     * Gets the Player by their UUID
     * @param uuid
     * @return the player
     */
    public abstract RelicPlayerAPI getPlayer(int uuid);

    /**
     * Gets the Player by their Name
     * @param name
     * @return the player
     */
    public abstract RelicPlayerAPI getPlayer(String name);

    /**
     * Gets the direct NPC by UUID
     * @param uuid
     * @return the npc by uuid
     */
    public abstract RelicNpcAPI getNPC(int uuid);

    /**
     * Gets the closest NPC by name.
     * @param name
     * @return the closest npc
     */
    public abstract RelicNpcAPI getNPC(String name);

    /**
     * Spawns a {@link NPC} at the specific location
     * @param name
     * @param location
     */
    public abstract void spawn_npc(String name, GridCoordinate location);

    /**
     * Spawns a {@link NPC} at the specific coordinate, and the Rotation Yaw
     * @param name
     * @param rotation
     */
    public abstract void spawn_npc(String name, GridCoordinate coordinate, Rotation rotation);

    /**
     * Spawns a {@link NPC} at the specified location.
     * @param id
     * @param location
     */
    public abstract void spawn_npc(int id, GridCoordinate location);

    /**
     * Spawns a {@link NPC} at the specified location and rotation.
     * @param id
     * @param location
     * @param rotation
     */
    public abstract void spawn_npc(int id, GridCoordinate location, Rotation rotation);

    /**
     * Spawns a {@link com.shattered.game.actor.object.WorldObject} with the specified name to the location
     * @param name
     * @param location
     */
    public abstract void spawn_object(String name, GridCoordinate location);

    /**
     * Spawns a {@link com.shattered.game.actor.object.WorldObject} in the specified amount of time
     * @param name
     * @param location
     * @param delay
     */
    public abstract void spawn_object_delayed(String name, GridCoordinate location, int delay);

    /**
     *
     * Spawns a {@link com.shattered.game.actor.object.WorldObject} at the specific location and rotation
     * @param name
     * @param location
     * @param rotation
     */
    public abstract void spawn_object(String name, GridCoordinate location, Rotation rotation);

    /**
     * Spawns a {@link com.shattered.game.actor.object.WorldObject} in the specified delayed amount
     * @param name
     * @param location
     * @param rotation
     * @param delay
     */
    public abstract void spawn_object_delayed(String name, GridCoordinate location, Rotation rotation, int delay);

    /**
     * Spawns a {@link com.shattered.game.actor.object.WorldObject} to the specified location with a default rotation of 0
     * @param id
     * @param location
     */
    public abstract void spawn_object(int id, GridCoordinate location);

    /**
     * Spawns a {@link com.shattered.game.actor.object.WorldObject} in the specified delayed time
     * @param id
     * @param location
     */
    public abstract void spawn_object_delayed(int id, GridCoordinate location, int delay);

    /**
     * Spawns a {@link com.shattered.game.actor.object.WorldObject} at the specified location with the rotation
     * @param id
     * @param location
     * @param rotation
     */
    public abstract void spawn_object(int id, GridCoordinate location, Rotation rotation);

    /**
     * Spawns a {@link com.shattered.game.actor.object.WorldObject} in the specified time of seconds
     * @param id
     * @param location
     * @param rotation
     * @param delay
     */
    public abstract void spawn_object_delayed(int id, GridCoordinate location, Rotation rotation, int delay);

    /**
     * Spawns a {@link com.shattered.game.actor.object.WorldObject} to the specified location, then in the end of the duration, will replace
     *        the spawn object with the specified object.
     * @param name
     * @param location
     * @param delay
     * @param objectName
     */
    public abstract void spawn_object_then_replace(String name, GridCoordinate location, int delay, String objectName);

    /**
     * Spawns a {@link com.shattered.game.actor.object.WorldObject} to the specified location, then in the end of the duration, will replace
     *        the spawn object with the specified object.
     * @param name
     * @param location
     * @param rotation
     * @param delay
     * @param objectName
     */
    public abstract void spawn_object_then_replace(String name, GridCoordinate location, Rotation rotation, int delay, String objectName);

    /**
     * Spawns a {@link com.shattered.game.actor.object.WorldObject} to the specified location, then in the end of the duration, will replace
     *      the spawn object with the specified object.
     * @param name
     * @param location
     * @param rotation
     * @param delay
     * @param objectId
     */
    public abstract void spawn_object_then_replace(String name, GridCoordinate location, Rotation rotation, int delay, int objectId);

    /**
     * Plays a sfx at the given location
     * @param name
     * @param location
     */
    public abstract void play_sfx(String name, GridCoordinate location);

    /**
     * Spawns a GFX (Emitter) at the specified location.
     * @param name
     * @param location
     */
    public abstract void spawn_emitter(String name, GridCoordinate location);

    /**
     * Spawns a GFX (Emitter) at the specified location.
     * @param id
     * @param location
     */
    public abstract void spawn_emitter(int id, GridCoordinate location);

    /**
     * Spawns a GFX (Emitter) at the specified location.
     * @param id
     * @param location
     * @param rotation
     */
    public abstract void spawn_emitter(int id, GridCoordinate location, Rotation rotation);

}
