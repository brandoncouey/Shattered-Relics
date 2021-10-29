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
public class ProjectileUDataTable {

    /**
     * Represents the ID of the AnimSequence.
     */
    public int id;

    /**
     * Represents the Name of the Animation
     */
    private String name;


    /**
     * Parses the Item UDataTables.
     */
    public static void parse() {

        try {
            Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "projectile_dt.json"));

            // typecasting obj to JSONObject
            JSONArray items = (JSONArray) dataTable;

            int tables = 0;

            for (int index = 0; index < items.size(); index++) {
                JSONObject entry = (JSONObject) items.get(index);
                int id = index + 1;
                String name = (String) entry.get("Name");
                ProjectileUDataTable table = new ProjectileUDataTable();
                table.setId(id);
                table.setName(name);
                UDataTableRepository.getProjectileDataTable().put(id, table);
                UDataTableRepository.getProjectileReferenceDataTable().put(name.toLowerCase(), id);
                tables++;
            }
            SystemLogger.sendSystemMessage("Successfully parsed " + tables + " projectile data tables.");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets the Sound Data Table for the specified sfx name
     * @param name
     * @return
     */
    public static ProjectileUDataTable forName(String name) {
        String proj = name;
        if (name.contains("_"))
            proj = name.replace("_", " ");
        if (!UDataTableRepository.getProjectileReferenceDataTable().containsKey(proj.toLowerCase())) return null;
        return UDataTableRepository.getProjectileDataTable().get(UDataTableRepository.getProjectileReferenceDataTable().get(proj.toLowerCase()));
    }
}
