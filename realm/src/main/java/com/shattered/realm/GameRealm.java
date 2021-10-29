package com.shattered.realm;

import com.shattered.account.Account;
import com.shattered.account.AccountInformation;
import com.shattered.account.RealmAccount;
import com.shattered.account.components.RealmAccountComponents;
import com.shattered.connections.WorldListEntry;
import com.shattered.system.SystemLogger;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author JTlr Frost 7/20/18 : 5:39 PM
 */
public class GameRealm {

    /**
     * Represents All Accounts within the GameRealm
     */
    @Getter
    private static final List<RealmAccount> accounts = new CopyOnWriteArrayList<>();

    /**
     * Represents All GameRealm Entries
     * 
     * This RealmList is different than the ServerConnections.getWorldList()
     *      this list is used for writing to the client as
     *          ServerConnections is used for holding the information from the worlds.
     */
    @Getter
    private static final List<WorldListEntry> worldList = new CopyOnWriteArrayList<>();


    /**
     * Method called upon Channel Server Connection Initialized
     */
    public static void onChannelConnect() {
        for (RealmAccount account : accounts) {
            if (account == null) continue;
            account.component(RealmAccountComponents.FRIENDS_CHANNEL).onChannelConnect();
        }
    }


    /**
     * Finds an informationResult within the GameRealm
     *
     * @param accountName
     * @return informationResult
     */
    public static RealmAccount findAccount(String accountName) {
        return (RealmAccount) accounts.stream().filter(Objects::nonNull).filter(account -> accountName.equalsIgnoreCase(account.getAccountInformation().getAccountName())).findFirst().orElse(null);
    }

    /**
     * Finds an account for the account uuid
     * @param uuid
     * @return
     */
    public static RealmAccount findAccount(int uuid) {
        for (Account account : getAccounts()) {
            if (account == null) continue;
            if (account.getAccountInformation().getAccountId() == uuid)
                return (RealmAccount) account;
        }
        return null;
    }

    /**
     * Checks if an Account is within the GameRealm
     * @param accountName
     * @return
     */
    public static boolean containsAccount(String accountName) {
        return accounts.stream().filter(Objects::nonNull).anyMatch(account -> accountName.equalsIgnoreCase(account.getAccountInformation().getAccountName()));
    }

    /**
     * Adds the {@link Account} to the {@code GameRealm}
     * @param account
     * @return added
     */
    public static boolean addAccount(RealmAccount account) {
        synchronized (accounts) {
            if (accounts.contains(account))
                return true;
            accounts.add(account);
           // RedisAccountStatusDB.getInstance().getCommands().set(account.getAccountInformation().getAccountId(), AccountInformation.OnlineStatus.REALM.ordinal());
            return true;
        }
    }

    /**
     * Removes The {@link Account} from the {@code GameRealm}.
     * @param account
     * @return removed
     */
    public static boolean removeAccount(Account account) {
        synchronized (accounts) {
            if (!accounts.contains(account))
                return true;
            accounts.remove(account);
            //RedisAccountStatusDB.getInstance().getCommands().set(account.getAccountInformation().getAccountId(), AccountInformation.OnlineStatus.OFFLINE.ordinal());
            return true;
        }
    }

    /**
     * Gets a list of accounts with a desired {@link AccountInformation.AccountLevel}
     *
     * @param accountLevel
     * @return
     */
    public static List<RealmAccount> listByAccountLevel(AccountInformation.AccountLevel accountLevel) {
        getAccounts().stream().filter(Objects::nonNull).filter(account -> account.getAccountInformation().getAccountLevel().equals(accountLevel)).forEach(accounts::add);
        return accounts;
    }

    /**
     * Finds a World List Entry for Connection UUID
     * @param connectionUuid
     * @return
     */
    public static WorldListEntry forUuid(String connectionUuid) {
        for (WorldListEntry entry : getWorldList()) {
            if (entry == null) continue;
            if (entry.getConnectionUuid().equals(connectionUuid)) return entry;
        }
        return null;
    }

    /**
     * Finds a World List Entry for Index
     * @param index
     * @return
     */
    public static WorldListEntry forIndex(int index) {
        for (WorldListEntry entry : getWorldList()) {
            if (entry == null) continue;
            if (entry.getId() == index) return entry;
        }
        return null;
    }

    /**
     * Registers a GameRealm
     * @param entry
     */
    public static void registerWorld(WorldListEntry entry) {
        synchronized (getWorldList()) {
            if (getWorldList().contains(entry))
                return;
            getWorldList().add(entry);
            SystemLogger.sendSystemMessage("GameRealm -> Successfully Registered Entry {cuuid=" + entry.getConnectionUuid()+ ", name=" + entry.getName() + ", host=" + entry.getSocket().getAddress().toString() + "}");
        }
    }

    /**
     * Unregisters a World Entry
     * @param entry
     */
    public static void unregisterWorld(WorldListEntry entry) {
        synchronized (getWorldList()) {
            if (!getWorldList().contains(entry)) return;
            getWorldList().remove(entry);
            SystemLogger.sendSystemMessage("GameRealm -> Successfully Unregistered Entry {cuuid=" + entry.getConnectionUuid() + ", name=" + entry.getName() + ", host=" + entry.getSocket().getAddress().toString() + "}");
        }
    }

    /**
     * Gets the First Available Entry.
     * @return
     */
    public static WorldListEntry getAvailableEntry() {
        for (WorldListEntry entry : getWorldList()) {
            if (entry == null) continue;;
            return entry;
        }
        return null;
    }

}
