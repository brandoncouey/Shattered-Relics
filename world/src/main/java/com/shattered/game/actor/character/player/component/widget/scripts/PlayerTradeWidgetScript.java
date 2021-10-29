package com.shattered.game.actor.character.player.component.widget.scripts;

import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.container.trade.TradeContainer;
import com.shattered.game.actor.character.player.component.trades.PlayerTradesComponent;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.vendor.VendorContainer;
import com.shattered.script.api.impl.PlayerAPI;
import com.shattered.script.types.WidgetScript;

import java.util.List;

public class PlayerTradeWidgetScript extends WidgetScript {

    /**
     * Regusters to a widget name
     * @return the widget name
     */
    @Override
    public String forwidget() {
        return "trading";
    }

    /**
     *
     * @param player
     * @param parameters
     * @param buttonId
     */
    @Override
    public void on_clicked_widget(PlayerAPI player, List<Integer> parameters, int buttonId) {
        TradeContainer trade = player.getPlayer().container(Containers.TRADE);
        if (!trade.isTrading()) {
            player.getPlayer().component(PlayerComponents.WIDGET).hideWidget("trading");
            return;
        }
        switch (buttonId) {
            case 0: {//Accepts Trade
                trade.accept();
                break;
            }
            case 1: {//Closes  Trade
                trade.forceClose();
                break;
            }
        }
    }
}
