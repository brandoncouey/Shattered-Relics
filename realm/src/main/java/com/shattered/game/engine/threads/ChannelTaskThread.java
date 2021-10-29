package com.shattered.game.engine.threads;

import com.shattered.BuildRealm;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Sharding;
import com.shattered.networking.session.ext.ChannelSession;
import com.shattered.system.SystemLogger;

public class ChannelTaskThread extends Thread {


    public ChannelTaskThread() {
        setName("Channel Task");
        setPriority(Thread.MIN_PRIORITY);
    }


    @Override
    public void run() {
        while (!BuildRealm.getInstance().getEngine().isShuttingDown()) {
            try {
                Thread.sleep(30000);// 30 seconds - MUST sleep first otherwise null-pointers will occur.
                if (!BuildRealm.getInstance().getNetwork().hasChannelSession() && BuildRealm.getInstance().getNetwork().hasCentralSession()) {
                    BuildRealm.getInstance().getNetwork().getCentralSession().sendMessage(PacketOuterClass.Opcode.S_RequestConnectionInfo, Sharding.RequestConnectionInfo.newBuilder().
                            setToken(ChannelSession.CHANNEL_TOKEN).
                            build());
                    SystemLogger.sendSystemMessage("Attempting to request a Channel Server...");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
