package com.shattered.script.api.impl;

import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.script.api.RelicPlayerTradesAPI;

public class PlayerTradesAPI extends RelicPlayerTradesAPI {

    /**
     * Creates the implementation of the Character Var API
     * @param player
     */
    public PlayerTradesAPI(Player player) {
        super(player);
    }

    /**
     * Adds experience to the specific trade and product
     *
     * @param trade
     * @param product
     */
    @Override
    public void add_xp(String trade, String product) {
        getPlayer().component(PlayerComponents.TRADESMAN).addExperience(trade, product);
    }

    /**
     * Adds experience to the specific trade and product
     *
     * @param trade
     * @param product
     * @param experience
     */
    @Override
    public void add_xp(String trade, String product, int experience) {
        getPlayer().component(PlayerComponents.TRADESMAN).addExperience(trade, product, experience);
    }

    /**
     * Gets the Current Overall Trade XP
     *
     * @param trade
     * @return trade xp
     */
    @Override
    public int getTradeXP(String trade) {
        return getPlayer().component(PlayerComponents.TRADESMAN).getCurrentXPForTrade(trade);
    }

    /**
     * Gets the Specific Product Current XP
     *
     * @param trade
     * @param product
     * @return
     */
    @Override
    public int getProductXP(String trade, String product) {
        return getPlayer().component(PlayerComponents.TRADESMAN).getExperienceForProduct(trade, product);
    }

    /**
     * Gets the Current Overall Total XP for the Technique
     *
     * @param trade
     * @param technique
     * @return
     */
    @Override
    public int getTechniqueXP(String trade, String technique) {
        return getPlayer().component(PlayerComponents.TRADESMAN).getCurrentXpForTechnique(trade, technique);
    }
}
