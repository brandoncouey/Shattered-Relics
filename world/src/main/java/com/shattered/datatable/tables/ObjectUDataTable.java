package com.shattered.datatable.tables;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.game.actor.object.ObjectActionType;
import com.shattered.system.SystemLogger;
import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

/**
 * @author JTlr Frost 11/1/2019 : 11:19 PM
 */
@Data
public class ObjectUDataTable {


    /**
     * Represents the Object Id
     */
    private int id = -1;

    /**
     * Represents the Object Name
     */
    private String name = "Unavailable";

    /**
     * Represents the Object Action Type
     */
    private ObjectActionType actionType = ObjectActionType.NONE;

    /**
     * Represents the Object's Action Override
     * used for interacting
     */
    public String objectActionOverride;

    /**
     * Represents the Respawn Rate
     */
    private int respawnRate;

    /**
     * Represents if Object Can Walk Ontop of
     */
    private boolean canWalkOn;

    /**
     * Parses the Item UDataTables.
     */
    public static void parse() {
        try {
            Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "world_object_dt.json"));

            // typecasting obj to JSONObject
            JSONArray items = (JSONArray) dataTable;

            int tables = 0;

            for (int index = 0; index < items.size(); index++) {

                ObjectUDataTable table = new ObjectUDataTable();
                JSONObject entry = (JSONObject) items.get(index);
                int id =  index + 1;
                String name = (String) entry.get("Name");
                String objectAction = (String) entry.get("ObjectAction");
                String actionOverride = (String) entry.get("ObjectActionOverride");

                ObjectActionType type = null;

                if (objectAction != null)
                    type = ObjectActionType.forName(objectAction);

                table.setId(id);
                table.setName(name);

                if (type != null)
                    table.setActionType(type);

                table.setObjectActionOverride(actionOverride);
                UDataTableRepository.getObjectDataTable().put(id, table);
                UDataTableRepository.getObjectNameReferenceTable().put(name.replace("_", " ").toLowerCase(), id);
                tables++;
            }
            SystemLogger.sendSystemMessage("Successfully parsed " + tables + " world object data tables.");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets the Object Data Table for the Name
     * @param name
     * @return
     */
    public static ObjectUDataTable forName(String name) {
        String object = name;
        if (object.contains("_"))
            object = object.replace("_", " ");
        if (!UDataTableRepository.getObjectNameReferenceTable().containsKey(object.toLowerCase()))
            return null;
        return UDataTableRepository.getObjectDataTable().get(UDataTableRepository.getObjectNameReferenceTable().get(object.toLowerCase()));
    }

    /**
     * Checks if the specified object id is a valid object
     * @param id
     * @return valid
     */
    public static boolean isObject(int id) {
        return UDataTableRepository.getObjectDataTable().containsKey(id);
    }

    /**
     * Checks if the specified object by name is a valid object
     * @param name
     * @return valid
     */
    public static boolean isObject(String name) {
        return forName(name) != null;
    }

}
