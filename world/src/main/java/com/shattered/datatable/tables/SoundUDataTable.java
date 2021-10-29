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
 * @author JTlr Frost 11/6/2019 : 3:10 PM
 */

public class SoundUDataTable {

    /**
     * Represents the ID of the AnimSequence.
     */
    @Setter
    public int id;

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
            Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "sfx_dt.json"));

            // typecasting obj to JSONObject
            JSONArray items = (JSONArray) dataTable;

            int tables = 0;

            for (int index = 0; index < items.size(); index++) {

                JSONObject entry = (JSONObject) items.get(index);
                int id = index + 1;
                String name = (String) entry.get("Name");

                SoundUDataTable table = new SoundUDataTable();
                table.setId(id);
                table.setName(name);
                UDataTableRepository.getSoundEffectDataTable().put(id, table);
                UDataTableRepository.getSoundEffectNameDataTable().put(name.replace("_", " ").toLowerCase(), id);
                tables++;
            }
            SystemLogger.sendSystemMessage("Successfully parsed " + tables + " sound effects data tables.");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        try {
            Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "music_tracks_dt.json"));

            // typecasting obj to JSONObject
            JSONArray items = (JSONArray) dataTable;

            int tables = 0;

            for (int index = 0; index < items.size(); index++) {

                JSONObject entry = (JSONObject) items.get(index);
                int id = index + 1;
                String name = (String) entry.get("Name");


                SoundUDataTable table = new SoundUDataTable();
                table.setId(id);
                table.setName(name);
                UDataTableRepository.getSoundTrackDataTable().put(id, table);
                UDataTableRepository.getSoundTrackNameDataTable().put(name.toLowerCase(), id);
                tables++;
            }
            SystemLogger.sendSystemMessage("Successfully parsed " + tables + " music track data tables.");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets the Sound Data Table for the specified sfx name
     * @param name
     * @return
     */
    public static SoundUDataTable forSFX(String name) {
        String sfx = name;
        if (name.contains("_"))
            sfx = name.replace("_", " ");
        if (!UDataTableRepository.getSoundEffectNameDataTable().containsKey(sfx.toLowerCase())) return null;
        return UDataTableRepository.getSoundEffectDataTable().get(UDataTableRepository.getSoundEffectNameDataTable().get(sfx.toLowerCase()));
    }

    /**
     * Gets the Sound Data Table for the specified track name
     * @param name
     * @return
     */
    public static SoundUDataTable forTrack(String name) {
        String sfx = name;
        if (name.contains("_"))
            sfx = name.replace("_", " ");
        if (!UDataTableRepository.getSoundTrackNameDataTable().containsKey(sfx.toLowerCase())) return null;
        return UDataTableRepository.getSoundTrackDataTable().get(UDataTableRepository.getSoundEffectNameDataTable().get(sfx.toLowerCase()));
    }
}
