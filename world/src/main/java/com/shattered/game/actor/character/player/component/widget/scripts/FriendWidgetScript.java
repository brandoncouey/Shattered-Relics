package com.shattered.game.actor.character.player.component.widget.scripts;

import com.shattered.game.GameWorld;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.channel.ChannelComponent;
import com.shattered.game.actor.character.player.component.channel.ChannelComponents;
import com.shattered.game.actor.character.player.component.channel.friend.FriendInformation;
import com.shattered.game.actor.character.player.component.container.trade.TradeContainer;
import com.shattered.game.actor.container.Containers;
import com.shattered.script.api.impl.PlayerAPI;
import com.shattered.script.types.WidgetScript;

import java.util.List;

public class FriendWidgetScript extends WidgetScript {

    /**
     * Regusters to a widget name
     * @return the widget name
     */
    @Override
    public String forwidget() {
        return "friend";
    }

    /**
     *
     * @param player
     * @param parameters
     * @param buttonId
     */
    @Override
    public void on_clicked_widget(PlayerAPI player, List<Integer> parameters, int buttonId) {
        if (parameters.isEmpty()) return;
        int friendIndex = parameters.get(0);
        FriendInformation friend = player.getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.FRIENDS_CHANNEL).getFriends().get(friendIndex);
        if (friend == null)
            return;

        Player friendTarget = GameWorld.findPlayerByUuid(friend.getUuid());
        if (friendTarget == null)
            return;
        player.getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).inviteMember(friendTarget);
    }
}
