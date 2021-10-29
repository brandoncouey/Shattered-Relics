package com.shattered.datatable.tables;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.game.actor.object.item.Item;
import com.shattered.system.SystemLogger;
import lombok.Data;


/**
 * Not sure if these should be editable by client or a t-p tool....
 */
@Data
public class VendorUDataTable {

    /**
     * Represents the Name of the Vendor
     */
    private String name;

    /**
     * Represents if Vendor is a General Store (Ability to sell anything there)
     */
    private boolean general;

    /**
     * Represents the Stock of the items.
     */
    private Item[] stock;

    /**
     * Parses the Vendor Data Table
     */
    public static void parse() {

        VendorUDataTable vendor1 = new VendorUDataTable();
        vendor1.setName("General Store");
        vendor1.setGeneral(true);

        Item[] stock1 = new Item[] {
                new Item(10, 1),
                new Item(5, 1),
                new Item(3, 2)
        };
        vendor1.setStock(stock1);

        UDataTableRepository.getVendorDataTable().put("default", vendor1);

        VendorUDataTable vendor2 = new VendorUDataTable();
        vendor2.setName("Gathering Supplies");

        Item[] stock2 = new Item[] { new Item(3, 1), new Item(4, 1) };
        vendor2.setStock(stock2);

        UDataTableRepository.getVendorDataTable().put("gathering.supplies", vendor2);

        SystemLogger.sendSystemMessage("Successfully parsed " + UDataTableRepository.getVendorDataTable().size() + " vendors.");
    }
}
