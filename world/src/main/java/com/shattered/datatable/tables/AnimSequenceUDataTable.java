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
 * @author JTlr Frost 11/6/2019 : 3:10 PM
 */
@Data
public class AnimSequenceUDataTable {

    /**
     * Represents All Blendposes for a hummanoid
     */
    public enum BlendPoses {

        NORMAL,

        MOUNTED,

        SWIMMING,

        CBT_ARCH_BOW,

        CBT_MELEE_TWOHAND_LONGSWORD,

        CBT_MELEE_TWINBLADE
    }

    /**
     * Represents the ID of the AnimSequence.
     */
    public int id;

    /**
     * Represents the Name of the Animation
     */
    private String name;

    /**
     * Represents the Duration of the Animation
     */
    private int duration;


    /**
     * Parses the Item UDataTables.
     */
    public static void parse() {

        try {
            Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "hummanoid_animseq_dt.json"));

            // typecasting obj to JSONObject
            JSONArray items = (JSONArray) dataTable;

            int tables = 0;

            for (int index = 0; index < items.size(); index++) {

                JSONObject entry = (JSONObject) items.get(index);
                int id = index + 1;
                String name = (String) entry.get("Name");
                long duration = (long) entry.get("Duration");


                AnimSequenceUDataTable table = new AnimSequenceUDataTable();
                table.setId(id);
                table.setName(name);
                table.setDuration((int) duration);
                UDataTableRepository.getHummanoidAnimSeqDataTable().put(id, table);
                UDataTableRepository.getHummanoidNameReferenceTable().put(name.toLowerCase().replace("_", " "), id);
                tables++;
            }
            SystemLogger.sendSystemMessage("Successfully parsed " + tables + " hummanoid animation sequence data tables.");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }


        try {
            Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "non_hummanoid_animseq_dt.json"));

            // typecasting obj to JSONObject
            JSONArray items = (JSONArray) dataTable;

            int tables = 0;

            for (int index = 0; index < items.size(); index++) {

                JSONObject entry = (JSONObject) items.get(index);
                int id = index + 1;
                String name = (String) entry.get("Name");
                long duration = (long) entry.get("Duration");


                AnimSequenceUDataTable table = new AnimSequenceUDataTable();
                table.setId(id);
                table.setName(name);
                table.setDuration((int) duration);
                UDataTableRepository.getNonHummanoidAnimSeqDataTable().put(id, table);
                UDataTableRepository.getNonHummanoidNameReferenceTable().put(name.toLowerCase().replace("_", " "), id);
                tables++;
            }

            SystemLogger.sendSystemMessage("Successfully parsed " + tables + " non hummanoid animation sequence data tables.");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets the Item Data Table for the Name
     * @param name
     * @return
     */
    public static AnimSequenceUDataTable forName(String name, boolean hummanoid) {
        String anim = name;
        if (name.contains("_"))
            anim = name.replace("_", " ");
        if (hummanoid) {
            if (!UDataTableRepository.getHummanoidNameReferenceTable().containsKey(anim.toLowerCase()))  {
                SystemLogger.sendSystemErrMessage("Attempted to play a hummanoid animation `" + anim.toLowerCase() + "` but was not found!");
                return null;
            }
            return UDataTableRepository.getHummanoidAnimSeqDataTable().get(UDataTableRepository.getHummanoidNameReferenceTable().get(anim.toLowerCase()));
        }
        if (!UDataTableRepository.getNonHummanoidNameReferenceTable().containsKey(anim.toLowerCase()))  {
            SystemLogger.sendSystemErrMessage("Attempted to play a non hummanoid animation `" + anim.toLowerCase() + "` but was not found!");
            return null;
        }
        return UDataTableRepository.getNonHummanoidAnimSeqDataTable().get(UDataTableRepository.getNonHummanoidNameReferenceTable().get(anim.toLowerCase()));
    }


}
