package com.shattered.script.api.impl;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.datatable.tables.SoundUDataTable;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.Character;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.npc.component.NPCComponents;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.channel.ChannelType;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.interaction.InteractionFlags;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.script.api.RelicActorAPI;
import com.shattered.script.api.RelicCharacterAPI;
import com.shattered.script.api.RelicNpcAPI;

public class NpcAPI extends RelicNpcAPI {

    /**
     *
     * @param npc
     */
    public NpcAPI(NPC npc) {
        super(npc);
    }

    @Override
    public int getId() {
        return npc.getId();
    }

    /**
     * Forces the npc to say a message
     *
     * @param message
     */
    @Override
    public void say(String message) {
        npc.component(NPCComponents.SOCIAL_CHANNEL).sendChannelMessage(ChannelType.LOCAL, message);
    }

    /**
     * Forces the npc to yell a message.
     *
     * @param message
     */
    @Override
    public void yell(String message) {
        npc.component(NPCComponents.SOCIAL_CHANNEL).sendChannelMessage(ChannelType.YELL, message);
    }

    /**
     * Makes the npc play a specific animation with the specified animation id
     *
     * @param id
     */
    @Override
    public void play_animation(int id) {
        npc.component(ActorComponents.ANIMATION).playAnimSequence(id);
    }

    /**
     * Makes the npc play a specific animation with the specified animation id
     *
     * @param animation
     */
    @Override
    public void play_animation(String animation) {
        npc.component(ActorComponents.ANIMATION).playAnimSequence(animation, npc.getDataTable().isHummanoid());
    }

    /**
     * Makes the NPC Stop playing their current animation
     */
    @Override
    public void stop_animation() {
        npc.component(ActorComponents.ANIMATION).stopAnimation();
    }

    /**
     * Gets the Name of the NPC
     *
     * @return
     */
    @Override
    public String getName() {
        return npc.getName();
    }

    /**
     * Gets the Client Index (the UUID) of the npc
     *
     * @return the npc's uuid
     */
    @Override
    public int getIndex() {
        return npc.getClientIndex();
    }

    /**
     * Gets the Actor's current state
     *
     * @return the actor state
     */
    @Override
    public ActorState getState() {
        if (npc == null)
            return ActorState.FINISHED;
        return npc.getState();
    }

    /**
     * Moves the Character to a specific character
     *
     * @param character
     */
    @Override
    public void move_to(RelicCharacterAPI character) {
        npc.component(ActorComponents.MOVEMENT).moveToActor(character.getActor());
    }

    /**
     * Moves the character to a specific coordinate
     *
     * @param location
     */
    @Override
    public void move_to(GridCoordinate location) {
        npc.component(ActorComponents.MOVEMENT).moveToLocation(location);
    }

    /**
     * Checks if the character is currently moving
     *
     * @return is moving
     */
    @Override
    public boolean getMoving() {
        return npc.component(ActorComponents.MOVEMENT).moving;
    }

    /**
     * Decreases the characters movement speed by the specified percent
     *
     * @param percent
     */
    @Override
    public int decrease_speed_by_percent(int percent) {
        return 0;
    }

    /**
     * Decreases the characters movement speed by the specified percent
     *
     * @param amount
     */
    @Override
    public void decrease_speed(int amount) {

    }

    /**
     * Decreases the characters movement speed by the specified percent
     * for the given amount of seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public int decrease_speed(int percent, float duration) {
        return 0;
    }

    /**
     * Increases the character movement speed by
     *
     * @param percent
     * @return the amount of percent
     */
    @Override
    public int increase_speed_by_percent(int percent) {
        return 0;
    }

    /**
     * Increases the character movement speed by
     *
     * @param amount
     */
    @Override
    public void increase_speed(int amount) {

    }

    /**
     * Increases the character movement speed by the specified percent for
     * the given amount of seconds
     *
     * @param percent
     * @param duration
     *
     * @return percent
     */
    @Override
    public int increase_speed(int percent, float duration) {
        return 0;
    }

    /**
     * Makes the Character face a specific Actor
     *
     * @param actor
     */
    @Override
    public void face_actor(RelicActorAPI actor) {
        npc.component(ActorComponents.MOVEMENT).faceActor(actor.getActor());
    }

    /**
     * Makes the Character face a specific coordinate
     *
     * @param coordinate
     */
    @Override
    public void face_location(GridCoordinate coordinate) {
        npc.component(ActorComponents.MOVEMENT).faceCoordinate(coordinate);
    }

    /**
     * Locks the npc, making them unable to move or rotate.
     */
    @Override
    public void lock() {
        npc.component(ActorComponents.MOVEMENT).lock();
    }

    /**
     * Unlocks the npc and gives them the ability to move and rotate.
     */
    @Override
    public void unlock() {
        npc.component(ActorComponents.MOVEMENT).unlock();
    }

    /**
     * Locks the npc for the length of the specified delay in seconds and then unlocks them once the
     * duration has ended.
     *
     * @param delay
     */
    @Override
    public void lock(int delay) {
        npc.component(ActorComponents.MOVEMENT).lock(delay);
    }

    /**
     * Adds the specified buff with the desired duration to the character
     *
     * @param name
     * @param duration
     */
    @Override
    public void add_buff(String name, float duration) {
        add_buff(name, this, 1, duration);
    }

    /**
     * Adds teh specified buff with the desired stacks and time duration.
     *
     * @param name
     * @param stacks
     * @param duration
     */
    @Override
    public void add_buff(String name, int stacks, float duration) {
        add_buff(name, this, stacks, duration);
    }

    /**
     * Adds the specified buff with the desired duration to the character
     *
     * @param name
     * @param source
     * @param duration
     */
    @Override
    public void add_buff(String name, RelicCharacterAPI source, float duration) {
        add_buff(name, source, 1, duration);
    }

    /**
     * Adds teh specified buff with the desired stacks and time duration.
     *
     * @param name
     * @param source
     * @param stacks
     * @param duration
     */
    @Override
    public void add_buff(String name, RelicCharacterAPI source, int stacks, float duration) {
        actor.component(CharacterComponents.BUFF).addBuff(name, (Character) source.getActor(), stacks, duration);
    }

    /**
     * Removes the specified buff from the character.
     *
     * @param name
     */
    @Override
    public void remove_buff(String name) {
        actor.component(CharacterComponents.BUFF).removeBuff(name);
    }

    /**
     * Checks if the sepcified buff is currently active
     *
     * @param name
     *
     * @return has the buff.
     */
    @Override
    public boolean has_buff(String name) {
        return actor.component(CharacterComponents.BUFF).hasBuff(name);
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
     * PLays the specified sfx at the actor's location
     *
     * @param id
     */
    @Override
    public void play_rsfx(int id) {
        if (!UDataTableRepository.getSoundEffectDataTable().containsKey(id)) return;
        for (Player player : npc.component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllPlayers()) {
            if (!player.component(GameObjectComponents.TRANSFORM_COMPONENT).isWithinDistance(npc, 5000)) continue;
            player.sendMessage(PacketOuterClass.Opcode.SMSG_PLAY_SOUND_EFFECT_AT_LOC, World.PlaySoundEffectAtLocation.newBuilder().setId(id).setTransform(npc.component(GameObjectComponents.TRANSFORM_COMPONENT).toWorldTransform()).build());
        }
    }

    /**
     * Destroys the npc and removes from the world
     */
    @Override
    public void destroy() {
        npc.onFinish();
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
        for (Player player : npc.component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllPlayers()) {
            if (!player.component(GameObjectComponents.TRANSFORM_COMPONENT).isWithinDistance(npc, 5000)) continue;
            player.sendMessage(PacketOuterClass.Opcode.SMSG_PLAY_SOUND_EFFECT_AT_LOC, World.PlaySoundEffectAtLocation.newBuilder().setId(table.id).setTransform(npc.component(GameObjectComponents.TRANSFORM_COMPONENT).toWorldTransform()).build());
        }
    }
}
