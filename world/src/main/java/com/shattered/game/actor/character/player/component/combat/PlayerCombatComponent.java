package com.shattered.game.actor.character.player.component.combat;

import com.shattered.account.Account;
import com.shattered.datatable.UDataTableRepository;
import com.shattered.datatable.tables.AbilityUDataTable;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.Character;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.components.combat.CharacterCombatComponent;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.channel.ChannelComponents;
import com.shattered.game.actor.character.player.component.container.equipment.EquipmentSlot;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.flags.FlagType;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.object.item.Item;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.WorldProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.script.ScriptManager;
import com.shattered.script.api.impl.PlayerAPI;
import com.shattered.script.types.AbilityScript;
import com.shattered.utilities.TimeUtility;
import com.shattered.utilities.ecs.ProcessComponent;
import com.shattered.utilities.ecs.ProcessInterval;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author JTlr Frost 11/5/2019 : 1:03 AM
 */
@ProcessComponent(interval = 0.1f)
public class PlayerCombatComponent extends CharacterCombatComponent {


    /**
     * Represents a map of projectiles that are outgoing
     * Integer represents their uuid.
     */
    @Getter
    private final Map<Integer, AbilityScript> ongoingProjectiles = new ConcurrentHashMap<>();

    /**
     * Represents if the player is aim blocking or not.
     */
    @Getter
    private boolean aimBlocking;

    private long defaultAttackCooldown;

    private int attackCounter;

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public PlayerCombatComponent(Object gameObject) {
        super(gameObject);
        cooldowns.put(0, System.currentTimeMillis());

        getVars().setVarInt("health", 250, true);
        getVars().setVarInt("max_health", 250, true);
    }

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
        super.onStart();
        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_SELECT_AREA_TELEGRAPH, new WorldProtoListener<World.SelectedAreaTelegraph>() {

            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(World.SelectedAreaTelegraph message, Player player) {
                PlayerCombatComponent combat = (PlayerCombatComponent) player.component(CharacterComponents.COMBAT);
                GridCoordinate coordinate = new GridCoordinate(message.getLocation().getX(), message.getLocation().getY(), message.getLocation().getZ());
                combat.onAbilityRequest(message.getAbilityId(), coordinate, 0);
            }
        }, World.SelectedAreaTelegraph.getDefaultInstance());


        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_TOGGLE_SHEATHE, new WorldProtoListener<PacketOuterClass.EmptyPayload>() {
            @Override
            public void handle(PacketOuterClass.EmptyPayload message, Player player) {
                PlayerCombatComponent combat = (PlayerCombatComponent) player.component(CharacterComponents.COMBAT);
                combat.sheathe();
            }
        }, PacketOuterClass.EmptyPayload.getDefaultInstance());

        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_SET_TARGET, new WorldProtoListener<World.SetTarget>() {

            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(World.SetTarget message, Player player) {
                int clientIndex = message.getClientIndex();

                if (clientIndex << 1 == 0) {
                    player.component(CharacterComponents.COMBAT).setTarget(null);
                    return;
                }

                boolean isPlayer = (clientIndex & 1) == 1;
                Character target;
                if (isPlayer)
                    target = player.component(PlayerComponents.CHARACTER_SYNCHRONIZE).getLocal().get(clientIndex >> 1);
                else
                    target = player.component(PlayerComponents.NPC_SYNCHRONIZE).getLocal().get(clientIndex >> 1);

                player.component(CharacterComponents.COMBAT).setTarget(target);
            }
        }, World.SetTarget.getDefaultInstance());

        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_AUTO_ATTACK, new WorldProtoListener<PacketOuterClass.EmptyPayload>() {
            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(PacketOuterClass.EmptyPayload message, Player player) {
                if (message == null)
                    System.out.println("oh fuck");
                if (player == null)
                    System.out.println("oh fuck me harder");
                PlayerCombatComponent combat = (PlayerCombatComponent) player.component(CharacterComponents.COMBAT);
                if (combat == null)
                    System.out.println("hmmm");
                combat.onAutoAttack();
                /*try {
                    PlayerCombatComponent combat = (PlayerCombatComponent) player.component(CharacterComponents.COMBAT);
                    combat.onAutoAttack();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        }, PacketOuterClass.EmptyPayload.getDefaultInstance());

        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_IS_AIMBLOCKING, new WorldProtoListener<PacketOuterClass.EmptyPayload>() {
            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(PacketOuterClass.EmptyPayload message, Player player) {
                PlayerCombatComponent combat = (PlayerCombatComponent) player.component(CharacterComponents.COMBAT);
                combat.onAimBlock();
            }
        }, PacketOuterClass.EmptyPayload.getDefaultInstance());

        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_NOT_AIMBLOCKING, new WorldProtoListener<PacketOuterClass.EmptyPayload>() {
            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(PacketOuterClass.EmptyPayload message, Player player) {
                PlayerCombatComponent combat = (PlayerCombatComponent) player.component(CharacterComponents.COMBAT);
                combat.onReleaseAimBlock();
            }
        }, PacketOuterClass.EmptyPayload.getDefaultInstance());


    }


    @Override
    public void onWorldAwake() {
        super.onWorldAwake();
        getVars().setVarInt("health", 250, true);
        getVars().setVarInt("max_health", 250, true);
        getVars().setVarInt("max_energy", 250, true);
        getVars().setVarInt("energy", 250, true);

    }

    /**
     * Toggles combat stance on/off
     */
    private void sheathe() {
        if (getPlayer().getState() == ActorState.DEAD)
            return;
        boolean sheathed = getVars().getVarBool("sheathed");
        getVars().setVarBool("sheathed", !sheathed, true);
    }

    /**
     *
     * @param actionId
     * @param pitch
     */
    public void onAbilityRequest(int actionId, float pitch) {
        onAbility(actionId, null, pitch);
    }

    /**
     *
     * @param actionId
     * @param coordinate
     * @param pitch
     */
    public void onAbilityRequest(int actionId, GridCoordinate coordinate, float pitch) {
        onAbility(actionId, coordinate, pitch);
    }

    public void onAutoAttack() {

            //If casting it while in a global cooldown
            /*if (System.currentTimeMillis() < cooldowns.get(0))
                return;

            boolean isBow = getPlayer().container(Containers.EQUIPMENT).getEquipmentFromSlot(EquipmentSlot.MAIN_HAND).getTable().getName().toLowerCase().contains("bow");


            // if (getPlayer().container(Containers.EQUIPMENT).getEquipmentFromSlot(EquipmentSlot.MAIN_HAND).get)

            //This is just for the bow, need to  append for all weapon types

            if (!isBow) {

                if (System.currentTimeMillis() > defaultAttackCooldown && attackCounter > 0) {

                    attackCounter = 0;
                    defaultAttackCooldown = 0;
                }

                switch (attackCounter) {
                    case 0:
                        ScriptManager.useAbility(new PlayerAPI(getPlayer()), "warrior attack1");
                        attackCounter++;
                        defaultAttackCooldown = TimeUtility.duration(TimeUnit.SECONDS, 2);
                        break;
                    case 1:
                        ScriptManager.useAbility(new PlayerAPI(getPlayer()), "warrior attack2");
                        attackCounter = 0;
                        break;
                }
            }*/
            //setGlobalCooldown();
            //Bow
            if (true) {
                //ScriptManager.useAbility(new PlayerAPI(getPlayer()), "quickfire");
                // getPlayer().component(CharacterComponents.COMBAT).sendProjectile("arrow", getPlayer().component(CharacterComponents.COMBAT).getTarget(), 3, 300.f);

                if (getPlayer() == null)
                    System.out.println("oh no");
                if (getPlayer().component(CharacterComponents.COMBAT) == null)
                    System.out.println("oh what the fuck");
                if (getPlayer().component(CharacterComponents.COMBAT).getTarget() == null)
                    System.out.println("HMMMMM");
                getPlayer().component(CharacterComponents.COMBAT).hitCharacter(getPlayer().component(CharacterComponents.COMBAT).getTarget(), 30, 0f, null);
                /*if (getPlayer().component(CharacterComponents.COMBAT).getTarget() != null) {
                    getPlayer().component(CharacterComponents.COMBAT).hitCharacter(getPlayer().component(CharacterComponents.COMBAT).getTarget(), 30, 0f, null);
                }*/
            }
    }

    public void onAimBlock() {
        setAimBlocking(true);
        /*if (getPlayer().container(Containers.EQUIPMENT).getMainHandName().contains("bow")) {

            //set blendspace
        } else {
            //Render blocking animation
            //Set is blocking combat var
        }*/
    }

    public void onReleaseAimBlock() {
        setAimBlocking(false);
        //Release blendspace
        //Set is not blocking combat var
    }


    /**
     * Method called upon client requesting an ability after pressing the action id
     * @param actionId
     */
    private void onAbility(int actionId, GridCoordinate coordinate, float pitch) {
        if (getPlayer().getState() == ActorState.DEAD)
            return;

        //Ensures it's a valid registered ability
        AbilityUDataTable abilityUDataTable = UDataTableRepository.getAbilityDataTable().get(actionId);
        if (abilityUDataTable == null)
            return;

        //If casting it while in a global cooldown
        if (System.currentTimeMillis() < cooldowns.get(0) && abilityUDataTable.isEffectedByGlobalCooldown())
            return;

        //Checks if there is a cooldown currently on-going
        if (cooldowns.containsKey(actionId)) {//TODO charges
            if (System.currentTimeMillis() < cooldowns.get(actionId))
                return;
        }

        //TODO check of coordinate is in LOS / near by.


        if (ScriptManager.useAbility(new PlayerAPI(getPlayer()), abilityUDataTable.getName().replace("_", " ").toLowerCase(), coordinate, pitch) && abilityUDataTable.getCastTime() <= 0)
        {
            if (abilityUDataTable.getCoolDownTime() > 0)
                setAbilityCooldown(actionId, abilityUDataTable.getCoolDownTime());

            if (abilityUDataTable.isAffectsGlobalCooldown())
                setGlobalCooldown();
        }

    }

    public boolean isDueling() {
        return getCharacter().component(ActorComponents.TRANS_VAR).getVarInt("dueled_puuid") != -1;
    }

    /**
     * Gets the Current Combat Level
     * @param includeGrimoire
     * @return the combat level
     */
    public int getCombatLevel(boolean includeGrimoire) {
        float combatLevel = 1;
        float accuracy = getStatLevelForExperience(component(ActorComponents.VAR).getVarInt("xp_accuracy")) * 0.125f;
        float strength = getStatLevelForExperience(component(ActorComponents.VAR).getVarInt("xp_strength")) * 0.125f;
        float stamina = getStatLevelForExperience(component(ActorComponents.VAR).getVarInt("xp_stamina")) * 0.136f;
        float resilience = getStatLevelForExperience(component(ActorComponents.VAR).getVarInt("xp_resilience")) * 0.136f;
        float focus = getStatLevelForExperience(component(ActorComponents.VAR).getVarInt("xp_focus")) * 0.125f;
        float intellect = getStatLevelForExperience(component(ActorComponents.VAR).getVarInt("xp_intellect")) * 0.125f;
        combatLevel += accuracy + strength + stamina + resilience + focus + intellect/* + GRIMOIRE_LEVEL + GRIMOIRE2_LEVEL*/;//TODO
        if (includeGrimoire) {

            Item grimoire1 = getPlayer().container(Containers.EQUIPMENT).getEquipmentFromSlot(EquipmentSlot.GRIMOIRE_1);
            if (grimoire1 != null)
                combatLevel += getGrimoireLevelForExpereince(component(ActorComponents.VAR).getVarInt("xp_" + grimoire1.getName().replace(" Grimoire", "").replace(" ", "_")));

            Item grimoire2 = getPlayer().container(Containers.EQUIPMENT).getEquipmentFromSlot(EquipmentSlot.GRIMOIRE_2);
            if (grimoire2 != null)
                combatLevel += getGrimoireLevelForExpereince(component(ActorComponents.VAR).getVarInt("xp_" + grimoire2.getName().replace(" Grimoire", "").replace(" ", "_")));
        }
        return (int) combatLevel;
    }

    /**
     * Sets the Aimblocking for the Player
     * @param aimBlocking
     */
    public void setAimBlocking(boolean aimBlocking) {
        this.aimBlocking = aimBlocking;
        component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
    }

    /**
     * Gets the Level for Experience
     * @param experience
     * @return
     */
    public int getStatLevelForExperience(int experience) {
        for (int i = 70; i >= 2; i--)
            if (experience >= getStatExperienceForLevel(i)) {
                return i;
            }
        return 1;
    }

    /**
     * Gets the Experience from Level
     * @param level
     * @return experience
     */
    public int getStatExperienceForLevel(int level) {
        int xpNeeded = 0;
        double baseValue = 28;
        if (level == 1)
            return (int) baseValue;
        for (int i = 2; i <= level; i++)
            xpNeeded += Math.floor(baseValue = ((baseValue) * 1.144));
        return (int) xpNeeded;
    }

    /**
     * Gets the Experience from Level
     * @param level
     * @return experience
     */
    public int getGrimoireExperienceForLevel(int level) {
        int xpNeeded = 0;
        double baseValue = 255;
        if (level == 1)
            return (int) baseValue;
        for (int i = 2; i <= level; i++)
            xpNeeded += Math.floor(baseValue = ((baseValue) * 1.85));
        return (int) xpNeeded;
    }

    public int getGrimoireLevelForExpereince(int currentXp) {
        for (int i = 15; i >= 2; i--)
            if (currentXp >= getGrimoireExperienceForLevel(i)) {
                return i;
            }
        return 1;
    }

    public void forfeit(Player other) {

    }


    @Override
    public void onTick(long deltaTime) {
        super.onTick(deltaTime);
        if (getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).isInParty()) {
            getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).refreshValues();
        }
    }


    @Override
    public void onDeath(Actor source) {
        super.onDeath(source);
        if (isDueling()) {
            int puuid = getCharacter().component(ActorComponents.TRANS_VAR).getVarInt("dueled_puuid");
            Player target = getPlayer().component(PlayerComponents.CHARACTER_SYNCHRONIZE).getLocal().get(puuid);
            if (target != null) {
                target.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You have defeated " + getCharacter().getName() + " in a duel.");
                getCharacter().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage(target.getName() + " has defeated you in a duel.");
                target.component(ActorComponents.TRANS_VAR).setVarInt("dueled_puuid", 0);
                target.component(ActorComponents.TRANS_VAR).setVarBool("dueling", false);
            }
            getCharacter().component(ActorComponents.TRANS_VAR).setVarInt("dueled_puuid", 0);
            getCharacter().component(ActorComponents.TRANS_VAR).setVarBool("dueling", false);
        }
    }
}
