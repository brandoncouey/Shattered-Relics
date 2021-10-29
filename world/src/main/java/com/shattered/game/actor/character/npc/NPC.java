package com.shattered.game.actor.character.npc;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.datatable.tables.NPCUDataTable;
import com.shattered.datatable.tables.SoundUDataTable;
import com.shattered.engine.tasks.DelayedTaskTicker;
import com.shattered.game.GameWorld;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.Character;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.npc.component.NPCComponents;
import com.shattered.game.actor.character.npc.component.channel.NPCChannelComponent;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.interaction.InteractionFlags;
import com.shattered.game.actor.container.Container;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.container.loot.LootContainer;
import com.shattered.game.actor.character.npc.container.NPCContainerManager;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.actor.object.component.transform.Vector3;
import com.shattered.game.actor.object.component.transform.Rotation;
import com.shattered.game.actor.character.npc.component.NPCComponentManager;
import com.shattered.game.actor.character.npc.component.combat.NPCCombatComponent;
import com.shattered.game.actor.vendor.VendorContainer;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.script.ScriptManager;
import com.shattered.script.types.NPCScript;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost 7/29/18 : 10:45 AM
 */
public class NPC extends Character {

    /**
     * Represents the current lifespan of the NPC
     * Default is set to 0 = Infinite.
     */
    @Getter
    @Setter
    private int lifeSpan;

    /**
     * Represents the Container Manager for the NPC
     */
    @Getter
    @Setter
    private NPCContainerManager containerManager;

    /**
     * Creates and Spawns a new NPC with the provided id, and grid coordinate.
     * @param id
     * @param coordinate
     */
    public NPC(int id, GridCoordinate coordinate) {
       this(id, coordinate, new Rotation(0, 0, 0));
    }

    /**
     * Creates and Spawns a new NPC with the provided id, grid coordinate, and the provided rotation.
     *
     * @param id
     * @param location
     * @param rotation
     */
    public NPC(int id, GridCoordinate location, Rotation rotation) {
        super(id);
        setComponentManager(new NPCComponentManager(this));
        setContainerManager(new NPCContainerManager(this));
        addComponents();
        component(GameObjectComponents.TRANSFORM_COMPONENT).setLocation(location);
        component(GameObjectComponents.TRANSFORM_COMPONENT).setRotation(rotation);
        GameWorld.addNPC(this);
        onAwake();

    }

    /**
     * Gets the name of the npc from the data table, if null it returns "Unavailable"
     * @return
     */
    @Override
    public String getName() {
        if (getDataTable() != null)
            return getDataTable().getName();
        return "Unavailable";
    }

    /**
     * Method called for initializing and loading the npc.
     */
    @Override
    public void onAwake() {
        super.onAwake();
        getComponentManager().onStart();
        getContainerManager().onStart();
        setState(ActorState.ALIVE);
        getComponentManager().onWorldAwake();
        getContainerManager().onWorldAwake();
    }

    /**O
     * Called upon on Death
     */
    @Override
    public void onDeath(Actor source) {
        if (getState().equals(ActorState.DEAD))
            return;
        super.onDeath(source);
        getComponentManager().onDeath(source);
        getContainerManager().onDeath(source);

        if (getDataTable() != null) {
            if (!getDataTable().getDeathSoundEffect().isEmpty())
                playSoundEffect(getDataTable().getDeathSoundEffect());

            if (!getDataTable().getDeathAnimationName().isEmpty())
                component(ActorComponents.ANIMATION).playAnimSequence(getDataTable().getDeathAnimationName(), getDataTable().isHummanoid());
        }

        DelayedTaskTicker.delayTask(() -> {
            onRespawn();
            onFinish();
        }, getDataTable().getRespawnRate());
    }

    /**
     * Called upon Respawned
     */
    public void onRespawn() {
        DelayedTaskTicker.delayTask(() -> {
            NPC npc = new NPC(id, component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation());
            for (InteractionFlags flag : component(ActorComponents.INTERACTION).getSpawnFlags()) {
                npc.component(ActorComponents.INTERACTION).getSpawnFlags().add(flag);
                npc.component(ActorComponents.INTERACTION).flag(flag);
            }
            //TODO change the location
        }, 5000.f);
    }

    /**
     * Initializes the Game Object Containers
     */
    @Override
    public void addComponents() {
        super.addComponents();
        getComponentManager().attatch(NPCComponents.SOCIAL_CHANNEL, new NPCChannelComponent(this));
        getComponentManager().attatch(CharacterComponents.COMBAT, new NPCCombatComponent(this));

        //this needs to be appended IF the npc has a vendor..
        getContainerManager().attatch(Containers.VENDOR, new VendorContainer(this));
        getContainerManager().attatch(Containers.LOOT, new LootContainer(this));
    }

    /**
     * Method called every game tick
     */
    @Override
    public void onTick(long deltaTime) {
        super.onTick(deltaTime);
        getComponentManager().onTick(deltaTime);
        getContainerManager().onTick(deltaTime);

        //TODO update with the new deltaseconds
       /* if (interval == ProcessInterval.SECOND) {
           if (lifeSpan > 0) {
               lifeSpan--;
               if (lifeSpan <= 0)
                   onFinish();
           }
        }
*/
    }

    /**
     * Represents the Finishing Method
     */
    @Override
    public void onFinish() {
        super.onFinish();
        getComponentManager().onFinish();
        getContainerManager().onFinish();
        GameWorld.removeNPC(this);
    }

    public void playSoundEffect(String name) {
        SoundUDataTable table = SoundUDataTable.forSFX(name);
        if (table == null) return;
        for (Player player : component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllPlayers()) {
            if (player == null) continue;
            if (!player.component(GameObjectComponents.TRANSFORM_COMPONENT).isWithinDistance(this, 2500)) continue;

            playSoundEffect(player, table.id);
        }
    }

    /**
     * Plays a Sound cue at the {@link NPC} {@link Vector3}
     *
     * Volume is a modifier attribute and should mostly remain at 1.
     *
     * @param id
     */
    public void playSoundEffect(Player player, int id) {
        player.sendMessage(PacketOuterClass.Opcode.SMSG_PLAY_SOUND_EFFECT_AT_LOC, World.PlaySoundEffectAtLocation.newBuilder().setId(id).setTransform(component(GameObjectComponents.TRANSFORM_COMPONENT).toWorldTransform()).build());
    }

    public <T extends Container> T container(Containers<T> components) {
        return (T) getContainerManager().get(components);
    }

    /**
     * Overrides the parent component manager getter with ours.
     * @return component manager
     */
    @Override
    public NPCComponentManager getComponentManager() {
        return (NPCComponentManager) super.getComponentManager();
    }

    /**
     * Gets the Data Entry List for the Current {@link NPC}
     * @return
     */
    public NPCUDataTable getDataTable() {
        return UDataTableRepository.getNpcDataTable().get(getId());
    }

    /**
     * Gets the NPC Script Associated with this NPC
     * @return the npc script
     */
    public NPCScript getScript() { return ScriptManager.getNpcScripts().get(getName().toLowerCase()); }

}
