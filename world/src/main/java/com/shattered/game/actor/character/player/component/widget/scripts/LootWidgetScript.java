package com.shattered.game.actor.character.player.component.widget.scripts;

import com.shattered.game.GameWorld;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.channel.ChannelComponents;
import com.shattered.game.actor.character.player.component.channel.friend.FriendInformation;
import com.shattered.game.actor.container.loot.LootContainer;
import com.shattered.script.api.impl.PlayerAPI;
import com.shattered.script.types.WidgetScript;

import java.util.List;

public class LootWidgetScript extends WidgetScript {

    /**
     * Regusters to a widget name
     * @return the widget name
     */
    @Override
    public String forwidget() {
        return "loot";
    }

    /**
     *
     * @param player
     * @param parameters
     * @param buttonId
     */
    @Override
    public void on_clicked_widget(PlayerAPI player, List<Integer> parameters, int buttonId) {
        if (player.getPlayer().getContainerManager().getCurrent() == null) return;
        LootContainer loot = (LootContainer) player.getPlayer().getContainerManager().getCurrent();
        if (loot == null) return;
        switch (buttonId) {
            case 0: {//Closes out of it
                loot.setLooter(null);
                break;
            }
            case 1: {//Loot all
                loot.lootAll();
                break;
            }

        }
    }
}
