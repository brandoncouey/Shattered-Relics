package com.shattered.script.api.impl;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.datatable.tables.SoundUDataTable;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.interaction.InteractionFlags;
import com.shattered.game.actor.object.WorldObject;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.actor.object.component.transform.Rotation;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.script.api.RelicObjectAPI;

public class ObjectAPI extends RelicObjectAPI {


    /**
     * 
     * @param object
     */
    public ObjectAPI(WorldObject object) {
        super(object);
    }

    /**
     * Gets the object id
     *
     * @return the object id
     */
    @Override
    public int getId() {
        return object.getId();
    }

    /**
     * Gets the Name of the NPC
     *
     * @return
     */
    @Override
    public String getName() {
        return object.getName();
    }

    /**
     * Gets the Client Index (the UUID) of the npc
     *
     * @return the npc's uuid
     */
    @Override
    public int getIndex() {
        return object.getClientIndex();
    }

    /**
     * Gets the current Actor State of this object
     *
     * @return the actor state
     */
    @Override
    public ActorState getState() {
        return object.getState();
    }

    /**
     * Applys flags to the npc for availability for interaction
     *
     * @param flags
     */
    @Override
    public void flag(InteractionFlags... flags) {
        for (InteractionFlags f : flags)
            actor.component(ActorComponents.INTERACTION).flag(f);
    }

    /**
     * Gets the {@link WorldObject}s Grid Coordinate
     *
     * @return the location
     */
    @Override
    public GridCoordinate getLocation() {
        return object.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation();
    }

    /**
     * Gets the Object's Rotation
     *
     * @return the rotation
     */
    @Override
    public Rotation getRotation() {
        return object.component(GameObjectComponents.TRANSFORM_COMPONENT).getRotation();
    }

    /**
     * Destroys the object, and sets its state to finished.
     */
    @Override
    public void destroy() {
        object.onFinish();
    }


    /**
     * PLays the specified sfx at the actor's location
     *
     * @param id
     */
    @Override
    public void play_rsfx(int id) {
        if (!UDataTableRepository.getSoundEffectDataTable().containsKey(id)) return;
        for (Player player : object.component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllPlayers()) {
            if (!player.component(GameObjectComponents.TRANSFORM_COMPONENT).isWithinDistance(object, 5000)) continue;
            player.sendMessage(PacketOuterClass.Opcode.SMSG_PLAY_SOUND_EFFECT_AT_LOC, World.PlaySoundEffectAtLocation.newBuilder().setId(id).setTransform(object.component(GameObjectComponents.TRANSFORM_COMPONENT).toWorldTransform()).build());
        }
    }

    /**
     * PLays the specified sfx at the actor's location
     *
     * @param name
     */
    @Override
    public void play_rsfx(String name) {
        SoundUDataTable table = SoundUDataTable.forSFX(name);
        if (table == null) return;
        for (Player player : object.component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllPlayers()) {
            if (!player.component(GameObjectComponents.TRANSFORM_COMPONENT).isWithinDistance(object, 5000)) continue;
            player.sendMessage(PacketOuterClass.Opcode.SMSG_PLAY_SOUND_EFFECT_AT_LOC, World.PlaySoundEffectAtLocation.newBuilder().setId(table.id).setTransform(object.component(GameObjectComponents.TRANSFORM_COMPONENT).toWorldTransform()).build());
        }
    }
}
