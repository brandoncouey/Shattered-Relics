package com.shattered.datatable.tables;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.datatable.tables.types.QuestRequirement;
import com.shattered.datatable.tables.types.VariableRequirement;
import com.shattered.game.actor.character.player.component.combat.CombatStyle;
import com.shattered.game.actor.character.player.component.container.equipment.EquipmentSlot;
import com.shattered.game.actor.object.item.Item;
import com.shattered.system.SystemLogger;
import com.shattered.utilities.VariableUtility;
import lombok.Data;
import lombok.Getter;
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
public class ItemUDataTable {

    enum MaterialType {

        NONE,

        CLOTH,

        LEATHER,

        CHAIN,

        PLATE,

        DAGGER,

        SWORD,

        MACE,

        SPEAR,

        SCIMITAR,

        AXE,

        CROSSBOW,

        BOW,

        STAFF,

        WAND,

        GRIMOIRE

        ;

    }


    /**
     * Represents the Item Id
     */
    private int id = -1;

    /**
     * Represents the Name of the Item
     */
    private String name = "Unavailable";

    /**
     * Represents the Equipment Type (Slot)
     */
    @Getter
    private int equipmentType;

    /**
     * Represents the Equipment Material Type
     */
    @Getter
    private MaterialType materialType;

    /**
     * Represents the Combat Style
     */
    private int attackStyle;

    /**
     * Represents if the Item is Two Handed
     */
    private boolean isTwoHanded;

    /**
     * Represents the Current cost of the Item
     */
    private int cost;

    /**
     * Represents the Max Stack
     */
    private int maxStack = 1;

    /**
     * Represents if Subscription Only
     */
    private boolean subscription;

    /**
     * Represents if the Item is From Online Store
     */
    private boolean fromStore;

    /**
     * Represents if Cosmetic Override
     */
    private boolean cosmeticOverride;

    /**
     * Represents if the item is a tool or not.
     */
    private boolean isTool;

    /**
     * Represents if the item is Craftable or not.
     */
    private boolean isCraftable;

    /**
     * Represents if the item is Craftable or not.
     */
    private boolean isTradeable;

    /**
     * Represents if the item can be destroyed or not.
     */
    private boolean canDestroy;

    /**
     * Represents the materials required to make the current item
     */
    private List<Item> materials;

    /**
     * Represents the creation amount
     */
    private int creationAmount = 1;

    /**
     * Represents the amount of experience gained from producing the item
     */
    private int creationXp;

    /**
     * Represents the requirements to make the current item
     */
    private List<VariableRequirement> variableRequirements;

    /**
     * Represents the requirements to make the current item
     */
    private List<QuestRequirement> questRequirements;

    /**
     * Represents the equipment accuracy bonuses
     */
    private int accuracy;

    /**
     * Represents the equipment accuracy bonuses
     */
    private int strength;

    /**
     * Represents the equipment crit strike bonuses
     */
    private int criticalStrike;

    /**
     * Represents the equipment stamina bonuses
     */
    private int stamina;

    /**
     * Represents the equipment speed bonuses
     */
    private int speed;

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
     * Represents the PvP Power
     */
    private int pvpPower;

    /**
     * Represents the PvP Resistance
     */
    private int pvpResistance;

    /**
     * Represents if Stackable
     * @return
     */
    public boolean isStackable() {
        return maxStack > 1;
    }

    /**
     * Sets the Equipment Type
     * @param type
     */
    public void setEquipmentType(EquipmentSlot type) {
        this.equipmentType = type.ordinal();
    }

    /**
     * Sets the material type
     * @param materialType
     */
    public void setMaterialType(int materialType) { this.materialType = MaterialType.values()[materialType]; }



    /**
     * Sets the Attack Style
     * @param style
     */
    public void setAttackStyle(CombatStyle style) { this.attackStyle = style.ordinal(); }

    /**
     * Gets the Combat style by Attack Style Id
     * @return
     */
    public CombatStyle getAttackStyle() { return CombatStyle.forId(attackStyle); }

    /**
     * Retrieves the blend pose for the weapon
     * @return the blend pose for weapon
     */
    public int getBlendPoseForWeapon() {
        if (getName().toLowerCase().contains("bow"))
            return AnimSequenceUDataTable.BlendPoses.CBT_ARCH_BOW.ordinal();
        else if (getName().toLowerCase().contains("twin"))
            return AnimSequenceUDataTable.BlendPoses.CBT_MELEE_TWINBLADE.ordinal();
        else if (getName().toLowerCase().contains("longsword"))
            return AnimSequenceUDataTable.BlendPoses.CBT_MELEE_TWOHAND_LONGSWORD.ordinal();
        return AnimSequenceUDataTable.BlendPoses.NORMAL.ordinal();
    }

    /**
     * Retrieves the blend pose for the weapon
     * @return the blend pose for weapon
     */
    public static int getBlendPoseForWeapon(Item item) {
        if (item == null)
            return AnimSequenceUDataTable.BlendPoses.NORMAL.ordinal();
        if (item.getName().toLowerCase().contains("bow"))
            return AnimSequenceUDataTable.BlendPoses.CBT_ARCH_BOW.ordinal();
        else if (item.getName().toLowerCase().contains("twin"))
            return AnimSequenceUDataTable.BlendPoses.CBT_MELEE_TWINBLADE.ordinal();
        else if (item.getName().toLowerCase().contains("longsword"))
            return AnimSequenceUDataTable.BlendPoses.CBT_MELEE_TWOHAND_LONGSWORD.ordinal();
        return AnimSequenceUDataTable.BlendPoses.NORMAL.ordinal();
    }

    /**
     * Parses the Object UDataTables.
     */
    public static void parse() {
        try {
            Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "item_dt.json"));

            // typecasting obj to JSONObject
            JSONArray items = (JSONArray) dataTable;

            int tables = 0;

            for (int index = 0; index < items.size(); index++) {

                JSONObject entry = (JSONObject) items.get(index);
                int id =  index + 1;//This is the DT Row Name (Uses integers to count for the actual ID)
                String name = (String) entry.get("Name");
                String effect = (String) entry.get("Effect");
                String description = (String) entry.get("Description");
                long max_stack = (long) entry.get("MaxStack");
                String category = (String) entry.get("Category");
                String rarity = (String) entry.get("Rarity");
                String equipSlot = (String) entry.get("EquipmentSlot");
                String equipType = (String) entry.get("EquipmentType");
                boolean isTool = (Boolean) entry.get("IsTool");
                boolean isCraftable = (Boolean) entry.get("IsCraftable");
                boolean isTradeable = (Boolean) entry.get("IsTradeable");
                boolean canDestroy = (Boolean) entry.get("CanDestroy");
                long accuracy = (long) entry.get("Accuracy");
                long criticalStrike = (long) entry.get("CriticalStrikeChance");
                long stamina = (long) entry.get("Stamina");
                long strength = (long) entry.get("Strength");
                long speed = (long) entry.get("Speed");
                long meleeResistance = (long) entry.get("MeleeResistance");
                long archeryResistance = (long) entry.get("ArcheryResistance");
                long magicResistance = (long) entry.get("MagicResistance");
                ItemUDataTable table = new ItemUDataTable();
                table.setId(id);
                table.setName(name.replace("_", " "));
                table.setCost(VariableUtility.random(3, 5));
                table.setMaxStack((int) max_stack);
                table.setEquipmentType(EquipmentSlot.forName(equipSlot));
                table.setTool(isTool);
                table.setCraftable(isCraftable);
                table.setTradeable(isTradeable);
                table.setCanDestroy(canDestroy);
                table.setAccuracy((int) accuracy);
                table.setCriticalStrike((int) criticalStrike);
                table.setStamina((int) stamina);
                table.setStrength((int) strength);
                table.setSpeed((int) speed);
                table.setMeleeResistance((int) meleeResistance);
                table.setArrowResistance((int) archeryResistance);
                table.setMagicResistance((int) magicResistance);
                UDataTableRepository.getItemDataTable().put(id, table);
                UDataTableRepository.getItemNameReferenceTable().put(name.replace("_", " ").toLowerCase(), table);
                tables++;
            }
            SystemLogger.sendSystemMessage("Successfully parsed " + tables + " item data tables.");


            try {
                Object materialDatabase = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "creation_materials_dt.json"));

                // typecasting obj to JSONObject
                JSONArray mats = (JSONArray) materialDatabase;

                int mTables = 0;

                for (int index = 0; index < mats.size(); index++) {

                    JSONObject entry = (JSONObject) mats.get(index);
                    String name = (String) entry.get("Name");
                    String trade = (String) entry.get("Trade");
                    long creationAmount = (long) entry.get("CreationAmount");
                    long creationXp = (long) entry.get("Experience");
                    JSONArray materials = (JSONArray) entry.get("Materials");

                    ItemUDataTable table = ItemUDataTable.forName(name);
                    if (table == null) {
                        SystemLogger.sendSystemErrMessage("Unable to load creation materials table for item: " + name +"!");
                        continue;
                    }


                    List<Item> materialList = new ArrayList<>(materials.size());
                    for (int mindex = 0; mindex < materials.size(); mindex++) {
                        JSONObject mat = (JSONObject) materials.get(mindex);
                        String materialName = (String) mat.get("Name");
                        long materialAmount = (long) mat.get("Amount");
                        ItemUDataTable materialTable = ItemUDataTable.forName(materialName);
                        if (materialTable == null) {
                            SystemLogger.sendSystemErrMessage("Unable to load creation materials table for item material: " + materialName +"!");
                            continue;
                        }

                        materialList.add(new Item(materialTable.getId(), (int) materialAmount));
                    }
                    table.setMaterials(materialList);
                    table.setCreationAmount((int) creationAmount);
                    table.setCreationXp((int) creationXp);

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

                    //Represents invalid trade
                    if (TradesUDataTable.forTrade(trade.toLowerCase().replace("_", "")) == null) {
                        TradesUDataTable tradesUDataTable = new TradesUDataTable();
                        tradesUDataTable.getProducts().add(name.toLowerCase().replace("_", ""));
                        tradesUDataTable.getXpVariables().add(trade.toLowerCase().replace("_", "") + "." + name.toLowerCase().replace("_", "") + ".xp");
                        tradesUDataTable.getProducedVariables().add(trade.toLowerCase().replace("_", "") + "." + name.toLowerCase().replace("_", "") + ".produced");
                        UDataTableRepository.getTradesDataTable().put(trade.toLowerCase().replace("_", ""), tradesUDataTable);
                        UDataTableRepository.getTradeDataTableReference().put(trade.toLowerCase().replace("_", ""), (index + 1));
                    } else {
                        TradesUDataTable tradesUDataTable = TradesUDataTable.forTrade(trade.toLowerCase().replace("_", ""));
                        tradesUDataTable.getProducts().add(name.toLowerCase().replace("_", ""));
                        tradesUDataTable.getXpVariables().add(trade.toLowerCase().replace("_", "") + "." + name.toLowerCase().replace("_", "") + ".xp");
                        tradesUDataTable.getProducedVariables().add(trade.toLowerCase().replace("_", "") + "." + name.toLowerCase().replace("_", "") + ".produced");
                    }

                    mTables++;
                }
                SystemLogger.sendSystemMessage("Successfully parsed " + mTables + " item material data tables.");
                SystemLogger.sendSystemMessage("Successfully parsed " + UDataTableRepository.getTradesDataTable().size() + " skill trades.");

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }

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
        ItemUDataTable table = forName(name);
        return table == null ? -1 : table.getId();
    }


    /**
     * Gets the Item Data Table for the Name
     * @param name
     * @return
     */
    public static ItemUDataTable forName(String name) {
        String item = name;
        if (name.contains("_"))
            item = name.replace("_", " ");
        return UDataTableRepository.getItemNameReferenceTable().get(item.toLowerCase());
    }

}
