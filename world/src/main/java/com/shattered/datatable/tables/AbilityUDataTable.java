package com.shattered.datatable.tables;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.datatable.tables.types.QuestRequirement;
import com.shattered.datatable.tables.types.VariableRequirement;
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

/**
 * @author JTlr Frost 11/1/2019 : 11:11 PM
 */
@Data
public class AbilityUDataTable {


    /**
     * Represents the Item Id
     */
    private int id = -1;

    /**
     * Represents the Name of the Item
     */
    private String name = "Unavailable";

    /**
     * Represents the amount of energy the ability requires
     */
    private int energy;

    /**
     * Represents the amount of charges available for the ability
     */
    private int charges;

    /**
     * Represents the cast time for the ability in seconds (0 = instant)
     */
    private int castTime;

    /**
     * Represents the range the ability requires
     */
    private int range;

    /**
     * Represents the Radius of the ability
     */
    private int radius;

    /**
     * Represents the requirements to make the current item
     */
    private List<VariableRequirement> variableRequirements;

    /**
     * Represents the requirements to make the current item
     */
    private List<QuestRequirement> questRequirements;

    /**
     * Represents if the ability triggers global cooldown
     */
    private boolean affectsGlobalCooldown = true;

    /**
     * Represents if you can't use this ability while in global cooldown
     */
    private boolean effectedByGlobalCooldown = true;

    /**
     * Represents the cooldown time of the ability
     */
    private int coolDownTime;

    /**
     * Represents if you can use the ability while stunned
     */
    private boolean canUseWhileStunned;

    /**
     * Represents if you can use the ability while magic locked
     */
    private boolean canUseWhileMagicLocked = true;//Set true cause there are less magic abilities than normal.

    /**
     * Repesents if you can use the ability while disoriented
     */
    private boolean canUseWhileDisoriented;

    /**
     * Represents if you can use the ability while disarmed
     */
    private boolean canUseWhileDisarmed;

    /**
     * Represents if you can use the ability while snared
     */
    private boolean canUseWhleSnared = true;



    /**
     * Parses the Object UDataTables.
     */
    public static void parse() {
        try {
            Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "./repository/data_tables/ability_dt.json"));

            // typecasting obj to JSONObject
            JSONArray items = (JSONArray) dataTable;

            int tables = 0;

            for (int index = 0; index < items.size(); index++) {

                JSONObject entry = (JSONObject) items.get(index);
                int id =  index + 1;//This is the DT Row Name (Uses integers to count for the actual ID)
                String name = (String) entry.get("Name");
                AbilityUDataTable table = new AbilityUDataTable();
                table.setId(id);
                table.setName(name.replace("_", " "));
                table.setCharges((int) ((long) entry.get("Charges")));
                table.setCastTime((int) ((long) entry.get("CastTime")));
                table.setEnergy((int) ((long) entry.get("Energy")));
                table.setRange((int) ((long) entry.get("Range")));
                table.setRadius((int) ((long) entry.get("Radius")));
                table.setAffectsGlobalCooldown((Boolean) entry.get("AffectsGlobalCooldown"));
                table.setEffectedByGlobalCooldown((Boolean) entry.get("EffectedByGlobalCooldown"));
                table.setCanUseWhileStunned((Boolean) entry.get("CanUseWhileStunned"));
                table.setCanUseWhileMagicLocked((Boolean) entry.get("CanUseWhileMagicLocked"));
                table.setCanUseWhileDisoriented((Boolean) entry.get("CanUseWhileDisoriented"));
                table.setCanUseWhileDisarmed((Boolean) entry.get("CanUseWhileDisarmed"));
                table.setCanUseWhleSnared((Boolean) entry.get("CanUseWhileSnared"));
                table.setCoolDownTime((int) ((long) entry.get("CooldownTime")));


                //======== Requirements ==================


                //Quest

                List<QuestRequirement> questReqs = new ArrayList<>();

                // typecasting obj to JSONObject
                JSONArray qreqs = (JSONArray) entry.get("QuestRequirements");

                for (int qIndex = 0; qIndex < qreqs.size(); qIndex++) {
                    JSONObject qr = (JSONObject) qreqs.get(qIndex);


                    String questName = (String) qr.get("QuestName");
                    String state = (String) qr.get("State");
                    String description = (String) qr.get("Description");

                    QuestRequirement requirement = new QuestRequirement();
                    requirement.setName(questName);
                    requirement.setState(QuestRequirement.forState(state));
                    requirement.setDescription(description);
                    questReqs.add(requirement);

                }
                table.setQuestRequirements(questReqs);

                //Variables

                List<VariableRequirement> varReqs = new ArrayList<>();

                // typecasting obj to JSONObject
                JSONArray vreqs = (JSONArray) entry.get("VariableRequirements");


                for (int vIndex = 0; vIndex < vreqs.size(); vIndex++) {
                    JSONObject vr = (JSONObject) vreqs.get(vIndex);


                    String variable = (String) vr.get("VariableName");
                    String type = (String) vr.get("VarType");
                    String value = (String) vr.get("Value");
                    String description = (String) vr.get("Description");

                    VariableRequirement requirement = new VariableRequirement();
                    requirement.setName(variable);
                    requirement.setType(VariableRequirement.forType(type));
                    requirement.setValue(value);
                    requirement.setDescription(description);
                    varReqs.add(requirement);

                }
                table.setVariableRequirements(varReqs);

                //======== Requirements ==================



                UDataTableRepository.getAbilityDataTable().put(id, table);
                UDataTableRepository.getAbilityNameReferenceTable().put(name.replace("_", " ").toLowerCase(), table);
                tables++;

            }
            SystemLogger.sendSystemMessage("Successfully parsed " + tables + " ability data tables.");



        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets the id for the name of the item
     * @param name
     * @return the id of the item
     */
    public static int forId(String name) {
        AbilityUDataTable table = forName(name);
        return table == null ? -1 : table.getId();
    }


    /**
     * Gets the Item Data Table for the Name
     * @param name
     * @return
     */
    public static AbilityUDataTable forName(String name) {
        String ability = name;
        if (name.contains("_"))
            ability = name.replace("_", " ");
        return UDataTableRepository.getAbilityNameReferenceTable().get(ability.toLowerCase());
    }

    public static AbilityUDataTable forId(int id) {
        return UDataTableRepository.getAbilityDataTable().get(id);
    }

}
