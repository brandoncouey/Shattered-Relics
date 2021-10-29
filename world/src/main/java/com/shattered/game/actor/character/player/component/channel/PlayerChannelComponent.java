package com.shattered.game.actor.character.player.component.channel;

import com.shattered.account.Account;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.channel.friend.ChannelFriendComponent;
import com.shattered.game.actor.character.player.component.channel.party.ChannelPartyComponent;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.component.WorldComponent;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.WorldProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.script.ScriptManager;
import com.shattered.script.api.impl.PlayerAPI;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.Components;
import com.shattered.utilities.ecs.ProcessComponent;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost 10/27/2019 : 5:58 PM
 */
@ProcessComponent
public class PlayerChannelComponent extends WorldComponent {


    /**
     * Represents the Channel Component Manager
     */
    @Getter
    @Setter
    private ChannelComponentManager channelComponents;

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public PlayerChannelComponent(Object gameObject) {
        super(gameObject);
        setChannelComponents(new ChannelComponentManager(getPlayer()));
        addComponents();
    }

    /**
     * Adds all the Default Channel Components
     */
    public void addComponents() {
        getChannelComponents().attatch(ChannelComponents.FRIENDS_CHANNEL, new ChannelFriendComponent(getPlayer()));
        getChannelComponents().attatch(ChannelComponents.PARTY_CHANNEL, new ChannelPartyComponent(getPlayer()));
    }

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
        getChannelComponents().onStart();
        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_CHAT_MESSAGE, new WorldProtoListener<World.ChatRequestMessage>() {

            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(World.ChatRequestMessage message, Player player)  {
                try {
                    String chatMessage = message.getMessage();
                    ChannelType type = ChannelType.forId(message.getType());
                    if (type == null) return;
                    if (chatMessage.isEmpty() || chatMessage.equals(" ")) return;

                    if (chatMessage.startsWith(".")) {


                        chatMessage = chatMessage.replace(".", "");
                        if (chatMessage == null || chatMessage.length() < 1)
                            return;
                        String syntax = chatMessage.split(" ")[0];
                        String[] arguments = chatMessage.toLowerCase().split(" ");

                        if (ScriptManager.executeCommand(new PlayerAPI(player), syntax, arguments))
                            return;

                    }
                    player.component(PlayerComponents.SOCIAL_CHANNEL).sendChannelMessage(type, chatMessage);
                } catch (Exception e) {

                }
            }
        }, World.ChatRequestMessage.getDefaultInstance());
    }

    /**
     * Used for handling the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {
        sendDefaultMessage("Welcome to Shattered Relics.");
        onChannelConnect();
    }

    /**
     * Called upon Channel Server Connection
     */
    public void onChannelConnect() {
        getChannelComponents().onWorldAwake();
    }

    /**
     * Called once per world cycle per each instance.
     */
    @Override
    public void onTick(long deltaTime) {
        getChannelComponents().onTick(deltaTime);//TODO this was second....
    }

    /**
     * Called once Actor is finished
     */
    @Override
    public void onFinish() {
        getChannelComponents().onFinish();

    }

    /**
     * Sends a message to all account in local area.
     * @param message
     */
    public void sendChannelMessage(ChannelType type, String message) {
        switch (type) {

            //Public Chat Message
            case YELL:
            case LOCAL: {
                for (Player local : getPlayer().component(GameObjectComponents.ZONE_COMPONENT).getNode().getNodePlayers().values()) {
                    if (local == null)
                        continue;

                    if (local != getPlayer()) {
                        if (local.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().distanceTo(getPlayer().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation()) > (type == ChannelType.YELL ? 4000 : 2000)) {
                            continue;
                        }
                    }
                    local.sendMessage(PacketOuterClass.Opcode.SMSG_CHAT_MESSAGE, World.ChannelMessage.newBuilder().setFromIndex(getPlayer().getClientIndex() << 1 | 1).setFromName(getPlayer().getName()).setType(type.ordinal()).setMessage(message).setPermissionLevel(getPlayer().getAccount().getAccountInformation().getAccountLevel().ordinal()).build());
                }
                break;
            }
            case PARTY: {
                if (getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).isInParty())
                    getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).sendMessage(message);
                 else
                    sendDefaultMessage("You are not in a party.");
                break;
            }

        }
    }

    /**
     * Sends a Default Message with No From Actor.
     * @param message
     */
    public void sendDefaultMessage(String message) {
        getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_GAME_MESSAGE, World.GameMessage.newBuilder().setType(ChannelType.INFO.ordinal()).setMessage(message).build());
    }

    /**
     * Sends a combat log message
     * @param message
     */
    public void sendCombatLog(String message) {
        getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_GAME_MESSAGE, World.GameMessage.newBuilder().setType(ChannelType.COMBAT.ordinal()).setMessage(message).build());
    }

    /**
     *
     * @param type
     * @param message
     */
    public void sendMessage(ChannelType type, String message) {
        getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_GAME_MESSAGE, World.GameMessage.newBuilder().setType(type.ordinal()).setMessage(message).build());
    }

    /**
     * Gets a piece of Game Object Component
     * @param components
     */
    public <T extends Component> T channel(Components<T> components) {
        return getChannelComponents().get(components);
    }


}
