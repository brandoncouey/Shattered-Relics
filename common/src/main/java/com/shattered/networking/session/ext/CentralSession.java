package com.shattered.networking.session.ext;

import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Universal;
import com.shattered.networking.session.Session;
import com.shattered.system.SystemLogger;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;


/**
 * @author JTlr Frost 7/15/18 : 1:47 PM
 * 
 * This session is used by EVERY server that is not the central server.
 * This is the wire between server->central. Every server will automatically keep alive.
 */
public class CentralSession extends Session {


    /**
     * Represents the Connection Uuid
     */
    @Getter
    @Setter
    private String connectionUuid;
    
    /**
     * Creates a new Channel Line Session for the World Server
     * @param channel
     * @param connectionUuid
     */
    public CentralSession(Channel channel, String connectionUuid) {
        super(channel);
        setConnectionUuid(connectionUuid);
    }

    /**
     * Initializes the Thread for Sending the 'Central Server' a ping to keep the connection alive.
     */
    @Override
    public void invoke() {

        /**
         *
         * @param channel
         */
            new Thread(() -> {
                try {

                    if (getChannel() == null) return;

                    while (getChannel().isActive()) {

                        Thread.sleep(1000 * 10);
                        
                        sendMessage(PacketOuterClass.Opcode.U_Ping, Universal.Ping.newBuilder().setCuuid(getConnectionUuid()).build());
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }).start();
        
    }

    /**
     * Receives the Incoming Client Messages
     * @param message
     */
    @Override
    public void messageReceived(Object message) {
        super.messageReceived(message);
    }


    /**
     * Disconnects the {@link Channel}
     */
    @Override
    public void disconnect() {
        super.disconnect();
        SystemLogger.sendSystemMessage("Central Worker Service has been unregistered. Local Address[Address=" + getChannel().localAddress().toString() + "]");
    }
}
