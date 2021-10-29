package com.shattered.game.actor.character.player;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.shattered.account.Account;
import com.shattered.datatable.tables.SoundUDataTable;
import com.shattered.engine.tasks.DelayedTaskTicker;
import com.shattered.game.GameWorld;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.Character;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.player.component.PlayerComponentManager;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.actionbar.PlayerActionBarComponent;
import com.shattered.game.actor.character.player.component.channel.PlayerChannelComponent;
import com.shattered.game.actor.character.player.component.combat.PlayerCombatComponent;
import com.shattered.game.actor.character.player.component.container.PlayerContainerManager;
import com.shattered.game.actor.character.player.component.container.abilitybook.AbilityBookContainer;
import com.shattered.game.actor.character.player.component.container.mailbox.MailboxContainer;
import com.shattered.game.actor.character.player.component.container.storage.BankContainer;
import com.shattered.game.actor.character.player.component.container.trade.TradeContainer;
import com.shattered.game.actor.character.player.component.reputation.PlayerReputationComponent;
import com.shattered.game.actor.character.player.component.trades.PlayerTradesComponent;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.interaction.InteractionFlags;
import com.shattered.game.actor.container.Container;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.character.player.component.container.equipment.EquipmentContainer;
import com.shattered.game.actor.character.player.component.container.storage.InventoryContainer;
import com.shattered.game.actor.character.player.component.interaction.PlayerInteractionComponent;
import com.shattered.game.actor.character.player.component.interaction.dialogue.PlayerDialogComponent;
import com.shattered.game.actor.character.player.component.managers.WorldLevelManagerComponent;
import com.shattered.game.actor.character.player.component.model.PlayerModelComponent;
import com.shattered.game.actor.character.player.component.quest.PlayerQuestComponent;
import com.shattered.game.actor.character.player.component.synchronize.character.PlayerSynchronizeComponent;
import com.shattered.game.actor.character.player.component.synchronize.npc.NPCSynchronizeComponent;
import com.shattered.game.actor.character.player.component.synchronize.object.ObjectSynchronizeComponent;
import com.shattered.game.actor.character.player.component.widget.PlayerWidgetComponent;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.networking.messages.QueuedMessage;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.script.ScriptManager;
import com.shattered.system.SystemLogger;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.Components;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * @author JTlr Frost 7/27/18 : 6:39 PM
 */
public class Player extends Character {


    /**
     * Represents the Network Channel between the server and the client.
     */
    @Getter
    @Setter
    private Channel channel;

    /**
     * Represents the Game Account associated with this charcater
     */
    @Getter
    @Setter
    private Account account;

    /**
     * Represents the Character Information associated for this Character
     */
    @Getter
    @Setter
    private PlayerInformation playerInformation;

    /**
     * Represents the Component Manager for the player's {@link Containers}
     */
    @Setter
    @Getter
    private PlayerContainerManager containerManager;

    /**
     * The queue of pending {@link Message}s.
     */
    @Getter
    private final BlockingQueue<QueuedMessage> messages = new ArrayBlockingQueue<>(548);


    /**
     * Creates a new instance of the Character, Setting the network channel, and account information
     * @param channel
     * @param account
     * @param playerInformation
     */
    public Player(Channel channel, Account account, PlayerInformation playerInformation) {
        super(playerInformation.getId());
        setComponentManager(new PlayerComponentManager(this));
        setContainerManager(new PlayerContainerManager(this));
        setChannel(channel);
        setAccount(account);
        setPlayerInformation(playerInformation);
        addComponents();
        getComponentManager().onFetchData();
        getContainerManager().onFetchData();
    }

    /**
     * Represents the Name of Character
     * @return the name
     */
    @Override
    public String getName() {
        return getPlayerInformation().getName();
    }

    /**
     * Method called upon loading of the player.
     */
    @Override
    public void onAwake() {
        try {
            super.onAwake();
            getContainerManager().onStart();
            getComponentManager().onStart();
            setState(ActorState.ALIVE);
            GameWorld.addPlayer(this);
            SystemLogger.sendSystemMessage("PlayerInformation -> " + getPlayerInformation().getName() + " has entered the Shattered World!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called upon the Character's health reaching 0
     * @param source
     */
    @Override
    public void onDeath(Actor source) {
        super.onDeath(source);
        component(ActorComponents.MOVEMENT).lock();
        component(ActorComponents.ANIMATION).stopAnimation();
        getComponentManager().onDeath(source);
        getContainerManager().onDeath(source);
        ScriptManager.onDeath(this, source);
        component(PlayerComponents.WIDGET).showWidget("death");
        component(ActorComponents.ANIMATION).playAnimSequence("death", true);
        component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You have died.");
        DelayedTaskTicker.delayTask(this::onResurection, 3000.f);
    }

    /**
     * Called upon being Resurected
     */
    public void onResurection() {
        //TODO make this a script component
        component(ActorComponents.VAR).setVarInt("health", component(ActorComponents.VAR).getVarInt("max_health"));
        component(GameObjectComponents.ZONE_COMPONENT).teleport(new GridCoordinate(121870, 134520, 96));
        component(ActorComponents.ANIMATION).stopAnimation();
        component(ActorComponents.MOVEMENT).unlock();
        setState(ActorState.ALIVE);
    }

    /**
     * Represents the Initialization of Adding Containers and Components
     */
    @Override
    public void addComponents() {
        try {
            super.addComponents();

            //Represents Container Components
            attatchContainer(Containers.INVENTORY, new InventoryContainer(this));
            attatchContainer(Containers.EQUIPMENT, new EquipmentContainer(this));
            attatchContainer(Containers.TRADE, new TradeContainer(this));
            attatchContainer(Containers.MAILBOX, new MailboxContainer(this));
            attatchContainer(Containers.BANK, new BankContainer(this));
            attatchContainer(Containers.ABILITY_BOOK, new AbilityBookContainer(this));


            //Represents all Character Components
            attatchComponent(CharacterComponents.COMBAT, new PlayerCombatComponent(this));


            //Represents all Player Components
            attatchComponent(PlayerComponents.WORLD_LEVEL_MANAGER, new WorldLevelManagerComponent(this));
            attatchComponent(PlayerComponents.CHARACTER_SYNCHRONIZE, new PlayerSynchronizeComponent(this));
            attatchComponent(PlayerComponents.NPC_SYNCHRONIZE, new NPCSynchronizeComponent(this));
            attatchComponent(PlayerComponents.OBJECT_SYNCHRONIZE, new ObjectSynchronizeComponent(this));
            attatchComponent(PlayerComponents.MODEL_BLOCK, new PlayerModelComponent(this));
            attatchComponent(PlayerComponents.SOCIAL_CHANNEL, new PlayerChannelComponent(this));
            attatchComponent(PlayerComponents.INTERACTION, new PlayerInteractionComponent(this));
            attatchComponent(PlayerComponents.DIALOG, new PlayerDialogComponent(this));
            attatchComponent(PlayerComponents.QUEST, new PlayerQuestComponent(this));
            attatchComponent(PlayerComponents.WIDGET, new PlayerWidgetComponent(this));
            attatchComponent(PlayerComponents.ACTION_BAR, new PlayerActionBarComponent(this));
            attatchComponent(PlayerComponents.TRADESMAN, new PlayerTradesComponent(this));
            attatchComponent(PlayerComponents.REPUTATION, new PlayerReputationComponent(this));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method called after the world has been loaded on the client
     */
    public void onWorldAwake() {
        try {
            getContainerManager().onWorldAwake();
            getComponentManager().onWorldAwake();
            ScriptManager.onWorldAwake(this);
            component(ActorComponents.INTERACTION).flag(InteractionFlags.ATTACKABLE);
            component(ActorComponents.VAR).setVarInt("xp_accuracy", 0);
            component(ActorComponents.VAR).setVarInt("xp_strength", 0);
            component(ActorComponents.VAR).setVarInt("xp_resilience", 0);
            component(ActorComponents.VAR).setVarInt("xp_stamina", 0);
            component(ActorComponents.VAR).setVarInt("xp_focus", 0);
            component(ActorComponents.VAR).setVarInt("xp_intellect", 0);
            component(ActorComponents.VAR).setVarInt("xp_fire_mage", 0);
            component(ActorComponents.VAR).setVarInt("xp_warrior", 0);
            component(ActorComponents.VAR).setVarInt("xp_archer", 0);

            //Testing
            playSoundEffect("coins_add");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method called every world cycle (tick)
     * This method is called once per frame.
     */
    @Override
    public void onTick(long deltaTime) {
        try {
            super.onTick(deltaTime);
            getComponentManager().onTick(deltaTime);
            getContainerManager().onTick(deltaTime);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method called upon logout and/or disconnection.
     */
    @Override
    public void onFinish() {
        if (getState().equals(ActorState.FINISHED)) return;
        super.onFinish();
        getComponentManager().onUpdateData();
        getContainerManager().onUpdateData();
        getComponentManager().onFinish();
        getContainerManager().onFinish();
        GameWorld.removePlayer(this);
    }

    /**
     * Sends an Empty Payload Message to the client
     * @param opcode
     */
    public void sendMessage(PacketOuterClass.Opcode opcode) {
        sendMessage(opcode, PacketOuterClass.EmptyPayload.newBuilder().build());
    }

    /**
     * Sends a packet to the client with the specified opcode and proto message.
     * @param opcode
     * @param message
     */
    public void sendMessage(PacketOuterClass.Opcode opcode, GeneratedMessageV3 message) {
        if (channel == null || !channel.isActive() || !channel.isOpen()) return;
        channel.writeAndFlush(PacketOuterClass.Packet.newBuilder().setOpcode(opcode).setPayload(message.toByteString()).build())
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }


    /**
     * Plays a Sound cue at the {@link Player} {@link GridCoordinate}
     *
     * Volume is a modifier attribute and should mostly remain at 1.
     *
     * @param name
     */
    public void playSoundEffect(String name) {
        SoundUDataTable table = SoundUDataTable.forSFX(name);
        if (table != null)
            playSoundEffect(table.id);
    }

    /**
     * Plays a Sound cue at the {@link Player} {@link GridCoordinate}
     *
     * Volume is a modifier attribute and should mostly remain at 1.
     *
     * @param name
     */
    public void playSoundEffect(String name, GridCoordinate coordinate) {
        SoundUDataTable table = SoundUDataTable.forSFX(name);
        if (table != null)
            playSoundEffect(table.id, coordinate);
    }


    /**
     * Plays a Sound cue at the {@link Player} {@link GridCoordinate}
     *
     * Volume is a modifier attribute and should mostly remain at 1.
     *
     * @param id
     */
    public void playSoundEffect(int id) {
        sendMessage(PacketOuterClass.Opcode.SMSG_PLAY_SOUND_EFFECT, World.PlaySoundCue.newBuilder().setCueId(id).build());
    }

    /**
     * Plays a Sound cue at the {@link Player} {@link GridCoordinate}
     *
     * Volume is a modifier attribute and should mostly remain at 1.
     *
     * @param id
     */
    public void playSoundEffect(int id, GridCoordinate coordinate) {
        World.WorldTransform.Builder transform = World.WorldTransform.newBuilder();
        transform.setLocation(coordinate.toWorldVector());
        sendMessage(PacketOuterClass.Opcode.SMSG_PLAY_SOUND_EFFECT_AT_LOC, World.PlaySoundEffectAtLocation.newBuilder().setId(id).setTransform(transform.build()).build());
    }


    /**
     * Plays a Sound Track
     *
     * Volume is a modifier attribute and should mostly remain at 1.
     *
     * @param name
     */
    public void playSoundTrack(String name) {
        SoundUDataTable table = SoundUDataTable.forTrack(name);
        if (table != null)
            playSoundTrack(table.id);
    }


    /**
     * Plays a Sound Track
     *
     * Volume is a modifier attribute and should mostly remain at 1.
     *
     * @param id
     */
    public void playSoundTrack(int id) {
        sendMessage(PacketOuterClass.Opcode.SMSG_PLAY_SOUND_TRACK, World.PlaySoundCue.newBuilder().setCueId(id).build());
    }

    /**
     * Overrides the getComponentManager getter from parent to our new
     *      player c manager.
     * @return the component manager
     */
    @Override
    public PlayerComponentManager getComponentManager() {
        return (PlayerComponentManager) super.getComponentManager();
    }

    /**
     * Gets the Character Container for Component
     * @param components
     * @param <T>
     * @return the container
     */
    public <T extends Container> T container(Containers<T> components) {
        return (T) getContainerManager().get(components);
    }

    /**
     * Attatches a Component
     * @param components
     * @param component
     */
    public void attatchComponent(Components<?> components, Component component) {
        getComponentManager().getComponents().put(components, component);
    }

    /**
     * Attaches a Container Component
     * @param components
     * @param component
     */
    public void attatchContainer(Components<?> components, Container component) {
        getContainerManager().getComponents().put(components, component);
    }



}
