package com.shattered.script.api;

import com.shattered.game.actor.character.player.Player;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@NonNull
@RequiredArgsConstructor
public abstract class RelicPlayerTradesAPI {

    /**
     * Represents the Character for the Quest API
     */
    @Getter
    protected final Player player;

    /**
     * Adds experience to the specific trade and product
     *
     * @param trade
     * @param product
     */
    public abstract void add_xp(String trade, String product);

    /**
     * Adds experience to the specific trade and product
     *
     * @param trade
     * @param product
     * @param experience
     */
    public abstract void add_xp(String trade, String product, int experience);

    /**
     * Gets the Current Overall Trade XP
     * @param trade
     * @return trade xp
     */
    public abstract int getTradeXP(String trade);

    /**
     * Gets the Specific Product Current XP
     * @param trade
     * @param product
     * @return
     */
    public abstract int getProductXP(String trade, String product);

    /**
     * Gets the Current Overall Total XP for the Technique
     * @param trade
     * @param technique
     * @return
     */
    public abstract int getTechniqueXP(String trade, String technique);



}
