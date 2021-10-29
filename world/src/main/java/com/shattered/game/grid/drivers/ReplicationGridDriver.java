package com.shattered.game.grid.drivers;

import com.shattered.game.GameWorld;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.game.grid.ReplicationGridNode;
import com.shattered.game.grid.ReplicationNode;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JTlr Frost 10/25/2019 : 4:26 PM
 */
public class ReplicationGridDriver extends ReplicationDriver {

    /**
     * Represents a Map of Possible Zones
     */
    @Getter private final Map<GridCoordinate, ReplicationGridNode> nodes = new ConcurrentHashMap<>();

    /**
     *
     * @param sizeX
     * @param sizeY
     * @param tileSize
     */
    public ReplicationGridDriver(int sizeX, int sizeY, int tileSize) {
        super(sizeX, sizeY, tileSize);
    }

    /**
     *
     * @param actor
     * @return the node cell
     */
    @Override
    public ReplicationNode getNode(Actor actor) {
        GridCoordinate coord = actor.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation();
        int x = (int) Math.floor(coord.getX() / getTileSize());
        int y = (int) Math.floor(coord.getY() / getTileSize());
        ReplicationGridNode cell = nodes.get(new GridCoordinate(x, y));
        if (cell == null) {
            cell = createCell(x, y);
        }
        return cell;
    }

    /**
     * Creates a cell with the provided X and Y Coordinate, and appends
     *  all adjacent cells to the origin cell created.
     * @param x
     * @param y
     * @return the grid node
     */
    public ReplicationGridNode createCell(int x, int y) {
        GridCoordinate coordinate = new GridCoordinate(x, y);
        ReplicationGridNode cell = new ReplicationGridNode(coordinate);
        nodes.put(coordinate, cell);
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int yOffset = -1; yOffset <= 1; yOffset++) {
                int xx = x + xOffset;
                int yy = y + yOffset;
                ReplicationGridNode partner = nodes.get(new GridCoordinate(xx, yy));
                if (partner == null) continue;
                cell.addAdjacent(partner);
                if (!partner.getAdjacentNodes().containsKey(coordinate))
                    partner.addAdjacent(cell);
            }
        }
        return cell;
    }

    {

        for (int x = 0; x == getSizeX(); x++) {
            for (int y = 0; y == getSizeY(); y++) {
                nodes.put(new GridCoordinate(x, y), new ReplicationGridNode(new GridCoordinate(x, y)));
            }
        }

        for (int x = 0; x == getSizeX(); x++) {
            for (int y = 0; y == getSizeY(); y++) {
                ReplicationGridNode cell = nodes.get(new GridCoordinate(x, y));
                for (int xOffset = -1; xOffset <= 1; xOffset++) {
                    for (int yOffset = -1; yOffset <= 1; yOffset++) {
                        int xx = x + xOffset;
                        int yy = y + yOffset;
                        ReplicationGridNode partner = nodes.get(new GridCoordinate(xx, yy));
                        if (partner == null) continue;
                        cell.addAdjacent(partner);
                    }
                }
            }
        }
    }
}