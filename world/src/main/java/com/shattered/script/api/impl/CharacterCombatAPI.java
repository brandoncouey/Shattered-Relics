package com.shattered.script.api.impl;

import com.shattered.engine.tasks.DelayedTaskTicker;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.Character;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.components.combat.CharacterCombatComponent;
import com.shattered.game.actor.character.components.combat.CombatDefinitions;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.interaction.InteractionFlags;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.script.api.RelicCharacterAPI;
import com.shattered.script.api.RelicCharacterCombatAPI;
import com.shattered.script.types.AbilityScript;
import com.shattered.utilities.math.MathUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CharacterCombatAPI extends RelicCharacterCombatAPI {

    /**
     *
     * @param character
     */
    public CharacterCombatAPI(Character character) {
        super(character);
    }

    /**
     * Gets the current target that the player has targeted
     *
     * @return the current target
     */
    @Override
    public RelicCharacterAPI getTarget() {
        Character previousTarget = character.component(CharacterComponents.COMBAT).getTarget();
        if (previousTarget instanceof Player)
            return new PlayerAPI((Player) previousTarget);
        return new NpcAPI((NPC) previousTarget);
    }

    /**
     * Gets the previous target that was player targeted
     *
     * @return the previous target
     */
    @Override
    public RelicCharacterAPI getPreviousTarget() {
        Character previousTarget = character.component(CharacterComponents.COMBAT).getPreviousTarget();
        if (previousTarget instanceof Player)
            return new PlayerAPI((Player) previousTarget);
        return new NpcAPI((NPC) previousTarget);
    }

    /**
     * Gets the list of all characters that are currently attacking you
     *
     * @return a list of all current targets
     */
    @Override
    public List<RelicCharacterAPI> getTargets() {
        List<RelicCharacterAPI> collectedTargets = new ArrayList<>();
        character.component(CharacterComponents.COMBAT).getTargets().keySet().stream().filter(Objects::nonNull).forEach(target ->
                collectedTargets.add((target instanceof NPC) ? new NpcAPI((NPC) target) : new PlayerAPI((Player) target)));
        return collectedTargets;
    }

    /**
     * Clears the current target for the player and removes target UI on the client.
     */
    @Override
    public void clear_target() {
        character.component(CharacterComponents.COMBAT).clearTarget();
    }

    /**
     * Checks if the character is currently in combat
     *
     * @return in combat
     */
    @Override
    public boolean in_combat() {
        return character.component(CharacterComponents.COMBAT).isInCombat();
    }

    /**
     * Method used for attacking with a projectile to the current
     * target and deals the specified damage
     *
     * @param projectile
     * @param damage
     */
    @Override
    public void send_projectile(String projectile, int damage) {
        send_projectile(projectile, damage, 0);
    }

    /**
     * Method used for attacking with a projectile to the current
     * target and deals the specified damage and links it to the specified script
     *
     * @param projectile
     * @param damage
     * @param script
     */
    @Override
    public void send_projectile(String projectile, int damage, AbilityScript script) {
        send_projectile(projectile, damage, 0, script);
    }

    /**
     * Method used for attacking with a projectile to the current
     * target at a delayed 10th of a second and deals the specfied damage
     *
     * @param projectile
     * @param damage
     * @param delay
     */
    @Override
    public void send_projectile(String projectile, int damage, float delay) {
        send_projectile(projectile, damage, delay, null);
    }

    /**
     * Method used for attacking with a projectile to the current
     * target at a delayed 10th of a second and deals the specfied damage and links it to the specified script
     *
     * @param projectile
     * @param damage
     * @param delay
     * @param script
     */
    @Override
    public void send_projectile(String projectile, int damage, float delay, AbilityScript script) {
        character.component(CharacterComponents.COMBAT).sendProjectile(projectile, (Character) getTarget().getActor(), damage, delay, script);
    }

    /**
     * Method used for attacking with a projectile to the specified
     * target.
     *
     * @param projectile
     * @param target
     * @param damage
     */
    @Override
    public void send_projectile(String projectile, RelicCharacterAPI target, int damage) {
        send_projectile(projectile, target, damage, 0);
    }

    /**
     * Method used for attacking with a projectile to the specified and links it to the specified script
     * target.
     *
     * @param projectile
     * @param target
     * @param damage
     * @param script
     */
    @Override
    public void send_projectile(String projectile, RelicCharacterAPI target, int damage, AbilityScript script) {
        character.component(CharacterComponents.COMBAT).sendProjectile(projectile, (Character) target.getActor(), damage, 0, script);
    }

    /**
     * Method used for attacking with a projectile to the specified
     * target with added 10th of a second delay * delay
     *
     * @param projectile
     * @param target
     * @param damage
     * @param delay
     */
    @Override
    public void send_projectile(String projectile, RelicCharacterAPI target, int damage, float delay) {
        send_projectile(projectile, target, damage, delay, null);
    }

    /**
     * Method used for attacking with a projectile to the specified
     * target with added 10th of a second delay * delay and links it to the specified script
     *
     * @param projectile
     * @param target
     * @param damage
     * @param delay
     * @param script
     */
    @Override
    public void send_projectile(String projectile, RelicCharacterAPI target, int damage, float delay, AbilityScript script) {
        character.component(CharacterComponents.COMBAT).sendProjectile(projectile, (Character) target.getActor(), damage, delay, script);
    }

    /**
     * Hits the desired actor with the specified damage amount instantly
     *
     * @param target
     * @param amount
     */
    @Override
    public void hit(RelicCharacterAPI target, int amount) {
        hit(target, amount, 0);
    }

    /**
     * Hits the desired character with the specified damage amount and links it to the specified script
     *
     * @param target
     * @param amount
     * @param script
     */
    @Override
    public void hit(RelicCharacterAPI target, int amount, AbilityScript script) {
        hit(target, amount, 0, script);
    }


    //source->hit(target)

    /**
     * Hits the desired actor with the specified damage amount in the specified seconds
     *
     * @param target
     * @param amount
     */
    @Override
    public void hit(RelicCharacterAPI target, int amount, float delay) {
        hit(target, amount, delay, null);
    }

    /**
     * Hits the desired character with the specified damage amount
     * default unit is seconds and links it to the specified script
     *
     * @param target
     * @param amount
     * @param delay
     * @param script
     */
    @Override
    public void hit(RelicCharacterAPI target, int amount, float delay, AbilityScript script) {
        character.component(CharacterComponents.COMBAT).hitCharacter((Character) target.getActor(), amount, delay, script);
    }

    /**
     * Heals the desired character with the specified healing amount
     *
     * @param source
     * @param amount
     */
    @Override
    public void heal(RelicCharacterAPI source, int amount) {
        heal(source, amount, 0);
    }

    /**
     * Heals the desired character with the specified healing amount and links it to the specified script
     *
     * @param source
     * @param amount
     * @param script
     */
    @Override
    public void heal(RelicCharacterAPI source, int amount, AbilityScript script) {
        heal(source, amount, 0, script);
    }

    /**
     * Heals the desired character with the specified healing amount
     *
     * @param source
     * @param amount
     * @param delay
     */
    @Override
    public void heal(RelicCharacterAPI source, int amount, float delay) {
        heal(source, amount, delay,null);
    }

    /**
     * Heals the desired character with the specified healing amount and links it to the specified script
     *
     * @param source
     * @param amount
     * @param delay
     * @param script
     */
    @Override
    public void heal(RelicCharacterAPI source, int amount, float delay, AbilityScript script) {
        character.component(CharacterComponents.COMBAT).healCharacter((Character) source.getActor(), amount, delay, script);
    }

    /**
     * Finds the next possible potential target if there is no current set target
     *
     * @return the next possible target
     */
    @Override
    public RelicCharacterAPI getPossibleTarget() {
        List<Character> characters = character.component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllCharacters();
        Character t = character.component(CharacterComponents.COMBAT).getTarget();
        if (t != null) {
            if (!t.getState().equals(ActorState.ALIVE));
            if (t instanceof NPC)
                return new NpcAPI((NPC) t);
            else
                return new PlayerAPI((Player) t);
        }
        for (Character c : characters) {
            if (c == null) continue;
            if (c.equals(getCharacter())) continue;//We don't want to target ourself.
            if (!c.component(ActorComponents.INTERACTION).isFlagged(InteractionFlags.ATTACKABLE)) continue;
            if (!c.getState().equals(ActorState.ALIVE)) continue;
            if (!character.component(GameObjectComponents.ZONE_COMPONENT).isWithinDistance(c, 1800))//TODO make this an option
                continue;
            if (c instanceof NPC)
                return new NpcAPI((NPC) c);
            else
                return new PlayerAPI((Player) c);
        }
        return null;
    }

    /**
     * Finds a list of possible targets with the given specified maximum amount of targets.
     *
     * @param maximum
     * @return a list of possible targets with the specified maximum number of enemies.
     */
    @Override
    public List<RelicCharacterAPI> get_possible_targets(int maximum) {
       return get_possible_targets(maximum, 1800);
    }

    /**
     * Finds a list of possible targets with the given specified maximum amount of targets and certain distance.
     *
     * @param maximum
     * @param maxDistance
     * @return a list of possible targets with the specified maximum number of enemies.
     */
    @Override
    public List<RelicCharacterAPI> get_possible_targets(int maximum, int maxDistance) {
        List<RelicCharacterAPI> targets = new ArrayList<>();
        List<Character> characters = character.component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllCharacters();
        Character t = character.component(CharacterComponents.COMBAT).getTarget();
        if (t != null) {
            if (!t.getState().equals(ActorState.ALIVE));
            if (t instanceof NPC)
                targets.add(new NpcAPI((NPC) t));
            else
                targets.add(new PlayerAPI((Player) t));
        }
        for (Character c : characters) {
            if (c == null) continue;
            if (c.equals(getCharacter())) continue;//We don't want to target ourself.
            if (targets.size() >= maximum)
                return targets;
            if (!c.component(ActorComponents.INTERACTION).isFlagged(InteractionFlags.ATTACKABLE)) continue;
            if (!c.getState().equals(ActorState.ALIVE)) continue;
            if (!character.component(GameObjectComponents.ZONE_COMPONENT).isWithinDistance(c, maxDistance))//TODO make this an option
                continue;
            if (c instanceof NPC)
                targets.add(new NpcAPI((NPC) c));
            else
                targets.add(new PlayerAPI((Player) c));
        }
        return targets;
    }

    /**
     * Interrupts the Character from Casting
     */
    @Override
    public void interrupt() {
        character.component(CharacterComponents.COMBAT).interruptCasting(false);
        character.component(CharacterComponents.COMBAT).magicLock(5);
    }

    /**
     * Stuns the Character with the specified Duration in seconds
     *
     * @param duration
     */
    @Override
    public float stun(float duration) {
        return character.component(CharacterComponents.COMBAT).stun(duration);
    }

    /**
     * Checks if the Character is Stunned
     *
     * @return is stunned
     */
    @Override
    public boolean getStunned() {
        return  character.component(CharacterComponents.COMBAT).isStunned();
    }

    /**
     * Disarms the Character with the specified Duration in seconds
     *
     * @param duration
     */
    @Override
    public float disarm(float duration) {
        return character.component(CharacterComponents.COMBAT).disarm(duration);
    }

    /**
     * Checks if the Character is disarmed.
     *
     * @return is disarmed
     */
    @Override
    public boolean getDisarmed() {
        return character.component(CharacterComponents.COMBAT).isDisarmed();
    }

    /**
     * Snares the Character with the specified duration in seconds
     *
     * @param duration
     */
    @Override
    public float snare(float duration) {
        return character.component(CharacterComponents.COMBAT).snare(duration);
    }

    /**
     * Checks if the Character is Snared
     *
     * @return is snared
     */
    @Override
    public boolean getSnared() {
        return character.component(CharacterComponents.COMBAT).isSnared();
    }

    /**
     * Disorients the Character with the specified duration in seconds
     *
     * @param duration
     */
    @Override
    public float disorient(float duration) {
        return character.component(CharacterComponents.COMBAT).disorient(duration);
    }

    /**
     * Checks if the Character is disoriented
     *
     * @return is disoriented
     */
    @Override
    public boolean getDisoriented() {
        return character.component(CharacterComponents.COMBAT).isDisoriented();
    }

    /**
     * Disorients the Character with the specified duration in seconds
     *
     * @param duration
     * @return the amount of time they're magic locked for
     */
    @Override
    public float magicLock(float duration) {
        return character.component(CharacterComponents.COMBAT).magicLock(duration);
    }

    /**
     * Checks if the Character is Magic Locked
     *
     * @return
     */
    @Override
    public boolean getMagicLocked() {
        return character.component(CharacterComponents.COMBAT).isMagicLocked();
    }

    /**
     * Gets the hostiltiy level of the character
     *
     * @return the hostility level
     */
    @Override
    public CharacterCombatComponent.HostilityLevel getHostilityLevel() {
        return character.component(CharacterComponents.COMBAT).getHostilityLevel();
    }

    /**
     * Sets the hostility level for the character
     *
     * @param level
     */
    @Override
    public void set_hostility(CharacterCombatComponent.HostilityLevel level) {
        character.component(CharacterComponents.COMBAT).setHostilityLevel(level);
    }

    /**
     * Gets the Critical Strike Chance
     *
     * @return critical strike chance
     */
    @Override
    public int getCritChance() {
        return character.component(CharacterComponents.COMBAT).getCriticalStrikeChance();
    }

    /**
     * Increases critical strike c hance by the specified percent
     *
     * @param percent
     */
    @Override
    public void increase_crit_chance(int percent) {
        int stat = character.component(CharacterComponents.COMBAT).getAccuracy();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.ACCURACY, MathUtilities.getPercentOf(percent, stat));
    }

    /**
     * Increases critical strike c hance by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void increase_crit_chance(int percent, float duration) {
        int stat = character.component(CharacterComponents.COMBAT).getMagicResistance();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.CRITICAL_STRIKE, MathUtilities.getPercentOf(percent, stat));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.CRITICAL_STRIKE, MathUtilities.getPercentOf(percent, stat)), duration);

    }

    /**
     * Decreases critical strike c hance by the specified percent
     *
     * @param percent
     */
    @Override
    public void decrease_crit_chance(int percent) {
        int stat = character.component(CharacterComponents.COMBAT).getCriticalStrikeChance();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.CRITICAL_STRIKE, MathUtilities.getPercentOf(percent, stat));

    }

    /**
     * Decreases critical strike c hance by the specified percent
     * for the desired miliseconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void decrease_crit_chance(int percent, float duration) {
        int stat = character.component(CharacterComponents.COMBAT).getCriticalStrikeChance();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.CRITICAL_STRIKE, MathUtilities.getPercentOf(percent, stat));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.CRITICAL_STRIKE, MathUtilities.getPercentOf(percent, stat)), duration);
    }

    /**
     * Gets the Accuracy level
     *
     * @return accuracy
     */
    @Override
    public int getAccuracy() {
        return character.component(CharacterComponents.COMBAT).getAccuracy();
    }

    /**
     * Increases accuracy by the specified percent
     *
     * @param percent
     */
    @Override
    public void increase_accuracy(int percent) {
        int stat = character.component(CharacterComponents.COMBAT).getAccuracy();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.ACCURACY, MathUtilities.getPercentOf(percent, stat));

    }

    /**
     * Increases accuracy by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void increase_accuracy(int percent, float duration) {
        int stat = character.component(CharacterComponents.COMBAT).getAccuracy();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.ACCURACY, MathUtilities.getPercentOf(percent, stat));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.ACCURACY, MathUtilities.getPercentOf(percent, stat)), duration);

    }

    /**
     * Decreases accuracy by the specified percent
     *
     * @param percent
     */
    @Override
    public void decrease_accuracy(int percent) {
        int stat = character.component(CharacterComponents.COMBAT).getAccuracy();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.ACCURACY, MathUtilities.getPercentOf(percent, stat));
    }

    /**
     * Decreases accuracy by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void decrease_accuracy(int percent, float duration) {
        int stat = character.component(CharacterComponents.COMBAT).getAccuracy();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.ACCURACY, MathUtilities.getPercentOf(percent, stat));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.ACCURACY, MathUtilities.getPercentOf(percent, stat)), duration);
    }

    /**
     * Gets the Strength level
     *
     * @return strength
     */
    @Override
    public int getStrength() {
        return character.component(CharacterComponents.COMBAT).getStrength();
    }

    /**
     * Increases strength by the specified percent
     *
     * @param percent
     */
    @Override
    public void increase_strength(int percent) {
        int stat = character.component(CharacterComponents.COMBAT).getStrength();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.STRENGTH, MathUtilities.getPercentOf(percent, stat));

    }

    /**
     * Increases strength by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void increase_strength(int percent, float duration) {
        int stat = character.component(CharacterComponents.COMBAT).getStrength();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.STRENGTH, MathUtilities.getPercentOf(percent, stat));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.STRENGTH, MathUtilities.getPercentOf(percent, stat)), duration);
    }

    /**
     * Decreases strength by the specified percent
     *
     * @param percent
     */
    @Override
    public void decrease_strength(int percent) {
        int stat = character.component(CharacterComponents.COMBAT).getStrength();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.STRENGTH, MathUtilities.getPercentOf(percent, stat));
    }

    /**
     * Decreases strength by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void decrease_strength(int percent, float duration) {
        int stat = character.component(CharacterComponents.COMBAT).getStrength();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.STRENGTH, MathUtilities.getPercentOf(percent, stat));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.STRENGTH, MathUtilities.getPercentOf(percent, stat)), duration);
    }

    /**
     * Gets the Resilience level
     *
     * @return resilience
     */
    @Override
    public int getResilience() {
        return character.component(CharacterComponents.COMBAT).getResilience();
    }

    /**
     * Increases the maximum resilience by the specified percent
     *
     * @param percent
     */
    @Override
    public void increase_resilience(int percent) {
        int stat = character.component(CharacterComponents.COMBAT).getResilience();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.RESILIENCE, MathUtilities.getPercentOf(percent, stat));
    }

    /**
     * Increases the maximum resilience by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void increase_resilience(int percent, float duration) {
        int stat = character.component(CharacterComponents.COMBAT).getResilience();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.RESILIENCE, MathUtilities.getPercentOf(percent, stat));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.RESILIENCE, MathUtilities.getPercentOf(percent, stat)), duration);
    }

    /**
     * Decreases the maximum resilience by the specified percent
     *
     * @param percent
     */
    @Override
    public void decrease_resilience(int percent) {
        int stat = character.component(CharacterComponents.COMBAT).getResilience();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.RESILIENCE, MathUtilities.getPercentOf(percent, stat));

    }

    /**
     * Decreases the maximum resilience by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void decrease_resilience(int percent, float duration) {
        int stat = character.component(CharacterComponents.COMBAT).getResilience();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.RESILIENCE, MathUtilities.getPercentOf(percent, stat));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.RESILIENCE, MathUtilities.getPercentOf(percent, stat)), duration);
    }

    /**
     * Gets the Stamina Level
     *
     * @return stamina
     */
    @Override
    public int getStamina() {
        return character.component(CharacterComponents.COMBAT).getStamina();
    }

    /**
     * Gets the Maximum Stamina level
     *
     * @return maximum stamina
     */
    @Override
    public int getMaxStamina() {
        return character.component(CharacterComponents.COMBAT).getMaxStamina();
    }

    /**
     * Increases the maximum stamina by the specified percent
     *
     * @param percent
     */
    @Override
    public void increase_maximum_stamina(int percent) {
        int maxStamina = character.component(CharacterComponents.COMBAT).getMaxStamina();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.STAMINA, MathUtilities.getPercentOf(percent, maxStamina));
    }

    /**
     * Increases the maximum stamina by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void increase_maximum_stamina(int percent, float duration) {
        int maxStamina = character.component(CharacterComponents.COMBAT).getMaxStamina();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.STAMINA, MathUtilities.getPercentOf(percent, maxStamina));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.STAMINA, MathUtilities.getPercentOf(percent, maxStamina)), duration);
    }

    /**
     * Decreases the maximum stamina by the specified percent
     *
     * @param percent
     */
    @Override
    public void decrease_maximum_stamina(int percent) {
        int maxStamina = character.component(CharacterComponents.COMBAT).getMaxStamina();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.STAMINA, MathUtilities.getPercentOf(percent, maxStamina));
    }

    /**
     * Decreases the maximum stamina by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void decrease_maximum_stamina(int percent, float duration) {
        int maxStamina = character.component(CharacterComponents.COMBAT).getMaxStamina();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.STAMINA, MathUtilities.getPercentOf(percent, maxStamina));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.STAMINA, MathUtilities.getPercentOf(percent, maxStamina)), duration);
    }

    /**
     * Gets the Energy Level
     *
     * @return energy
     */
    @Override
    public int getEnergy() {
        return character.component(CharacterComponents.COMBAT).getEnergy();
    }

    /**
     * Gets the Maximum Energy Level
     *
     * @return maximum energy
     */
    @Override
    public int getMaxEnergy() {
        return character.component(CharacterComponents.COMBAT).getMaxEnergy();
    }

    /**
     * Increases the maximum energy by the specified percent
     *
     * @param percent
     */
    @Override
    public void increase_maximum_energy(int percent) {
        int maxEnergy = character.component(CharacterComponents.COMBAT).getMaxEnergy();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.ENERGY, MathUtilities.getPercentOf(percent, maxEnergy));
    }

    /**
     * Increases the maximum energy by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void increase_maximum_energy(int percent, float duration) {
        int maxEnergy = character.component(CharacterComponents.COMBAT).getMaxEnergy();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.ENERGY, MathUtilities.getPercentOf(percent, maxEnergy));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.ENERGY, MathUtilities.getPercentOf(percent, maxEnergy)), duration);
    }

    /**
     * Decreases the maximum energy by the specified percent
     *
     * @param percent
     */
    @Override
    public void decrease_maximum_energy(int percent) {
        int maxEnergy = character.component(CharacterComponents.COMBAT).getMaxEnergy();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.ENERGY, MathUtilities.getPercentOf(percent, maxEnergy));
    }

    /**
     * Decreases the maximum energy by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void decrease_maximum_energy(int percent, float duration) {
        int maxEnergy = character.component(CharacterComponents.COMBAT).getMaxEnergy();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.ENERGY, MathUtilities.getPercentOf(percent, maxEnergy));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.ENERGY, MathUtilities.getPercentOf(percent, maxEnergy)), duration);
    }

    /**
     * Gets the Melee Resistance level
     *
     * @return melee resistance
     */
    @Override
    public int getMeleeResistance() {
        return character.component(CharacterComponents.COMBAT).getMeleeResistance();
    }

    /**
     * Increases the melee resistance by the specified percent
     *
     * @param percent
     */
    @Override
    public void increase_melee_resistance(int percent) {
        int melee = character.component(CharacterComponents.COMBAT).getMeleeResistance();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.MELEE_RESISTANCE, MathUtilities.getPercentOf(percent, melee));
    }

    /**
     * Increases the melee resistance by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void increase_melee_resistance(int percent, float duration) {
        int melee = character.component(CharacterComponents.COMBAT).getMeleeResistance();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.MELEE_RESISTANCE, MathUtilities.getPercentOf(percent, melee));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.MELEE_RESISTANCE, MathUtilities.getPercentOf(percent, melee)), duration);
    }

    /**
     * Decreases the melee resistance by the specified percent
     *
     * @param percent
     */
    @Override
    public void decrease_melee_resistance(int percent) {
        int melee = character.component(CharacterComponents.COMBAT).getMeleeResistance();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.MELEE_RESISTANCE, MathUtilities.getPercentOf(percent, melee));
    }

    /**
     * Decreases the melee resistance by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void decrease_melee_resistance(int percent, float duration) {
        int melee = character.component(CharacterComponents.COMBAT).getMeleeResistance();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.MELEE_RESISTANCE, MathUtilities.getPercentOf(percent, melee));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.MELEE_RESISTANCE, MathUtilities.getPercentOf(percent, melee)), duration);
    }

    /**
     * Gets the Archery Resistance level
     *
     * @return archery resistance
     */
    @Override
    public int getArcheryResistance() {
        return character.component(CharacterComponents.COMBAT).getArrowResistance();
    }

    /**
     * Increases the archery resistance by the specified percent
     *
     * @param percent
     */
    @Override
    public void increase_archery_resistance(int percent) {
        int archery = character.component(CharacterComponents.COMBAT).getArrowResistance();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.ARCHERY_RESISTANCE, MathUtilities.getPercentOf(percent, archery));

    }

    /**
     * Decreases the archery resistance by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void increase_archery_resistance(int percent, float duration) {
        int archery = character.component(CharacterComponents.COMBAT).getArrowResistance();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.ARCHERY_RESISTANCE, MathUtilities.getPercentOf(percent, archery));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.ARCHERY_RESISTANCE, MathUtilities.getPercentOf(percent, archery)), duration);
    }

    /**
     * Decreases the archery resistance by the specified percent
     *
     * @param percent
     */
    @Override
    public void decrease_archery_resistance(int percent) {
        int melee = character.component(CharacterComponents.COMBAT).getArrowResistance();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.ARCHERY_RESISTANCE, MathUtilities.getPercentOf(percent, melee));
    }

    /**
     * Decreases the archery resistance by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void decrease_archery_resistance(int percent, float duration) {
        int archery = character.component(CharacterComponents.COMBAT).getArrowResistance();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.ARCHERY_RESISTANCE, MathUtilities.getPercentOf(percent, archery));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.ARCHERY_RESISTANCE, MathUtilities.getPercentOf(percent, archery)), duration);
    }

    /**
     * Gets the Magic Resistance Level
     *
     * @return magic resistance
     */
    @Override
    public int getMagicResistance() {
        return character.component(CharacterComponents.COMBAT).getMagicResistance();
    }

    /**
     * Increases the magic resistance by the specified percent
     *
     * @param percent
     */
    @Override
    public void increase_magic_resistance(int percent) {
        int stat = character.component(CharacterComponents.COMBAT).getMagicResistance();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.MAGIC_RESISTANCE, MathUtilities.getPercentOf(percent, stat));
    }

    /**
     * Increases the magic resistance by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void increase_magic_resistance(int percent, float duration) {
        int stat = character.component(CharacterComponents.COMBAT).getMagicResistance();
        character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.MAGIC_RESISTANCE, MathUtilities.getPercentOf(percent, stat));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.MAGIC_RESISTANCE, MathUtilities.getPercentOf(percent, stat)), duration);
    }

    /**
     * Decreases the magic resistance by the specified percent
     * for the desired seconds
     *
     * @param percent
     */
    @Override
    public void decrease_magic_resistance(int percent) {
        int stat = character.component(CharacterComponents.COMBAT).getMagicResistance();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.MAGIC_RESISTANCE, MathUtilities.getPercentOf(percent, stat));
    }

    /**
     * Decreases the magic resistance by the specified percent
     * for the desired seconds
     *
     * @param percent
     * @param duration
     */
    @Override
    public void decrease_magic_resistance(int percent, float duration) {
        int stat = character.component(CharacterComponents.COMBAT).getMagicResistance();
        character.component(CharacterComponents.COMBAT).decreaseBonuses(CharacterCombatComponent.StatTypes.MAGIC_RESISTANCE, MathUtilities.getPercentOf(percent, stat));
        DelayedTaskTicker.delayTask(() -> character.component(CharacterComponents.COMBAT).increaseBonuses(CharacterCombatComponent.StatTypes.MAGIC_RESISTANCE, MathUtilities.getPercentOf(percent, stat)), duration);
    }

    /**
     * Gets the Hit Damage against the target
     *
     * @param style
     * @param target
     * @return the hit damage
     */
    @Override
    public int get_hit_damage(CombatDefinitions.AttackStyle style, RelicCharacterAPI target) {
        return get_hit_damage(style, target, true);
    }

    /**
     * Gets the Hit Damage against the target
     *
     * @param style
     * @param target
     * @param mainHand
     * @return the hit damage
     */
    @Override
    public int get_hit_damage(CombatDefinitions.AttackStyle style, RelicCharacterAPI target, boolean mainHand) {
        return get_hit_damage(style, target, mainHand, 0);
    }

    /**
     * Gets the Hit Damage against the target
     *
     * @param style
     * @param target
     * @param mainHand
     * @param bonus
     * @return the hit damage
     */
    @Override
    public int get_hit_damage(CombatDefinitions.AttackStyle style, RelicCharacterAPI target, boolean mainHand, float bonus) {
        return character.component(CharacterComponents.COMBAT).getHit(style, (Character) target.getActor(), mainHand, bonus);
    }

    /**
     * Gets the Hit Chance against the target
     *
     * @param style
     * @param target
     * @return the hit chance
     */
    @Override
    public int get_hit_chance(CombatDefinitions.AttackStyle style, RelicCharacterAPI target) {
        return get_hit_chance(style, target, true);
    }

    /**
     * Gets the Hit chance agains the target
     *
     * @param style
     * @param target
     * @param mainHand
     * @return the hit chance
     */
    @Override
    public int get_hit_chance(CombatDefinitions.AttackStyle style, RelicCharacterAPI target, boolean mainHand) {
        return character.component(CharacterComponents.COMBAT).getHitChance(style, (Character) target.getActor(), mainHand);
    }


}
