package com.shattered.datatable.tables;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.game.actor.object.item.Item;
import com.shattered.system.SystemLogger;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TradesUDataTable {

    /**
     * Represents a list of all the trades and their current product count.
     */
    private List<String> products = new ArrayList<>();

    /**
     * Represents a list of all the variables binded to a product xp
     */
    private List<String> xpVariables = new ArrayList<>();

    /**
     * Represents a list of all variables binded to producing products
     */
    private List<String> producedVariables = new ArrayList<>();


    /**
     * Gets the Trade Data Table for the specified trade name
     * @param name
     * @return
     */
    public static TradesUDataTable forTrade(String name) {
        String trade = name;
        if (name.contains("_"))
            trade = name.replace("_", " ");
        if (!UDataTableRepository.getTradesDataTable().containsKey(trade.toLowerCase())) return null;
        return UDataTableRepository.getTradesDataTable().get(trade);
    }


}
