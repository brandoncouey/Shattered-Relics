package com.shattered.game.actor.character.player.component.widget;

import com.shattered.account.Account;
import com.shattered.account.responses.AccountResponses;
import com.shattered.datatable.tables.AbilityUDataTable;
import com.shattered.datatable.tables.ItemUDataTable;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.ActorType;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.components.variable.PlayerVariableRepository;
import com.shattered.game.actor.object.WorldObject;
import com.shattered.game.actor.vendor.VendorContainer;
import com.shattered.game.component.WorldComponent;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.WorldProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Proxy;
import com.shattered.networking.proto.Shared;
import com.shattered.networking.proto.World;
import com.shattered.connections.ServerType;
import com.shattered.script.ScriptManager;

/**
 * @author JTlr Frost 11/18/2019 : 5:22 AM
 */
public class PlayerWidgetComponent extends WorldComponent {

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public PlayerWidgetComponent(Object gameObject) {
        super(gameObject);
    }


    /**
     * Sets the desired Widget Param with the specified key and value
     * @param widget
     * @param key
     * @param value
     */
    public void setWidgetParam(String widget, String key, String value) {
        sendMessage(PacketOuterClass.Opcode.SMSG_SET_WIDGET_PARAM_STR, Shared.WidgetParamStr.newBuilder().setWidget(widget).setKey(key).setValue(value).build());
    }

    /**
     * Sets the desired Widget Param with the specified key and value
     * @param widget
     * @param key
     * @param value
     */
    public void setWidgetParam(String widget, String key, int value) {
        sendMessage(PacketOuterClass.Opcode.SMSG_SET_WIDGET_PARAM_INT, Shared.WidgetParamInt.newBuilder().setWidget(widget).setKey(key).setValue(value).build());
    }

    /**
     * Sets the desired Widget Param with the specified key and value
     * @param widget
     * @param key
     * @param value
     */
    public void setWidgetParam(String widget, String key, boolean value) {
        sendMessage(PacketOuterClass.Opcode.SMSG_SET_WIDGET_PARAM_BOOL, Shared.WidgetParamBool.newBuilder().setWidget(widget).setKey(key).setValue(value).build());
    }

    /**
     * Creates a Widget
     * @param widgetName
     */
    public void createWidget(String widgetName) {
        sendMessage(PacketOuterClass.Opcode.SMSG_CREATE_WIDGET, World.StructWidget.newBuilder().setWidgetName(widgetName).build());
    }

    /**
     * Destructs a Widget
     * @param widgetName
     */
    public void destructWidget(String widgetName) {
        sendMessage(PacketOuterClass.Opcode.SMSG_DESTRUCT_WIDGET, World.StructWidget.newBuilder().setWidgetName(widgetName).build());
    }

    /**
     * Shows a Widget
     * @param widgetName
     */
    public void showWidget(String widgetName) {
        sendMessage(PacketOuterClass.Opcode.SMSG_SHOW_WIDGET, World.StructWidget.newBuilder().setWidgetName(widgetName).build());
    }

    /**
     * Hides a widget
     * @param widgetName
     */
    public void hideWidget(String widgetName) {
        sendMessage(PacketOuterClass.Opcode.SMSG_HIDE_WIDGET, World.StructWidget.newBuilder().setWidgetName(widgetName).build());
    }

    /**
     *
     * @param actorType
     * @param actorIndex
     * @param markerType
     */
    public void setMarker(ActorType actorType, int actorIndex, int markerType) {
        switch (actorType) {
            case NPC:
                sendMessage(PacketOuterClass.Opcode.SMSG_SET_MARKER_ON_NPC, World.SetMarkerOnNPC.newBuilder().setNpcIndex(actorIndex).setMarkerType(markerType).build());
                break;
        }
    }

    /**
     *
     * @param actorType
     * @param actorIndex
     * @param markerType
     */
    public void updateMarker(ActorType actorType, int actorIndex, int markerType) {
        switch (actorType) {
            case NPC:
                sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_MARKER_ON_NPC, World.UpdateMarkerOnNPC.newBuilder().setNpcIndex(actorIndex).setMarkerType(markerType).build());
                break;
        }
    }

    /**
     *
     * @param actorType
     * @param actorIndex
     */
    public void removeMarker(ActorType actorType, int actorIndex) {
        switch (actorType) {
            case NPC:
                sendMessage(PacketOuterClass.Opcode.SMSG_REMOVE_MARKER_ON_NPC, World.RemoveMarkerOnNPC.newBuilder().setNpcIndex(actorIndex).build());
                break;
        }
    }

    /**
     * Sends the Required Notification -1 means local player
     * @param message
     */
    public void sendWarningMessage(Actor onActor, String message) {
        int type = onActor instanceof Player ? 0 : onActor instanceof NPC ? 1 : onActor instanceof WorldObject ? 2 : -1;
        sendMessage(PacketOuterClass.Opcode.SMSG_DISPLAY_ACTOR_WARNING_MESSAGE, World.ActorWarningMessage.newBuilder().setUuid(onActor.getClientIndex() << 2 | type).setMessage(message).build());
    }

    /**
     * Sends the Required Notification -1 means local player
     * @param message
     */
    public void sendWarningMessage(String message) {
        sendMessage(PacketOuterClass.Opcode.SMSG_DISPLAY_ACTOR_WARNING_MESSAGE, World.ActorWarningMessage.newBuilder().setUuid(getPlayer().getClientIndex() << 2 | 0).setMessage(message).build());
    }

    /**
     *
     * @param stat
     * @param experience
     */
    public void sendStatIncreasedNotification(String stat, int experience) {
        int varId = PlayerVariableRepository.forVarIntId(stat);
        if (varId > 0) sendStatIncreasedNotification(varId, experience);
    }

    /**
     * Sends a Stat Increased Experience Notification
     * @param statId
     * @param experience
     */
    public void sendStatIncreasedNotification(int statId, int experience) {
        sendMessage(PacketOuterClass.Opcode.SMSG_STAT_XP_NOTIFICATION, World.IXPNotification.newBuilder().setId(statId).setAmount(experience).build());
    }

    /**
     * Sends a Stat Mastery Increase Notification
     * @param statId
     * @param state
     */
    public void sendStatMasteryNotification(int statId, int state) {
        sendMessage(PacketOuterClass.Opcode.SMSG_STAT_MASTERY_NOTIFICATION, World.MasteryNotification.newBuilder().setId(statId).setState(state).build());
    }

    /**
     * Sends a Stat XP Increase Notification
     * @param stat
     * @param amount
     */
    public void sendStatXPNotification(String stat, int amount) {
        int statId = -1;
        sendMessage(PacketOuterClass.Opcode.SMSG_STAT_XP_NOTIFICATION, World.IXPNotification.newBuilder().setId(statId).setAmount(amount).build());
    }

    /**
     * Sends a Trade Mastery Increase Notification
     * @param trade
     * @param state
     */
    public void sendTradeMasteryNotification(String trade, int state) {
        int tradeId = -1;
        sendMessage(PacketOuterClass.Opcode.SMSG_TRADE_MASTERY_NOTIFICATION, World.MasteryNotification.newBuilder().setId(tradeId).setState(state).build());
    }

    /**
     * Sends a Trade XP Increase Notification
     * @param trade
     * @param amount
     */
    public void sendTradeXPNotification(String trade, int amount) {
        int tradeId = -1;
        sendMessage(PacketOuterClass.Opcode.SMSG_TRADE_XP_NOTIFICATION, World.IXPNotification.newBuilder().setId(tradeId).setAmount(amount).build());
    }

    /**
     * Sends a Combat Level Up Notification
     * @param level
     */
    public void sendCombatLevelNotification(int level) {
        sendMessage(PacketOuterClass.Opcode.SMSG_COMBAT_LEVEL_NOTIFICATION, World.CombatLevelUpNotification.newBuilder().setLevel(level).build());
    }

    /**
     * If it's an item its a recipe, if its not, it's an ability.
     * @param name
     * @param isItem
     */
    public void sendLearnedNotification(String name, boolean isItem) {
        int itemId = isItem ? ItemUDataTable.forId(name) : AbilityUDataTable.forId(name);
        itemId = itemId << 1 | (isItem ? 1 : 0);
        sendMessage(PacketOuterClass.Opcode.SMSG_LEARNED_NOTIFICATION, World.LearnedNotification.newBuilder().setItemId(itemId).build());
    }

    /**
     * Sends a quest completed notification
     * @param questName
     */
    public void sendQuestCompletedNotification(String questName) {
        int questId = -1;//TODO look up quest by id
        sendMessage(PacketOuterClass.Opcode.SMSG_QUEST_COMPLETED_NOTIFICATION, World.QuestNotification.newBuilder().setQuestName(questName).build());
    }

    /**
     * Sends a Quest discovered notification
     * @param questName
     */
    public void sendQuestDiscoveredNotification(String questName) {
        int questId = -1;//TODO look up quest by id
        sendMessage(PacketOuterClass.Opcode.SMSG_QUEST_DISCOVERED_NOTIFICATION, World.QuestNotification.newBuilder().setQuestName(questName).build());
    }

    /**
     * Sends a Reputation Level Up Notification
     * @param name
     * @param state
     */
    public void sendReputationGainedNotification(String name, int state) {
        sendMessage(PacketOuterClass.Opcode.SMSG_REPUTATION_GAINED_NOTIFICATION, World.SXPNotification.newBuilder().setName(name).setAmount(state).build());
    }

    /**
     * Sends a Reputation XP Notification
     * @param name
     * @param amount
     */
    public void sendReputationXPNotification(String name, int amount) {
        sendMessage(PacketOuterClass.Opcode.SMSG_REPUTATION_GAINED_NOTIFICATION, World.SXPNotification.newBuilder().setName(name).setAmount(amount).build());
    }

    /**
     * Sends an Item Acquuired Notification
     * @param id
     * @param amount
     */
    public void sendItemAcquiredNotification(int id, int amount) {
        sendMessage(PacketOuterClass.Opcode.SMSG_ITEM_REWARD_NOTIFICATION, World.ItemSlot.newBuilder().setId(id).setAmount(amount).build());
    }

    /**
     * Sends the Action Time Progress Bar (Cancel Action)
     * @param action
     * @param duration
     */
    public void sendActionTimeProgressBar(String action, float duration) {
        sendMessage(PacketOuterClass.Opcode.SMSG_DISPLAY_ACTION_PROGRESS_BAR, World.DisplayActionProgressBar.newBuilder().setAction(action).setDuration((int) duration).build());//TODO change this shit
    }

    @Override
    public void onStart() {

        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_REQUEST_BUTTON_HANDLE, new WorldProtoListener<Shared.ButtonRequestHandle>() {

            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(Shared.ButtonRequestHandle message, Player player) {
                ScriptManager.onClickedWidget(message.getWidgetName(), player, message.getParametersList(), message.getButtonId());
            }
        }, Shared.ButtonRequestHandle.getDefaultInstance());


        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_REQUEST_LOGOUT, new WorldProtoListener<Shared.RequestLogout>() {
            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(Shared.RequestLogout message, Player player) {

                //TODO handle logout, for now lets just make it logout.
                //TODO make it so they can't be in combat... etcc....

                //need a response...
                player.sendMessage(PacketOuterClass.Opcode.P_ServerAvailability, Proxy.ServerAvailability.newBuilder().setType(ServerType.REALM.ordinal()).build());

                //TODO wait for response until sending this...

            }
        }, Shared.RequestLogout.getDefaultInstance());


        ProtoEventListener.registerListener(PacketOuterClass.Opcode.P_ServerAvailability, new WorldProtoListener<Proxy.ServerAvailabilityResponse>() {

            /**
             *
             * @param message
             * @param player
             */
            @Override
            public void handle(Proxy.ServerAvailabilityResponse message, Player player) {
                if (message.getAvailable()) {
                    player.sendMessage(PacketOuterClass.Opcode.SMSG_LOGOUT, Shared.RequestLogout.newBuilder().setRealm(true).build());
                    player.sendMessage(PacketOuterClass.Opcode.P_TransferWorldToRealm, Proxy.RequestWorldToRealmTransfer.newBuilder().setAccountName(player.getAccount().getAccountInformation().getAccountName()).setAccountPassword(player.getAccount().getAccountInformation().getPassword()).build());
                } else {
                    player.sendMessage(PacketOuterClass.Opcode.SMSG_LOGOUT_WITH_RESPONSE, Shared.LogoutWithResponse.newBuilder().setResponseId(AccountResponses.ERR_SYSTEM_UNAVAILABLE.ordinal()).build());
                }
            }
        }, Proxy.ServerAvailabilityResponse.getDefaultInstance());


    }

    @Override
    public void onWorldAwake() {
    }

    @Override
    public void onTick(long deltaTime) {

    }

    @Override
    public void onFinish() {

    }
}
