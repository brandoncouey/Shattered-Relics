package com.shattered.game.grid.builders;

import com.shattered.datatable.tables.NPCUDataTable;
import com.shattered.datatable.tables.ObjectUDataTable;
import com.shattered.game.GameWorld;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.components.combat.CharacterCombatComponent;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.interaction.InteractionFlags;
import com.shattered.game.actor.object.WorldObject;
import com.shattered.game.actor.object.component.transform.Vector3;
import com.shattered.game.actor.object.component.transform.Rotation;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.game.volume.trigger.TriggerVolume;
import com.shattered.system.SystemLogger;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author JTlr Frost 10/25/2019 : 4:26 PM
 */
public class GridNodeBuilder {


    /**
     * Parses the NPC Exports
     */
    public static void parseNPCsExports() {
        try {

            BufferedReader reader = new BufferedReader(new FileReader("/repository/exports/npcs.txt"));
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                String[] split = line.split(",");
                String name = convertInstanceToName(split[0].split("=")[1]);
                NPCUDataTable table = NPCUDataTable.forName(name);
                if (table == null)  {
                    SystemLogger.sendSystemErrMessage("Unable to process NPC Spawn for blueprint instance: " + name + ", make sure the blueprint name matches spacing with _!");
                    continue;
                }
                int locX = Integer.parseInt(split[1].split("=")[1]);
                int locY = Integer.parseInt(split[2].split("=")[1]);
                int locZ = Integer.parseInt(split[3].split("=")[1]);
                int rotYaw = Integer.parseInt(split[4].split("=")[1]);
                boolean talkable = Integer.parseInt(split[5].split("=")[1]) == 1;
                boolean tradeable = Integer.parseInt(split[6].split("=")[1]) == 1;
                boolean pickpocketable = Integer.parseInt(split[7].split("=")[1]) == 1;
                boolean attackable = Integer.parseInt(split[8].split("=")[1]) == 1;
                String hostility = ""/*split[9].split("=")[1]*/;
                count++;



                NPC npc = new NPC(table.getId(), new GridCoordinate(locX, locY, locZ), new Rotation(rotYaw));
                npc.component(ActorComponents.INTERACTION).toggle(InteractionFlags.CAN_TALK_TO, talkable);
                npc.component(ActorComponents.INTERACTION).toggle(InteractionFlags.TRADEABLE, tradeable);
                npc.component(ActorComponents.INTERACTION).toggle(InteractionFlags.PICKPOCKETABLE, pickpocketable);
                npc.component(ActorComponents.INTERACTION).toggle(InteractionFlags.ATTACKABLE, attackable);

                if (talkable)
                    npc.component(ActorComponents.INTERACTION).getSpawnFlags().add(InteractionFlags.CAN_TALK_TO);

                if (tradeable)
                    npc.component(ActorComponents.INTERACTION).getSpawnFlags().add(InteractionFlags.TRADEABLE);

                if (pickpocketable)
                    npc.component(ActorComponents.INTERACTION).getSpawnFlags().add(InteractionFlags.PICKPOCKETABLE);

                if (attackable)
                    npc.component(ActorComponents.INTERACTION).getSpawnFlags().add(InteractionFlags.ATTACKABLE);

            }
            SystemLogger.sendSystemMessage("Successfully loaded " + count + " npc spawns.");
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses the NPC Exports
     */
    public static void parseMapVolumeExports() {
        try {

            BufferedReader reader = new BufferedReader(new FileReader("/repository/exports/map-volumes.txt"));
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");
                String name = split[0].split("=")[1];
                int originX = Integer.parseInt(split[1].split("=")[1]);
                int originY = Integer.parseInt(split[2].split("=")[1]);
                int originZ = Integer.parseInt(split[3].split("=")[1]);
                int extentX = Integer.parseInt(split[4].split("=")[1]);
                int extentY = Integer.parseInt(split[5].split("=")[1]);
                int extentZ = Integer.parseInt(split[6].split("=")[1]);
                TriggerVolume triggerVolume = new TriggerVolume(new Vector3(originX, originY, originZ), new Vector3(extentX, extentY, extentZ));
                GameWorld.getVolumes().put(name, triggerVolume);
                count++;
            }
            SystemLogger.sendSystemMessage("Successfully loaded " + count + " map volumes.");
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Parses the NPC Exports
     */
    public static void parseLocationPoints() {
        try {

            BufferedReader reader = new BufferedReader(new FileReader("/repository/exports/location-points.txt"));
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");
                String name = split[0].split("=")[1];
                int locationX = Integer.parseInt(split[1].split("=")[1]);
                int locationY = Integer.parseInt(split[2].split("=")[1]);
                int locationZ = Integer.parseInt(split[3].split("=")[1]);
                GameWorld.getLocationPoints().put(name.toLowerCase(), new GridCoordinate(locationX, locationY, locationZ));
                count++;
            }
            SystemLogger.sendSystemMessage("Successfully loaded " + count + " location points.");
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Parses the Object Exports
     */
    public static void parseObjectsExports() {
        try {

            BufferedReader reader = new BufferedReader(new FileReader("/repository/exports/objects.txt"));
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");
                String name = convertInstanceToName(split[0].split("=")[1]);
                ObjectUDataTable table = ObjectUDataTable.forName(name);
                if (table == null)  {
                    SystemLogger.sendSystemErrMessage("Unable to process Object Spawn for blueprint instance: " + name + ", make sure the blueprint name matches to the specific object name!");
                    continue;
                }
                float locX = Float.parseFloat(split[1].split("=")[1]);
                float locY = Float.parseFloat(split[2].split("=")[1]);
                float locZ = Float.parseFloat(split[3].split("=")[1]);
                float rotYaw = Float.parseFloat(split[4].split("=")[1]);
                boolean interactable = Integer.parseInt(split[5].split("=")[1]) == 1;
                WorldObject worldObject = new WorldObject(table.getId(), new GridCoordinate(locX, locY, locZ), new Rotation(rotYaw));
                worldObject.component(ActorComponents.INTERACTION).toggle(InteractionFlags.CAN_INTERACT_WITH, interactable);
                count ++;
            }
            SystemLogger.sendSystemMessage("Successfully loaded " + count + " object spawns.");
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String convertInstanceToName(String instance) {
        StringBuilder builder = new StringBuilder();
        String[] segments = instance.split("_");
        for (int index = 1; index < segments.length; index++) {
            builder.append(segments[index].replaceAll("[0-9]", ""));
            if (index < segments.length - 1)
                builder.append("_");
        }
        return builder.toString();
    }
   
}
