package com.shattered.system.commands;

import com.shattered.game.GameWorld;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.grid.ReplicationGridNode;
import com.shattered.system.SystemCommand;

public class NeighbourNPCS implements SystemCommand {
    /**
     * @return the Syntaxs
     */
    @Override
    public String[] getSyntax() {
        return new String[] {"nnpcs"};
    }

    /**
     * @param args
     * @return
     */
    @Override
    public boolean execute(String... args) {
        int npcIndex = Integer.parseInt(args[1]);
        NPC npc = GameWorld.findNPC(npcIndex);
        if (npc != null) {
            ReplicationGridNode node = (ReplicationGridNode) GameWorld.getReplicationGrid().getNode(npc);
            System.out.println("NPCS:" + node.getNodeNPCS().size());
            for (ReplicationGridNode adjacent : node.getAdjacentNodes().values()) {
                System.out.println(adjacent.getNodeNPCS().size());
            }
        }
        return true;
    }
}
