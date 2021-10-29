package com.shattered.game.actor.character.player.component.container.trade;

import com.shattered.game.GameWorld;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.container.Container;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.object.item.Item;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.WorldProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Shared;
import com.shattered.networking.proto.World;
import com.shattered.utilities.ecs.ProcessComponent;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Brad
 */
@ProcessComponent
public class TradeContainer extends Container {


    /**
     * Represents the Current {@link Player} you're trading with
     */
    @Getter
    @Setter
    private Player current;

    /**
     * Represents the amount of coins currently offering
     */
    @Getter
    @Setter
    private int coins;

    /**
     * Represents if the other Player has Accepted
     */
    @Getter
    @Setter
    private boolean accepted;

    /**
     * Used for {@link Containers} identifying.
     */
    public TradeContainer() {
        super(null, 12);
    }

    /**
     * Creates a new Trade Container for the {@link Player}
     * Doubles the container size for both parties.
     * @param player
     */
    public TradeContainer(Player player) {
        super(player, 12);
    }

    /**
     * Called upon the {@link Player} onStart method
     */
    @Override
    public void onStart() {
        super.onStart();

        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_OFFER_COINS, new WorldProtoListener<World.TradeOfferCoins>() {

            @Override
            public void handle(World.TradeOfferCoins message, Player player) {
                Player target = player.container(Containers.TRADE).getCurrent();
                if (target!= null) {
                    if (player.component(ActorComponents.VAR).getVarInt("coins") >= message.getCoins()) {
                        player.container(Containers.TRADE).setCoins(message.getCoins());
                        target.sendMessage(PacketOuterClass.Opcode.SMSG_SET_WIDGET_PARAM_INT, Shared.WidgetParamInt.newBuilder().setWidget("trading").setKey("coins").setValue(message.getCoins()).build());
                    } else {
                        player.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You do not have that many coins to offer.");
                    }
                }
            }
        }, World.TradeOfferCoins.getDefaultInstance());

        //TODO register thy buttons!

        /*ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_TRADE_CANCELED, new WorldProtoListener<PacketOuterClass.EmptyPayload>() {
            *//**
             * @param message
             * @param player
             *//*
            @Override
            public void handle(PacketOuterClass.EmptyPayload message, Character player) {
                Character tradingWith = player.container(Containers.TRADE).getCurrent();
                if (tradingWith != null) {
                    tradingWith.component(CharacterComponents.SOCIAL_CHANNEL).sendDefaultMessage(player.getName() + " has canceled the trade.");
                    tradingWith.component(CharacterComponents.WIDGET).destructWidget("trade");
                    tradingWith.container(Containers.TRADE).close();
                }

                player.component(CharacterComponents.SOCIAL_CHANNEL).sendDefaultMessage("Trade canceled.");
                player.container(Containers.TRADE).close();

            }
        }, PacketOuterClass.EmptyPayload.getDefaultInstance());


        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_TRADE_ACCEPTED, new WorldProtoListener<PacketOuterClass.EmptyPayload>() {
            *//**
             * @param message
             * @param player
             *//*
            @Override
            public void handle(PacketOuterClass.EmptyPayload message, Character player) {
                player.container(Containers.TRADE).accept();
            }
        }, PacketOuterClass.EmptyPayload.getDefaultInstance());*/
    }


    /**
     * Called upon each Game Tick
     */
    @Override
    public void onTick(long deltaTime) {
        if (getCurrent() != null) {
            if (!isWithinDistance(getCurrent(), 350) || !getCurrent().getChannel().isActive() || !GameWorld.containsPlayer(getCurrent().getClientIndex())) {
                getCurrent().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Trade canceled.");
                getCurrent().component(PlayerComponents.WIDGET).destructWidget("trade");
                getCurrent().container(Containers.TRADE).close();
                component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Trade canceled.");
                getPlayer().component(PlayerComponents.WIDGET).destructWidget("trade");
                container(Containers.TRADE).close();
            }
        }
    }

    /**
     * Attempts to trade with another player
     * @param target
     */
    public void trade(Player target) {

        //Prevents opening another trade if they are already in a trade.
        if (target.container(Containers.TRADE).isTrading()) {
            sendDefaultMessage(target.getName() + " is currently busy.");
            return;
        }
        setCurrent(target);
        target.container(Containers.TRADE).setCurrent(getPlayer());

        getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Attempting to trade " + target.getName() + "...");


        //TODO ignoring trades


        //TODO trading with friends only, etc

        target.component(PlayerComponents.WIDGET).showWidget("trade");
        getCurrent().component(PlayerComponents.WIDGET).showWidget("trade");
        //TODO set name ontop for both
    }

    /**
     * Called upon clicking of an item while inside of a trade.
     * @param item
     */
    public void addToTrade(int slotId, Item item) {
        //Represents Their offers
        TradeContainer tradee = getCurrent().container(Containers.TRADE);

        //TODO append it to the mf Grid form.
        if (item.getTable().isCosmeticOverride() || item.getTable().isFromStore()) {//TODO add more untradeable checks
            sendDefaultMessage("That item is untradeable.");
            return;
        }

        add(item);
        setAccepted(false);
        container(Containers.INVENTORY).deleteItemFromSlotCompletely(slotId);
        tradee.updateWindow();
        updateWindow();
    }

    /**
     * Called upon removing an item from trade.
     * @param slotId
     */
    public void removeFromTrade(int slotId) {
        //TODO
        Item slot = get(slotId);
        if (slot != null) {
            set(slotId, null);
            container(Containers.INVENTORY).add(slot);
            setAccepted(false);
            reorderContainer();
            updateWindow();
        }
    }

    /**
     * Updates the Trade Window
     */
    public void updateWindow() {
        //Ensures there is still a person in the trade.
        if (getCurrent() == null) return;

        //This updates our window + their offer
        sendOfferUpdate();
        sendTheirOfferUpdate(false);
    }

    @Override
    public void hide() {
        sendHide("trade");
    }

    /**
     * Sends that you've accepted the trade
     */
    public void accept() {
        if (getCurrent() == null) {
            close();
            return;
        }
        //We are double checking this to ensure not getting scammed
        if (getVars().getVarInt("coins") < coins) {
            if (getCurrent() != null) {
                getCurrent().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Trade canceled.");
                getCurrent().container(Containers.TRADE).clear();
            }
            component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Trade canceled.");
            close();
            return;
        }
        setAccepted(true);

        ///Sends the other person MY offer
        getCurrent().container(Containers.TRADE).sendTheirOfferUpdate(true);

        if (getCurrent().container(Containers.TRADE).isAccepted() && isAccepted()) {



            for (Item item : getItems()) {
                if (item == null) continue;
                //TODO check for inv space
                getCurrent().container(Containers.INVENTORY).addItem(item);
            }

            for (Item item : getCurrent().container(Containers.TRADE).getItems()) {
                if (item == null) continue;
                //TODO check for inv space
                container(Containers.INVENTORY).addItem(item);
            }


            getCurrent().component(ActorComponents.VAR).incrementVarInt("coins", getCoins());
            getCurrent().component(ActorComponents.VAR).decrementVarInt("coins", getCurrent().container(Containers.TRADE).getCoins());


            component(ActorComponents.VAR).incrementVarInt("coins", getCurrent().container(Containers.TRADE).getCoins());
            component(ActorComponents.VAR).decrementVarInt("coins", getCoins());


            component(PlayerComponents.WIDGET).destructWidget("trade");
            getCurrent().component(PlayerComponents.WIDGET).destructWidget("trade");
            getCurrent().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Trade completed.");
            sendDefaultMessage("Trade completed.");
            getCurrent().container(Containers.TRADE).close();
            close();
        }
    }

    /**
     * Clears the Container of all Items
     */
    public void close() {
        setAccepted(false);
        setCurrent(null);
        setCoins(0);
        for (int index = 0; index < getItems().length; index++) {
            if (getItems()[index] == null) continue;
            container(Containers.INVENTORY).addItem(getItems()[index]);
        }
        clear();
    }

    /**
     * Sends all of the items currently in the offer of our trade window.
     */
    public void sendOfferUpdate() {
        World.UpdateItemContainerFull.Builder addItems = World.UpdateItemContainerFull.newBuilder();
        addItems.setContainerId(Containers.Type.TRADE.ordinal());
        for (int index = 0; index < getItems().length; index++) {
            World.ItemSlot.Builder itemBuilder = World.ItemSlot.newBuilder();
            int cost = 0;
            if (getItems()[index] == null) continue;
            int itemId = getItems()[index].getId();
            int amount = getItems()[index].getAmount();
            if (getItems()[index].getTable() != null)
                cost = getItems()[index].getTable().getCost();
            if (amount < 0)
                amount = 0;
            itemBuilder.setId(itemId);
            itemBuilder.setAmount(amount);
            addItems.addItemSlot(itemBuilder);
        }
        getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_ITEM_CONTAINER_FULL, addItems.build());
    }

    /**
     * Sends all of their inventory update
     */
    public void sendTheirOfferUpdate(boolean accepted) {
        if (getCurrent() == null) return;
        getPlayer().component(PlayerComponents.WIDGET).setWidgetParam("trading", "accepted", accepted);
        World.UpdateItemContainerFull.Builder addItems = World.UpdateItemContainerFull.newBuilder();
        addItems.setContainerId(Containers.Type.TRADE_OFFER.ordinal());
        TradeContainer trade = getCurrent().container(Containers.TRADE);
        for (int index = 0; index < getItems().length; index++) {
            World.ItemSlot.Builder itemBuilder = World.ItemSlot.newBuilder();
            int cost = 0;
            if (trade.getItems()[index] == null) continue;
            int itemId = trade.getItems()[index].getId();
            int amount = trade.getItems()[index].getAmount();
            if (trade.getItems()[index].getTable() != null)
                cost = trade.getItems()[index].getTable().getCost();
            if (amount < 0)
                amount = 0;
            itemBuilder.setId(itemId);
            itemBuilder.setAmount(amount);
            addItems.addItemSlot(itemBuilder);
        }
        getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_ITEM_CONTAINER_FULL, addItems.build());
    }

    /**
     * Closes the Trade
     */
    public void forceClose() {
        if (getCurrent() != null) {
            getCurrent().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Trade canceled.");
            getCurrent().component(PlayerComponents.WIDGET).destructWidget("trade");
            getCurrent().container(Containers.TRADE).close();
            component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Trade canceled.");
            close();
        }
    }

    /**
     * Checks if the player is in a current trade
     * @return
     */
    public boolean isTrading() {
        return current != null;
    }

    /**
     * Method called when a 'Slot' is used.
     *
     * @param slotId
     * @param itemId
     */
    @Override
    public void onSlot(int slotId, int itemId) {

    }
}
