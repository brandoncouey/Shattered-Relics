package com.shattered.game.actor.character.player.component.synchronize.npc;

import com.shattered.datatable.tables.NPCUDataTable;
import com.shattered.game.GameObject;
import com.shattered.game.GameWorld;
import com.shattered.account.Account;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.components.combat.CharacterCombatComponent;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.animation.AnimSequence;
import com.shattered.game.actor.components.flags.ActorFlagUpdateComponent;
import com.shattered.game.actor.components.flags.FlagType;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.actor.character.npc.component.combat.NPCCombatComponent;
import com.shattered.game.actor.components.interaction.InteractionFlags;
import com.shattered.game.actor.components.movement.ActorMovementComponent;
import com.shattered.game.actor.components.movement.MovementFlags;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.actor.object.component.transform.TransformComponent;
import com.shattered.game.grid.ReplicationGridNode;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.utilities.VariableUtility;
import com.shattered.utilities.ecs.Component;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JTlr Frost 9/9/2019 : 4:43 AM
 */
public class NPCSynchronizeComponent extends Component {

    /**
     * Represents the Npcs
     */
    @Getter
    @Setter
    private Map<Integer, NPC> local;

    @Getter
    @Setter
    private Map<Integer, NPC> queued;


    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public NPCSynchronizeComponent(GameObject gameObject) {
        super(gameObject);
        setLocal(new ConcurrentHashMap<>());
        setQueued(new ConcurrentHashMap<>());

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
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {
        onTick(0);//This doesn't even use deltaTime so fuck it.
    }

    /**
     * Called once per world cycle per each instance.
     */
    @Override
    public void onTick(long deltaTime) {
        checkRegionForAdditional();

        World.NPCSynchronize.Builder builder = World.NPCSynchronize.newBuilder();
        for (NPC npc : getLocal().values()) {

            if (npc == null) continue;


            World.LocalNPC.Builder npcBuilder = World.LocalNPC.newBuilder();
            ActorFlagUpdateComponent flagUpdate = npc.component(ActorComponents.FLAG_UPDATE);
            TransformComponent transform = npc.component(GameObjectComponents.TRANSFORM_COMPONENT);
            NPCCombatComponent combat = (NPCCombatComponent) npc.component(CharacterComponents.COMBAT);
            ActorMovementComponent movement = npc.component(ActorComponents.MOVEMENT);
            NPCUDataTable table = npc.getDataTable();

            if (flagUpdate == null) continue;
            if (table == null) continue;

            boolean needsAdded = needsAdded(npc);
            boolean needsRemoved = needsRemoved(npc);

            npcBuilder.setNpcId(npc.getId());
            npcBuilder.setNpcIndex(npc.getClientIndex());

            npcBuilder.setFlags(flagUpdate.getFlags());
            npcBuilder.setNeedsAdded(needsAdded);
            npcBuilder.setNeedsRemoved(needsRemoved);

            if (needsAdded) {
                //TODO send all variables
            }


            if (needsAdded || flagUpdate.isFlagged(FlagType.TRANSFORM)) {
                //Appends the Transform Flag Update

                World.CharacterTransformUpdate.Builder movementBuilder = World.CharacterTransformUpdate.newBuilder();

                if (needsAdded) {
                    movementBuilder.setPosition(transform.getLocation().toProto());
                }

                if (movement.isFlagged(MovementFlags.FORWARD)) {
                    if (movement.getCurrentDestination() != null) {
                        movementBuilder.setPosition(movement.getCurrentDestination().toProto());
                    }
                }

                movementBuilder.setSpeed(movement.getSpeed());
                movementBuilder.setFlags(movement.getFlags());
                movementBuilder.setDirection(movement.getDirection());
                npcBuilder.setMovement(movementBuilder.build());

            }

            if (needsAdded || flagUpdate.isFlagged(FlagType.MODEL_BLOCK)) {
                npcBuilder.setNpcName(VariableUtility.formatString(npc.getName()));
                npcBuilder.setHealth(npc.component(ActorComponents.VAR).getVarInt("health"));
                npcBuilder.setMaxHealth(npc.component(ActorComponents.VAR).getVarInt("max_health"));
                npcBuilder.setInteractFlags(npc.component(ActorComponents.INTERACTION).getFlags());
                npcBuilder.setHostilityLevel(combat.getHostilityLevel().ordinal() << 2 | (getPlayer().component(CharacterComponents.COMBAT).isTarget(npc) ? 1 : 0));
            }

            if (needsAdded || flagUpdate.isFlagged(FlagType.MAP_MARKER)) {
                npcBuilder.setMarker(World.MapMarker.newBuilder().setId(0).setClamps(false).setGlobal(false)).build();
            }

            //if needs combat


            if (flagUpdate.isFlagged(FlagType.HIT_MARK)) {
                for (World.HitMark mark : combat.getHits()) {
                    if (npc.component(ActorComponents.INTERACTION).isFlagged(InteractionFlags.ATTACKABLE) && mark.getType() == CharacterCombatComponent.HIT_MARK_HEAL) continue;
                    npcBuilder.addHitMarks(mark);
                }
            }

            if (needsAdded || flagUpdate.isFlagged(FlagType.ANIMATION)) {
                AnimSequence sequence = npc.component(ActorComponents.ANIMATION).getAnimSequence();
                if (sequence != null) {
                    npcBuilder.setAnimSequenceId(sequence.getId() << 1 | ((table.isHummanoid() ? 1 : 0)));
                }
            }

            if (needsRemoved)
                getLocal().remove(npc.getClientIndex());

            getQueued().remove(npc.getClientIndex());

            npcBuilder.build();
            builder.addLocalNpc(npcBuilder);
        }
        getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_NPC_SYNCHRONIZE, builder.build());
    }

    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {

    }

    public void checkRegionForAdditional() {
        ReplicationGridNode node = (ReplicationGridNode) GameWorld.getReplicationGrid().getNode(getPlayer());
        for (NPC npc : node.getNodeNPCS().values()) {
            if (npc == null) continue;
            if (!getLocal().containsKey(npc.getClientIndex())) {
                getLocal().put(npc.getClientIndex(), npc);
                getQueued().put(npc.getClientIndex(), npc);
            }
        }

        for (ReplicationGridNode adjacent : node.getAdjacentNodes().values()) {
            if (adjacent == null) continue;
            for (NPC adjNPC : adjacent.getNodeNPCS().values()) {
                if (adjNPC == null) continue;
                if (!getLocal().containsKey(adjNPC.getClientIndex())) {
                    getLocal().put(adjNPC.getClientIndex(), adjNPC);
                    getQueued().put(adjNPC.getClientIndex(), adjNPC);
                }
            }
        }

    }

    /**
     * Checks if the local npc is within your cell and adjacent cells
     * @param npcIndex
     * @return is near
     */
    public boolean isNPCNear(int npcIndex) {
        return getLocal().containsKey(npcIndex);
    }

    /**
     * Checks if a npc is within 200 units from you
     * @param npcIndex
     * @param radiusUnits
     * @return is near
     */
    public boolean isNPCNear(int npcIndex, int radiusUnits) {
        if (!isNPCNear(npcIndex)) return false;
        NPC npc = getLocal().get(npcIndex);
        if (npc == null) return false;
        int units =  npc.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().distanceTo(getPlayer().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation());
        if (units <= radiusUnits)
            return true;
        return false;
    }

    /**
     *
     * @param npc
     * @return
     */
    public boolean needsAdded(NPC npc) {
        if (getQueued().containsKey(npc.getClientIndex())) return true;
        return false;
    }

    /**
     *
     * @param npc
     * @return
     */
    public boolean needsRemoved(NPC npc) {
        if (npc.getState().equals(ActorState.FINISHED)) return true;
        return false;
    }

    /**
     * Gets the Actor
     * @return
     */
    public Actor getActor() { return (Actor) gameObject;}

    /**
     * Gets the Character
     * @return the player
     */
    public Player getPlayer() {
        return (Player) gameObject;
    }
}
