package com.shattered.game.actor.object;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.datatable.tables.ObjectUDataTable;
import com.shattered.game.GameWorld;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.flags.FlagType;
import com.shattered.game.actor.components.interaction.InteractionFlags;
import com.shattered.game.actor.object.component.WorldObjectComponentManager;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.actor.object.component.transform.Vector3;
import com.shattered.game.actor.object.component.transform.Rotation;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.Components;
import lombok.Getter;

/**
 * @author JTlr Frost 7/27/18 : 6:17 PM
 */
public class WorldObject extends Actor {

    /**
     * Represents the Displaying the Object as an Item
     */
    @Getter
    private int asItemId;

    /**
     *  Initializes a new WorldObject with the provided id and coordinate.
     * @param id
     * @param coordinate
     */
    public WorldObject(int id, GridCoordinate coordinate) {
        this(id, coordinate, new Rotation(0, 0, 0));
    }
    
    /**
     *  Initializes a new WorldObject with the provided id, coordinate, and it's rotation.
     * @param id
     * @param location
     */
    public WorldObject(int id, GridCoordinate location, Rotation rotation) {
        super(id);
        if (id > 0) {
            setComponentManager(new WorldObjectComponentManager(this));
            addComponents();
            component(GameObjectComponents.TRANSFORM_COMPONENT).setLocation(location);
            component(GameObjectComponents.TRANSFORM_COMPONENT).setRotation(rotation);
            component(ActorComponents.INTERACTION).flag(InteractionFlags.CAN_INTERACT_WITH);
            onAwake();
        }
    }


    /**
     * Gets the name of the world object from the data table, if null returns unavailable
     *
     * @return
     */
    @Override
    public String getName() {
        if (getDataTable() != null)
            return getDataTable().getName();
        return "Unavailable";
    }

    /**
     * Method called upon initialization / loading of the world object.
     */
    @Override
    public void onAwake() {
        GameWorld.addObject(this);
        setState(ActorState.ALIVE);
        getComponentManager().onStart();
    }

    /**
     * Represents When the 'Game Object' is finished
     */
    @Override
    public void onFinish() {
        setState(ActorState.FINISHED);
        getComponentManager().onFinish();
        GameWorld.removeObject(this);
    }

    /**
     * Adds the Components
     */
    public void addComponents() {
        super.addComponents();
    }

    /**
     * Represents the 'Cycle' Method Call
     */
    public void onTick(long deltaTime) {
        super.onTick(deltaTime);
        getComponentManager().onTick(deltaTime);
    }

    /**
     * Plays a Sound cue at the {@link WorldObject} {@link Vector3}
     *
     * Volume is a modifier attribute and should mostly remain at 1.
     *
     * @param id
     */
    public void playSoundEffect(Player player, int id) {
        player.sendMessage(PacketOuterClass.Opcode.SMSG_PLAY_SOUND_EFFECT_AT_LOC, World.PlaySoundEffectAtLocation.newBuilder().setId(id).setTransform(component(GameObjectComponents.TRANSFORM_COMPONENT).toWorldTransform()).build());
    }

    /**
     * Sets the Object to show as an Item
     * @param asItemId
     */
    public void setAsItemId(int asItemId) {
        this.asItemId = asItemId;
        component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
    }

    /**
     * Gets a piece of Game Object Component
     * @param components
     */
    public <T extends Component> T component(Components<T> components) {
        return getComponentManager().get(components);
    }


    /**
     * Gets the data table for the WorldObject
     * @return the data table
     */
    public ObjectUDataTable getDataTable() {
        return UDataTableRepository.getObjectDataTable().get(getId());
    }

}
