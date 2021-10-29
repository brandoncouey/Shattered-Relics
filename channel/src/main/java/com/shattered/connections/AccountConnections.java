package com.shattered.connections;

import com.shattered.account.Account;
import com.shattered.account.ChannelAccount;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author JTlr Frost 2/1/2020 : 3:57 PM
 */
public class AccountConnections {

    /**
     * Represents a List of Characters
     */
    @Getter
    private static Map<Integer, ChannelAccount> channelAccounts = new ConcurrentHashMap<>();

    /**
     * Checks if the ChannelAccount is online by UUID.
     * @param uuid
     * @return
     */
    public static boolean isOnline(int uuid) {
       return channelAccounts.containsKey(uuid) && channelAccounts.get(uuid) != null;
    }

    /**
     * Checks if a ChannelAccount is Online by Name.
     * @param name
     * @return
     */
    public static boolean isOnline(String name) {
        for (ChannelAccount channelAccount : channelAccounts.values()) {
            if (channelAccount == null) continue;
            if (channelAccount.getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    /**
     * Gets the Channel Account for UUID
     * @param uuid
     * @return
     */
    public static ChannelAccount getAccountForId(int uuid) {
        for (ChannelAccount channelAccount : channelAccounts.values()) {
            if (channelAccount == null) continue;
            if (channelAccount.getUuid() == uuid) return channelAccount;
        }
        return null;
    }

    /**
     * Gets the Chanenl Account for the Name
     * @param name
     * @return
     */
    public static ChannelAccount getAccountForName(String name) {
        for (ChannelAccount channelAccount : channelAccounts.values()) {
            if (channelAccount == null) continue;
            if (channelAccount.getName().equalsIgnoreCase(name)) return channelAccount;
        }
        return null;
    }

    /**
     * Registers an Account to the Channel Server
     * @param account
     */
    public static void registerAccount(ChannelAccount account) {
        if (channelAccounts.containsKey(account.getUuid())) return;
        channelAccounts.put(account.getUuid(), account);
    }

    /**
     * Deregisters an Account from the Channel Server
     * @param uuid
     */
    public static void deregisterAccount(int uuid) {
        if (!channelAccounts.containsKey(uuid)) return;
        channelAccounts.remove(uuid);
    }
}
