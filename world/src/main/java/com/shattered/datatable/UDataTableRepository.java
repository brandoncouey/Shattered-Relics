package com.shattered.datatable;

import com.shattered.datatable.tables.*;
import com.shattered.datatable.tables.ItemUDataTable;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JTlr Frost 11/1/2019 : 11:11 PM
 * These data tables are generated via the client.
 */
public class UDataTableRepository {

    /**
     * Represents the Item DataTable Path
     */
    public static final String DATA_TABLE_PATHS = "../repository/data_tables/";

    /**
     * Represents the Item DataTable Repository
     */
    @Getter
    private static final Map<Integer, ItemUDataTable> itemDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the Item Name Reference Table Repository
     */
    @Getter
    private static final Map<String, ItemUDataTable> itemNameReferenceTable = new ConcurrentHashMap<>();//TODO replace the value with Integer reference

    /**
     * Represents the Ability DataTable Repository
     */
    @Getter
    private static final Map<Integer, AbilityUDataTable> abilityDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the Ability Name Reference Table Repository
     */
    @Getter
    private static final Map<String, AbilityUDataTable> abilityNameReferenceTable = new ConcurrentHashMap<>();//TODO replace the value with Integer reference

    /**
     * Represents the Buff DataTable Repository
     */
    @Getter
    private static final Map<Integer, BuffUDataTable> buffDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the Buff Name Reference Table Repository
     */
    @Getter
    private static final Map<String, BuffUDataTable> buffNameReferenceTable = new ConcurrentHashMap<>();//TODO replace the value with Integer reference

    /**
     * Represents the NPC DataTable Repository
     */
    @Getter
    private static final Map<Integer, NPCUDataTable> npcDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the NPC Name Reference Table Repository
     */
    @Getter
    private static final Map<String, Integer> npcNameReferenceTable = new ConcurrentHashMap<>();//TODO replace the value with Integer reference

    /**
     * Represents the Object DataTable Repository
     */
    @Getter
    private static final Map<Integer, ObjectUDataTable> objectDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the Object Name Reference Table Repository
     */
    @Getter
    private static final Map<String, Integer> objectNameReferenceTable = new ConcurrentHashMap<>();//TODO replace the value with Integer reference


    /**
     * Represents the Projectile DataTable Repository
     */
    @Getter
    private static final Map<Integer, ProjectileUDataTable> projectileDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the Projectile Reference Repository
     */
    @Getter
    private static final Map<String, Integer> projectileReferenceDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the hummanoid Anim Sequence DataTable Repository
     */
    @Getter
    private static final Map<Integer, AnimSequenceUDataTable> hummanoidAnimSeqDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the Sound Effect Datatable Repository
     */
    @Getter
    private static final Map<Integer, SoundUDataTable> soundEffectDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the Sound Effect Name Reference Repository
     */
    @Getter
    private static final Map<String, Integer> soundEffectNameDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the Sound Effect Datatable Repository
     */
    @Getter
    private static final Map<Integer, SoundUDataTable> soundTrackDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the Sound Effect Name Reference Repository
     */
    @Getter
    private static final Map<String, Integer> soundTrackNameDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the Hummanoid Anim Name Reference Table Repository
     */
    @Getter
    private static final Map<String, Integer> hummanoidNameReferenceTable = new ConcurrentHashMap<>();

    /**
     * Represents the Non Hummanoid Anim Sequence Datatable Repository
     */
    @Getter
    private static final Map<Integer, AnimSequenceUDataTable> nonHummanoidAnimSeqDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the Non Hummanoid Anim Name Reference Table Repository
     */
    @Getter
    private static final Map<String, Integer> nonHummanoidNameReferenceTable = new ConcurrentHashMap<>();

    /**
     * Represents the Emitter Datatable Repository
     */
    @Getter
    private static final Map<Integer, EmitterUDataTable> emitterDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the Emitter Reference Table Repository
     */
    @Getter
    private static final Map<String, Integer> emitterNameReferenceTable = new ConcurrentHashMap<>();


    /**
     * Represents the Vendor DataTable Repository
     */
    @Getter
    private static final Map<String, VendorUDataTable> vendorDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the Trades DataTable Repository
     */
    @Getter
    private static final Map<String, TradesUDataTable> tradesDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the Trades Reference Repository
     */
    @Getter
    private static final Map<String, Integer> tradeDataTableReference = new ConcurrentHashMap<>();

    /**
     * Represents the Sound Effect Datatable Repository
     */
    @Getter
    private static final Map<Integer, MountUDataTable> mountsDataTable = new ConcurrentHashMap<>();

    /**
     * Represents the Sound Effect Name Reference Repository
     */
    @Getter
    private static final Map<String, Integer> mountsNameDataTable = new ConcurrentHashMap<>();


    /**
     * Parses each data table and puts into the map
     */
    public static void parse() {
        ItemUDataTable.parse();
        NPCUDataTable.parse();
        ObjectUDataTable.parse();
        AnimSequenceUDataTable.parse();
        VendorUDataTable.parse();
        SoundUDataTable.parse();
        ProjectileUDataTable.parse();
        AbilityUDataTable.parse();
        BuffUDataTable.parse();
        EmitterUDataTable.parse();
        MountUDataTable.parse();;
    }
    


    
}
