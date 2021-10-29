package com.shattered.game.actor.character.player.component.trades;

import com.shattered.account.Account;
import com.shattered.datatable.UDataTableRepository;
import com.shattered.datatable.tables.ItemUDataTable;
import com.shattered.datatable.tables.TradesUDataTable;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.component.WorldComponent;

public class PlayerTradesComponent extends WorldComponent {

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public PlayerTradesComponent(Object gameObject) {
        super(gameObject);
    }


    @Override
    public void onStart() {
    }

    @Override
    public void onWorldAwake() {
        if (isPlayer()) {
        }
    }

    /**
     * Adds a experience for a specific trade and technique
     * @param trade
     * @param product
     * @return
     */
    public int addExperience(String trade, String product) {
        ItemUDataTable table = ItemUDataTable.forName(product);
        if (trade == null) return 0;
        return addExperience(trade, product, table.getCreationXp());
    }

    /**
     *
     * @param trade
     * @param product
     * @param experience
     */
    public int addExperience(String trade, String product, int experience) {
        TradesUDataTable table = TradesUDataTable.forTrade(trade);
        if (trade == null) return 0;
        String variable = trade.toLowerCase() + "." + product + ".xp";
        //TODO xp bonuses
        if (table.getXpVariables().contains(variable)) {
            int oldProductXp = getExperienceForProduct(trade, product);
            int oldTechniqueTier = 0;
            int oldTradeTier = getTradeTierForCurrentXP(trade);
            //TODO grab oldTechniqueXp
            //TODO grab oldFishermanXp

            component(ActorComponents.VAR).incrementVarInt(variable, experience);


            int newProductXp = getExperienceForProduct(trade, product);

            //TODO product level up (tier)
            //TODO technique level up (tier)
            //TODO Fisherman level up (tier)
        }
        return experience;
    }

    /**
     *
     * @param trade
     * @param product
     * @return
     */
    public int getExperienceForProduct(String trade, String product) {
        TradesUDataTable table = TradesUDataTable.forTrade(trade);
        if (trade == null) return 0;
        return component(ActorComponents.VAR).getVarInt(trade.toLowerCase() + "." + product + ".xp");
    }

    /**
     * Gets the current tier level with the given experience
     * @return the current trade tier level.
     */
    public int getTradeTierForCurrentXP(String trade) {
        TradesUDataTable table = TradesUDataTable.forTrade(trade);
        if (table == null) return 1;
        trade = trade.toLowerCase().replace(" ", "_");
        if (!table.getProducts().contains(trade)) return 1;
        int products = table.getProducts().size();
        int currentXp = getCurrentXPForTrade(trade);
        if (currentXp >= getMaxXPForTechniqueTier(3, products)) return 4;
        int t2 = (int) (22_550 * table.getProducts().size() * 1.1);
        int t3 = (int) (t2 * 2.5 * 1.1);
        if (currentXp >= t3)
            return 3;
        if (currentXp >= t2)
            return 2;
        return 1;
    }

    /**
     * Gets the current xp per trade.
     * @param trade
     * @return
     */
    public int getCurrentXPForTrade(String trade) {
        int experience = 0;
        TradesUDataTable table = TradesUDataTable.forTrade(trade);
        if (table == null) return experience;
        for (String variable : table.getXpVariables())
            experience += component(ActorComponents.VAR).getVarInt(variable);
        return experience;
    }

    /**
     * Gets the current experience for a specific technique
     * @param trade
     * @param technique
     * @return
     */
    public int getCurrentXpForTechnique(String trade, String technique) {
        int experience = 0;
        TradesUDataTable table = TradesUDataTable.forTrade(trade);
        if (table == null) return experience;
        for (String variable : table.getXpVariables())
            experience += component(ActorComponents.VAR).getVarInt(variable);//TODO edit
        return 0;
    }

    /**
     * Gets the maximum experience for a technique tier level
     * @param tier
     * @param products
     * @return the maximum experience required for the tier.
     */
    public static int getMaxXPForTechniqueTier(int tier, int products) {
        double value = 0;
        for (int i = 0; i < products; i++)
            value += getMaxXPForProductTier(tier);
        return (int) value;
    }

    /**
     * Gets the maximum experience needed to achieve the product tier.
     * @param tier
     * @return the maximum xp for the product tier
     */
    public static int getMaxXPForProductTier(int tier) {
        double baseValue = 22_550;
        if (tier == 1)
            return (int) baseValue;
        for (int i = 1; i < tier; i++)
            Math.floor(baseValue = ((baseValue * 2.5) * 1.1));
        return (int) baseValue;
    }


}
