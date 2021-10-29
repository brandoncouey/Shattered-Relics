package com.shattered.game.actor.character.components.buff;

import com.shattered.datatable.tables.BuffUDataTable;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.Character;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.component.WorldComponent;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.script.ScriptManager;
import com.shattered.script.api.RelicCharacterAPI;
import com.shattered.script.api.impl.NpcAPI;
import com.shattered.script.api.impl.PlayerAPI;
import com.shattered.script.types.BuffScript;
import com.shattered.utilities.ecs.ProcessComponent;
import com.shattered.utilities.ecs.ProcessInterval;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ProcessComponent(interval = 1.f)
public class CharacterBuffComponent extends WorldComponent {

    /**
     * Represents a collection of all current buffs
     */
    @Getter
    private final Map<String, BuffScript> buffs = new ConcurrentHashMap<>();

    /**
     * Represents a collection of all current debuffs
     */
    @Getter
    private final Map<String, BuffScript> debuffs = new ConcurrentHashMap<>();

    /**
     * Creates a new constructor setting the {@link Character}
     *
     * @param gameObject
     */
    public CharacterBuffComponent(Object gameObject) {
        super(gameObject);
    }

    /**
     * Adds a buff with the specified name  and duration
     *      and default stacks set to 1.
     * @param name
     * @param duration
     */
    public void addBuff(String name, int duration) {
        addBuff(name, null, 1, duration);
    }

    /**
     * Adds a buff with the specified name  and duration
     *      and default stacks set to 1.
     * @param name
     * @param duration
     */
    public void addBuff(String name, Character source, int duration) {
        addBuff(name, source, 1, duration);
    }

    /**
     * Adds a buff with the specified name, stacks and duration
     * @param name
     * @param stacks
     * @param duration
     */
    public void addBuffAPI(String name, RelicCharacterAPI source, int stacks, float duration) {
        addBuff(name, (Character) source.getActor(), stacks, duration);
    }

    /**
     * Adds a buff with the specified name, stacks and duration
     * @param name
     * @param stacks
     * @param duration
     */
    public void addBuff(String name, Character source, int stacks, float duration) {
        String formattedName = name.toLowerCase().replace("_", " ");
        BuffScript script = ScriptManager.getBuffScript(name);
        BuffUDataTable dataTable = BuffUDataTable.forName(name);
        if (dataTable == null) return;
        if (script == null) return;
        try {
            BuffScript newInstance = script.getClass().newInstance();
            newInstance.setCharacter(isNPC() ? new NpcAPI(getNPC()) : new PlayerAPI(getPlayer()));
            newInstance.setStacks(stacks);
            newInstance.setDuration(duration);

            if (source instanceof NPC && source != null)
                newInstance.setSource(new NpcAPI((NPC) source));

            if (source instanceof Player && source != null)
                newInstance.setSource(new PlayerAPI((Player) source));

            addBuff(newInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a buff for the player
     * @param buff
     */
    private void addBuff(BuffScript buff) {
        if (buff.name() == null) return;
        BuffUDataTable table = BuffUDataTable.forName(buff.name());
        if (table == null) return;

        if (!buff.can_apply()) return;


        /*if (table.getStack() > 1) {
            if (buff.getStacks() + 1 > table.getStack()) {
                buff.setStacks(table.getStack());
            } else
                buff.setStacks(buff.getStacks() + 1);
        }*/

        if (table.getStack() > table.getStack())
            buff.setStacks(table.getStack());

        buff.on_applied();


        if (table.isDebuff())
            debuffs.put(buff.name().toLowerCase().replace("_", " "), buff);
        else
            buffs.put(buff.name().toLowerCase().replace("_", " "), buff);

        Character character = (Character) buff.getSource().getActor();
        if (character instanceof Player) {
            Player pSource = (Player) character;
            if (isPlayer())
                getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_ADUP_BUFF, World.AddBuff.newBuilder().setClientIndex(getCharacter().getClientIndex() << 1 | (isNPC() ? 0 : 1)).setId(table.getId()).setStacks(buff.getStacks()).setDuration(buff.getDuration()).build());
            pSource.sendMessage(PacketOuterClass.Opcode.SMSG_ADUP_BUFF, World.AddBuff.newBuilder().setClientIndex(getCharacter().getClientIndex() << 1 | (isNPC() ? 0 : 1)).setId(table.getId()).setStacks(buff.getStacks()).setDuration(buff.getDuration()).build());
        } else {
            if (isPlayer())
                getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_ADUP_BUFF, World.AddBuff.newBuilder().setClientIndex(getCharacter().getClientIndex() << 1 | 1).setId(table.getId()).setStacks(buff.getStacks()).setDuration(buff.getDuration()).build());
        }

        //This sends everyone whos attacking it the buff.
       /* getCharacter().component(CharacterComponents.COMBAT).getTargets().stream().filter(Objects::nonNull).filter(n -> n instanceof Player).forEach(n -> {
            Player player = (Player) n;
            player.sendMessage(PacketOuterClass.Opcode.SMSG_ADUP_BUFF, World.AddBuff.newBuilder().setClientIndex(getCharacter().getClientIndex() << 1 | (isNPC() ? 0 : 1)).setId(table.getId()).setStacks(buff.getStacks()).setDuration(buff.getDuration()).build());
        });*/

    }

    /**
     * Check if the character has a current buff
     * @param buff
     * @return has buff
     */
    public boolean hasBuff(String buff) {
        BuffUDataTable table = BuffUDataTable.forName(buff);
        if (table == null) return false;
        return table.isDebuff() ? debuffs.containsKey(buff.toLowerCase().replace("_", " ")) : buffs.containsKey(buff.toLowerCase().replace("_", " "));
    }

    /**
     * Removes the specified buff
     * @param buff
     */
    public void removeBuff(String buff) {
        BuffUDataTable table = BuffUDataTable.forName(buff);
        if (table == null)
            return;

        if (table.isDebuff()) {
            if (debuffs.containsKey(buff)) {
                debuffs.get(buff).on_finished();
                debuffs.remove(buff);
                sendMessage(PacketOuterClass.Opcode.SMSG_REMOVE_BUFF, World.RemoveBuff.newBuilder().setClientIndex(getCharacter().getClientIndex() << 1 | (isNPC() ? 0 : 1)).setId(table.getId()).build());
            }
        } else {

            if (buffs.containsKey(buff)) {
                buffs.get(buff).on_finished();
                buffs.remove(buff);
                sendMessage(PacketOuterClass.Opcode.SMSG_REMOVE_BUFF, World.RemoveBuff.newBuilder().setClientIndex(getCharacter().getClientIndex() << 1 | (isNPC() ? 0 : 1)).setId(table.getId()).build());
            }
        }
    }

    /**
     * Removes all the current buffs
     */
    public void removeBuffs() {
        synchronized (buffs) {
            for (BuffScript b : buffs.values())
                removeBuff(b.name());
        }
    }

    /**
     * Removes all the current buffs
     */
    public void removeDebuffs() {
        synchronized (debuffs) {
            for (BuffScript b : debuffs.values())
                removeBuff(b.name());
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onTick(long deltaTime) {
        super.onTick(deltaTime);

        if (!getCharacter().getState().equals(ActorState.ALIVE)) {
            removeDebuffs();
            removeBuffs();
            return;
        }

        for (BuffScript b : buffs.values()) {

            b.setDuration(b.getDuration() - 1);

            if (b.getDuration() <= 0) {
                removeBuff(b.name().toLowerCase().replace("_", " "));
                continue;
            }

            if (b.getTick() > 0) {
                b.setTick(b.getTick() - 1);

            } else {

                int tick = b.on_tick();
                b.setTick(tick);

                if (b.getDuration() <= 0)
                    removeBuff(b.name().toLowerCase().replace("_", " "));
            }

        }


        for (BuffScript db : debuffs.values()) {

            db.setDuration(db.getDuration() - 1);

            if (db.getDuration() <= 0) {
                removeBuff(db.name().toLowerCase().replace("_", " "));
                continue;
            }

            if (db.getTick() > 0) {
                db.setTick(db.getTick() - 1);

            } else {

                int tick = db.on_tick();
                db.setTick(tick);

                if (db.getDuration() <= 0)
                    removeBuff(db.name().toLowerCase().replace("_", " "));
            }

        }
    }

    /**
     *
     * @param source
     */
    @Override
    public void onDeath(Actor source) {
        super.onDeath(source);
        removeBuffs();
        removeDebuffs();
    }
}
