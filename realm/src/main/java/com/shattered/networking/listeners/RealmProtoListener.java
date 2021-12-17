package com.shattered.networking.listeners;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.shattered.account.RealmAccount;

/**
 * @author JTlr Frost 9/8/2019 : 10:50 PM
 */
public abstract class RealmProtoListener<T extends Message> implements ProtoListener {


    /**
     *
     * @param message
     * @param account
     */
    public abstract void handle(T message, RealmAccount account);

    

    /**
     * Handles the Raw Message
     * @param message
     * @param account
     */
    public void handleRaw(Message message, RealmAccount account)  {
        try {
            handle((T) message, account);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
