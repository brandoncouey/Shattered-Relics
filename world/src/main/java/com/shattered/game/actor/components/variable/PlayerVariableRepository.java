package com.shattered.game.actor.components.variable;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.system.SystemLogger;
import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlayerVariableRepository {

    /**
     * Represents a map of every player int variables
     */
    @Getter
    private static final Map<Integer, String> varInts = new HashMap<>();

    /**
     * Represents a map of player int default variables
     */
    @Getter
    private static final Map<Integer, Integer> varIntsDefaults = new HashMap<>();

    /**
     * Represents a map of player variable values linked to the var int keys.
     */
    private static final Map<String, Integer> varIntsReference = new HashMap<>();

    /**
     * Represents a map of every player str variables
     */
    @Getter
    private static final Map<Integer, String> varStrs = new HashMap<>();

    /**
     * Represents a map of player variable values linked to the var str keys
     */
    private static final Map<String, Integer> varStrsReference = new HashMap<>();

    /**
     * Represents a map of every player bool variables
     */
    @Getter
    private static final Map<Integer, String> varBools = new HashMap<>();

    /**
     * Represents a map of player variable values linked to the var bool keys
     */
    private static final Map<String, Integer> varBoolsReference = new HashMap<>();

    /**
     * Represents a map of every player transient int variables
     */
    @Getter
    private static final Map<Integer, String> tVarInts = new HashMap<>();

    /**
     * Represents a map of player transient variable values linked to the var int keys.
     */
    private static final Map<String, Integer> tVarIntsReference = new HashMap<>();

    /**
     * Represents a map of every player str transient variables
     */
    @Getter
    private static final Map<Integer, String> tVarStrs = new HashMap<>();

    /**
     * Represents a map of player transient variable values linked to the var str keys
     */
    private static final Map<String, Integer> tVarStrsReference = new HashMap<>();

    /**
     * Represents a map of every player bool transient variables
     */
    @Getter
    private static final Map<Integer, String> tVarBools = new HashMap<>();

    /**
     * Represents a map of player transient variable values linked to the var bool keys
     */
    private static final Map<String, Integer> tVarBoolsReference = new HashMap<>();

    /**
     * Parses all of the Variable Maps
     */
    public static void parse() {
        parseVarInts();
        parseVarStrs();
        parseVarBools();
        parseTVarInts();
        parseTVarStrs();
        parseTVarBools();
    }

    /**
     * Parses all of the Int Variables
     */
    public static void parseVarInts() {
        try {
            Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "var_int_dt.json"));
            JSONArray items = (JSONArray) dataTable;
            int vars = 0;
            for (int index = 0; index < items.size(); index++) {
                JSONObject entry = (JSONObject) items.get(index);
                int id = index + 1;
                String reference = (String) entry.get("Name");
                long defaultValue = (Long) entry.get("DefaultValue");
                varInts.put(id, reference);
                varIntsDefaults.put(id, (int) defaultValue);
                varIntsReference.put(reference, (int) id);
                vars++;
            }
            SystemLogger.sendSystemMessage("Successfully parsed " + vars + " var ints.");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Parses all of the String Variables
     */
   public static void parseVarStrs() {
       try {
           Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "var_str_dt.json"));
           JSONArray items = (JSONArray) dataTable;
           int vars = 0;
           for (int index = 0; index < items.size(); index++) {
               JSONObject entry = (JSONObject) items.get(index);
               int id = index + 1;
               String reference = (String) entry.get("Name");
               varStrs.put(id, reference);
               varStrsReference.put(reference, (int) id);
               vars++;
           }
           SystemLogger.sendSystemMessage("Successfully loaded " + vars + " var strings.");

       } catch (IOException | ParseException e) {
           e.printStackTrace();
       }
   }

    /**
     * Parses all of the Bool Variables
     */
   public static void parseVarBools() {
       try {
           Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "var_bool_dt.json"));
           JSONArray items = (JSONArray) dataTable;
           int vars = 0;
           for (int index = 0; index < items.size(); index++) {
               JSONObject entry = (JSONObject) items.get(index);
               int id = index + 1;
               String reference = (String) entry.get("Name");
               varBools.put(id, reference);
               varBoolsReference.put(reference, (int) id);
               vars++;
           }
           SystemLogger.sendSystemMessage("Successfully loaded " + vars + " var bools.");
       } catch (IOException | ParseException e) {
           e.printStackTrace();
       }
   }

    /**
     * Parses all of the Int Variables
     */
    public static void parseTVarInts() {
        try {
            Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "tvar_int_dt.json"));
            JSONArray items = (JSONArray) dataTable;
            int vars = 0;
            for (int index = 0; index < items.size(); index++) {
                JSONObject entry = (JSONObject) items.get(index);
                int id = index + 1;
                String reference = (String) entry.get("Name");
                tVarInts.put(id, reference);
                tVarIntsReference.put(reference, (int) id);
                vars++;
            }
            SystemLogger.sendSystemMessage("Successfully parsed " + vars + " transient var ints.");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Parses all of the String Variables
     */
   public static void parseTVarStrs() {
       try {
           Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "tvar_str_dt.json"));
           JSONArray items = (JSONArray) dataTable;
           int vars = 0;
           for (int index = 0; index < items.size(); index++) {
               JSONObject entry = (JSONObject) items.get(index);
               int id = index + 1;
               String reference = (String) entry.get("Name");
               tVarStrs.put(id, reference);
               tVarStrsReference.put(reference, (int) id);
               vars++;
           }
           SystemLogger.sendSystemMessage("Successfully loaded " + vars + " transient var strings.");

       } catch (IOException | ParseException e) {
           e.printStackTrace();
       }
   }

    /**
     * Parses all of the Bool Variables
     */
   public static void parseTVarBools() {
       try {
           Object dataTable = new JSONParser().parse(new FileReader(UDataTableRepository.DATA_TABLE_PATHS + "tvar_bool_dt.json"));
           JSONArray items = (JSONArray) dataTable;
           int vars = 0;
           for (int index = 0; index < items.size(); index++) {
               JSONObject entry = (JSONObject) items.get(index);
               int id = index + 1;
               String reference = (String) entry.get("Name");
               tVarBools.put(id, reference);
               tVarBoolsReference.put(reference, (int) id);
               vars++;
           }
           SystemLogger.sendSystemMessage("Successfully loaded " + vars + " transient var bools.");
       } catch (IOException | ParseException e) {
           e.printStackTrace();
       }
   }

    /**
     * Finds the VarId for the variable reference, if not found returns -1
     * @param reference
     * @return the varint varid
     */
   public static int forVarIntId(String reference) {
       if (varIntsReference.containsKey(reference))
           return varIntsReference.get(reference);
       return -1;
   }

    /**
     * Finds a VarId for the variable reference, if not found returns -1
     * @param reference
     * @return the varstr varid
     */
   public static int forVarStrId(String reference) {
       if (varStrsReference.containsKey(reference))
           return varStrsReference.get(reference);
       return -1;
   }

    /**
     * Finds a VarId for the variable reference, if not found returns -1
     * @param reference
     * @return the varbool varid
     */
   public static int forVarBoolId(String reference) {
       if (varBoolsReference.containsKey(reference))
           return varBoolsReference.get(reference);
       return -1;
   }

   /**
     * Finds the VarId for the variable reference, if not found returns -1
     * @param reference
     * @return the varint varid
     */
   public static int forTVarIntId(String reference) {
       if (tVarIntsReference.containsKey(reference))
           return tVarIntsReference.get(reference);
       return -1;
   }

    /**
     * Finds a VarId for the variable reference, if not found returns -1
     * @param reference
     * @return the varstr varid
     */
   public static int forTVarStrId(String reference) {
       if (tVarStrsReference.containsKey(reference))
           return tVarStrsReference.get(reference);
       return -1;
   }

    /**
     * Finds a VarId for the variable reference, if not found returns -1
     * @param reference
     * @return the varbool varid
     */
   public static int forVarTBoolId(String reference) {
       if (tVarBoolsReference.containsKey(reference))
           return tVarBoolsReference.get(reference);
       return -1;
   }


}
