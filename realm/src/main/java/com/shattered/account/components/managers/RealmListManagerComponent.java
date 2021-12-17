package com.shattered.account.components.managers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.shattered.account.RealmAccount;
import com.shattered.account.components.RealmAccountComponents;
import com.shattered.account.responses.AccountResponses;
import com.shattered.account.Account;
import com.shattered.networking.listeners.ProtoEventRepository;
import com.shattered.networking.listeners.RealmProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Proxy;
import com.shattered.networking.proto.Realm;
import com.shattered.networking.proto.Shared;
import com.shattered.networking.session.ext.WorldSession;
import com.shattered.realm.GameRealm;
import com.shattered.connections.WorldListEntry;
import com.shattered.utilities.ecs.Component;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

/**
 * @author JTlr Frost 9/5/2019 : 11:41 PM
 */
@Log
public class RealmListManagerComponent extends Component {


    /**
     * Represents the Name of the Last World Logged in
     */
    @Getter
    @Setter
    private String lastWorld;
    
    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public RealmListManagerComponent(Object gameObject) {
        super(gameObject);
    }

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
        //Registers The Request GameRealm List
        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.CMSG_REQUEST_REALM_LIST, new RealmProtoListener<PacketOuterClass.EmptyPayload>() {

            /**
             * @param message
             * @param account
             * @throws InvalidProtocolBufferException
             */
            @Override
            public void handle(PacketOuterClass.EmptyPayload message, RealmAccount account) {

                Realm.UpdateRealmList.Builder builder = Realm.UpdateRealmList.newBuilder();

                for (WorldListEntry entry : GameRealm.getWorldList()) {
                    if (entry == null) continue;
                    Realm.UpdateRealmList.WorldEntry.Builder world = Realm.UpdateRealmList.WorldEntry.newBuilder();
                    world.setIndex(entry.getId());
                    world.setName(entry.getName());
                    world.setLocation(entry.getLocation());
                    world.setType(entry.getType());
                    world.setPopulation(entry.getPopulation());
                    world.build();
                    builder.addEntry(world);
                }
                builder.getEntryList().forEach(e -> System.out.println(e.getIndex()));
                account.sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_REALM_LIST, builder.build());
            }
        }, PacketOuterClass.EmptyPayload.getDefaultInstance());

        //Registers the World login Request
        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.CMSG_WORLD_LOGIN_REQUEST, new RealmProtoListener<Realm.WorldLoginRequest>() {

            /**
             * @param message
             * @param account
             */
            @Override
            public void handle(Realm.WorldLoginRequest message, RealmAccount account) {
                WorldListEntry world = GameRealm.forIndex(message.getWorldIndex());
                if (world != null) {
                    account.sendMessage(PacketOuterClass.Opcode.P_TransferToWorld, Proxy.TransferToWorld.newBuilder().setCuuid(world.getConnectionUuid()).setToken(WorldSession.WORLD_TOKEN)
                            .setPermissionLevel(account.getAccountInformation().getAccountLevel().ordinal()).
                            setCharacterId(account.component(RealmAccountComponents.CHARACTER_MANAGER).getCharacterInformation().getId()).
                            setCharacterName(account.component(RealmAccountComponents.CHARACTER_MANAGER).getCharacterInformation().getName()).
                            setMapName(account.component(RealmAccountComponents.CHARACTER_MANAGER).getCharacterInformation().getMapName()).
                            setAccountId(account.getAccountInformation().getAccountId()).setAccountName(account.getAccountInformation().getAccountName()).
                            setPassword(account.getAccountInformation().getPassword()).setHost(world.getSocket().getHostName()).setPort(world.getSocket().getPort()).build());
                    account.component(RealmAccountComponents.REALM_LIST_MANAGER).setLastWorld(world.getName());
                } else
                    account.sendMessage(PacketOuterClass.Opcode.SMSG_LOGIN_RESPONSE, Shared.LoginResponse.newBuilder().setResponseId(AccountResponses.ERR_SYSTEM_UNAVAILABLE.ordinal()).build());

            }
        }, Realm.WorldLoginRequest.getDefaultInstance());

    }

    /**
     * Initializes Once the 'ChannelLine' Has been Awakened.
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {

    }

    /**
     * Called once per world cycle per each instance.
     */
    @Override
    public void onTick(long deltaSeconds) {

    }

    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {
        
    }

    /**
     * Gets the GameRealm Account
     * @return
     */
    public RealmAccount getAccount() {
        return (RealmAccount) gameObject;
    }

    /**
     * Database name.
     */
    @Override
    public String getDatabaseName() {
        return null;
    }

    /**
     * Table name.
     */
    @Override
    public String getTableName() {
        return null;
    }
}
