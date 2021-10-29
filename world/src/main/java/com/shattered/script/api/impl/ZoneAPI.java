package com.shattered.script.api.impl;

import com.shattered.datatable.tables.EmitterUDataTable;
import com.shattered.datatable.tables.NPCUDataTable;
import com.shattered.datatable.tables.ObjectUDataTable;
import com.shattered.datatable.tables.SoundUDataTable;
import com.shattered.engine.tasks.DelayedTaskTicker;
import com.shattered.game.GameWorld;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.actor.object.WorldObject;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.actor.object.component.transform.Rotation;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.script.api.RelicActorAPI;
import com.shattered.script.api.RelicNpcAPI;
import com.shattered.script.api.RelicPlayerAPI;
import com.shattered.script.api.RelicZoneAPI;

import java.util.ArrayList;
import java.util.List;

public class ZoneAPI extends RelicZoneAPI {


    /**
     * Represents the Zone API for implementation
     * @param actor
     */
    public ZoneAPI(Actor actor) {
        super(actor);
    }

    /**
     * Gets the Location of the Character
     *
     * @return the location
     */
    @Override
    public GridCoordinate getLocation() {
        return actor.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation();
    }

    /**
     * Gets the rotation of the Character
     *
     * @return the rotation
     */
    @Override
    public Rotation getRotation() {
        return actor.component(GameObjectComponents.TRANSFORM_COMPONENT).getRotation();
    }

    /**
     * Checks if the player has successfully loaded in
     *
     * @return has awaken in the world (onWorldAwake)
     */
    @Override
    public boolean getLoaded() {
        return actor.component(PlayerComponents.WORLD_LEVEL_MANAGER).isWorldLevelLoaded();
    }

    /**
     * Gets the current zone name
     *
     * @return the current zone
     */
    @Override
    public String getName() {
        return actor.component(GameObjectComponents.ZONE_COMPONENT).getZoneName();
    }

    /**
     * Gets the previous zone name
     *
     * @return the previous zone name
     */
    @Override
    public String getLastName() {
        return actor.component(GameObjectComponents.ZONE_COMPONENT).getLastZoneName();
    }

    /**
     * Returns a List of all Local Characters within your area.
     *
     * @return all of the local players
     */
    @Override
    public List<PlayerAPI> getPlayers() {//TODO for npc
        if (actor instanceof Player) {
            List<PlayerAPI> local = new ArrayList<>();
            for (Player l : actor.component(PlayerComponents.CHARACTER_SYNCHRONIZE).getLocal().values()) {
                if (l == null) continue;
                local.add(new PlayerAPI(l));
            }
            return local;
        }
        if (actor instanceof NPC) {
            List<PlayerAPI> local = new ArrayList<>();
            for (Player l : actor.component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllPlayers()) {
                if (l == null) continue;
                local.add(new PlayerAPI(l));
            }
            return local;
        }
        return null;
    }

    /**
     * Returns a List of all Local NPCs within your area.
     *
     * @return all of the local networked npcs
     */
    @Override
    public List<NpcAPI> getNpcs() {
        if (actor instanceof Player) {
            List<NpcAPI> local = new ArrayList<>();
            for (NPC l : actor.component(PlayerComponents.NPC_SYNCHRONIZE).getLocal().values()) {
                if (l == null) continue;
                local.add(new NpcAPI(l));
            }
            return local;
        }
        if (actor instanceof NPC) {
            List<NpcAPI> local = new ArrayList<>();
            for (NPC l : actor.component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllNPCs()) {
                if (l == null) continue;
                local.add(new NpcAPI(l));
            }
            return local;
        }
        return null;
    }

    /**
     * Returns a List of all Local World Objects within your area.
     *
     * @return all local networked objects
     */
    @Override
    public List<ObjectAPI> getObjects() {//TODO for NPC
        if (actor instanceof Player) {
            List<ObjectAPI> local = new ArrayList<>();
            for (WorldObject l : actor.component(PlayerComponents.OBJECT_SYNCHRONIZE).getLocal().values()) {
                if (l == null) continue;
                local.add(new ObjectAPI(l));
            }
            return local;
        }
        return null;
    }

    /**
     * Returns the distance to a specified actor
     *
     * @param actor
     * @return the distance in units
     */
    @Override
    public int distance_to(RelicActorAPI actor) {
        return this.actor.component(GameObjectComponents.TRANSFORM_COMPONENT).distanceTo(actor.getActor());
    }

    /**
     * Gets the distance between you and a coordinate
     *
     * @param coordinate
     * @return
     */
    @Override
    public int distance_to(GridCoordinate coordinate) {
        return actor.component(GameObjectComponents.TRANSFORM_COMPONENT).distanceTo(coordinate);
    }

    /**
     * CHecks if you're within a specified distance to an actor
     *
     * @param actor
     * @param units
     * @return is within distance
     */
    @Override
    public boolean is_within_distance(RelicActorAPI actor, int units) {
        return distance_to(actor) <= units;
    }

    /**
     * Checks if you're within a certain distance of a specified coordinate
     *
     * @param coordinate
     * @param units
     * @return is within distance
     */
    @Override
    public boolean is_within_distance(GridCoordinate coordinate, int units) {
        return distance_to(coordinate) <= units;
    }

    /**
     * Teleports the player to the specific location
     *
     * @param coordinate
     */
    @Override
    public void teleport(GridCoordinate coordinate) {
        actor.component(GameObjectComponents.ZONE_COMPONENT).teleport(coordinate);
    }

    /**
     * Teleports the player to a specific coordinate and loads a new zone (continent)
     *
     * @param coordinate
     * @param zone
     */
    @Override
    public void teleport_to_zone(GridCoordinate coordinate, String zone) {
        //TODO
    }

    /**
     * Gets the Player by their UUID
     *
     * @param uuid
     * @return the player
     */
    @Override
    public RelicPlayerAPI getPlayer(int uuid) {
        Player target = actor.component(GameObjectComponents.ZONE_COMPONENT).getNode().getPlayer(uuid);
        return target == null ?  null : new PlayerAPI(target);
    }

    /**
     * Gets the Player by their Name
     *
     * @param name
     * @return the player
     */
    @Override
    public RelicPlayerAPI getPlayer(String name) {
        Player target = actor.component(GameObjectComponents.ZONE_COMPONENT).getNode().getPlayer(name);
        return target == null ?  null : new PlayerAPI(target);
    }

    /**
     * Gets the direct NPC by UUID
     *
     * @param uuid
     * @return the npc by uuid
     */
    @Override
    public RelicNpcAPI getNPC(int uuid) {
        NPC target = actor.component(GameObjectComponents.ZONE_COMPONENT).getNode().getNPC(uuid);
        return target == null ?  null : new NpcAPI(target);
    }

    /**
     * Gets the closest NPC by name.
     *
     * @param name
     * @return the closest npc
     */
    @Override
    public RelicNpcAPI getNPC(String name) {
        NPC target = actor.component(GameObjectComponents.ZONE_COMPONENT).getNode().getNPC(name);
        return target == null ?  null : new NpcAPI(target);
    }


    /**
     * Spawns a {@link NPC} at the specific location
     *
     * @param name
     * @param location
     */
    @Override
    public void spawn_npc(String name, GridCoordinate location) {
        NPCUDataTable table = NPCUDataTable.forName(name);
        if (table != null) {
            new NPC(table.getId(), location);
        }
    }

    /**
     * Spawns a {@link NPC} at the specific coordinate, and the Rotation Yaw
     *
     * @param name
     * @param coordinate
     * @param rotation
     */
    @Override
    public void spawn_npc(String name, GridCoordinate coordinate, Rotation rotation) {
        NPCUDataTable table = NPCUDataTable.forName(name);
        if (table != null)
            new NPC(table.getId(), coordinate, rotation);
    }

    /**
     * Spawns a {@link NPC} at the specified location.
     *
     * @param id
     * @param location
     */
    @Override
    public void spawn_npc(int id, GridCoordinate location) {
        new NPC(id, location);
    }

    /**
     * Spawns a {@link NPC} at the specified location and rotation.
     *
     * @param id
     * @param location
     * @param rotation
     */
    @Override
    public void spawn_npc(int id, GridCoordinate location, Rotation rotation) {
        new NPC(id, location, rotation);
    }

    /**
     * Spawns a {@link WorldObject} with the specified name to the location
     *
     * @param name
     * @param location
     */
    @Override
    public void spawn_object(String name, GridCoordinate location) {
        ObjectUDataTable table = ObjectUDataTable.forName(name);
        if (table != null)
            new WorldObject(table.getId(), location);
    }

    /**
     * Spawns a {@link WorldObject} in the specified amount of time
     *
     * @param name
     * @param location
     * @param delay
     */
    @Override
    public void spawn_object_delayed(String name, GridCoordinate location, int delay) {
        DelayedTaskTicker.delayTask(new Runnable() {
            @Override
            public void run() {
                ObjectUDataTable table = ObjectUDataTable.forName(name);
                if (table != null)
                    new WorldObject(table.getId(), location);
            }
        }, delay * 2);//This thread ticks twice per second
    }

    /**
     * Spawns a {@link WorldObject} at the specific location and rotation
     *
     * @param name
     * @param location
     * @param rotation
     */
    @Override
    public void spawn_object(String name, GridCoordinate location, Rotation rotation) {
        ObjectUDataTable table = ObjectUDataTable.forName(name);
        if (table != null)
            new WorldObject(table.getId(), location, rotation);
    }

    /**
     * Spawns a {@link WorldObject} in the specified delayed amount
     *
     * @param name
     * @param location
     * @param rotation
     * @param delay
     */
    @Override
    public void spawn_object_delayed(String name, GridCoordinate location, Rotation rotation, int delay) {
        DelayedTaskTicker.delayTask(new Runnable() {
            @Override
            public void run() {
                ObjectUDataTable table = ObjectUDataTable.forName(name);
                if (table != null)
                    new WorldObject(table.getId(), location, rotation);
            }
        }, delay * 2);
    }

    /**
     * Spawns a {@link WorldObject} to the specified location with a default rotation of 0
     *
     * @param id
     * @param location
     */
    @Override
    public void spawn_object(int id, GridCoordinate location) {
        new WorldObject(id, location);
    }

    /**
     * Spawns a {@link WorldObject} in the specified delayed time
     *
     * @param id
     * @param location
     * @param delay
     */
    @Override
    public void spawn_object_delayed(int id, GridCoordinate location, int delay) {
        DelayedTaskTicker.delayTask(new Runnable() {
            @Override
            public void run() {
                new WorldObject(id, location);
            }
        }, delay * 2);
    }

    /**
     * Spawns a {@link WorldObject} at the specified location with the rotation
     *
     * @param id
     * @param location
     * @param rotation
     */
    @Override
    public void spawn_object(int id, GridCoordinate location, Rotation rotation) {
        new WorldObject(id, location, rotation);
    }

    /**
     * Spawns a {@link WorldObject} in the specified time of seconds
     *
     * @param id
     * @param location
     * @param rotation
     * @param delay
     */
    @Override
    public void spawn_object_delayed(int id, GridCoordinate location, Rotation rotation, int delay) {
        DelayedTaskTicker.delayTask(new Runnable() {
            @Override
            public void run() {
                new WorldObject(id, location, rotation);
            }
        }, delay * 2);
    }

    /**
     * Spawns a {@link WorldObject} to the specified location, then in the end of the duration, will replace
     * the spawn object with the specified object.
     *
     * @param name
     * @param location
     * @param delay
     * @param objectName
     */
    @Override
    public void spawn_object_then_replace(String name, GridCoordinate location, int delay, String objectName) {
        ObjectUDataTable table = ObjectUDataTable.forName(name);
        if (table != null) {
            WorldObject toReplace = new WorldObject(table.getId(), location);
            DelayedTaskTicker.delayTask(new Runnable() {
                @Override
                public void run() {
                    toReplace.onFinish();
                    ObjectUDataTable replace = ObjectUDataTable.forName(objectName);
                    if (replace != null) {
                        new WorldObject(replace.getId(), location);
                    }
                }
            }, delay * 2);
        }
    }

    /**
     * Spawns a {@link WorldObject} to the specified location, then in the end of the duration, will replace
     * the spawn object with the specified object.
     *
     * @param name
     * @param location
     * @param rotation
     * @param delay
     * @param objectName
     */
    @Override
    public void spawn_object_then_replace(String name, GridCoordinate location, Rotation rotation, int delay, String objectName) {
        ObjectUDataTable table = ObjectUDataTable.forName(name);
        if (table != null) {
            WorldObject toReplace = new WorldObject(table.getId(), location);
            DelayedTaskTicker.delayTask(new Runnable() {
                @Override
                public void run() {
                    toReplace.onFinish();
                    ObjectUDataTable replace = ObjectUDataTable.forName(objectName);
                    if (replace != null) {
                        new WorldObject(replace.getId(), location, rotation);
                    }
                }
            }, delay * 2);
        }
    }

    /**
     * Spawns a {@link WorldObject} to the specified location, then in the end of the duration, will replace
     * the spawn object with the specified object.
     *
     * @param name
     * @param location
     * @param rotation
     * @param delay
     * @param objectId
     */
    @Override
    public void spawn_object_then_replace(String name, GridCoordinate location, Rotation rotation, int delay, int objectId) {
        ObjectUDataTable table = ObjectUDataTable.forName(name);
        if (table != null) {
            WorldObject toReplace = new WorldObject(table.getId(), location, rotation);
            DelayedTaskTicker.delayTask(new Runnable() {
                @Override
                public void run() {
                    toReplace.onFinish();
                    if (ObjectUDataTable.isObject(objectId)) {
                        new WorldObject(objectId, location, rotation);
                    }
                }
            }, delay * 2);
        }
    }

    /**
     * Plays a sfx at the given location
     *
     * @param name
     * @param location
     */
    @Override
    public void play_sfx(String name, GridCoordinate location) {
        SoundUDataTable sfx = SoundUDataTable.forSFX(name);
        if (sfx == null) return;
        for (RelicPlayerAPI zp : getPlayers())
            zp.play_sfx(name, location);
    }

    /**
     * Spawns a GFX (Emitter) at the specified location.
     *
     * @param name
     * @param location
     */
    @Override
    public void spawn_emitter(String name, GridCoordinate location) {
        EmitterUDataTable emitter = EmitterUDataTable.forId(name);
        if (emitter == null) return;
        GameWorld.spawnEmitterAtLocation(emitter.getId(), location, new Rotation(0.f, 0.f, 0.f));
    }

    /**
     * Spawns a GFX (Emitter) at the specified location.
     *
     * @param id
     * @param location
     */
    @Override
    public void spawn_emitter(int id, GridCoordinate location) {
        GameWorld.spawnEmitterAtLocation(id, location, new Rotation(0.f, 0.f, 0.f));
    }

    /**
     * Spawns a GFX (Emitter) at the specified location.
     *
     * @param id
     * @param location
     * @param rotation
     */
    @Override
    public void spawn_emitter(int id, GridCoordinate location, Rotation rotation) {
        GameWorld.spawnEmitterAtLocation(id, location, rotation);
    }

}
