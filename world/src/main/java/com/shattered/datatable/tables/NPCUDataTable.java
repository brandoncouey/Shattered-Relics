package com.shattered.datatable.tables;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.game.actor.character.components.combat.CharacterCombatComponent;
import com.shattered.game.actor.container.loot.PossibleLoot;
import com.shattered.system.SystemLogger;
import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author JTlr Frost 11/1/2019 : 11:16 PM
 */
@Data
public class NPCUDataTable {

    /**
     * Represents the Name of the NPC Id
     */
    private int id = -1;

    /**
     * Represents the Name of the NPC
     */
    private String name = "Unavailable";

    /**
     * Represents if a Hummanoid
     */
    private boolean hummanoid;

    /**
     * Represents if the NPC is skinnable
     */
    private boolean skinnable;

    /**
     * Represents if a NPC Randomly Walks
     */
    private boolean randomlyWalks;

    /**
     * Represents the Walk Speed
     */
    private int speed;

    /**
     * Represents the Mesh Offset
     */
    private int meshOffset;

    /**
     * Represents the Respawn Rate (Death to Spawn)
     */
    private int respawnRate;

    /**
     * Represents the Death Animation Id
     */
    private String deathAnimationName;

    /**
     * Represents the Death Sound Effect
     */
    private String deathSoundEffect;

    /**
     * Represents the Hostility Level
     */
    private CharacterCombatComponent.HostilityLevel hostilityLevel = CharacterCombatComponent.HostilityLevel.FRIENDLY;

    /**
     * Represents the amount of xp given on death
     */
    private int xp;

    /**
     * Represents the possible loot for a npc
     */
    private List<PossibleLoot> possibleLoot;

    /**
     * Represents the Aggro Radius
     */
    private int aggroRadius = 200;

    /**
     * Represents the NPC Stamina Bonus
     */
    private int stamina = 100;

    /**
     * Represents the NPC Energy Bonus
     */
    private int energy;

    /**
     * Represents the Critical Strike Chance
     */
    private int criticalStrikeChance;

    /**
     * Represents the NPC Accuracy Bonus
     */
    private int accuracy;

    /**
     * Represents the NPC Strength Bonus
     */
    private int strength;

    /**
     * Represents the NPC Resilience Bonus
     */
    private int resilience;

    /**
     * Represents the Melee Resistance
     */
    private int meleeResistance;

    /**
     * Represents the Arrow Resistance
     */
    private int arrowResistance;

    /**
     * Represents the Magic Resistance
     */
    private int magicResistance;

    /**
     * Parses the Item UDataTables.
     */
    public static void parse() {
        try {
            Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "npc_dt.json"));

            // typecasting obj to JSONObject
            JSONArray items = (JSONArray) dataTable;

            int tables = 0;
            int dropTables = 0;

            for (int index = 0; index < items.size(); index++) {

                JSONObject entry = (JSONObject) items.get(index);
                int id =  index + 1;
                String name = (String) entry.get("Name");
                boolean hummanoid = (Boolean) entry.get("Hummanoid");
                long meshOffset = (Long) entry.get("MeshOffset");
                long respawnRate = (Long) entry.get("RespawnTime");
                long speed = (Long) entry.get("DefaultSpeed");
                String deathAnim = (String) entry.get("DeathAnimation");
                String deathSFX = (String) entry.get("DeathSoundEffect");
                boolean randomlyWalks = (Boolean) entry.get("RandomlyWalks");
                boolean skinnable = (Boolean) entry.get("Skinnable");
                String hostilityLevel = (String) entry.get("HostiltiyLevel");
                long aggroRadius = (Long) entry.get("AggroRadius");
                long stamina = (Long) entry.get("Stamina");
                long energy = (Long) entry.get("Energy");
                long critChance = (Long) entry.get("CriticalStrikeChance");
                long accuracy = (Long) entry.get("Accuracy");
                long strength = (Long) entry.get("Strength");
                long resilience = (Long) entry.get("Resilience");
                long meleeResistance = (Long) entry.get("MeleeResistance");
                long archeryResistance = (Long) entry.get("ArcheryResistance");
                long magicResistance = (Long) entry.get("MagicResistance");
                long xp = (Long) entry.get("XP");

                NPCUDataTable table = new NPCUDataTable();
                table.setId(id);
                table.setName(name);
                table.setHummanoid(hummanoid);
                table.setMeshOffset((int) meshOffset);
                table.setRespawnRate((int) respawnRate);
                table.setDeathAnimationName(deathAnim);
                table.setDeathSoundEffect(deathSFX);
                table.setRandomlyWalks(randomlyWalks);
                table.setSkinnable(skinnable);
                table.setSpeed((int) speed);
                table.setHostilityLevel(CharacterCombatComponent.HostilityLevel.forName(hostilityLevel));
                table.setAggroRadius((int) aggroRadius);
                table.setStamina((int) stamina);
                table.setEnergy((int) energy);
                table.setCriticalStrikeChance((int) critChance);
                table.setAccuracy((int) accuracy);
                table.setStrength((int) strength);
                table.setResilience((int) resilience);
                table.setMeleeResistance((int) meleeResistance);
                table.setArrowResistance((int) archeryResistance);
                table.setMagicResistance((int) magicResistance);
                table.setXp((int) xp);


                // typecasting obj to JSONObject
                JSONArray possibleLoot = (JSONArray) entry.get("PossibleLoot");
                List<PossibleLoot> allLoot = new ArrayList<>();
                for (int lIndex = 0; lIndex < possibleLoot.size(); lIndex++) {
                    JSONObject loot = (JSONObject) possibleLoot.get(lIndex);
                    String item = (String) loot.get("ItemName");
                    long minAmount = (Long) loot.get("MinAmount");
                    long maxAmount = (Long) loot.get("MaxAmount");
                    long dropChance = (Long) loot.get("DropChance");
                    allLoot.add(new PossibleLoot(item, (int) minAmount, (int) maxAmount, (float) dropChance));
                }
                if (possibleLoot.size() > 0)
                    dropTables++;
                table.setPossibleLoot(allLoot);

                UDataTableRepository.getNpcDataTable().put(id, table);
                UDataTableRepository.getNpcNameReferenceTable().put(name.replace("_", " ").toLowerCase(), id);
                tables++;
            }

            SystemLogger.sendSystemMessage("Successfully parsed " + dropTables + " npc drop tables.");
            SystemLogger.sendSystemMessage("Successfully parsed " + tables + " npc data tables.");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets the NPC Data Table for the Name
     * @param name
     * @return
     */
    public static NPCUDataTable forName(String name) {
        String npc = name;
        if (npc.contains("_"))
            npc = name.replace("_", " ");
        if (!UDataTableRepository.getNpcNameReferenceTable().containsKey(npc.toLowerCase()))
            return null;
        return UDataTableRepository.getNpcDataTable().get(UDataTableRepository.getNpcNameReferenceTable().get(npc.toLowerCase()));
    }

}
