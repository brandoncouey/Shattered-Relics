package com.shattered.datatable.tables;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.system.SystemLogger;
import lombok.Data;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

/**
 * @author JTlr Frost 11/6/2019 : 4:12 PM
 */
@Data
public class EmitterUDataTable {

    /**
     * Represents the Emitter Id
     */
    private int id;

    /**
     * Represents the Name of the Animation
     */
    @Setter
    private String name;

    /**
     * Parses the Item UDataTables.
     */
    public static void parse() {

        try {
            Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "/repository/data_tables/emitters_dt.json"));

            // typecasting obj to JSONObject
            JSONArray items = (JSONArray) dataTable;

            int tables = 0;

            for (int index = 0; index < items.size(); index++) {

                JSONObject entry = (JSONObject) items.get(index);
                int id = index + 1;
                String name = (String) entry.get("Name");

                EmitterUDataTable table = new EmitterUDataTable();
                table.setId(id);
                table.setName(name);
                UDataTableRepository.getEmitterDataTable().put(id, table);
                UDataTableRepository.getEmitterNameReferenceTable().put(name.replace("_", " ").toLowerCase(), id);
                tables++;
            }
            SystemLogger.sendSystemMessage("Successfully parsed " + tables + " emitter effects data tables.");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }


    /**
     * Gets the Sound Data Table for the specified sfx name
     * @param name
     * @return
     */
    public static EmitterUDataTable forId(String name) {
        String emitter = name;
        if (name.contains("_"))
            emitter = name.replace("_", " ");
        if (!UDataTableRepository.getEmitterNameReferenceTable().containsKey(emitter.toLowerCase())) return null;
        return UDataTableRepository.getEmitterDataTable().get(UDataTableRepository.getEmitterNameReferenceTable().get(emitter.toLowerCase()));
    }
}
