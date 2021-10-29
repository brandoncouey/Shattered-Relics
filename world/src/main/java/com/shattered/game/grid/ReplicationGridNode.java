package com.shattered.game.grid;

import com.shattered.game.actor.Actor;
import com.shattered.game.actor.character.Character;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.object.WorldObject;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.actor.projectile.Projectile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JTlr Frost 10/25/2019 : 3:59 PM
 */
@RequiredArgsConstructor
public class ReplicationGridNode extends ReplicationNode {


    /**
     * Represents the Coordinate of this Node.
     */
    @Getter
    private final GridCoordinate coordinate;

    /**
     * Represents all the GridNodes that are currently adjacent to this Node.
     */
    @Getter
    private final Map<GridCoordinate, ReplicationGridNode> adjacentNodes = new ConcurrentHashMap<>(8);

    /**
     * Represents the Map of Local Objects
     */
    @Getter
    private final Map<Integer, WorldObject> nodeObjects = new ConcurrentHashMap<>();

    /**
     * Represents the Map of Local NPCs
     */
    @Getter
    private final Map<Integer, NPC> nodeNPCS = new ConcurrentHashMap<>();

    /**
     * Represents the Map of Local Characters
     */
    @Getter
    private final Map<Integer, Player> nodePlayers = new ConcurrentHashMap<>();

    /**
     * Represents the Map of All Ongoing Projectiles
     */
    @Getter
    private final Map<Integer, Projectile> ongoingProjectiles = new ConcurrentHashMap<>();

    /**
     * Represents a Map of ALL current Adjacent Node Characters
     */
    @Getter
    private final Map<Integer, Player> adjacentNodePlayers = new ConcurrentHashMap<>();

    /**
     * Represents a Map of ALL current Adjacent Node NPCs
     */
    @Getter
    private final Map<Integer, NPC> adjacentNodeNPCs = new ConcurrentHashMap<>();

    /**
     * Represents a Map of ALL current Adjacent Node Players
     */
    @Getter
    private final Map<Integer, WorldObject> adjacentNodeObjects = new ConcurrentHashMap<>();

    /**
     * Creates an array list, and adds all possible characters into them.
     * @return a list of all characters.
     */
    public List<Character> getAllCharacters() {
        ArrayList<Character> local = new ArrayList<>();
        getAllPlayers().stream().filter(Objects::nonNull).forEach(local::add);
        getAllNPCs().stream().filter(Objects::nonNull).forEach(local::add);
        return local;
    }

    /**
     * Creates an array list, and adds all possible players into them.
     * @return a list of all the players
     */
    public List<Player> getAllPlayers() {
        ArrayList<Player> local = new ArrayList<>();
        nodePlayers.values().stream().filter(Objects::nonNull).forEach(local::add);
        adjacentNodePlayers.values().stream().filter(Objects::nonNull).forEach(local::add);
        return local;
    }

    /**
     * Creates an array list, and adds all possible npcs into them.
     * @return a list of all the npcs
     */
    public List<NPC> getAllNPCs() {
        ArrayList<NPC> local = new ArrayList<>();
        nodeNPCS.values().stream().filter(Objects::nonNull).forEach(local::add);
        adjacentNodeNPCs.values().stream().filter(Objects::nonNull).forEach(local::add);
        return local;
    }

    /**
     * Fetches the npc from current node or from the adjacent nodes
     * @return the npcs
     */
    public NPC getNPC(int index) {
        if (nodeNPCS.containsKey(index))
            return nodeNPCS.get(index);

        if (adjacentNodeNPCs.containsKey(index))
            return adjacentNodeNPCs.get(index);

        return null;
    }

    /**
     * Finds the npc with the given name
     * @param name
     * @return the npc
     */
    public NPC getNPC(String name) {
        Optional<NPC> target = nodeNPCS.values().stream().filter(Objects::nonNull).filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
        if (target.isPresent())
            return target.get();
        target = adjacentNodeNPCs.values().stream().filter(Objects::nonNull).filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
        return target.isPresent() ? null : target.get();
    }

    /**
     * Finds the player with the given name
     * @param name
     * @return the player
     */
    public Player getPlayer(String name) {
        Optional<Player> target = nodePlayers.values().stream().filter(Objects::nonNull).filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
        if (target.isPresent())
            return target.get();
        target = adjacentNodePlayers.values().stream().filter(Objects::nonNull).filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
        return target.isPresent() ? null : target.get();
    }

    /**
     * Fetches the player from current node or from the adjacent nodes
     * @return the player
     */
    public Player getPlayer(int index) {
        if (nodePlayers.containsKey(index))
            return nodePlayers.get(index);

        if (adjacentNodePlayers.containsKey(index))
            return adjacentNodePlayers.get(index);

        return null;
    }


    /**
     * Adds an Adjacent GridNode to the Current Grid Node
     * @param gridNode
     */
    public void addAdjacent(ReplicationGridNode gridNode) {
        if (gridNode == this) return;
        adjacentNodes.put(gridNode.getCoordinate(), gridNode);

        //Fills the new Cell with current node characters.
        for (Player nodePlayer : getNodePlayers().values()) {
            if (nodePlayer == null) continue;
            gridNode.getAdjacentNodePlayers().put(nodePlayer.getClientIndex(), nodePlayer);
        }

        //Fills the new cell with current node npcs.
        for (NPC nodeNPC : getNodeNPCS().values()) {
            if (nodeNPC == null) continue;
            gridNode.getAdjacentNodeNPCs().put(nodeNPC.getClientIndex(), nodeNPC);
        }

        //Fills the new Cell with current node objects
        for (WorldObject nodeObject : getNodeObjects().values()) {
            if (nodeObject == null) continue;
            gridNode.getAdjacentNodeObjects().put(nodeObject.getClientIndex(), nodeObject);
        }
    }


    /**
     * Method called upon adding an actor to the grid node.
     * @param actor
     */
    @Override
    public void onAdd(Actor actor) {
        if (actor instanceof Player) {
            getNodePlayers().put(actor.getClientIndex(), (Player) actor);

            for (ReplicationGridNode node : adjacentNodes.values()) {
                if (node == null) continue;
                //node.getAdjacentNodeCharacters().put(actor.getClientIndex(), (Character) actor);
            }
        }

        if (actor instanceof NPC) {
            getNodeNPCS().put(actor.getClientIndex(), (NPC) actor);
            for (ReplicationGridNode node : adjacentNodes.values()) {
                if (node == null) continue;
                //node.getAdjacentNodeNPCs().put(actor.getClientIndex(), (NPC) actor);
            }
        }

        if (actor instanceof WorldObject) {
            getNodeObjects().put(actor.getClientIndex(), (WorldObject) actor);
            for (ReplicationGridNode node : adjacentNodes.values()) {
                if (node == null) continue;
                // node.getAdjacentNodeObjects().put(actor.getClientIndex(), (WorldObject) actor);
            }
        }

        if (actor instanceof Projectile) {
            getOngoingProjectiles().put(actor.getClientIndex(), (Projectile) actor);
            for (ReplicationGridNode node : adjacentNodes.values()) {
                if (node == null) continue;
                // node.getAdjacentNodeProjectiles().put(actor.getClientIndex(), (WorldObject) actor);
            }
        }
    }

    /**
     * Method called upon removing from a grid node
     * @param actor
     */
    @Override
    public void onRemoved(Actor actor) {
        if (actor instanceof Player) {
            getNodePlayers().remove(actor.getClientIndex());
            for (ReplicationGridNode node : adjacentNodes.values()) {
                if (node == null) continue;
                node.getAdjacentNodePlayers().remove(actor.getClientIndex());
            }
        }

        if (actor instanceof NPC) {
            getNodeNPCS().remove(actor.getClientIndex());
            for (ReplicationGridNode node : adjacentNodes.values()) {
                if (node == null) continue;
                node.getAdjacentNodeNPCs().remove(actor.getClientIndex());
            }
        }

        if (actor instanceof WorldObject) {
            getNodeObjects().remove(actor.getClientIndex());
            for (ReplicationGridNode node : adjacentNodes.values()) {
                if (node == null) continue;
                node.getAdjacentNodeObjects().remove(actor.getClientIndex());
            }
        }
    }
}
