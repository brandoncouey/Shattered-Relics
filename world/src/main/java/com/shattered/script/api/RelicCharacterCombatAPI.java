package com.shattered.script.api;

import com.shattered.engine.tasks.DelayedTaskTicker;
import com.shattered.game.actor.character.Character;
import com.shattered.game.actor.character.components.combat.CharacterCombatComponent;
import com.shattered.game.actor.character.components.combat.CombatDefinitions;
import com.shattered.script.types.AbilityScript;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@NonNull
@RequiredArgsConstructor
public abstract class RelicCharacterCombatAPI {

    /**
     * Represents the Character for the Quest API
     */
    @Getter
    protected final Character character;

    /**
     * Gets the current target that the player has targeted
     * @return the current target
     */
    public abstract RelicCharacterAPI getTarget();

    /**
     * Gets the previous target that was player targeted
     * @return the previous target
     */
    public abstract RelicCharacterAPI getPreviousTarget();

    /**
     * Gets the list of all characters that are currently attacking you
     * @return a list of all current targets
     */
    public abstract List<RelicCharacterAPI> getTargets();

    /**
     * Clears the current target for the player and removes target UI on the client.
     */
    public abstract void clear_target();

    /**
     * Checks if the character is currently in combat
     * @return in combat
     */
    public abstract boolean in_combat();

    /**
     * Method used for attacking with a projectile to the current
     *  target and deals the specified damage
     * @param projectile
     */
    public abstract void send_projectile(String projectile, int damage);

    /**
     * Method used for attacking with a projectile to the current
     *  target and deals the specified damage and links it to the specified script
     * @param projectile
     * @param damage
     * @param script
     */
    public abstract void send_projectile(String projectile, int damage, AbilityScript script);

    /**
     * Method used for attacking with a projectile to the current
     *  target at a delayed 10th of a second and deals the specfied damage
     * @param projectile
     * @param damage
     * @param delay
     */
    public abstract void send_projectile(String projectile, int damage, float delay);

    /**
     * Method used for attacking with a projectile to the current
     *  target at a delayed 10th of a second and deals the specfied damage and links it to the specified script
     * @param projectile
     * @param damage
     * @param delay
     * @param script
     */
    public abstract void send_projectile(String projectile, int damage, float delay, AbilityScript script);

    /**
     * Method used for attacking with a projectile to the specified
     *  target.
     * @param projectile
     * @param target
     * @param damage
     */
    public abstract void send_projectile(String projectile, RelicCharacterAPI target, int damage);

    /**
     * Method used for attacking with a projectile to the specified and links it to the specified script
     *  target.
     * @param projectile
     * @param target
     * @param damage
     * @param script
     */
    public abstract void send_projectile(String projectile, RelicCharacterAPI target, int damage, AbilityScript script);

    /**
     * Method used for attacking with a projectile to the specified
     *  target with added 10th of a second delay * delay
     * @param projectile
     * @param target
     * @param damage
     * @param delay
     */
    public abstract void send_projectile(String projectile, RelicCharacterAPI target, int damage, float delay);

    /**
     * Method used for attacking with a projectile to the specified
     *  target with added 10th of a second delay * delay and links it to the specified script
     * @param projectile
     * @param target
     * @param damage
     * @param delay
     * @param script
     */
    public abstract void send_projectile(String projectile, RelicCharacterAPI target, int damage, float delay, AbilityScript script);


    /**
     * Hits the desired character with the specified damage amount
     * @param target
     * @param amount
     */
    public abstract void hit(RelicCharacterAPI target, int amount);

    /**
     * Hits the desired character with the specified damage amount and links it to the specified script
     * @param target
     * @param amount
     * @param script
     */
    public abstract void hit(RelicCharacterAPI target, int amount, AbilityScript script);


    /**
     * Hits the desired character with the specified damage amount
     * @param source
     * @param amount
     */
    public abstract void hit(RelicCharacterAPI source, int amount, float delay);

    /**
     * Hits the desired character with the specified damage amount and links it to the specified script
     * @param source
     * @param amount
     * @param delay
     * @param script
     */
    public abstract void hit(RelicCharacterAPI source, int amount, float delay, AbilityScript script);

    /**
     * Heals the desired character with the specified healing amount
     * @param target
     * @param amount
     */
    public abstract void heal(RelicCharacterAPI target, int amount);

    /**
     * Heals the desired character with the specified healing amount and links it to the specified script
     * @param target
     * @param amount
     * @param script
     */
    public abstract void heal(RelicCharacterAPI target, int amount, AbilityScript script);

    /**
     * Heals the desired character with the specified healing amount
     *  default unit is seconds
     * @param target
     * @param amount
     */
    public abstract void heal(RelicCharacterAPI target, int amount, float delay);

    /**
     * Heals the desired character with the specified healing amount
     *  default unit is seconds and links it to the specified script
     * @param target
     * @param amount
     * @param delay
     * @param script
     */
    public abstract void heal(RelicCharacterAPI target, int amount, float delay, AbilityScript script);


    /**
     * Finds the next possible potential target if there is no current set target
     * @return the next possible target
     */
    public abstract RelicCharacterAPI getPossibleTarget();

    /**
     * Finds a list of possible targets with the given specified maximum amount of targets.
     * @param maximum
     * @return a list of possible targets with the specified maximum number of enemies.
     */
    public abstract List<RelicCharacterAPI> get_possible_targets(int maximum);

    /**
     * Finds a list of possible targets with the given specified maximum amount of targets and certain distance.
     * @param maximum
     * @param maxDistance
     * @return a list of possible targets with the specified maximum number of enemies.
     */
    public abstract List<RelicCharacterAPI> get_possible_targets(int maximum, int maxDistance);

    /**
     * Interrupts the Character from Casting
     */
    public abstract void interrupt();

    /**
     * Stuns the Character with the specified Duration in seconds
     * @param duration
     * @return the amount of seconds they're stunned for
     */
    public abstract float stun(float duration);

    /**
     * Checks if the Character is Stunned
     * @return is stunned
     */
    public abstract boolean getStunned();

    /**
     * Disarms the Character with the specified Duration in seconds
     * @param duration
     * @return the amount of seconds they're disarmed for
     */
    public abstract float disarm(float duration);

    /**
     * Checks if the Character is disarmed.
     * @return is disarmed
     */
    public abstract boolean getDisarmed();

    /**
     * Snares the Character with the specified duration in seconds
     * @param duration
     * @return the amount of seconds they're snared for
     */
    public abstract float snare(float duration);

    /**
     * Checks if the Character is Snared
     * @return is snared
     */
    public abstract boolean getSnared();

    /**
     * Disorients the Character with the specified duration in seconds
     * @param duration
     * @return the amount of time they're disoriented for
     */
    public abstract float disorient(float duration);

    /**
     * Checks if the Character is disoriented
     * @return is disoriented
     */
    public abstract boolean getDisoriented();

    /**
     * Disorients the Character with the specified duration in seconds
     * @param duration
     * @return the amount of time they're magic locked for
     */
    public abstract float magicLock(float duration);

    /**
     * Checks if the Character is Magic Locked
     * @return
     */
    public abstract boolean getMagicLocked();

    /**
     * Gets the hostiltiy level of the character
     * @return the hostility level
     */
    public abstract CharacterCombatComponent.HostilityLevel getHostilityLevel();

    /**
     * Sets the hostility level for the character
     * @param level
     */
    public abstract void set_hostility(CharacterCombatComponent.HostilityLevel level);

    /**
     * Gets the Critical Strike Chance
     * @return critical strike chance
     */
    public abstract int getCritChance();

    /**
     * Increases critical strike c hance by the specified percent
     * @param percent
     */
    public abstract void increase_crit_chance(int percent);

    /**
     * Increases critical strike c hance by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void increase_crit_chance(int percent, float duration);

    /**
     * Decreases critical strike c hance by the specified percent
     * @param percent
     */
    public abstract void decrease_crit_chance(int percent);

    /**
     * Decreases critical strike c hance by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void decrease_crit_chance(int percent, float duration);

    /**
     * Gets the Accuracy level
     * @return accuracy
     */
    public abstract int getAccuracy();

    /**
     * Increases accuracy by the specified percent
     * @param percent
     */
    public abstract void increase_accuracy(int percent);

    /**
     * Increases accuracy by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void increase_accuracy(int percent, float duration);

    /**
     * Decreases accuracy by the specified percent
     * @param percent
     */
    public abstract void decrease_accuracy(int percent);

    /**
     * Decreases accuracy by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void decrease_accuracy(int percent, float duration);

    /**
     * Gets the Strength level
     * @return strength
     */
    public abstract int getStrength();

    /**
     * Increases strength by the specified percent
     * @param percent
     */
    public abstract void increase_strength(int percent);

    /**
     * Increases strength by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void increase_strength(int percent, float duration);

    /**
     * Decreases strength by the specified percent
     * @param percent
     */
    public abstract void decrease_strength(int percent);

    /**
     * Decreases strength by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void decrease_strength(int percent, float duration);

    /**
     * Gets the Resilience level
     * @return resilience
     */
    public abstract int getResilience();

    /**
     * Increases the maximum resilience by the specified percent
     * @param percent
     */
    public abstract void increase_resilience(int percent);

    /**
     * Increases the maximum resilience by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void increase_resilience(int percent, float duration);

    /**
     * Decreases the maximum resilience by the specified percent
     * @param percent
     */
    public abstract void decrease_resilience(int percent);

    /**
     * Decreases the maximum resilience by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void decrease_resilience(int percent, float duration);

    /**
     * Gets the Stamina Level
     * @return stamina
     */
    public abstract int getStamina();

    /**
     * Gets the Maximum Stamina level
     * @return maximum stamina
     */
    public abstract int getMaxStamina();

    /**
     * Increases the maximum stamina by the specified percent
     * @param percent
     */
    public abstract void increase_maximum_stamina(int percent);

    /**
     * Increases the maximum stamina by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void increase_maximum_stamina(int percent, float duration);

    /**
     * Decreases the maximum stamina by the specified percent
     * @param percent
     */
    public abstract void decrease_maximum_stamina(int percent);

    /**
     * Decreases the maximum stamina by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void decrease_maximum_stamina(int percent, float duration);

    /**
     * Gets the Energy Level
     * @return energy
     */
    public abstract int getEnergy();

    /**
     * Gets the Maximum Energy Level
     * @return maximum energy
     */
    public abstract int getMaxEnergy();

    /**
     * Increases the maximum energy by the specified percent
     * @param percent
     */
    public abstract void increase_maximum_energy(int percent);

    /**
     * Increases the maximum energy by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void increase_maximum_energy(int percent, float duration);

    /**
     * Decreases the maximum energy by the specified percent
     * @param percent
     */
    public abstract void decrease_maximum_energy(int percent);

    /**
     * Decreases the maximum energy by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void decrease_maximum_energy(int percent, float duration);

    /**
     * Gets the Melee Resistance level
     * @return melee resistance
     */
    public abstract int getMeleeResistance();

    /**
     * Increases the melee resistance by the specified percent
     * @param percent
     */
    public abstract void increase_melee_resistance(int percent);

    /**
     * Increases the melee resistance by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void increase_melee_resistance(int percent, float duration);

    /**
     * Decreases the melee resistance by the specified percent
     * @param percent
     */
    public abstract void decrease_melee_resistance(int percent);

    /**
     * Decreases the melee resistance by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void decrease_melee_resistance(int percent, float duration);

    /**
     * Gets the Archery Resistance level
     * @return archery resistance
     */
    public abstract int getArcheryResistance();

    /**
     * Increases the archery resistance by the specified percent
     * @param percent
     */
    public abstract void increase_archery_resistance(int percent);

    /**
     * Decreases the archery resistance by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void increase_archery_resistance(int percent, float duration);

    /**
     * Decreases the archery resistance by the specified percent
     * @param percent
     */
    public abstract void decrease_archery_resistance(int percent);

    /**
     * Decreases the archery resistance by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void decrease_archery_resistance(int percent, float duration);

    /**
     * Gets the Magic Resistance Level
     * @return magic resistance
     */
    public abstract int getMagicResistance();

    /**
     * Increases the magic resistance by the specified percent
     * @param percent
     */
    public abstract void increase_magic_resistance(int percent);

    /**
     * Increases the magic resistance by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void increase_magic_resistance(int percent, float duration);

    /**
     * Decreases the magic resistance by the specified percent
     *  for the desired miliseconds
     * @param percent
     */
    public abstract void decrease_magic_resistance(int percent);

    /**
     * Decreases the magic resistance by the specified percent
     *  for the desired miliseconds
     * @param percent
     * @param duration
     */
    public abstract void decrease_magic_resistance(int percent, float duration);


    /**
     * Gets the Hit Damage against the target
     * @param style
     * @param target
     * @return the hit damage
     */
    public abstract int get_hit_damage(CombatDefinitions.AttackStyle style, RelicCharacterAPI target);

    /**
     * Gets the Hit Damage against the target
     * @param style
     * @param target
     * @param mainHand
     * @return the hit damage
     */
    public abstract int get_hit_damage(CombatDefinitions.AttackStyle style, RelicCharacterAPI target, boolean mainHand);

    /**
     * Gets the Hit Damage against the target
     * @param style
     * @param target
     * @param mainHand
     * @param bonus
     * @return the hit damage
     */
    public abstract int get_hit_damage(CombatDefinitions.AttackStyle style, RelicCharacterAPI target, boolean mainHand, float bonus);

    /**
     * Gets the Hit Chance against the target
     * @param style
     * @param target
     * @return the hit chance
     */
    public abstract int get_hit_chance(CombatDefinitions.AttackStyle style, RelicCharacterAPI target);

    /**
     * Gets the Hit chance agains the target
     * @param style
     * @param target
     * @param mainHand
     * @return the hit chance
     */
    public abstract int get_hit_chance(CombatDefinitions.AttackStyle style, RelicCharacterAPI target, boolean mainHand);



}
