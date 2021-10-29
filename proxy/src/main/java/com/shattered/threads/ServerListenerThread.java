package com.shattered.threads;

import com.shattered.BuildProxy;
import com.shattered.ServerConstants;
import com.shattered.networking.NetworkBootstrap;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Proxy;
import com.shattered.networking.session.ext.ProxySession;
import com.shattered.router.ProxyRouting;
import com.shattered.connections.ServerType;
import com.shattered.system.SystemLogger;

/**
 * @author JTlr Frost 8/30/2019 : 2:01 PM
 */
public class ServerListenerThread extends Thread {

    /**
     * 
     */
    @Override
    public void run() {
       
        while (true) {
            try {

                Thread.sleep(1000 * 15);
                
                //If we have a successful central server
                if (BuildProxy.getInstance().getNetwork().getCentralSession() != null) {
                    
                    //Attempts to see if the realm should be null
                    if (ProxyRouting.getRealmSession() != null && !ProxyRouting.getRealmSession().getChannel().isActive()) {
                        ProxyRouting.setRealmSession(null);
                    }
                    
                    //If we don't have one at all
                    if (ProxyRouting.getRealmSession() == null) {
                        //Attempts to get a realm.
                        SystemLogger.sendSystemMessage("ServerListenerThread -> Attempting to request a realm...");
                        NetworkBootstrap.sendPacket(BuildProxy.getInstance().getNetwork().getCentralSession().getChannel(), PacketOuterClass.Opcode.P_RequestRealm, Proxy.RequestRealm.newBuilder().setCuuid(BuildProxy.getInstance().getNetwork().getConnectionUuid()).build());
                    }
                    
                    
                } else {
                    //Attempt to connect to a central server
                    SystemLogger.sendSystemMessage("ServerListenerThread -> Attempting to connect to a central server...");
                    BuildProxy.getInstance().getNetwork().authenticate(ServerType.PROXY, BuildProxy.getInstance().getNetwork().connect(ServerConstants.CENTRAL_HOST, ServerConstants.CENTRAL_DEFAULT_PORT), ProxySession.PROXY_TOKEN);
                }
                
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
