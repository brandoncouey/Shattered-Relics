package com.shattered.game.engine.threads;

import com.shattered.BuildWorld;
import com.shattered.ServerConstants;
import com.shattered.connections.ServerType;
import com.shattered.game.GameWorld;
import com.shattered.networking.NetworkBootstrap;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Sharding;
import com.shattered.networking.session.ext.ChannelSession;
import com.shattered.networking.session.ext.WorldSession;
import com.shattered.system.SystemLogger;

/**
 * @author JTlr Frost 1/21/2020 : 12:34 AM
 */
public class CentralTaskThread extends Thread {

    /**
     * Represents the Central Server Thread
     */
    public CentralTaskThread() {
        setName("Central Task Thread");
    }


    @Override
    public void run() {
        while (!BuildWorld.getInstance().getEngine().isShuttingDown()) {
            try {
                Thread.sleep(30000);// 30 seconds - MUST sleep first otherwise null-pointers will occur.

                if (!BuildWorld.getInstance().getNetwork().hasCentralSession()) {
                    BuildWorld.getInstance().getNetwork().authenticate(ServerType.WORLD, BuildWorld.getInstance().getNetwork().connect(ServerConstants.CENTRAL_HOST, ServerConstants.CENTRAL_DEFAULT_PORT), WorldSession.WORLD_TOKEN);
                    SystemLogger.sendSystemMessage("Attempting to request Central Server...");
                }

                if (BuildWorld.getInstance().getNetwork().hasCentralSession()) {
                    if (BuildWorld.getInstance().getNetwork().getPortIndex() != 0) {
                        BuildWorld.getInstance().getNetwork().getCentralSession().sendMessage(PacketOuterClass.Opcode.S_WorldInformation, Sharding.WorldInformation.newBuilder().
                                setCuuid(BuildWorld.getInstance().getNetwork().getConnectionUuid()).
                                setIndex(BuildWorld.getInstance().getNetwork().getPortIndex()).
                                setName(GameWorld.WORLD_NAME).setLocation(GameWorld.WORLD_LOCATION).
                                setType(GameWorld.WORLD_TYPE).setPopulation(GameWorld.getPopulation()).
                                build());
                        System.out.println("Sending with index: " + BuildWorld.getInstance().getNetwork().getPortIndex());
                    }
                }

               if (!BuildWorld.getInstance().getNetwork().hasChannelSession() && BuildWorld.getInstance().getNetwork().hasCentralSession()) {
                    BuildWorld.getInstance().getNetwork().getCentralSession().sendMessage(PacketOuterClass.Opcode.S_RequestConnectionInfo, Sharding.RequestConnectionInfo.newBuilder().setToken(ChannelSession.CHANNEL_TOKEN).build());
                   SystemLogger.sendSystemMessage("Attempting to request a Channel Server...");
               }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
