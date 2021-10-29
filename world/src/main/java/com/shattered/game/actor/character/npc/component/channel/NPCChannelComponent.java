package com.shattered.game.actor.character.npc.component.channel;

import com.shattered.account.Account;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.channel.ChannelType;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.component.WorldComponent;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;

public class NPCChannelComponent extends WorldComponent {

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public NPCChannelComponent(Object gameObject) {
        super(gameObject);
    }

    /**
     * Sends a message to all account in local area.
     * @param message
     */
    public void sendChannelMessage(ChannelType type, String message) {
        switch (type) {

            //Public Chat Message
            case YELL:
            case LOCAL: {
                for (Player local : getNPC().component(GameObjectComponents.ZONE_COMPONENT).getNode().getNodePlayers().values()) {
                    if (local == null)
                        continue;
                    if (local.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().distanceTo(getNPC().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation()) > (type == ChannelType.YELL ? 4000 : 2000))
                        continue;
                    local.sendMessage(PacketOuterClass.Opcode.SMSG_CHAT_MESSAGE, World.ChannelMessage.newBuilder().setFromIndex(getNPC().getClientIndex() << 1 | 0).setFromName(getNPC().getName()).setType(type.ordinal()).setMessage(message).setPermissionLevel(0).build());
                }
            }
        }
    }

}
