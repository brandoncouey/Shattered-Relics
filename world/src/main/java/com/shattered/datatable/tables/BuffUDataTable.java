package com.shattered.datatable.tables;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.system.SystemLogger;
import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

/**
 * @author JTlr Frost 11/1/2019 : 11:11 PM
 */
@Data
public class BuffUDataTable {


    /**
     * Represents the Item Id
     */
    private int id = -1;

    /**
     * Represents the Name of the Item
     */
    private String name = "Unavailable";

    /**
     * Represents the amount of stacks allowed available for the buff
     */
    private int stack = 1;

    /**
     * Represents the duration of the buff
     */
    private int baseDuration = 15;

    /**
     * Represents if the buff type is a debuff.
     */
    public boolean debuff = false;



    /**
     * Parses the Object UDataTables.
     */
    public static void parse() {
        try {
            Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "../repository/data_tables/buffs_dt.json"));

            // typecasting obj to JSONObject
            JSONArray items = (JSONArray) dataTable;

            int tables = 0;

            for (int index = 0; index < items.size(); index++) {

                JSONObject entry = (JSONObject) items.get(index);
                int id =  index + 1;
                String name = (String) entry.get("Name");
                BuffUDataTable table = new BuffUDataTable();
                table.setId(id);
                table.setName(name.replace("_", " "));
                table.setStack((int) ((long) entry.get("MaxStacks")));
                //table.setBaseDuration((int) ((long) entry.get("BaseDuration")));
                table.setDebuff((boolean) entry.get("IsDebuff"));
                UDataTableRepository.getBuffDataTable().put(id, table);
                UDataTableRepository.getBuffNameReferenceTable().put(name.replace("_", " ").toLowerCase(), table);
                tables++;
            }
            SystemLogger.sendSystemMessage("Successfully parsed " + tables + " buff data tables.");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets the id for the name of the buff
     * @param name
     * @return the id of the buff
     */
    public static int forId(String name) {
        BuffUDataTable table = forName(name);
        return table == null ? -1 : table.getId();
    }


    /**
     * Gets the Buff Data Table for the Name
     * @param name
     * @return the data table
     */
    public static BuffUDataTable forName(String name) {
        String ability = name;
        if (name.contains("_"))
            ability = name.replace("_", " ");
        return UDataTableRepository.getBuffNameReferenceTable().get(ability.toLowerCase());
    }

}
