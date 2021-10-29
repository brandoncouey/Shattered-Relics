package com.shattered.game.actor.character.npc.component.combat;

import com.shattered.account.Account;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.Character;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.components.combat.CharacterCombatComponent;
import com.shattered.game.actor.character.components.combat.CombatDefinitions;
import com.shattered.game.actor.character.components.combat.Hit;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.combat.PlayerCombatComponent;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.flags.FlagType;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.actor.object.component.transform.Vector3;
import com.shattered.script.ScriptManager;
import com.shattered.script.api.impl.NpcAPI;
import com.shattered.script.api.impl.PlayerAPI;
import com.shattered.script.types.NPCCombatScript;
import com.shattered.utilities.VariableUtility;
import com.shattered.utilities.ecs.ProcessComponent;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author JTlr Frost 11/6/2019 : 9:25 PM
 */
@ProcessComponent(interval = 0.1f)
public class NPCCombatComponent extends CharacterCombatComponent {

    /**
     * Represents the last Target Tile.
     */
    @Getter @Setter
    private Vector3 lastTargetTile;

    @Getter @Setter
    private NPCCombatScript combatScript;

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public NPCCombatComponent(Object gameObject) {
        super(gameObject);
    }



    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
        getVars().setVarInt("health", getNPC().getDataTable().getStamina());
        getVars().setVarInt("max_health", getNPC().getDataTable().getStamina());
        getVars().setVarInt("energy", getNPC().getDataTable().getEnergy());
        getVars().setVarInt("max_energy", getNPC().getDataTable().getEnergy());
        getVars().setVarInt("accuracy", getNPC().getDataTable().getAccuracy());
        getVars().setVarInt("strength", getNPC().getDataTable().getStrength());
        getVars().setVarInt("resilience", getNPC().getDataTable().getResilience());
    }

    /**
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {
        component(CharacterComponents.COMBAT).setHostilityLevel(getNPC().getDataTable().getHostilityLevel());
    }

    /**
     * Called once per world cycle per each instance.
     */
    @Override
    public void onTick(long deltaTime) {
        super.onTick(deltaTime);

        if (getCharacter().getState() != ActorState.ALIVE)
            return;

        if (!isInCombat()) {
            setCombatScript(null);
            getNPC().component(ActorComponents.MOVEMENT).setRunning(false);
        }

        if (getNPC().component(CharacterComponents.COMBAT).getHostilityLevel().equals(HostilityLevel.HOSTILE) && combatScript == null && !isInCombat()) {
            getNPC().component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllPlayers().stream().filter(Objects::nonNull).filter(c -> c.getState() == ActorState.ALIVE).forEach(character -> {
                if (isWithinDistance(character, getNPC().getDataTable().getAggroRadius())) {
                    try {

                        NPCCombatScript script = ScriptManager.getNPCCombatScript(getNPC().getName());
                        if (script != null) {
                            setCombatScript(script.getClass().newInstance());
                            script = getCombatScript();
                            script.setNpc(new NpcAPI(getNPC()));
                        }

                        setTarget(character);
                        CharacterCombatComponent targetCombat = getTarget().component(CharacterComponents.COMBAT);

                        targetCombat.addTarget(getNPC());

                        if (targetCombat.getTarget() == null)
                            targetCombat.setTarget(getNPC());

                        component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            });
        }

        if (getTarget() == null && getTargets().size() > 0) {
            for (Character t : getTargets().keySet()) {
                if (t != null)
                setTarget(t);
            }
        }

        //NPCs wont follow you if they don't have a combat script.
        if (getTarget() != null && combatScript != null)
            if (combatScript.followTarget)
                moveToTarget();

        if (combatScript != null) {
            if (!isStunned())//TODO this may not be the best place to put this but... fk it for now its viable
                combatScript.on_tick(deltaTime);
            combatScript.ticks++;
        }
    }

    public void moveToTarget() {
        moveToTarget(combatScript == null ? 150 : combatScript.distance);
    }

    public void moveToTarget(int distance) {
        if (target == null)
            return;

        getNPC().component(ActorComponents.MOVEMENT).setRunning(true);

        //We are moving to the target now
        if (lastTargetTile == null) {
            lastTargetTile = target.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation();
            component(ActorComponents.MOVEMENT).addPathToMovementQueue(lastTargetTile);
            return;
        }
        if (target.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().distanceTo(lastTargetTile) > distance) {
            lastTargetTile = target.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation();
            component(ActorComponents.MOVEMENT).stop();
            component(ActorComponents.MOVEMENT).addPathToMovementQueue(lastTargetTile);
            return;
        }
        if (component(GameObjectComponents.TRANSFORM_COMPONENT).distanceTo(lastTargetTile) <= distance) {
            component(ActorComponents.MOVEMENT).stop();
            lastTargetTile = null;
            return;
        }
    }


    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {

    }

    @Override
    public void onDeath(Actor source) {
        for (Map.Entry<Character, List<Hit>> s : getTargets().entrySet()) {
            if (!(source instanceof Player)) continue;
            final Map<String, Integer> classDamage = new HashMap<>();

            int totalDamage = 0;
            int totalMeleeDamage = 0;
            int totalArcheryDamage = 0;
            int totalMageDamage = 0;
            float accuracyBonus = 0f;

            for (Hit hit : s.getValue()) {

                if (hit.getStyle() == CombatDefinitions.AttackStyle.MELEE)
                    totalMeleeDamage += hit.getDamage();

                if (hit.getStyle() == CombatDefinitions.AttackStyle.ARCHERY) {
                    totalArcheryDamage += hit.getDamage();
                    accuracyBonus += (1.02 * hit.getDamage()) - hit.getDamage();
                }

                if (hit.getStyle() == CombatDefinitions.AttackStyle.MAGIC) {
                    totalMageDamage += hit.getDamage();
                    accuracyBonus += (1.02 * hit.getDamage()) - hit.getDamage();
                }

                if (hit.getClassType() != null) {
                    if (!classDamage.containsKey("xp_" + hit.getClassType().name().toLowerCase()))
                        classDamage.put("xp_" + hit.getClassType().name().toLowerCase(), hit.getDamage());
                    else {
                        int amount = classDamage.get("xp_" + hit.getClassType().name().toLowerCase());
                        classDamage.put("xp_" + hit.getClassType().name().toLowerCase(), amount + hit.getDamage());
                    }
                }

                totalDamage += hit.getDamage();
            }

            //TODO xp bonuses for higher mob level?


            final int strengthXP = VariableUtility.getPercent((totalMeleeDamage / getMaxStamina()) * 100, getNPC().getDataTable().getXp());
            final int archeryXP = VariableUtility.getPercent((totalArcheryDamage / getMaxStamina()) * 100, getNPC().getDataTable().getXp());
            final int mageXP = VariableUtility.getPercent((totalMageDamage / getMaxStamina()) * 100, getNPC().getDataTable().getXp());
            final int accuracyXP = VariableUtility.getPercent((totalDamage / getMaxStamina()) * 100, (int) ((getNPC().getDataTable().getXp() / 3) + accuracyBonus));
            final int staminaXP = VariableUtility.getPercent((totalDamage / getMaxStamina()) * 100, getNPC().getDataTable().getXp() / 3);
            final int resilienceXP = VariableUtility.getPercent((totalDamage / getMaxStamina()) * 100, getNPC().getDataTable().getXp() / 3);


            //NPC rewards 30xp total
            //The amount of % you damage in a specific style grants you a % of that xp in that stat
            //Rewards the

            PlayerCombatComponent combat = (PlayerCombatComponent) s.getKey().component(CharacterComponents.COMBAT);

            int oldCombatLevel = combat.getCombatLevel(true);

            s.getKey().component(ActorComponents.VAR).incrementVarInt("xp_accuracy", accuracyXP);//TODO this should be given by weapon type
            s.getKey().component(ActorComponents.VAR).incrementVarInt("xp_strength", strengthXP);
            s.getKey().component(ActorComponents.VAR).incrementVarInt("xp_resilience", resilienceXP);
            s.getKey().component(ActorComponents.VAR).incrementVarInt("xp_stamina", staminaXP);
            s.getKey().component(ActorComponents.VAR).incrementVarInt("xp_focus", archeryXP);
            s.getKey().component(ActorComponents.VAR).incrementVarInt("xp_intellect", mageXP);

            //Adds the Grimoire XP
            for (Map.Entry<String, Integer> cd : classDamage.entrySet()) {
                s.getKey().component(ActorComponents.VAR).incrementVarInt(cd.getKey(), cd.getValue() / 2);
                System.out.println("Incrementing var :" + cd.getKey() + ", " + cd.getValue());
            }

            int newCombatLevel = combat.getCombatLevel(true);

            if (newCombatLevel > oldCombatLevel) {
                s.getKey().component(PlayerComponents.WIDGET).sendCombatLevelNotification(newCombatLevel);
                s.getKey().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You've reached level " + newCombatLevel + "!");
            }


        }

        if (combatScript != null)
            combatScript.on_death((source instanceof Player) ? new PlayerAPI((Player) source) : new NpcAPI((NPC) source));

        super.onDeath(source);//Called afterwards to destroy, it clears the targets so calling it first will nullify the rest.
    }

}
