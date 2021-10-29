package com.shattered.game.actor.character.player.component.interaction;

import com.shattered.account.Account;
import com.shattered.engine.tasks.DelayedTaskTicker;
import com.shattered.game.GameWorld;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.ActorType;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.interaction.InteractionFlags;
import com.shattered.game.actor.object.WorldObject;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.component.WorldComponent;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.WorldProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.script.ScriptManager;
import com.shattered.script.types.ActionScript;
import com.shattered.system.SystemLogger;

/**
 * @author JTlr Frost 10/29/2019 : 12:37 AM
 */
public class PlayerInteractionComponent extends WorldComponent {


    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public PlayerInteractionComponent(Object gameObject) {
        super(gameObject);
    }

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
        //TODO register other listeners

        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_OBJECT_INTERACTION, new WorldProtoListener<World.ActorInteraction>() {

            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(World.ActorInteraction message, Player player) {
                int objectId = message.getId();
                int objectIndex = message.getIndex();
                InteractionModifier modifier = InteractionModifier.forId(message.getModifier());
                if (objectId < 1 || objectIndex < 1) { return;
                }
                if (player.component(PlayerComponents.INTERACTION).inArea(ActorType.OBJECT, objectId, objectIndex))
                    player.component(PlayerComponents.INTERACTION).handleObjectInteraction(modifier, objectId, objectIndex);
            }
        }, World.ActorInteraction.getDefaultInstance());

        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_NPC_INTERACTION, new WorldProtoListener<World.ActorInteraction>() {

            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(World.ActorInteraction message, Player player) {
                int npcId = message.getId();
                int npcIndex = message.getIndex();
                InteractionModifier modifier = InteractionModifier.forId(message.getModifier());
                if (npcId < 1 || npcIndex < 1) return;

                if (player.component(PlayerComponents.INTERACTION).inArea(ActorType.NPC, npcId, npcIndex))
                    player.component(PlayerComponents.INTERACTION).handleNPCInteraction(modifier, npcId, npcIndex);
            }
        }, World.ActorInteraction.getDefaultInstance());

        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_CHARACTER_INTERACTION, new WorldProtoListener<World.ActorInteraction>() {

            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(World.ActorInteraction message, Player player) {
                int playerIndex = message.getIndex();
                InteractionModifier modifier = InteractionModifier.forId(message.getModifier());
                Player target;
                if ((target = GameWorld.findPlayer(playerIndex)) != null && modifier != null) {
                    player.component(PlayerComponents.INTERACTION).handleCharacterInteraction(modifier, target);
                }
            }
        }, World.ActorInteraction.getDefaultInstance());
    }

    /**
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {

    }

    /**
     * Called once per world cycle per each instance.
     */
    @Override
    public void onTick(long deltaTime) {

    }

    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {

    }

    /**
     * Checks if the Actor is in the Area
     * @param type
     * @param id
     * @param clientIndex
     * @return
     */
    public boolean inArea(ActorType type, int id, int clientIndex) {
        switch (type) {
            case NPC:
                NPC npc = getPlayer().component(PlayerComponents.NPC_SYNCHRONIZE).getLocal().get(clientIndex);
                return npc != null && npc.getId() == id;
            case OBJECT:
                WorldObject object = getPlayer().component(PlayerComponents.OBJECT_SYNCHRONIZE).getLocal().get(clientIndex);
                return object != null && object.getId() == id;
        }
        return false;
    }

    /**
     * Handles the Character Interaction
     * @param target
     */
    public void handleCharacterInteraction(InteractionModifier modifier, Player target) {
        deregisterActions();
        if (getPlayer().getState().equals(ActorState.DEAD)) {
            sendDefaultMessage("You are dead!");
            return;
        }
        switch (modifier) {
            //Trade
            case NORMAL: {
                container(Containers.TRADE).trade(target);
                break;
            }
            //Follow
            case SHIFT: {
                sendDefaultMessage("You begin to follow " + target.getName() + ".");
                break;
            }
            //Duel
            case CNTRL: {
                //TODO auto decline duels.
                //Prevents Spam Dueling
                if (target.component(ActorComponents.TRANS_VAR).getVarInt("dueled_puuid") < 1 || target.component(ActorComponents.TRANS_VAR).getVarBool("dueling") != true) {
                    getCharacter().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage(target.getName() + " is busy.");
                    return;
                }
                sendDefaultMessage("You request to duel " + target.getName() + ".");
                target.component(PlayerComponents.WIDGET).showWidget("duel");
                target.component(ActorComponents.TRANS_VAR).setVarInt("duel_puuid", getCharacter().getClientIndex());
                target.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage(getCharacter().getName() + " has requested to duel you.");
                break;
            }
            default:
                SystemLogger.sendSystemErrMessage("Unhandled player interaction {Modifier=" + modifier.name() + "}.");
                break;
        }
    }

    /**
     * Handles the NPC Interaction
     * @param npcId
     * @param modifier
     * @param npcIndex
     */
    public void handleNPCInteraction(InteractionModifier modifier, int npcId, int npcIndex) {
        if (inArea(ActorType.NPC, npcId, npcIndex)) {
            NPC npc = getPlayer().component(PlayerComponents.NPC_SYNCHRONIZE).getLocal().get(npcIndex);
            if (npc != null) {
                deregisterActions();
                if (getPlayer().getState().equals(ActorState.DEAD)) {
                    sendDefaultMessage("You are dead!");
                    return;
                }
                //TODO lets attempt to make a mf follow me.
                switch (modifier) {
                    case NORMAL:
                        if (npc.container(Containers.LOOT).isLootable(getPlayer())) {
                            npc.container(Containers.LOOT).openLoot(getPlayer());
                            return;
                        }
                         if (npc.component(ActorComponents.INTERACTION).isFlagged(InteractionFlags.CAN_TALK_TO) ||
                                npc.component(ActorComponents.INTERACTION).isFlagged(InteractionFlags.CAN_INTERACT_WITH)) {
                            ScriptManager.onNormalInteract(getPlayer(), npc);
                            return;
                        }
                        break;
                    case SHIFT:
                        if (npc.component(ActorComponents.INTERACTION).isFlagged(InteractionFlags.TRADEABLE) ||
                                (!npc.getDataTable().isHummanoid() && npc.getDataTable().isSkinnable())) {
                            ScriptManager.onShiftInteract(getPlayer(), npc);
                        }
                        break;
                    case CNTRL:
                        if (npc.component(ActorComponents.INTERACTION).isFlagged(InteractionFlags.PICKPOCKETABLE)) {
                            ScriptManager.onCntrlInteract(getPlayer(), npc);
                        }
                        break;
                }
            }
        }
    }

    /**
     * Handles the Object Interaction
     * @param objectId
     * @param modifier
     * @param objectIndex
     */
    public void handleObjectInteraction(InteractionModifier modifier, int objectId, int objectIndex) {
        if (inArea(ActorType.OBJECT, objectId, objectIndex)) {
            WorldObject worldObject = getPlayer().component(PlayerComponents.OBJECT_SYNCHRONIZE).getLocal().get(objectIndex);

            if (!worldObject.component(ActorComponents.INTERACTION).isFlagged(InteractionFlags.CAN_INTERACT_WITH))
                return;
            deregisterActions();
            if (getPlayer().getState().equals(ActorState.DEAD)) {
                sendDefaultMessage("You are dead!");
                return;
            }
            switch (modifier) {
                case NORMAL:
                    ScriptManager.onNormalInteract(getPlayer(), worldObject);
                    break;
                case SHIFT:
                    ScriptManager.onShiftInteract(getPlayer(), worldObject);
                    break;
                case CNTRL:
                    ScriptManager.onCntrlInteract(getPlayer(), worldObject);
                    break;
            }
        }
    }


    /**
     * Gets the {@link Player}
     * @return
     */
    public Player getPlayer() {
        return (Player) gameObject;
    }

    /**
     * Deregisters any possible windows / actions going on
     */
    public void deregisterActions() {
        getPlayer().component(PlayerComponents.DIALOG).exit();
        getPlayer().container(Containers.TRADE).forceClose();
        getPlayer().getContainerManager().close();
    }

}
