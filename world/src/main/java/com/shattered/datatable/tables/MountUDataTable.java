package com.shattered.datatable.tables;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.system.SystemLogger;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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
public class MountUDataTable {

    /**
     * Represents the ID of the AnimSequence.
     */
    private int id;

    /**
     * Represents the Name of the Animation
     */
    private String name;

    /**
     * Represents the Speed Bonus of the Mount
     */
    private int speedBonus;


    /**
     * Parses the Item UDataTables.
     */
    public static void parse() {

        try {
            Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "mounts_dt.json"));

            // typecasting obj to JSONObject
            JSONArray items = (JSONArray) dataTable;

            int tables = 0;

            for (int index = 0; index < items.size(); index++) {

                JSONObject entry = (JSONObject) items.get(index);
                int id = index + 1;
                String name = (String) entry.get("Name");
                long speed = (Long) entry.get("SpeedBonus");

                MountUDataTable table = new MountUDataTable();
                table.setId(id);
                table.setName(name);
                table.setSpeedBonus((int) speed);
                UDataTableRepository.getMountsDataTable().put(id, table);
                UDataTableRepository.getMountsNameDataTable().put(name.replace("_", " ").toLowerCase(), id);
                tables++;
            }
            SystemLogger.sendSystemMessage("Successfully parsed " + tables + " mount data tables.");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets the Sound Data Table for the specified track name
     * @param name
     * @return
     */
    public static MountUDataTable forName(String name) {
        String mount = name;
        if (name.contains("_"))
            mount = name.replace("_", " ");
        if (!UDataTableRepository.getMountsNameDataTable().containsKey(mount.toLowerCase())) return null;
        return UDataTableRepository.getMountsDataTable().get(UDataTableRepository.getMountsNameDataTable().get(mount.toLowerCase()));
    }
}
