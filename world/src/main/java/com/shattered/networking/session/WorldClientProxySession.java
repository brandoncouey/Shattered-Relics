package com.shattered.networking.session;

import com.google.protobuf.Message;
import com.shattered.account.AccountInformation;
import com.shattered.account.WorldAccount;
import com.shattered.game.actor.character.player.PlayerInformation;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.WorldProtoListener;
import com.shattered.networking.messages.QueuedMessage;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.session.ext.ClientSession;
import com.shattered.system.SystemLogger;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author JTlr Frost 8/31/2019 : 2:08 AM
 */
@Log
public class WorldClientProxySession extends ClientSession {

    /**
     * Represents the Current Account
     */
    @Setter
    @Getter
    private WorldAccount account;



    /**
     * Creates a new Channel Line Session for the World ServerConnections
     *
     * @param channel
     * @param connUuid
     */
    public WorldClientProxySession(Channel channel, String connUuid, int accountId, int permissionLevel, String accountName, int characterId, String characterName, String password) {
        super(channel, connUuid);
        setAccount(new WorldAccount(channel, new AccountInformation(accountId, accountName, connUuid, password, AccountInformation.AccountLevel.forId(permissionLevel)), new PlayerInformation(characterId, characterName)));
    }

    /**
     *
     * @param object
     */
    @Override
    public void messageReceived(Object object) {
        if (!(((PacketOuterClass.Packet) object).getOpcode() == PacketOuterClass.Opcode.CMSG_TRANSFORM_UPDATE))
            SystemLogger.sendSystemMessage("Incoming WorldClientMessage -> " + ((PacketOuterClass.Packet) object).getOpcode());

        //Attempts to pull the Opcode from the message
        PacketOuterClass.Opcode opcode = ((PacketOuterClass.Packet) object).getOpcode();
        if (opcode == null) return;


        //Ensures a Valid Opcode
        if (ProtoEventListener.getBuilders().get(opcode) == null) return;

        //Ensures the Opcode is Registered
        WorldProtoListener listener = (WorldProtoListener) ProtoEventListener.forOpcode(opcode);
        if (listener == null) {
            SystemLogger.sendSystemErrMessage("Incoming Unhandled Opcode: " + opcode.name());
            return;
        }

        //Attempts to Decode the Message
        try {
            Message message = ProtoEventListener.decode((PacketOuterClass.Packet) object);
            if (message == null) return;
            if (account != null) {

                if (account.getPlayer().getMessages().size() >= 548){
                    SystemLogger.sendSystemMessage("Dropping incoming packet, queue full: " + ((PacketOuterClass.Packet) object).getOpcode().name());
                    return;
                }
                QueuedMessage queuedMessage = new QueuedMessage(listener, message);
                account.getPlayer().getMessages().add(queuedMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Disconnects the World Client Proxy Session
     */
    @Override
    public void disconnect() {
        super.disconnect();
        if (account != null) {
            account.onFinish();
        }
        getChannel().disconnect();
    }
}
