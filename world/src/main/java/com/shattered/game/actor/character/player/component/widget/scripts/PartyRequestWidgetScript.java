package com.shattered.game.actor.character.player.component.widget.scripts;

import com.shattered.game.GameWorld;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.channel.ChannelComponent;
import com.shattered.game.actor.character.player.component.channel.ChannelComponents;
import com.shattered.game.actor.character.player.component.channel.friend.FriendInformation;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.script.api.impl.PlayerAPI;
import com.shattered.script.types.WidgetScript;

import java.util.List;

public class PartyRequestWidgetScript extends WidgetScript {

    /**
     * Regusters to a widget name
     * @return the widget name
     */
    @Override
    public String forwidget() {
        return "party_invite";
    }

    /**
     *
     * @param player
     * @param parameters
     * @param buttonId
     */
    @Override
    public void on_clicked_widget(PlayerAPI player, List<Integer> parameters, int buttonId) {
        switch (buttonId) {
            case 0: {//Declinend
                Player inviter = GameWorld.findPlayerByUuid(player.getPlayer().component(ActorComponents.TRANS_VAR).getVarInt("party_invite_uuid"));
                player.getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Party invite declined.");
                player.getPlayer().component(ActorComponents.TRANS_VAR).setVarInt("party_invite_uuid", -1);
                if (inviter == null) {
                    return;
                }
                inviter.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage(player.getName() + " declined your party invite.");
                break;
            }
            case 1: {//Accepted
                Player inviter = GameWorld.findPlayerByUuid(player.getPlayer().component(ActorComponents.TRANS_VAR).getVarInt("party_invite_uuid"));
                if (inviter == null)
                    return;
                inviter.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).addMember(player.getPlayer());
                break;
            }
        }
    }
}
