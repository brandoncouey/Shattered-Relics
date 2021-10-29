package com.shattered.game.engine.threads;

import com.shattered.BuildWorld;
import com.shattered.engine.tasks.DelayedTaskTicker;
import com.shattered.game.GameWorld;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.object.WorldObject;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.networking.messages.QueuedMessage;

/**
 * @author JTlr Frost 9/7/2019 : 8:07 PM
 */
public class WorldLogicThread extends Thread {


    private static final long PROCESSING_INTERVAL = 25L;

    private static long lastFrameUpdate = System.currentTimeMillis();

    /**
     * Represents the World Thread
     */
    public WorldLogicThread() {
        setPriority(Thread.MAX_PRIORITY);
        setName("World Thread");
    }

    /**
     *
     */
    @Override
    public void run() {


        while (!BuildWorld.getInstance().getEngine().isShuttingDown()) {

            final long currentFrame = System.currentTimeMillis();
            final long deltaTime = currentFrame - lastFrameUpdate;

                for (Player player : GameWorld.getCharacters()) {
                    if (player == null) continue;

                    while (!player.getMessages().isEmpty()) {
                        try {
                            QueuedMessage message = player.getMessages().poll();
                            message.getListener().handleRaw(message.getMessage(), player);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }


            //Loops through account and updates them.
            for (Player player : GameWorld.getCharacters()) {
                try {
                    if (player == null) continue;
                    if (!player.component(PlayerComponents.WORLD_LEVEL_MANAGER).isWorldLevelLoaded()) continue;
                    player.onTick(deltaTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Loops through npcs and updates them.
            for (NPC npc : GameWorld.getNpcs()) {
                try {
                    if (npc == null) continue;
                    if (npc.getState().equals(ActorState.CONSTRUCT) || npc.getState().equals(ActorState.FINISHED))
                        continue;
                    npc.onTick(deltaTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Loops through objects an updates them.
            for (WorldObject object : GameWorld.getObjects()) {
                try {
                    if (object == null) continue;
                    if (object.getState().equals(ActorState.CONSTRUCT) || object.getState().equals(ActorState.FINISHED))
                        continue;
                    object.onTick(deltaTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (Player player : GameWorld.getCharacters()) {
                try {
                    if (player == null) continue;
                    if (!player.component(PlayerComponents.WORLD_LEVEL_MANAGER).isWorldLevelLoaded()) continue;
                    player.component(PlayerComponents.CHARACTER_SYNCHRONIZE).onTick(deltaTime);
                    player.component(PlayerComponents.NPC_SYNCHRONIZE).onTick(deltaTime);
                    player.component(PlayerComponents.OBJECT_SYNCHRONIZE).onTick(deltaTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            //Loops through account and removes the update flags
            for (Player player : GameWorld.getCharacters()) {
                try {
                    if (player == null) continue;
                    if (!player.component(PlayerComponents.WORLD_LEVEL_MANAGER).isWorldLevelLoaded()) continue;
                    player.component(ActorComponents.FLAG_UPDATE).clearFlags();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Loops through npcs and clears their update flags
            for (NPC npc : GameWorld.getNpcs()) {
                try {
                    if (npc == null) continue;
                    if (npc.getState().equals(ActorState.CONSTRUCT) || npc.getState().equals(ActorState.FINISHED)) continue;
                    npc.component(ActorComponents.FLAG_UPDATE).clearFlags();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Loops through objects and clears their update flags.
            for (WorldObject worldObject : GameWorld.getObjects()) {
                try {
                    if (worldObject == null) continue;
                    if (worldObject.getState().equals(ActorState.CONSTRUCT) || worldObject.getState().equals(ActorState.FINISHED)) continue;
                    worldObject.component(ActorComponents.FLAG_UPDATE).clearFlags();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            lastFrameUpdate = System.currentTimeMillis();
            long difference = lastFrameUpdate - currentFrame;
            try {
                if (difference < PROCESSING_INTERVAL) {
                    Thread.sleep(PROCESSING_INTERVAL - difference);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}

