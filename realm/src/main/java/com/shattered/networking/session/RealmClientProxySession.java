package com.shattered.networking.session;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.shattered.account.components.RealmAccountComponents;
import com.shattered.authentication.AccountAuthentication;
import com.shattered.account.RealmAccount;
import com.shattered.account.responses.AccountResponses;
import com.shattered.database.mysql.MySQLEntry;
import com.shattered.networking.NetworkBootstrap;
import com.shattered.networking.listeners.ProtoEventRepository;
import com.shattered.networking.listeners.ProtoListener;
import com.shattered.networking.listeners.RealmProtoListener;
import com.shattered.networking.message.ProtoMessageRepository;
import com.shattered.networking.message.QueuedMessage;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Proxy;
import com.shattered.networking.proto.Realm;
import com.shattered.networking.proto.Shared;
import com.shattered.networking.session.ext.ClientSession;
import com.shattered.realm.GameRealm;
import com.shattered.connections.WorldListEntry;
import com.shattered.system.SystemLogger;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author JTlr Frost <brradc@gmail.com> 6/15/2019
 *
 * This class is handled for a client->proxy routing messages to the realm. 
 */
@Log
public class RealmClientProxySession extends ClientSession implements MySQLEntry {


    /**
     * Represents the Current Account
     */
    @Getter
    @Setter
    private RealmAccount account;

    /**
     * Represents the registry for this session instance
     */
    @Getter
    private final ProtoMessageRepository registry = new ProtoMessageRepository();

    /**
     * Represents a List of Decoders
     */
    @Getter
    private final Map<ProtoListener<?>, PacketOuterClass.Opcode> listeners = new HashMap<>();

    /**
     * Represents a List of Builders
     */
    @Getter
    private final Map<PacketOuterClass.Opcode, GeneratedMessageV3> builders = new HashMap<>();

    /**
     * The queue of pending {@link Message}s.
     */
    @Getter
    private final ConcurrentLinkedQueue<QueuedMessage> mapQueues = new ConcurrentLinkedQueue<>();

    /**
     * Creates a new Channel Line Session for the World ServerConnections
     *
     * @param channel
     * @param connUuid
     */
    public RealmClientProxySession(Channel channel, String connUuid) {
        super(channel, connUuid);
    }

    @Override
    public void invoke() {
        
        //Registers the Transfer To GameRealm Request
        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.P_TransferToRealm, new ProtoListener<Proxy.TransferToRealm>() {

            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Proxy.TransferToRealm message, Session session) {
                
                RealmClientProxySession proxySession = (RealmClientProxySession) session;
                //Attempts to authenticate
                proxySession.authenticate(session.getChannel(), message.getAccountName(), message.getPassword(), false,false);
                
            }
        }, Proxy.TransferToRealm.getDefaultInstance());

        //Registers the Client Login Request
        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.CMSG_LOGIN_REQUEST, new ProtoListener<Shared.LoginRequest>() {

            @Override
            public void handle(Shared.LoginRequest message, Session session) {
                SystemLogger.sendSystemMessage("CMSG_LOGIN_REQUEST -> Username=" + message.getUserId() + ", Password=" + message.getPassword() + ", RequestType=" + message.getRequestType() + ", IsDiscord=" + message.getIsDiscord());

                //Represents the Major Patch
                int majorPatch = message.getBuild();

                //Represents the Minor Patch
                int minorPatch = message.getSubBuild();

                //Represents the Hot Patch
                int hotPatch = message.getHotPatch();

                //Represents if logging in with discord
                boolean isDiscord = message.getIsDiscord();

                //Login With Discord ? token : username
                String userId = message.getUserId();

                //Login With Discord does not give password
                String password = "";

                //Sets password if it's not login with discord
                if (!isDiscord)
                     password = message.getPassword();

                //Represents if GameRealm ? World
                boolean isWorld = message.getRequestType() == 1;
                
                RealmClientProxySession proxySession = (RealmClientProxySession) session;

                //Authenticates the Account
                proxySession.authenticate(proxySession.getChannel(), userId, password, isDiscord, isWorld);


            }
        }, Shared.LoginRequest.getDefaultInstance());

    }

    @SuppressWarnings("Duplicates")
    @Override
    public void messageReceived(Object object) {
        if (!(object instanceof PacketOuterClass.Packet)) return;

        PacketOuterClass.Opcode opcode = ((PacketOuterClass.Packet) object).getOpcode();

        if (opcode != PacketOuterClass.Opcode.CMSG_TRANSFORM_UPDATE)
            SystemLogger.sendSystemMessage("Incoming WorldClientMessage -> " + ((PacketOuterClass.Packet) object).getOpcode());

        try {

            if (ProtoEventRepository.forOpcode(opcode) == null) {
                SystemLogger.sendSystemErrMessage("Unhandled incoming packet, Opcode=" + opcode.name() + ".");
                return;
            }

            Message message = ProtoEventRepository.decode((PacketOuterClass.Packet) object);
            if (message == null)  {
                SystemLogger.sendSystemErrMessage("Dropping packet! Message is error, unknown packet.");
                return;
            }

            if (account != null) {
                handle(opcode, message);
            } else {
                super.messageReceived(object);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the incoming packet
     * @param opcode
     * @param message
     */
    public void handle(PacketOuterClass.Opcode opcode, Message message) {
        RealmProtoListener<?> handler = (RealmProtoListener<?>) ProtoEventRepository.forOpcode(opcode);

        if (handler == null || ProtoEventRepository.forOpcode(opcode) == null) {
            SystemLogger.sendSystemErrMessage("We have an unidentified packet being dropped! Null Handler and Opcode!");
            return;
        }
        handler.handleRaw(message, account);
    }

    /**
     * Handles Disconnecting of the Account
     */
    @Override
    public void disconnect() {
        super.disconnect();

        if (account != null) {
            account.onFinish();
        }
        
        getChannel().disconnect();
    }

    /**
     * Authenticates the Account
     * @param channel
     * @param accountName
     * @param password
     * @param isDiscord
     * @param isWorld
     */
    private void authenticate(Channel channel, String accountName, String password, boolean isDiscord, boolean isWorld) {

        try {
            //Attempts to find the Account Information
            AccountAuthentication authenticationRequest = new AccountAuthentication(accountName, password, isDiscord);


            // Attempts to find the collection with accountRequest (Insensitive)
            if (authenticationRequest.getResults() == null) {
                //Results were not found, and sending an Err
                sendAuthenticationResponse(channel, AccountResponses.ERR_LOADING_ACCOUNT);
                return;
            } else {

                //If there are results with the requested entries, it will begin to fetch the information from the datatable.
                authenticationRequest.fetch();

                /* Checks if the Authentication has an ErrCode, If So it will send the response. */
                //---------- If the results are null, It will represent a (currently successful authentication! ----------\\
                if (authenticationRequest.getResponseType() != null) {
                    sendAuthenticationResponse(channel, authenticationRequest.getResponseType());
                    return;
                }
            }

            //--------------- End Of Account Credentials -----------\\

            /* \\\\\\\\\\\\\\\\\\\\ IF Code reaches here' Sets the Authentication to 'Successful'. //////////////////////// */

            sendAuthenticationResponse(channel, AccountResponses.SUCCESSFUL);

            if (!isWorld) {
                //Initializes the Account
                setAccount(new RealmAccount(channel, authenticationRequest.getInformationResult()));
                getAccount().getAccountInformation().setConnectionUuid(getConnUuid());
            }

            WorldListEntry entry = GameRealm.getAvailableEntry();

            //Initializes the Player Information to the client
            NetworkBootstrap.sendPacket(channel, PacketOuterClass.Opcode.SMSG_ACCOUNT_INFORMATION, Realm.AccountInformation.newBuilder()
                    .setName(account.getAccountInformation()
                            .getAccountName()).setLevel(account.getAccountInformation().getAccountLevel().ordinal())
                    .setWorld(entry == null ? "" : entry.getName())
                    .setIndex(entry == null ? 0 : entry.getId()).build());

            //Initializes the Account Components
            account.onStart();


            if (account.component(RealmAccountComponents.CHARACTER_MANAGER).getCharacterInformation() != null) {
                Realm.CharacterInformation.Builder modelInfo = account.component(RealmAccountComponents.CHARACTER_MANAGER).getCharacterInformation().getModelInformation();
                account.sendMessage(PacketOuterClass.Opcode.SMSG_CHARACTER_INFORMATION, Realm.CharacterInformation.newBuilder()
                        .setName(account.component(RealmAccountComponents.CHARACTER_MANAGER).getCharacterInformation().getName())
                        .setLocation(account.component(RealmAccountComponents.CHARACTER_MANAGER).getCharacterInformation().getLocation())
                        .setMale(modelInfo.getMale())
                        .setRace(modelInfo.getRace())
                        .setHairStyle(modelInfo.getHairStyle())
                        .setEyebrowStyle(modelInfo.getEyebrowStyle())
                        .setEyeColor(modelInfo.getEyeColor())
                        .setBeardStyle(modelInfo.getBeardStyle())
                        .setHeadSlotId(modelInfo.getHeadSlotId())
                        .setShouldersSlotId(modelInfo.getShouldersSlotId())
                        .setChestSlotId(modelInfo.getChestSlotId())
                        .setPantsSlotId(modelInfo.getPantsSlotId())
                        .setWristsSlotId(modelInfo.getWristsSlotId())
                        .setGlovesSlotId(modelInfo.getGlovesSlotId())
                        .setBackSlotId(modelInfo.getBootsSlotId())
                        .setMainhandSlotId(modelInfo.getMainhandSlotId())
                        .setOffhandSlotId(modelInfo.getOffhandSlotId())
                        .build());
            } else {
                account.sendMessage(PacketOuterClass.Opcode.SMSG_CREATE_CHARACTER);
            }


            //Logs the Request
            SystemLogger.sendSystemMessage("Successful LoginRequest[Username=" + accountName + ", Password=" + password + "].");
        } catch (Exception e) {
            e.printStackTrace();
            sendAuthenticationResponse(channel, AccountResponses.ERR_LOADING_ACCOUNT);
        }
    }

    /**
     *
     * @param channel
     * @param responseType
     */
    private void sendAuthenticationResponse(Channel channel, AccountResponses responseType) {
        NetworkBootstrap.sendPacket(channel, PacketOuterClass.Opcode.SMSG_LOGIN_RESPONSE, Shared.LoginResponse.newBuilder().setResponseId(responseType.ordinal()).build());
    }


}
