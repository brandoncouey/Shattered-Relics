package com.shattered.game.actor.character.player.component.interaction.dialogue;

import com.shattered.account.Account;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.component.WorldComponent;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.WorldProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.script.types.NPCDialogScript;
import com.shattered.utilities.ecs.ProcessComponent;
import com.shattered.utilities.ecs.ProcessInterval;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost 11/10/2019 : 9:23 PM
 */
@ProcessComponent(interval = 0.5f)
public class PlayerDialogComponent extends WorldComponent {

    /**
     * Represents the Current Dialogue Currently Ongoing
     */
    @Getter
    @Setter
    public NPCDialogScript currentDialogue;

    /**
     * Represents the Current NPC within the Dialogue
     */
    @Getter
    @Setter
    public NPC currentNPC;

    /**
     * Represents if currently awaiting response
     */
    @Getter
    @Setter
    private boolean awaiting;
    
    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public PlayerDialogComponent(Object gameObject) {
        super(gameObject);
    }

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_DIALOG_OPTION, new WorldProtoListener<World.DialogSelectOption>() {

            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(World.DialogSelectOption message, Player player) {
                PlayerDialogComponent component = player.component(PlayerComponents.DIALOG);
                switch (message.getOptionId()) {
                    case -1:
                        if (component.getCurrentDialogue() != null && component.getCurrentNPC() != null) {
                            component.exit();
                        }
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        if (component.getCurrentDialogue() != null && component.getCurrentNPC() != null)
                            component.getCurrentDialogue().on_option(message.getOptionId());
                        break;
                }
                
            }
        }, World.DialogSelectOption.getDefaultInstance());
        
    }


    /**
     *  Called upon tick to check the distance from the npc
     */
    @Override
    public void onTick(long deltaTime) {
        if (getCurrentDialogue() != null && getCurrentNPC() != null) {
            if (!isWithinDistance(getCurrentNPC(), 350))
                exit();
        }
    }

    /**
     * Quits the dialog and shuts the dialog widget
     */
    public void exit() {
        if (getCurrentDialogue() != null) {
            component(PlayerComponents.WIDGET).hideWidget("dialog");
            getCurrentDialogue().on_finished();
            setCurrentDialogue(null);
            setCurrentNPC(null);
        }
    }
}
