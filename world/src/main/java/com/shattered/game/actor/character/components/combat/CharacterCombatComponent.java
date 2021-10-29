package com.shattered.game.actor.character.components.combat;

import com.shattered.datatable.tables.AbilityUDataTable;
import com.shattered.datatable.tables.ProjectileUDataTable;
import com.shattered.engine.tasks.DelayedTaskTicker;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.Character;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.actor.character.npc.component.combat.NPCCombatComponent;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.combat.PlayerCombatComponent;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.flags.FlagType;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.component.WorldComponent;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.script.ScriptManager;
import com.shattered.script.api.impl.NpcAPI;
import com.shattered.script.api.impl.PlayerAPI;
import com.shattered.script.types.AbilityScript;
import com.shattered.script.types.NPCCombatScript;
import com.shattered.utilities.TimeUtility;
import com.shattered.utilities.VariableUtility;
import com.shattered.utilities.ecs.ProcessComponent;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.shattered.game.actor.character.components.combat.CombatDefinitions.*;

@ProcessComponent(interval = 0.1f)
public class CharacterCombatComponent extends WorldComponent {

    /**
     * Represents the Maximum Times you can Crowd Control of a Single Type before a DR.
     */
    public static final int MAXIMUM_CCDR = 3;

    /**
     * Represents the Time in Seconds in which the Stun DR is lifted.
     */
    public static final int STUN_DR_DURATION = 60;

    /**
     * Represents the Time in Seconds in which the Magic Locked DR is lifted.
     */
    public static final int MAGIC_LOCKED_DR_DURATION = 60;

    /**
     * Represents the Time in Seconds in which the Snare DR is lifted.
     */
    public static final int SNARE_DR_DURATION = 60;

    /**
     * Represents the Time in Seconds in which the Disarm DR is lifted.
     */
    public static final int DISARM_DR_DURATION = 60;

    /**
     * Represents the Time in Seconds in which the Disorient DR is lifted.
     */
    public static final int DISORIENT_DR_DURATION = 60;


    public enum StatTypes {

        ACCURACY,

        STRENGTH,

        FOCUS,

        INTELLECT,

        RESILIENCE,

        STAMINA,

        ENERGY,

        CRITICAL_STRIKE,

        MELEE_RESISTANCE,

        ARCHERY_RESISTANCE,

        MAGIC_RESISTANCE,
    }

    /**
     * Represents the Character Hostility level
     */
    public enum HostilityLevel {

        FRIENDLY,

        PASSIVE,

        HOSTILE

        ;

        /**
         * Converts string to hostility level
         * @param name
         * @return the hostility level
         */
        public static HostilityLevel forName(String name) {
            switch (name.toLowerCase()) {
                case "friendly":
                    return HostilityLevel.FRIENDLY;
                case "passive":
                    return HostilityLevel.PASSIVE;
                case "hostile":
                    return HostilityLevel.HOSTILE;
            }
            return HostilityLevel.FRIENDLY;
        }
    }

    /**
     * Represents the Hit Mark Damage Types
     */
    public static final int HIT_MARK_DMG = 0,  HIT_MARK_HEAL = 1, HIT_MARK_DODGE = 3;

    /**
     * Represents the Hostility Level of the Character
     */
    @Getter
    private HostilityLevel hostilityLevel = HostilityLevel.FRIENDLY;

    /**
     * Represents a map of all current cooldowns incl the ability id and sysmilis
     */
    @Getter
    protected final Map<Integer, Long> cooldowns = new HashMap<>();

    /**
     * Represents your current 'targeted' actor
     */
    @Getter @Setter
    protected Character target;

    /**
     * Represents your previous 'targeted' actor
     */
    @Getter
    @Setter
    protected Character previousTarget;

    /**
     * Represents the Bonuses  for each stat
     */
    @Getter
    protected final Map<StatTypes, Integer> bonuses = new ConcurrentHashMap<>();

    /**
     * Represents a list of current ongoing hits that need to be processed.
     */
    @Getter
    protected final List<World.HitMark> hits = new CopyOnWriteArrayList<>();

    /**
     * Represents a list of all actors that have hit the target atleast once
     *  and now remains a potential target.
     *          if these are a player target, the overhead health bar
     *                  will remain shown.
     */
    @Getter
    protected final Map<Character, List<Hit>> targets = new ConcurrentHashMap<>();

    /**
     * Represents the grid coordinate of the player at the time they casted
     *  the ability not the target location.
     */
    @Getter
    @Setter
    protected GridCoordinate castedAtLocation;

    /**
     * Represents the current casting ability
     */
    @Getter
    protected AbilityScript castingAbility;

    /**
     * Represents the Current Attack Style
     */
    @Getter
    protected AttackStyle style = AttackStyle.MELEE;

    /**
     * Represents the time the ability was casted at in sysmilis
     */
    @Getter
    @Setter
    protected long castedTimestamp;

    /**
     * Represents the Duration of Stun
     */
    @Getter
    @Setter
    protected long stunnedDuration;

    /**
     * Represents the Duration of Magic Locked
     */
    @Getter
    @Setter
    protected long magicLockedDuration;

    /**
     * Represents the Duration of a snare
     */
    @Getter
    @Setter
    protected long snaredDuration;

    /**
     * Represents the Duration of a Disorient
     */
    @Getter
    @Setter
    protected long disorientedDuration;

    /**
     * Represents the Duration of a Disarm
     */
    @Getter
    @Setter
    protected long disarmedDuration;

    /**
     * Represents the Timestamp of when the Stun DR will be lifted.
     */
    @Getter
    @Setter
    protected long stunnedDRDuration;

    /**
     * Represents the Amount of Times you've been stunned within a DR timespan.
     */
    @Getter
    @Setter
    protected int stunnedCount;

    /**
     * Represents the Timestamp of when the Snare DR will be lifted.
     */
    @Getter
    @Setter
    protected long snaredDRDuration;

    /**
     * Represents the Amount of Times you've been snared within a DR timespan.
     */
    @Getter
    @Setter
    protected int snaredCount;

    /**
     * Represents the Timestamp of when the Disarm DR will be lifted.
     */
    @Getter
    @Setter
    protected long disarmedDRDuration;

    /**
     * Represents the Amount of Times you've been disarmed within a DR timespan.
     */
    @Getter
    @Setter
    protected int disarmedCount;

    /**
     * Represents the Timestamp of when the Disorient DR will be lifted.
     */
    @Getter
    @Setter
    protected long disorientedDRDuration;

    /**
     * Represents the Amount of Times you've been disoriented within a DR timespan.
     */
    @Getter
    @Setter
    protected int disorientedCount;

    /**
     * Represents the Timestamp of when the Magic Locked DR will be lifted.
     */
    @Getter
    @Setter
    protected long magicLockedDRDuration;

    /**
     * Represents the Amount of Times you've been magic locked within a DR timespan.
     */
    @Getter
    @Setter
    private int magicLockedCount;



    /**
     * Creates a new constructor setting the Owner
     *
     * @param gameObject
     */
    public CharacterCombatComponent(Object gameObject) {
        super(gameObject);
        Arrays.stream(StatTypes.values()).forEach(type -> bonuses.put(type, 0));
    }


    /**
     * Deals a specified amount of damage
     * @param source
     * @param amount
     */
    public void onDamage(Character source, int amount) {
        onDamage(source, amount, 0, null);
    }

    /**
     * Deals a specified amount of damage
     * @param source
     * @param amount
     */
    public void onDamage(Character source, int amount, AbilityScript script) {
        onDamage(source, amount, 0, script);
    }

    /**
     * Heals for a specified amount
     * @param source
     * @param amount
     */
    public void heal(Character source, int amount) {
        heal(source, amount, 0, null);
    }

    /**
     * Makes this actor hit another actor with the given amount
     * @param target
     * @param amount
     * @param delay
     */
    public void hitCharacter(Character target, int amount, float delay, AbilityScript ability) {
        if (target == null) return;
        target.component(CharacterComponents.COMBAT).onDamage(getCharacter(), amount, delay, ability);
    }

    /**
     * Makes this actor hit another actor with the given amount
     * @param target
     * @param amount
     * @param delay
     */
    public void healCharacter(Character target, int amount, float delay, AbilityScript ability) {
        component(CharacterComponents.COMBAT).heal(getCharacter(), amount, delay, ability);
    }

    /**
     * Method called upon the character getting damaged
     * @param source
     */
    public void onDamage(Character source, int amount, float delay, AbilityScript ability) {
        if (!getCharacter().getState().equals(ActorState.ALIVE)) return;
        if (delay > 0) {
            onDamageDelay(source, amount, delay, ability);
            return;
        }

        boolean isCrit = VariableUtility.random(1, 3) == 2;

        Hit hit = new Hit(isCrit, amount, ability == null ? AttackStyle.MELEE : ability.style(), ability != null ? ability.class_type() : null);

        if (isNPC() && !isInCombat()) {
            try {
                NPCCombatComponent combatComponent = (NPCCombatComponent) getNPC().component(CharacterComponents.COMBAT);
                NPCCombatScript script = ScriptManager.getNPCCombatScript(getNPC().getName());
                if (script != null) {
                    combatComponent.setCombatScript(script.getClass().newInstance());
                    script = combatComponent.getCombatScript();
                    script.setNpc(new NpcAPI(getNPC()));
                    if (script != null) {
                        script.on_attacked((source instanceof Player) ? new PlayerAPI((Player) source) : new NpcAPI((NPC) source), hit);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (ability != null && source instanceof Player)
            ability.on_hit(new PlayerAPI((Player) source), isPlayer() ? new PlayerAPI(getPlayer()) : new NpcAPI(getNPC()));

        //TODO take this to a NPCScript and modify the values.

        if (isPlayer() && isInvulnerable())  {
            //Show blocked?
            //hits.add(World.HitMark.newBuilder().setAmount(amount).setType(CharacterCombatComponent.HIT_MARK_DMG).setIsCriticial(isCrit).build());
            return;
        }

        hits.add(World.HitMark.newBuilder().setAmount(amount).setType(CharacterCombatComponent.HIT_MARK_DMG).setIsCriticial(isCrit).build());
        getVars().decrementVarInt("health", amount);

        if (isPlayer()) {
            if (ability != null)
                getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendCombatLog(VariableUtility.formatString(ability.name()) + " hit you for " + amount);
            else
                component(PlayerComponents.SOCIAL_CHANNEL).sendCombatLog(VariableUtility.formatString(source.getName()) + " hit you for " + amount);
        }

        component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
        component(ActorComponents.FLAG_UPDATE).flag(FlagType.HIT_MARK);


        if (!targets.containsKey(source)) {
            targets.put(source, new CopyOnWriteArrayList<>());
            targets.get(source).add(hit);
            component(ActorComponents.FLAG_UPDATE).flag(FlagType.TARGET);
        } else {
            targets.get(source).add(hit);
        }

        if (getVars().getVarInt("health") <= 0) {
            getCharacter().onDeath(source);
            return;
        }


        if (source.component(CharacterComponents.COMBAT).addTarget(getCharacter())) {
            component(ActorComponents.FLAG_UPDATE).flag(FlagType.TARGET);
        }


    }

    /**
     * Heals the current actor the specified amount and delay increment
     * @param source
     * @param amount
     * @param delay
     */
    public void heal(Character source, int amount, float delay, AbilityScript ability) {
        if (delay > 0) {
            onHealDelay(source, amount, delay);
            return;
        }

        if (getCharacter().getState().equals(ActorState.DEAD)) return;

        if ((getVars().getVarInt("health") + amount) > getVars().getVarInt("max_health"))
            getVars().setVarInt("health", getVars().getVarInt("max_health"));
        else
            getVars().incrementVarInt("health", amount);

        if (ability != null && isPlayer()) {
            component(PlayerComponents.SOCIAL_CHANNEL).sendCombatLog(VariableUtility.formatString(ability.name()) + " healed you for " + amount);
            ability.on_hit(new PlayerAPI(getPlayer()), source instanceof  Player ? new PlayerAPI((Player) source) : new NpcAPI((NPC) source));
        }

        if (ability == null && isPlayer()) {
            component(PlayerComponents.SOCIAL_CHANNEL).sendCombatLog(source.getName() + " healed you for " + amount);
        }

        hits.add(World.HitMark.newBuilder().setAmount(amount).setType(CharacterCombatComponent.HIT_MARK_HEAL).setIsCriticial(false).build());

        component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
        component(ActorComponents.FLAG_UPDATE).flag(FlagType.HIT_MARK);
    }

    /**
     * Delays the projectile damage based upon distance to target
     * @param source
     * @param amount
     */
    public void onDelayProjectileDamage(Character source, int amount, AbilityScript script) {
        int distanceDifference = distanceTo(source);
        float delayAmount = 2;
        if (distanceDifference >= 500)
            delayAmount += 2;
        if (distanceDifference >= 750)
            delayAmount += 2;
        if (distanceDifference >= 1000)
            delayAmount += 2;
        if (distanceDifference >= 1250)
            delayAmount += 2;
        if (distanceDifference >= 1500)
            delayAmount += 2;
        if (distanceDifference >= 1750)
            delayAmount += 2;
        onDamageDelay(source, amount, delayAmount, script);
    }

    /**
     * Delays the projectile heal based upon distance to target
     * @param source
     * @param amount
     */
    public void onDelayProjectileHeal(Character source, int amount) {
        int distanceDifference = distanceTo(source);
        float delayAmount = 2;
        if (distanceDifference >= 500)
            delayAmount += 2;
        if (distanceDifference >= 750)
            delayAmount += 2;
        if (distanceDifference >= 1000)
            delayAmount += 2;
        if (distanceDifference >= 1250)
            delayAmount += 2;
        if (distanceDifference >= 1500)
            delayAmount += 2;
        if (distanceDifference >= 1750)
            delayAmount += 2;
        onHealDelay(source, amount, delayAmount);
    }


    /**
     * Delays ondemand damage by the specified time and unit
     * @param source
     * @param amount
     * @param delay
     * @param script
     */
    public void onDamageDelay(Character source, int amount, float delay, AbilityScript script) {
        DelayedTaskTicker.delayTask(() -> {
            onDamage(source, amount, script);
        }, delay);

    }

    /**
     * Delays ondemand damage by the specified time and unit
     * @param source
     * @param amount
     * @param delay
     */
    public void onHealDelay(Character source, int amount, float delay) {
        DelayedTaskTicker.delayTask(() -> {
            heal(source, amount);
        }, delay);

    }

    /**
     * Sends a projectile to the specified target and damage
     * @param name
     * @param target
     * @param damage
     */
    public void sendProjectile(String name, Character target, int damage) {
        sendProjectile(name, target, damage, 0, null);
    }

    /**
     * Sends a projectile to the specified target and damage
     * @param name
     * @param target
     * @param damage
     * @param delay
     */
    public void sendProjectile(String name, Character target, int damage, float delay) {
        sendProjectile(name, target, damage, delay, null);
    }

    /**
     * Sends a projectile to the specified target and damage with the specified
     *  delay in 10th of a second increments
     * @param name
     * @param target
     * @param damage
     */
    public void sendProjectile(String name, Character target, int damage, float delay, AbilityScript script) {
        if (delay > 0) {
            DelayedTaskTicker.delayTask(() -> {

                sendProjectile(name, target, script == null ? 0 : script.pitch);
                if (target != null)
                    target.component(CharacterComponents.COMBAT).onDelayProjectileDamage(getCharacter(), damage, script);
            }, delay);
        } else {
            sendProjectile(name, target, script.pitch);
            if (target != null)
                target.component(CharacterComponents.COMBAT).onDelayProjectileDamage(getCharacter(), damage, script);
        }
    }

    /**
     * Sends a projectile to the current target with the specified projectile by name
     * @param name
     */
    private void sendProjectile(String name, Character target, float pitch) {
        if (!isPlayer()) return;
        ProjectileUDataTable table = ProjectileUDataTable.forName(name);
        if (table == null) return;
        sendProjectile(table.getId(), target, pitch);
    }


    /**
     * Sends a projectile to the current target and projectile by id
     * @param id
     * @param target
     * @param pitch
     */
    private void sendProjectile(int id, Character target, float pitch) {
        if (!isPlayer()) return;
        if (target != null) {
            boolean isPlayer = target instanceof Player;
            World.Projectile.Builder projectile = World.Projectile.newBuilder();
            projectile.setId(id).setUuid(VariableUtility.random(Integer.MAX_VALUE));
            component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllPlayers().stream().filter(Objects::nonNull).forEach(p ->
                    p.sendMessage(PacketOuterClass.Opcode.SMSG_SPAWN_PROJECTILE_FOR_ACTOR, World.SpawnProjectileForActor.newBuilder().setIndex(getActor().getClientIndex()).setToIndex(target.getClientIndex() << 1 | (isPlayer ? 1 : 0)).setProjectile(projectile.build()).setPitch(pitch).build()));
        } else {
            System.out.println("Sending projectile");
            World.Projectile.Builder projectile = World.Projectile.newBuilder();
            projectile.setId(id).setUuid(VariableUtility.random(Integer.MAX_VALUE));
            component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllPlayers().stream().filter(Objects::nonNull).forEach(p ->
                    p.sendMessage(PacketOuterClass.Opcode.SMSG_SPAWN_PROJECTILE_FOR_ACTOR, World.SpawnProjectileForActor.newBuilder().setIndex(getActor().getClientIndex()).setToIndex(0).setProjectile(projectile.build()).setPitch(pitch).build()));
        }
    }

    /**
     * Tells the client to put the ability on cooldown.
     *
     * @param id
     * @param duration
     */
    public void setAbilityCooldown(int id, float duration) {
        long remaining = TimeUtility.longToSeconds(duration);
        cooldowns.put(id, remaining);
        if (isPlayer())
            sendMessage(PacketOuterClass.Opcode.SMSG_DISPLAY_COOLDOWN_TIMER, World.AbilityCooldown.newBuilder().setAbilityId(id).setDuration(remaining).build());
    }

    /**
     * Sets the global cooldown and tells the client to put other abilities on global cooldown
     *      if reduced it will set the global cooldown from 1.5s to 1s.
     */
    public void setGlobalCooldown() {
        long remaining = System.currentTimeMillis() + 1050;
        cooldowns.put(0, remaining);

        if (isPlayer())
            sendMessage(PacketOuterClass.Opcode.SMSG_DISPLAY_COOLDOWN_TIMER, World.AbilityCooldown.newBuilder().setAbilityId(0).setDuration(remaining).build());
    }

    public boolean addTarget(Character character) {
        if (getTargets().containsKey(character)) return false;
        if (!character.getState().equals(ActorState.ALIVE)) return false;
        if (component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().distanceTo(character) > 8500) return false;
        getTargets().put(character, new CopyOnWriteArrayList<>());
        return true;
    }

    /**
     * Clears the target for the player
     */
    public void clearTarget() {
        setTarget(null);
        if (isPlayer())
            sendMessage(PacketOuterClass.Opcode.SMSG_CLEAR_TARGET);
    }

    /**
     * Checks if this actor is currently in combat.
     * @return in combat
     */
    public boolean isInCombat() {
        return getTarget() != null || !getTargets().isEmpty();
    }

    /**
     * Sets the casting ability with the proper parameters
     * @param castedAtLocation
     * @param castingAbility
     * @param castedTimestamp
     */
    public void setCastingAbility(GridCoordinate castedAtLocation, AbilityScript castingAbility, long castedTimestamp) {
        this.castedAtLocation = castedAtLocation;
        this.castingAbility = castingAbility;
        this.castedTimestamp = castedTimestamp;
    }

    @Override
    public void onTick(long deltaTime) {
        super.onTick(deltaTime);

        //Resets the DRs
        if (System.currentTimeMillis() >= stunnedDRDuration)
            setStunnedCount(0);
        if (System.currentTimeMillis() >= magicLockedDRDuration)
            setMagicLockedCount(0);
        if (System.currentTimeMillis() >= snaredDRDuration)
            setSnaredCount(0);
        if (System.currentTimeMillis() >= disarmedDRDuration)
            setDisarmedCount(0);
        if (System.currentTimeMillis() >= disorientedDRDuration)
            setDisorientedCount(0);


        //Must be a valid ability
        if (castingAbility == null || castedAtLocation == null) return;

        //Checks if you've moved too far away to cancel cast (10 units which is 1/5th of a single tile movement)
        if (distanceTo(castedAtLocation) > 2 && !castingAbility.can_walk()) {
            interruptCasting(false);
            return;
        }

        if (castingAbility.is_channel()) {
            castingAbility.on_use(new PlayerAPI(getPlayer()));
        }

        //Sends the successfully casted
        if (System.currentTimeMillis() >= castedTimestamp) {

            AbilityUDataTable table = AbilityUDataTable.forName(castingAbility.name());
            if (table != null) {
                if (table.getCoolDownTime() > 0)
                    setAbilityCooldown(table.getId(), table.getCoolDownTime());

                if (table.isAffectsGlobalCooldown())
                    setGlobalCooldown();

                castingAbility.on_use(new PlayerAPI(getPlayer()));
                setCastingAbility(null, null, 0);
            }
            DelayedTaskTicker.delayTask(() -> getCharacter().component(ActorComponents.ANIMATION).stopAnimation(), 6);

        }
    }//shit wants to act up when i start to screen share. nice.


    /**
     * Gets the Hit Chance for the Character
     * @param style
     * @param target
     * @return the hit chance
     */
    public int getHitChance(AttackStyle style, Character target, boolean isMainhand) {
        double accuracy = getAccuracy();

        double defence;
        double defenceMultipler = WEAKNESS_MULTIPLIER_NEUTRAL;

        //TODO this may just be an extra weakness system? for now the gear is doing the weaknesses.
       /* if (target.weakness == source.component(CharacterComponents.COMBAT).getStyle())
            defenceMultipler = WEAKNESS_MULTIPLIER_STRONGEST;

            //This is like a ice creature being attacked by ice
        else if (target.strongestProt == source.component(CharacterComponents.COMBAT).getStyle())
            defenceMultipler = WEAKNESS_MULTIPLIER_LOW;*/


        defence = target.component(CharacterComponents.COMBAT).getResilience();
        double defenceBonus = target.component(CharacterComponents.COMBAT).getResilience() * (WEAKNESS_MULTIPLIER_NEUTRAL - defenceMultipler);

        if (style == AttackStyle.MAGIC)
            defenceBonus += getMagicResistance();
        else if (style == AttackStyle.ARCHERY)
            defenceBonus += getArrowResistance();
        else if (style == AttackStyle.MELEE)
            defenceBonus += getMeleeResistance();

        defence += (int) defenceBonus;

        return (int) ((accuracy / defence * WEAKNESS_MULTIPLIER_NEUTRAL) * 100);
    }


    /**
     * Gets the Actual Hit for the Swing
     * @param style
     * @param target
     * @param mainHand
     * @return
     */
    public int getHit(AttackStyle style, Character target, boolean mainHand, float modifier) {
        if (target == null) return 0;
        double damage;
        double damageModifier = 1;
        boolean isCritical = component(CharacterComponents.COMBAT).getCriticalStrikeChance() > VariableUtility.random(100);
        if (isCritical)
            damageModifier += 0.25;

        //TODO this may just be an additional weakness system? For now the gear is doing the weaknesses
        /*if (source.component(CharacterComponents.COMBAT).getStyle() == target.weakness)//TODO
            damageModifier += 0.10;
        else if (source.component(CharacterComponents.COMBAT).getStyle() == target.strongestProt)//TODO
            damageModifier -= 0.30;*/

        double maxHit = getMaxHit(style, target, mainHand, damageModifier);
        double minHit = maxHit / (isCritical ? 1.4 : 1.8);
        damage = VariableUtility.random((int) Math.ceil(minHit), (int) Math.ceil(maxHit));
        return (int) Math.ceil(damage);
    }

    /**
     * Gets the Maximum Hit for the Character
     * @param style
     * @param target
     * @param mainHand
     * @param modifier
     * @return the maximum hit
     */
    public int getMaxHit(AttackStyle style, Character target, boolean mainHand, double modifier) {

        int damageModifier = (style == AttackStyle.MELEE ? getStrength() : style == AttackStyle.ARCHERY ? getFocus() : getIntellect());
        damageModifier += 3;//Bonuses
        double damage = ((damageModifier * 0.0008) * 1000) + (getAccuracy() * 0.0012) * 1000;
        double defence = target.component(CharacterComponents.COMBAT).getResilience();

        if (style == AttackStyle.MAGIC)
            defence += target.component(CharacterComponents.COMBAT).getMagicResistance();

        if (style == AttackStyle.ARCHERY)
            defence += target.component(CharacterComponents.COMBAT).getArrowResistance();

        if (style == AttackStyle.MELEE)
            defence += target.component(CharacterComponents.COMBAT).getMeleeResistance();

        damage *= modifier;

        if(getActor() instanceof Player && target instanceof Player)
            damage += (((Player) getActor()).container(Containers.EQUIPMENT).getPvpPower()* 0.001) * 1000;//pvp only

        damage -= (defence * 0.015) * 10;
        if(getActor() instanceof Player && target instanceof Player)
            damage -= (((Player) target).container(Containers.EQUIPMENT).getPvpResistance() * 0.001) * 1000;//pvp only

        return (int) (mainHand ? damage : (damage / 2));
    }


    /**
     * Checks if the character is currently casting
     * @return is casting
     */
    public boolean isCasting() {
        return castingAbility != null;
    }

    /**
     * Interrupts the Casting for the Character
     */
    public void interruptCasting(boolean cooldown) {
        if (isPlayer()) {
            if (castingAbility != null) {
                castingAbility.on_canceled(new PlayerAPI(getPlayer()));
                setCastingAbility(null, null, 0);
                sendMessage(PacketOuterClass.Opcode.SMSG_CANCEL_CAST);
                getCharacter().component(ActorComponents.ANIMATION).stopAnimation();
                //Sets the ability on cooldown.
                if (cooldown) {
                    AbilityUDataTable table = AbilityUDataTable.forName(castingAbility.name());
                    if (table != null) {
                        if (table.getCoolDownTime() > 0)
                            setAbilityCooldown(table.getId(), table.getCoolDownTime());
                    }
                }
                return;
            }
        }
        //TODO NPC
    }

    /**
     * Stuns the Character for a Specific Duration
     * @param duration
     */
    public float stun(float duration) {
        if (stunnedCount >= MAXIMUM_CCDR) //We are DR'd.
            return 0;
        if (stunnedCount == 0)
            setStunnedDRDuration(TimeUtility.longToSeconds(STUN_DR_DURATION));
        duration -= (stunnedCount * 1.5);
        stunnedCount++;
        setStunnedDuration(TimeUtility.longToSeconds(duration));
        component(ActorComponents.ANIMATION).stopAnimation();
        DelayedTaskTicker.delayTask(() -> {
            component(ActorComponents.TRANS_VAR).setVarInt("crowd_control", 0);
        }, duration);
        return duration;
    }

    /**
     * Used for stopping the current stun
     */
    public void stopStunned() {
        setStunnedDuration(0);
        component(ActorComponents.TRANS_VAR).setVarInt("crowd_control", 0);
    }

    /**
     * Checks if character is stunned
     * @return is stunned
     */
    public boolean isStunned() {
        return System.currentTimeMillis() < getStunnedDuration();
    }

    /**
     * Locks Magical Abilities for a Specific Duration
     * @param duration
     */
    public float magicLock(float duration) {
        if (magicLockedCount >= MAXIMUM_CCDR) //We are DR'd.
            return 0;
        if (magicLockedCount == 0)
            setSnaredDRDuration(TimeUtility.longToSeconds(MAGIC_LOCKED_DR_DURATION));
        duration -= (magicLockedCount * 1.5);
        magicLockedCount++;
        setMagicLockedDuration(TimeUtility.longToSeconds(duration));
        component(ActorComponents.ANIMATION).stopAnimation();
        return duration;
    }

    /**
     * Used for stopping the current magic lock
     */
    public void stopMagicLocked() {
        setMagicLockedDuration(0);
        component(ActorComponents.TRANS_VAR).setVarInt("crowd_control", 0);
    }

    /**
     * Checks if character is Magic Locked
     * @return is magic locked
     */
    public boolean isMagicLocked() {
        return System.currentTimeMillis() < getMagicLockedDuration();
    }

    /**
     * Snares the character for the specified duration.
     * @param duration
     */
    public float snare(float duration) {
        if (snaredCount >= MAXIMUM_CCDR) //We are DR'd.
            return 0;
        if (snaredCount == 0)
            setSnaredDRDuration(TimeUtility.longToSeconds(SNARE_DR_DURATION));
        duration -= (snaredCount * 1.5);
        snaredCount++;
        setSnaredDuration(TimeUtility.longToSeconds(duration));
        component(ActorComponents.ANIMATION).stopAnimation();
        return duration;
    }

    /**
     * Used for stopping the current snare
     */
    public void stopSnare() {
        setSnaredDuration(0);
        component(ActorComponents.TRANS_VAR).setVarInt("crowd_control", 0);
    }

    /**
     * Checks if the Character is snared
     * @return is snared
     */
    public boolean isSnared() {
        return System.currentTimeMillis() < getSnaredDuration();
    }

    /**
     * Disorients the Character for the specified duration
     * @param duration
     */
    public float disorient(float duration) {
        if (disorientedCount >= MAXIMUM_CCDR) //We are DR'd.
            return 0;
        if (disorientedCount == 0)
            setDisorientedDRDuration(TimeUtility.longToSeconds(DISORIENT_DR_DURATION));
        duration -= (disorientedCount * 1.5);
        disorientedCount++;
        setDisorientedDuration(TimeUtility.longToSeconds(duration));
        component(ActorComponents.ANIMATION).stopAnimation();
        //TODO invert controls
        if (isPlayer()) {
            //sendMessage(Opcode.SMSG_REVERSE_MOVEMENT_CONTROLS, World.ReverseMovementControls.newBuilder().setBackwards(true).build());
        }
        return duration;
    }

    /**
     * Used for stopping the current disorient
     */
    public void stopDisorient() {
        setDisorientedDuration(0);
        component(ActorComponents.TRANS_VAR).setVarInt("crowd_control", 0);
        //sendMessage(Opcode.SMSG_REVERSE_MOVEMENT_CONTROLS, World.ReverseMovementControls.newBuilder().setBackwards(false).build());
    }

    /**
     * Checks if the Character is Disoriented
     * @return is disoriented
     */
    public boolean isDisoriented() {
        return System.currentTimeMillis() < getDisorientedDuration();
    }

    /**
     * Disarms the Character for the specified duration
     * @param duration
     */
    public float disarm(float duration) {
        if (disarmedCount >= MAXIMUM_CCDR) //We are DR'd.
            return 0;
        if (disarmedCount == 0)
            setDisarmedDRDuration(TimeUtility.longToSeconds(DISARM_DR_DURATION));
        duration -= (disarmedCount * 1.5);
        disarmedCount++;
        setDisarmedDuration(TimeUtility.longToSeconds(duration));
        component(ActorComponents.ANIMATION).stopAnimation();
        if (isPlayer()) {
            //This will hide the weapon while disarmed.
            getPlayer().component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);

            //Should spawn the item into the world.
           /* Character target = getTargets().get(VariableUtility.random(0, getTargets().size()));
            int mainHandId = container(Containers.EQUIPMENT).getEqipmentItemIdForSlot(EquipmentSlot.MAIN_HAND);
            int offHandId = container(Containers.EQUIPMENT).getEqipmentItemIdForSlot(EquipmentSlot.OFF_HAND);
            if (mainHandId > 0 || offHandId > 0) {
                WorldObject object = new WorldObject(4, target.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation());
                object.setAsItemId(mainHandId == 0 ? offHandId : mainHandId);
            }*/

            //This will show the weapon once the disarm is done.
        }
        return duration;
    }

    /**
     * Used for stopping the current disarm
     */
    public void stopDisarm() {
        setDisarmedDuration(0);
        component(ActorComponents.TRANS_VAR).setVarInt("crowd_control", 0);
    }

    /**
     * Checks if the Character is Disarmed
     * @return is disarmed
     */
    public boolean isDisarmed() {
        return System.currentTimeMillis() < getDisarmedDuration();
    }

    public boolean isInvulnerable() {
        if (isPlayer()){
            if (System.currentTimeMillis() < component(ActorComponents.MOVEMENT).getRollInvulnerability())
                return true;
        }
        return false;
    }

    @Override
    public void onClearedFlags() {
        super.onClearedFlags();
        hits.clear();
    }

    @Override
    public void onDeath(Actor source) {
        super.onDeath(source);

        if (source != null) {
            source.component(CharacterComponents.COMBAT).removeDeadTargets();
            source.component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
        }

        if (getTarget() != null) {
            getTarget().component(CharacterComponents.COMBAT).removeDeadTargets();
            getTarget().component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
        }

        for (Character t : getTargets().keySet()) {
            if (t == null) continue;
            t.component(CharacterComponents.COMBAT).removeDeadTargets();
            t.component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
        }

        setTarget(null);
        getTargets().clear();
        getHits().clear();
        component(CharacterComponents.BUFF).removeDebuffs();
        component(CharacterComponents.BUFF).removeBuffs();
    }

    public void removeDeadTargets() {
        if (getTarget() != null) {
            if (getTarget().getState() != ActorState.ALIVE) {
                getTargets().remove(getTarget());
                setTarget(null);
            }
        }
        synchronized (getTargets()) {
            for (Character t : getTargets().keySet()) {
                if (t.getState() != ActorState.ALIVE)
                    getTargets().remove(t);
            }

        }
    }

    /**
     * Checks if the specified character is a target
     * @param character
     * @return a target
     */
    public boolean isTarget(Character character) {
        if (getTarget() != null && getTarget().equals(character))
            return true;
        return getTargets().containsKey(character);
    }

    /**
     * Adds a bonus to the current bonuses
     * @param type
     * @param amount
     */
    public void increaseBonuses(StatTypes type, int amount) {
        getBonuses().put(type, getBonuses().get(type) + amount);
    }

    /**
     * Sets the bonus type to 0
     * @param type
     */
    public void clearBonuses(StatTypes type) {
        getBonuses().put(type, 0);
    }

    /**
     * Removes bonuses by amount, cannot go less than 0
     * @param type
     * @param amount
     */
    public void decreaseBonuses(StatTypes type, int amount) {
        getBonuses().put(type, getBonuses().get(type) - amount);
    }

    /**
     * Sets the Hostility Level & Flags for Update
     * @param hostilityLevel
     */
    public void setHostilityLevel(HostilityLevel hostilityLevel) {
        this.hostilityLevel = hostilityLevel;
        getCharacter().component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
    }

    public int getStamina() {
        if (isPlayer()) {
            PlayerCombatComponent combat = (PlayerCombatComponent) getPlayer().component(CharacterComponents.COMBAT);
            return combat.getStatLevelForExperience(getVars().getVarInt("xp_stamina")) + getBonuses().get(StatTypes.STAMINA);
        }
        return getNPC().getDataTable().getStamina();
    }

    public int  getMaxStamina() {
        if (isPlayer())
            return component(ActorComponents.VAR).getVarInt("max_stamina") + getBonuses().get(StatTypes.STAMINA);//TODO figure this part out
        return getNPC().getDataTable().getStamina() + getBonuses().get(StatTypes.STAMINA);
    }

    public int getEnergy() {
        if (isPlayer())
            return component(ActorComponents.VAR).getVarInt("energy");
        return getNPC().getDataTable().getEnergy();
    }

    public int getMaxEnergy() {
        if (isPlayer())
            return component(ActorComponents.VAR).getVarInt("max_energy") + getBonuses().get(StatTypes.ENERGY);//TODO figure this part out.
        return getNPC().getDataTable().getEnergy() + getBonuses().get(StatTypes.ENERGY);
    }

    public int getAccuracy() {
        if (isPlayer()) {
            PlayerCombatComponent combat = (PlayerCombatComponent) getPlayer().component(CharacterComponents.COMBAT);
            return combat.getStatLevelForExperience(getVars().getVarInt("xp_accuracy")) + getBonuses().get(StatTypes.ACCURACY);
        }
        return getNPC().getDataTable().getAccuracy() + + getBonuses().get(StatTypes.ACCURACY);
    }

    public int getStrength() {
        if (isPlayer()) {
            PlayerCombatComponent combat = (PlayerCombatComponent) getPlayer().component(CharacterComponents.COMBAT);
            return combat.getStatLevelForExperience(getVars().getVarInt("xp_strength")) + getBonuses().get(StatTypes.STRENGTH);
        }
        return getNPC().getDataTable().getStrength() + getBonuses().get(StatTypes.STRENGTH);
    }

    public int getFocus() {
        if (isPlayer()) {
            PlayerCombatComponent combat = (PlayerCombatComponent) getPlayer().component(CharacterComponents.COMBAT);
            return combat.getStatLevelForExperience(getVars().getVarInt("xp_focus")) + getBonuses().get(StatTypes.FOCUS);
        }
        return 0;
    }

    public int getIntellect() {
        if (isPlayer()) {
            PlayerCombatComponent combat = (PlayerCombatComponent) getPlayer().component(CharacterComponents.COMBAT);
            return combat.getStatLevelForExperience(getVars().getVarInt("xp_intellect")) + getBonuses().get(StatTypes.INTELLECT);
        }
        return 0;
    }

    public int getResilience() {
        if (isPlayer()) {
            PlayerCombatComponent combat = (PlayerCombatComponent) getPlayer().component(CharacterComponents.COMBAT);
            return combat.getStatLevelForExperience(getVars().getVarInt("xp_resilience")) + getBonuses().get(StatTypes.RESILIENCE);
        }
        return getNPC().getDataTable().getResilience() + + getBonuses().get(StatTypes.RESILIENCE);
    }

    public int getCriticalStrikeChance() {
        if(isPlayer())
            return container(Containers.EQUIPMENT).getCriticalStrikeChance() + getBonuses().get(StatTypes.CRITICAL_STRIKE);
        return getNPC().getDataTable().getCriticalStrikeChance() + getBonuses().get(StatTypes.CRITICAL_STRIKE);
    }

    public int getMeleeResistance() {
        if (isPlayer())
            return getPlayer().container(Containers.EQUIPMENT).getMeleeResistance() + getBonuses().get(StatTypes.MELEE_RESISTANCE);
        return getNPC().getDataTable().getMeleeResistance() + getBonuses().get(StatTypes.MELEE_RESISTANCE);
    }

    public int getArrowResistance() {
        if (isPlayer())
            return getPlayer().container(Containers.EQUIPMENT).getArcheryResistance() + getBonuses().get(StatTypes.ARCHERY_RESISTANCE);
        return getNPC().getDataTable().getArrowResistance() + getBonuses().get(StatTypes.ARCHERY_RESISTANCE);
    }

    public int getMagicResistance() {
        if (isPlayer())
            return getPlayer().container(Containers.EQUIPMENT).getMagicResistance() + getBonuses().get(StatTypes.MAGIC_RESISTANCE);
        return getNPC().getDataTable().getMagicResistance() + getBonuses().get(StatTypes.MAGIC_RESISTANCE);
    }

}
