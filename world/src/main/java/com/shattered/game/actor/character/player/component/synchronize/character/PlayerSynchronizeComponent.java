package com.shattered.game.actor.character.player.component.synchronize.character;

import com.shattered.game.GameObject;
import com.shattered.game.GameWorld;
import com.shattered.account.Account;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.components.combat.CharacterCombatComponent;
import com.shattered.game.actor.character.components.combat.CombatDefinitions;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.combat.PlayerCombatComponent;
import com.shattered.game.actor.character.player.component.model.Model;
import com.shattered.game.actor.components.interaction.InteractionFlags;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.character.player.component.container.equipment.EquipmentContainer;
import com.shattered.game.actor.character.player.component.container.equipment.EquipmentSlot;
import com.shattered.game.actor.character.player.component.model.PlayerModelComponent;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.animation.ActorAnimSequenceComponent;
import com.shattered.game.actor.components.flags.ActorFlagUpdateComponent;
import com.shattered.game.actor.components.flags.FlagType;
import com.shattered.game.actor.components.movement.ActorMovementComponent;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.object.component.transform.TransformComponent;
import com.shattered.game.component.WorldComponent;
import com.shattered.game.grid.ReplicationGridNode;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.utilities.ecs.PriorityComponent;
import com.shattered.utilities.ecs.ProcessComponent;
import com.shattered.utilities.ecs.ProcessInterval;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JTlr Frost 9/8/2019 : 4:19 PM
 */
public class PlayerSynchronizeComponent extends WorldComponent {

    /**
     * Represents a Map of all current local players
     */
    @Getter
    @Setter
    private Map<Integer, Player> local;

    /**
     * Represents a Map of all current local players appending queue
     */
    @Getter
    @Setter
    private Map<Integer, Player> appendQueue;

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public PlayerSynchronizeComponent(GameObject gameObject) {
        super(gameObject);
        setLocal(new ConcurrentHashMap<>());
        setAppendQueue(new ConcurrentHashMap<>());
    }

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
        checkRegionForAdditional();
    }

    /**
     * Initializes Once the 'ChannelLine' Has been Awakened.
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {
        checkRegionForAdditional();
    }

    /**
     * Called once per world cycle per each instance.
     */
    @Override
    public void onTick(long deltaTime) {

        try {
            checkRegionForAdditional();

            //Represents the Synchronize Message
            World.CharacterSynchronize.Builder synchronize = World.CharacterSynchronize.newBuilder();

            //Gives the Client our current index
            synchronize.setClientIndex(getPlayer().getClientIndex());

            //Begins looping the entire zone for each player and their attributes.
            for (Player localPlayer : getLocal().values()) {

                //Check if the player is legit.
                if (localPlayer == null) continue;

                //Represents the Character Entry
                World.LocalCharacter.Builder playerBuilder = World.LocalCharacter.newBuilder();

                //Represents the Actor Flags
                ActorFlagUpdateComponent flags = localPlayer.component(ActorComponents.FLAG_UPDATE);

                //Represents the Actor Movement Component
                ActorMovementComponent movement = localPlayer.component(ActorComponents.MOVEMENT);

                //Represents the Characters current Transform
                TransformComponent transform = localPlayer.component(GameObjectComponents.TRANSFORM_COMPONENT);

                //Represents the Character Model Block Component
                PlayerModelComponent model = localPlayer.component(PlayerComponents.MODEL_BLOCK);

                //Represents the Animation Component
                ActorAnimSequenceComponent animation = localPlayer.component(ActorComponents.ANIMATION);

                //Represents the Current Transform
                EquipmentContainer equipment = localPlayer.container(Containers.EQUIPMENT);

                //Represents the Combat Component
                PlayerCombatComponent combat = (PlayerCombatComponent) localPlayer.component(CharacterComponents.COMBAT);


                //Lets the client know the local player index.
                playerBuilder.setCharacterIndex(localPlayer.getClientIndex());

                //Lets the client know all of the incoming flag masks
                playerBuilder.setFlags(flags.getFlags());

                boolean needsAdded = needsAdded(localPlayer);

                //Adds or updates the current player ? true = add, false = update
                playerBuilder.setNeedsAdded(needsAdded);

                boolean needsRemoved = needsRemoved(localPlayer);

                //Removes the Character from being shown anymore.
                playerBuilder.setNeedsRemoved(needsRemoved);

                if (needsAdded || flags.isFlagged(FlagType.TRANSFORM)) {
                    //Appends the Transform Flag Update
                    World.CharacterMovementInput input = movement.getLastMovementInput();
                    if (needsAdded || input == null) {
                        //TODO
                        World.CharacterTransformUpdate.Builder movementBuilder = World.CharacterTransformUpdate.newBuilder();
                        movementBuilder.setPosition(transform.getLocation().toProto());
                        movementBuilder.setMountId(localPlayer.component(ActorComponents.TRANS_VAR).getVarInt("mount_id"));
                        movementBuilder.setSpeed(movement.getSpeed());
                        movementBuilder.setFlags(0);
                        movementBuilder.setDirection(transform.getRotation().getYaw());
                        playerBuilder.setMovement(movementBuilder.build());
                    } else {
                        //We are processing pre-existing orders
                        World.CharacterTransformUpdate.Builder movementBuilder = World.CharacterTransformUpdate.newBuilder();
                        movementBuilder.setPosition(input.getPosition());
                        movementBuilder.setFlags(input.getFlags());
                        movementBuilder.setSpeed(movement.getSpeed());
                        movementBuilder.setDirection(input.getDirection());
                        movementBuilder.setTime(input.getTime());
                        playerBuilder.setMovement(movementBuilder.build());
                    }

                }

                if (needsAdded) {
                    //TODO send variables to all others
                }

                //Appends the Model Block
                if (needsAdded || flags.isFlagged(FlagType.MODEL_BLOCK)) {
                    playerBuilder.setBlendId(model.getBlendId());

                    //TODO test instanced vars..
                    playerBuilder.setInteractFlags(localPlayer.component(ActorComponents.INTERACTION).getFlags());//TODO revert to Filter

                    //TODO make this an actual var
                    playerBuilder.setHealth(localPlayer.component(ActorComponents.VAR).getVarInt("health"));
                    playerBuilder.setMaxHealth(localPlayer.component(ActorComponents.VAR).getVarInt("max_health"));

                    playerBuilder.setEnergy(localPlayer.component(ActorComponents.VAR).getVarInt("energy"));
                    playerBuilder.setMaxEnergy(localPlayer.component(ActorComponents.VAR).getVarInt("max_energy"));

                    playerBuilder.setHostilityLevel(2 << 2 | (getPlayer().component(CharacterComponents.COMBAT).getTarget() == null ? 0 : getPlayer().component(CharacterComponents.COMBAT).getTarget().equals(localPlayer) ? 1 : 0));
                    playerBuilder.setAimBlocking(combat.isAimBlocking());
                    playerBuilder.setCombatLevel(combat.getCombatLevel(true));

                    World.CharacterModelBlock.Builder modelBlock = World.CharacterModelBlock.newBuilder();
                    modelBlock.setCharacterName(localPlayer.getName());
                    modelBlock.setIsMale(model.getModel().isMale());
                    modelBlock.setEyeColor(model.getModel().getEyeColor());
                    modelBlock.setRace(model.getModel().getRace().ordinal() << 5 | model.getModel().getSkinColor());
                    modelBlock.setHairStyle(model.getModel().getHairStyle() << 5 | model.getModel().getHairColor());
                    modelBlock.setEyebrowStyle(model.getModel().getEyebrowStyle() << 5 | model.getModel().getEyebrowColor());
                    modelBlock.setBeardStyle(model.getModel().getBeardStyle() << 5 | model.getModel().getBeardColor());
                    //modelBlock.setHidden(model.getModel().isHidden() ? 1: 0);//ADMINISTRATOR PRIVELAGE
                    modelBlock.setHeadSlotId(equipment.getEquipmentItemIdForSlot(EquipmentSlot.HEAD));
                    modelBlock.setNecklaceSlotId(equipment.getEquipmentItemIdForSlot(EquipmentSlot.NECKLACE));
                    modelBlock.setShouldersSlotId(equipment.getEquipmentItemIdForSlot(EquipmentSlot.SHOULDERS));
                    modelBlock.setBackSlotId(equipment.getEquipmentItemIdForSlot(EquipmentSlot.BACK));
                    modelBlock.setChestSlotId(equipment.getEquipmentItemIdForSlot(EquipmentSlot.CHEST));
                    modelBlock.setBeltSlotId(equipment.getEquipmentItemIdForSlot(EquipmentSlot.BELT));
                    modelBlock.setPantsSlotId(equipment.getEquipmentItemIdForSlot(EquipmentSlot.PANTS));
                    modelBlock.setWristsSlotId(equipment.getEquipmentItemIdForSlot(EquipmentSlot.WRISTS));
                    modelBlock.setGlovesSlotId(equipment.getEquipmentItemIdForSlot(EquipmentSlot.GLOVES));
                    modelBlock.setMainhandSlotId(equipment.getCurrentMainWeaponForSync());
                    modelBlock.setOffhandSlotId(equipment.getCurrentOffhandWeaponForSync());
                    modelBlock.setBootsSlotId(equipment.getEquipmentItemIdForSlot(EquipmentSlot.BOOTS));
                    modelBlock.build();
                    playerBuilder.setModel(modelBlock);
                }

                //Appends player hit marks.
                if (flags.isFlagged(FlagType.HIT_MARK)) {
                    for (World.HitMark mark : combat.getHits()) {
                        /*if (localPlayer.getClientIndex() == getPlayer().getClientIndex() && mark.getType() != CharacterCombatComponent.HIT_MARK_HEAL)
                            continue;*/
                       /* if (localPlayer.component(ActorComponents.INTERACTION).isFlagged(InteractionFlags.ATTACKABLE) && mark.getType() == CharacterCombatComponent.HIT_MARK_HEAL && localPlayer.getClientIndex() != getPlayer().getClientIndex())
                            continue;*/
                        playerBuilder.addHitMarks(mark);
                    }
                }

                //Appends the Current Animation
                if (flags.isFlagged(FlagType.ANIMATION)) {
                    playerBuilder.setAnimSequenceId(animation.getAnimSequence().getId());
                }

                if (needsRemoved)
                    getLocal().remove(localPlayer.getClientIndex());

                //Removes the Client Index from the Queued if popped.
                getAppendQueue().remove(localPlayer.getClientIndex());

                //Appends the Character to the Synchronize List.
                synchronize.addLocalCharacter(playerBuilder.build());
            }
            //Writes and flushes each and every individual player.
            getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_CHARACTER_SYNCHRONIZE, synchronize.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {
    }


    /**
     * Checks the Region for Additional
     */
    public void checkRegionForAdditional() {

        ReplicationGridNode node = (ReplicationGridNode) GameWorld.getReplicationGrid().getNode(getPlayer());

        for (Player player : node.getNodePlayers().values()) {
            if (player == null) continue;
            if (!getLocal().containsKey(player.getClientIndex())) {
                getLocal().put(player.getClientIndex(), player);
                getAppendQueue().put(player.getClientIndex(), player);
            }
        }

        for (ReplicationGridNode adjacent : node.getAdjacentNodes().values()) {
            if (adjacent == null) continue;
            for (Player adjPlayer : adjacent.getNodePlayers().values()) {
                if (adjPlayer == null) continue;
                if (!getLocal().containsKey(adjPlayer.getClientIndex())) {
                    getLocal().put(adjPlayer.getClientIndex(), adjPlayer);
                    getAppendQueue().put(adjPlayer.getClientIndex(), adjPlayer);
                }
            }
        }
    }

    /**
     *
     * Check if the Character Needs to be Added
     *
     * @param player
     * @return
     */
    public boolean needsAdded(Player player) {
        return getAppendQueue().containsKey(player.getClientIndex());
    }

    /**
     * Check if the Character Needs to be Removed
     * @param player
     * @return
     */
    public boolean needsRemoved(Player player) {
        return player == null || player.getState().equals(ActorState.FINISHED) || !player.getChannel().isActive()
                || (player.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().distanceTo(getPlayer().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation()) > 10000);
    }

    /**
     * Gets the Character 
     * @return
     */
    public Player getPlayer() {
        return (Player) gameObject;
    }
}
