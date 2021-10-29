package com.shattered.game.actor.character.player.component.widget.scripts;

import com.shattered.game.actor.vendor.VendorContainer;
import com.shattered.script.api.impl.PlayerAPI;
import com.shattered.script.types.WidgetScript;

import java.util.List;

public class VendorWidgetScript extends WidgetScript {

    /**
     * Regusters to a widget name
     * @return the widget name
     */
    @Override
    public String forwidget() {
        return "vendor";
    }

    /**
     *
     * @param player
     * @param parameters
     * @param buttonId
     */
    @Override
    public void on_clicked_widget(PlayerAPI player, List<Integer> parameters, int buttonId) {
        if (player.getPlayer().getContainerManager().getCurrent() instanceof VendorContainer) {

            VendorContainer vendorContainer = (VendorContainer) player.getPlayer().getContainerManager().getCurrent();
            if (buttonId == 0)
                vendorContainer.removeViewer(player.getPlayer());
            else
            if (vendorContainer != null)
                vendorContainer.purchase(player.getPlayer(), parameters.get(0), parameters.get(1));
        }
    }
}
