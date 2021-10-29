package com.shattered.script;

import com.shattered.WorldConstants;
import com.shattered.datatable.tables.AbilityUDataTable;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.components.combat.CharacterCombatComponent;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.interaction.InteractionModifier;
import com.shattered.game.actor.character.player.component.widget.WidgetEventRepository;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.interaction.InteractionFlags;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.actor.object.WorldObject;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.actor.object.component.transform.Vector3;
import com.shattered.game.actor.object.item.Item;
import com.shattered.game.actor.vendor.VendorContainer;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.script.api.impl.PlayerAPI;
import com.shattered.script.api.impl.NpcAPI;
import com.shattered.script.api.impl.ObjectAPI;
import com.shattered.script.impl.ScriptLoader;
import com.shattered.script.types.*;
import com.shattered.system.SystemLogger;
import com.shattered.utilities.ecs.ProcessInterval;
import lombok.Getter;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ScriptManager {

    /**
     * Represents All of the Plugins
     */
    @Getter
    private static ArrayList<PlayerScript> playerScripts = new ArrayList<>();

    /**
     * Represents a Map of all NPC Scripts Storing the NPC Name as the Key Value
     */
    @Getter
    private static Map<String, NPCScript> npcScripts = new HashMap<>();

    /**
     * Represents a Map of all NPC Dialog Scripts Storing the NPC Name as the Key Value
     */
    @Getter
    private static Map<String, NPCDialogScript> npcDialogScripts = new HashMap<>();

    /**
     * Represents a Map of all NPC Combat Scripts Scripts Storing the NPC Name as the Key Value
     */
    @Getter
    private static Map<String, NPCCombatScript> npcCombatScripts = new HashMap<>();

    /**
     * Represents a Map of all Object Scripts Storing the ObjectName as the Key Value
     */
    @Getter
    private static Map<String, ObjectScript> objectScripts = new HashMap<>();

    /**
     * Represents a Map of all Action Scripts Storing the ObjectName as the Key Value
     */
    @Getter
    private static Map<String, ActionScript> actionScripts = new HashMap<>();

    /**
     * Represents a Map of all Actionable Scripts Storing the Object Name as the Key Value
     */
    @Getter
    public static Map<String, ObjectActionScript> objectActionScripts = new HashMap<>();

    /**
     * Represents a Map of all Ability Scripts Storing the Ability name as the Key value
     */
    @Getter
    public static Map<String, AbilityScript> abilityScripts = new HashMap<>();

    /**
     * Represents a Map of all Buff Scripts Storing the Buff name as the Key value
     */
    @Getter
    public static Map<String, BuffScript> buffScripts = new HashMap<>();

    /**
     * Represents a Map of all Widget Scripts Storing the Widget name as the Key value
     */
    @Getter
    public static Map<String, WidgetScript> widgetScripts = new HashMap<>();

    /**
     * Represents a Map of all Widget Scripts Storing the Widget name as the Key value
     */
    @Getter
    public static Map<String, ItemUseScript> itemUseScripts = new HashMap<>();

    /**
     * Represents a List of all Command Scripts
     */
    @Getter
    private static ArrayList<CommandScript> commandScripts = new ArrayList<>();

    /**
     * Represents a Map of all Quest Scripts
     */
    @Getter
    private static Map<String, QuestScript> questScripts = new HashMap();


    /**
     * Attempts to grab the version, otherwise it will return the base launch version
     * @return the script version
     */
    public static String getScriptVersion() {
        try {
            File version = new File("repository/scripts/version.json");
            Scanner reader = new Scanner(version);
            return reader.nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return WorldConstants.SCRIPT_VERSION;
    }

    /**
     * Initializes all the plugins.
     */
    public static void init(boolean reload) {
        SystemLogger.sendSystemMessage("Initializing Scripting Manager -> Loading Version=" + getScriptVersion() + ".");
        try {
            File pathToJar = new File("repository/scripts/scripts-all-" + getScriptVersion() + ".jar");

            JarFile jarFile;
            jarFile = new JarFile(pathToJar);
            Enumeration<JarEntry> e = jarFile.entries();

            URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if(je.isDirectory() || !je.getName().endsWith(".class")){
                    continue;
                }
                // -6 because of .class
                String className = je.getName().substring(0,je.getName().length() -6);
                className = className.replace('/', '.');

                if (className.contains("$"))
                    continue;

                Class c = cl.loadClass(className);
                Object script = c.newInstance();

                //TODO check
                if (script instanceof PlayerScript) {
                    playerScripts.add((PlayerScript) script);
                }

                if (script instanceof NPCDialogScript) {
                    NPCDialogScript npc = (NPCDialogScript) script;
                    if (npc.for_npcs() != null) {
                        for (String npcName : npc.for_npcs()) {
                            if (!npcDialogScripts.containsKey(npcName)) {
                                npcDialogScripts.put(npcName.toLowerCase().replace("_", " "), npc);
                            } else {
                                SystemLogger.sendSystemErrMessage("Could not register " + npc.getClass().getSimpleName() + " as a dialog script for " + npcName + " as they already are binded to the dialog script " + npcDialogScripts.get(npcName).getClass().getSimpleName() + "!");
                            }
                        }
                    }
                    if (npc.fornpc() != null) {
                        if (!npcDialogScripts.containsKey(npc.fornpc())) {
                            npcDialogScripts.put(npc.fornpc().toLowerCase().replace("_", " "), npc);
                        } else {
                            SystemLogger.sendSystemErrMessage("Could not register " + npc.getClass().getSimpleName() + " as a dialog script for " + npc.fornpc() + " as they already are binded to the dialog script " + npcDialogScripts.get(npc.fornpc()).getClass().getSimpleName() + "!");
                        }
                    }
                }

                if (script instanceof NPCCombatScript) {
                    NPCCombatScript npc = (NPCCombatScript) script;
                    if (npc.for_npcs() != null) {
                        for (String npcName : npc.for_npcs()) {
                            if (!npcCombatScripts.containsKey(npcName)) {
                                npcCombatScripts.put(npcName.toLowerCase().replace("_", " "), npc);
                            } else {
                                SystemLogger.sendSystemErrMessage("Could not register " + npc.getClass().getSimpleName() + " as a combat script for " + npcName + " as they already are binded to the combat script " + npcCombatScripts.get(npcName).getClass().getSimpleName() + "!");
                            }
                        }
                    }
                    if (npc.fornpc() != null) {
                        if (!npcCombatScripts.containsKey(npc.fornpc())) {
                            npcCombatScripts.put(npc.fornpc().toLowerCase().replace("_", " "), npc);
                        } else {
                            SystemLogger.sendSystemErrMessage("Could not register " + npc.getClass().getSimpleName() + " as a combat script for " + npc.fornpc() + " as they already are binded to the combat script " + npcCombatScripts.get(npc.fornpc()).getClass().getSimpleName() + "!");
                        }
                    }
                }

                if (script instanceof NPCScript) {
                    NPCScript npc = (NPCScript) script;
                    if (npc.for_npcs() != null) {
                        for (String npcName : npc.for_npcs()) {
                            if (!npcScripts.containsKey(npcName)) {
                                npcScripts.put(npcName, npc);
                            } else {
                                SystemLogger.sendSystemErrMessage("Could not register " + npc.getClass().getSimpleName() + " as a script for " + npcName + " as they already are binded to script " + npcScripts.get(npcName).getClass().getSimpleName() + "!");
                            }
                        }
                    }
                    if (npc.fornpc() != null) {
                        if (!npcScripts.containsKey(npc.fornpc())) {
                            npcScripts.put(npc.fornpc(), npc);
                        } else {
                            SystemLogger.sendSystemErrMessage("Could not register " + npc.getClass().getSimpleName() + " as a script for " + npc.fornpc() + " as they already are binded to script " + npcScripts.get(npc.fornpc()).getClass().getSimpleName() + "!");
                        }
                    }
                }

                if (script instanceof ActionScript && (!(script instanceof ObjectActionScript))) {
                    ActionScript action = (ActionScript) script;
                    if (action.action_name() != null) {
                        if (!actionScripts.containsKey(action.action_name())) {
                            actionScripts.put(action.action_name(), action);
                        } else {
                            SystemLogger.sendSystemErrMessage("Could not register " + action.getClass().getSimpleName() + " as a action script for " + action + " as they already are binded to the action script " + actionScripts.get(action).getClass().getSimpleName() + "!");
                        }
                    }

                }

                if (script instanceof ObjectActionScript) {
                    ObjectActionScript object = (ObjectActionScript) script;
                    if (object.for_objects() != null) {
                        for (String objName : object.for_objects()) {
                            if (!objectActionScripts.containsKey(objName)) {
                                objectActionScripts.put(objName.toLowerCase(), object);
                            } else {
                                SystemLogger.sendSystemErrMessage("Could not register " + object.getClass().getSimpleName() + " as a object action script for " + objName + " as they already are binded to the action script " + objectActionScripts.get(objName).getClass().getSimpleName() + "!");
                            }
                        }
                    }
                    if (object.for_object() != null) {
                        if (!objectActionScripts.containsKey(object.for_object())) {
                            objectActionScripts.put(object.for_object().toLowerCase(), object);
                        } else {
                            SystemLogger.sendSystemErrMessage("Could not register " + object.getClass().getSimpleName() + " as a object action script for " + object.for_object() + " as they already are binded to the action script " + objectActionScripts.get(object.for_object()).getClass().getSimpleName() + "!");
                        }
                    }
                }

                if (script instanceof ObjectScript) {
                    ObjectScript object = (ObjectScript) script;
                    if (object.for_objects() != null) {
                        for (String objectName : object.for_objects()) {
                            if (!objectScripts.containsKey(objectName)) {
                                objectScripts.put(objectName, object);
                            } else {
                                SystemLogger.sendSystemErrMessage("Could not register " + object.getClass().getSimpleName() + " as a script for " + objectName + " as they already are binded to script " + objectScripts.get(objectName).getClass().getSimpleName() + "!");
                            }
                        }
                    }
                    if (object.forobject() != null) {
                        if (!objectScripts.containsKey(object.forobject())) {
                            objectScripts.put(object.forobject(), object);
                        } else {
                            SystemLogger.sendSystemErrMessage("Could not register " + object.getClass().getSimpleName() + " as a script for " + object.forobject() + " as they already are binded to script " + objectScripts.get(object.forobject()).getClass().getSimpleName() + "!");
                        }
                    }
                }

                if (script instanceof ItemUseScript) {
                    ItemUseScript item = (ItemUseScript) script;
                    if (item.for_items() != null) {
                        for (String itemname : item.for_items()) {
                            if (!itemUseScripts.containsKey(itemname)) {
                                itemUseScripts.put(itemname.toLowerCase(), item);
                            } else {
                                SystemLogger.sendSystemErrMessage("Could not register " + item.getClass().getSimpleName() + " as a script for " + itemname + " as they already are binded to script " + itemUseScripts.get(itemname).getClass().getSimpleName() + "!");
                            }
                        }
                    }
                    if (item.foritem() != null) {
                        if (!itemUseScripts.containsKey(item.foritem())) {
                            itemUseScripts.put(item.foritem().toLowerCase(), item);
                        } else {
                            SystemLogger.sendSystemErrMessage("Could not register " + item.getClass().getSimpleName() + " as a script for " + item.foritem() + " as they already are binded to script " + itemUseScripts.get(item.foritem()).getClass().getSimpleName() + "!");
                        }
                    }
                }

                if (script instanceof CommandScript) {
                    commandScripts.add((CommandScript) script);
                }

                if (script instanceof WidgetScript) {
                    WidgetScript widget = (WidgetScript) script;
                    //Used for 'Core' Widgets (i.e trading)
                    if (WidgetEventRepository.isHandled(widget.forwidget())) {
                        SystemLogger.sendSystemErrMessage("Could not register " + widget.forwidget() + " as a widget script as it is already registered as a core widget script!");
                        return;
                    }
                    if (!widgetScripts.containsKey(widget.forwidget())) {
                        widgetScripts.put(widget.forwidget(), widget);
                    } else {
                        SystemLogger.sendSystemErrMessage("Could not register " + widget.forwidget() + " as a widget script as it is already registered for the script " + widgetScripts.get(widget.forwidget()).getClass().getSimpleName() + "!");
                    }
                }

                if (script instanceof AbilityScript) {
                    AbilityScript ability = (AbilityScript) script;
                    if (!abilityScripts.containsKey(ability.name().toLowerCase())) {
                        abilityScripts.put(ability.name().toLowerCase().replace("_", " "), ability);
                    } else {
                        SystemLogger.sendSystemErrMessage("Could not register " + ability.name() + " as an ability script as it is already registered for the script " + abilityScripts.get(ability.name()).getClass().getSimpleName() + "!");
                    }
                }

                if (script instanceof BuffScript) {
                    BuffScript buff = (BuffScript) script;
                    if (!buffScripts.containsKey(buff.name())) {
                        buffScripts.put(buff.name().toLowerCase().replace("_", " "), buff);
                    } else {
                        SystemLogger.sendSystemErrMessage("Could not register " + buff.name() + " as a buff script as it is already registered for the script " + buffScripts.get(buff.name()).getClass().getSimpleName() + "!");
                    }
                }

                if (script instanceof QuestScript) {
                    QuestScript quest = (QuestScript) script;
                    quest.construct();
                    if (!questScripts.containsKey(quest.name())) {
                        questScripts.put(quest.name().toLowerCase().replace("_", " "), quest);
                    } else {
                        SystemLogger.sendSystemErrMessage("Could not register " + quest.name() + " as a quest script as it is already registered for the script " + questScripts.get(quest.name()).getClass().getSimpleName() + "!");
                    }
                }

            }
            cl.close();

            SystemLogger.sendSystemMessage("Successfully loaded " + playerScripts.size() + " player scripts.");
            SystemLogger.sendSystemMessage("Successfully loaded " + npcScripts.size() + " npc scripts.");
            SystemLogger.sendSystemMessage("Successfully loaded " + npcDialogScripts.size() + " npc dialog scripts.");
            SystemLogger.sendSystemMessage("Successfully loaded " + npcCombatScripts.size() + " npc combat scripts.");
            SystemLogger.sendSystemMessage("Successfully loaded " + objectScripts.size() + " object scripts.");
            SystemLogger.sendSystemMessage("Successfully loaded " + commandScripts.size() + " command scripts.");
            SystemLogger.sendSystemMessage("Successfully loaded " + actionScripts.size() + " action scripts.");
            SystemLogger.sendSystemMessage("Successfully loaded " + itemUseScripts.size() + " item use scripts.");
            SystemLogger.sendSystemMessage("Successfully loaded " + objectActionScripts.size() + " object action scripts.");
            SystemLogger.sendSystemMessage("Successfully loaded " + widgetScripts.size() + " widget scripts.");
            SystemLogger.sendSystemMessage("Successfully loaded " + abilityScripts.size() + " ability scripts.");
            SystemLogger.sendSystemMessage("Successfully loaded " + buffScripts.size() + " buff scripts.");
            SystemLogger.sendSystemMessage("Successfully loaded " + questScripts.size() + " quest scripts.");
        } catch (Exception e1) {
            e1.printStackTrace();
            SystemLogger.sendSystemErrMessage("Failed to load scripts, Cause=" + e1.getMessage() + ", " + e1.getCause());
            if (!reload)
                System.exit(0);
        }
    }



    /**
     * Reloads the Scripts and Loads the new version.
     */
    public static void reloadScripts() {
        commandScripts.clear();
        npcScripts.clear();
        objectScripts.clear();
        playerScripts.clear();
        npcDialogScripts.clear();
        npcCombatScripts.clear();
        abilityScripts.clear();
        buffScripts.clear();
        questScripts.clear();
        widgetScripts.clear();
        actionScripts.clear();
        itemUseScripts.clear();
        objectActionScripts.clear();
        init(true);
        SystemLogger.sendSystemMessage("Reloading scripts...");
    }


    /**
     * Executes a Command Script
     * @param character
     * @param syntax
     * @return executed
     */
    public static boolean executeCommand(PlayerAPI character, String syntax, String... arguments) {
        for (CommandScript commandScript : commandScripts) {
            if (commandScript == null) continue;
            if (commandScript.name().toLowerCase().equalsIgnoreCase(syntax)) {
                commandScript.on_execute(character, arguments);
                return true;
            }
        }
        return false;
    }

    /**
     * Called Upon the Constructing of the Character
     * @param actor
     */
    public static void onAwake(Actor actor) {
        if (actor instanceof Player) {
            for (PlayerScript playerScript : playerScripts) {
                if (playerScript == null) continue;
                playerScript.on_awake(new PlayerAPI((Player) actor));
            }
        }
        if (actor instanceof NPC) {
            for (NPCScript npcScript : npcScripts.values()) {
                if (npcScript == null) continue;
                if (npcScript.for_npcs() != null) {
                    for (String n : npcScript.for_npcs()) {
                        if (n.toLowerCase().equalsIgnoreCase(actor.getName())) {
                            npcScript.on_awake(new NpcAPI((NPC) actor));
                            return;
                        }
                    }
                } else if (npcScript.fornpc() != null) {
                    if (npcScript.fornpc().toLowerCase().equalsIgnoreCase(actor.getName())) {
                        npcScript.on_awake(new NpcAPI((NPC) actor));
                        return;
                    }

                }

            }
        }
        if (actor instanceof WorldObject) {
            for (ObjectScript objectScript : objectScripts.values()) {
                if (objectScript == null) continue;
                if (objectScript.for_objects() != null) {
                    for (String n : objectScript.for_objects()) {
                        if (n.toLowerCase().equalsIgnoreCase(actor.getName())) {
                            objectScript.on_awake(new ObjectAPI((WorldObject) actor));
                            return;
                        }
                    }
                } else if (objectScript.forobject() != null) {
                    if (objectScript.forobject().toLowerCase().equalsIgnoreCase(actor.getName())) {
                        objectScript.on_awake(new ObjectAPI((WorldObject) actor));
                        return;
                    }
                }
            }
        }
    }

    /**
     * Called Upon the World Awake of the Character
     * This is called when they are visually in the world.
     * @param actor
     */
    public static void onWorldAwake(Actor actor) {
        if (actor instanceof Player) {
            for (PlayerScript script : playerScripts) {
                if (script == null) continue;
                script.on_world_awake(new PlayerAPI((Player) actor));
            }
        }
        if (actor instanceof NPC) {
            for (NPCScript npcScript : npcScripts.values()) {
                if (npcScript == null) continue;
                if (npcScript.for_npcs() != null) {
                    for (String n : npcScript.for_npcs()) {
                        if (n.toLowerCase().equalsIgnoreCase(actor.getName())) {
                            npcScript.on_world_awake(new NpcAPI((NPC) actor));
                            return;
                        }
                    }
                } else if (npcScript.fornpc() != null) {
                    if (npcScript.fornpc().toLowerCase().equalsIgnoreCase(actor.getName())) {
                        npcScript.on_world_awake(new NpcAPI((NPC) actor));
                        return;
                    }

                }
            }
        }
        if (actor instanceof WorldObject) {
            for (ObjectScript objectScript : objectScripts.values()) {
                if (objectScript == null) continue;
                if (objectScript.for_objects() != null) {
                    for (String n : objectScript.for_objects()) {
                        if (n.toLowerCase().equalsIgnoreCase(actor.getName())) {
                            objectScript.on_world_awake(new ObjectAPI((WorldObject) actor));
                            return;
                        }
                    }
                } else if (objectScript.forobject() != null) {
                    if (objectScript.forobject().toLowerCase().equalsIgnoreCase(actor.getName())) {
                        objectScript.on_world_awake(new ObjectAPI((WorldObject) actor));
                        return;
                    }

                }

            }
        }
    }

    /**
     * Called Each Game Logic Tick
     * @param actor
     */
    public static void onTick(Actor actor, long deltaTime) {
        if (actor instanceof Player) {
            for (PlayerScript playerScript : playerScripts) {
                if (playerScript == null) continue;
                playerScript.on_update(new PlayerAPI((Player) actor), deltaTime);
            }
        }
        if (actor instanceof NPC) {
            for (NPCScript npcScript : npcScripts.values()) {
                if (npcScript == null) continue;

                if (npcScript.for_npcs() != null) {
                    for (String n : npcScript.for_npcs()) {
                        if (n.toLowerCase().equalsIgnoreCase(actor.getName())) {
                            npcScript.on_update(new NpcAPI((NPC) actor), deltaTime);
                            return;
                        }
                    }
                } else if (npcScript.fornpc() != null) {
                    if (npcScript.fornpc().toLowerCase().equalsIgnoreCase(actor.getName())) {
                        npcScript.on_update(new NpcAPI((NPC) actor), deltaTime);
                        return;
                    }

                }
            }
        }
        if (actor instanceof WorldObject) {
            for (ObjectScript objectScript : objectScripts.values()) {
                if (objectScript == null) continue;
                if (objectScript.for_objects() != null) {
                    for (String n : objectScript.for_objects()) {
                        if (n.toLowerCase().equalsIgnoreCase(actor.getName())) {
                            objectScript.on_update(new ObjectAPI((WorldObject) actor), deltaTime);
                            return;
                        }
                    }
                } else if (objectScript.forobject() != null) {
                    if (objectScript.forobject().toLowerCase().equalsIgnoreCase(actor.getName())) {
                        objectScript.on_update(new ObjectAPI((WorldObject) actor), deltaTime);
                        return;
                    }

                }
            }
        }
    }

    /**
     * Called Upon Logging out / Force Close of the Character
     * @param actor
     */
    public static void onDeath(Actor actor, Actor source) {
        if (actor instanceof Player) {
            for (PlayerScript playerScript : playerScripts) {
                if (playerScript == null) continue;
                playerScript.on_death(new PlayerAPI((Player) actor), source instanceof NPC ? new NpcAPI((NPC) source) : new PlayerAPI((Player) source) );
            }
        }
        if (actor instanceof NPC) {
            for (NPCScript npcScript : npcScripts.values()) {
                if (npcScript == null) continue;
                if (npcScript.for_npcs() != null) {
                    for (String n : npcScript.for_npcs()) {
                        if (n.toLowerCase().equalsIgnoreCase(actor.getName())) {
                            npcScript.on_death(new NpcAPI((NPC) actor), source instanceof NPC ? new NpcAPI((NPC) source) : new PlayerAPI((Player) source) );
                            return;
                        }
                    }
                } else if (npcScript.fornpc() != null) {
                    if (npcScript.fornpc().toLowerCase().equalsIgnoreCase(actor.getName())) {
                        npcScript.on_death(new NpcAPI((NPC) actor), source instanceof NPC ? new NpcAPI((NPC) source) : new PlayerAPI((Player) source) );
                        return;
                    }

                }
            }
        }
    }

    /**
     * Called Upon Logging out / Force Close of the Character
     * @param actor
     */
    public static void onFinished(Actor actor) {
        if (actor instanceof Player) {
            for (PlayerScript playerScript : playerScripts) {
                if (playerScript == null) continue;
                playerScript.on_finished(new PlayerAPI((Player) actor));
            }
        }
        if (actor instanceof NPC) {
            for (NPCScript npcScript : npcScripts.values()) {
                if (npcScript == null) continue;
                if (npcScript.for_npcs() != null) {
                    for (String n : npcScript.for_npcs()) {
                        if (n.toLowerCase().equalsIgnoreCase(actor.getName())) {
                            npcScript.on_finished(new NpcAPI((NPC) actor));
                            return;
                        }
                    }
                } else if (npcScript.fornpc() != null) {
                    if (npcScript.fornpc().toLowerCase().equalsIgnoreCase(actor.getName())) {
                        npcScript.on_finished(new NpcAPI((NPC) actor));
                        return;
                    }

                }
            }
        }
        if (actor instanceof WorldObject) {
            for (ObjectScript objectScript : objectScripts.values()) {
                if (objectScript == null) continue;
                if (objectScript.for_objects() != null) {
                    for (String n : objectScript.for_objects()) {
                        if (n.toLowerCase().equalsIgnoreCase(actor.getName())) {
                            objectScript.on_finished(new ObjectAPI((WorldObject) actor));
                            return;
                        }
                    }
                } else if (objectScript.forobject() != null) {
                    if (objectScript.forobject().toLowerCase().equalsIgnoreCase(actor.getName())) {
                        objectScript.on_finished(new ObjectAPI((WorldObject) actor));
                        return;
                    }

                }
            }
        }
    }

    /**
     * Called upon the player moving
     * @param actor
     * @param fromTransform
     */
    public static void onMoved(Actor actor, GridCoordinate fromTransform) {

    }

    /**
     * Calls the On Normal Interact Event
     * @param player
     * @param actor
     */
    public static void onNormalInteract(Player player, Actor actor) {
        if (actor instanceof NPC) {
            NPC npc = (NPC) actor;
            if (npc.component(ActorComponents.INTERACTION).isFlagged(InteractionFlags.CAN_TALK_TO) && npcDialogScripts.containsKey(actor.getName().toLowerCase()) && npc.getState().equals(ActorState.ALIVE)) {
                try {
                    NPCDialogScript dialog = npcDialogScripts.get(actor.getName().toLowerCase()).getClass().newInstance();
                    player.component(PlayerComponents.DIALOG).setCurrentDialogue(dialog);
                    player.component(PlayerComponents.DIALOG).setCurrentNPC(npc);
                    player.component(PlayerComponents.WIDGET).showWidget("dialog");
                    dialog.on_start(new PlayerAPI(player), new NpcAPI(npc));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (npcScripts.containsKey(actor.getName().toLowerCase().replace("_", " "))) {
                    npcScripts.get(actor.getName().toLowerCase().replace("_", " ")).on_normal_interact(new PlayerAPI(player), new NpcAPI(npc));
                } else {
                    player.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("[NPC Unhandled] NPC Id=" + actor.getId() + ", Modifier=NORMAL, Location=" + actor.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().toString() + ".");
                }
            }
        }
        if (actor instanceof WorldObject) {

            //Ensures the object has an action script attached to it. - Otherwise it will check for it's normal script
            if (objectActionScripts.containsKey(actor.getName().toLowerCase().replace("_", " "))) {

                //Pulls the Object Action Script Instance
                ObjectActionScript objectActionScript = objectActionScripts.get(actor.getName().toLowerCase().replace("_", " "));

                if (objectActionScript == null) return;

                //Checks to ensure the primary modifier check is of this interaction type (Normal)
                if (objectActionScript.for_modifier() != null) {
                    //Ensures the object
                    if (!objectActionScript.for_modifier().equals(InteractionModifier.NORMAL))
                        return;

                    //Checks if it's inside of the modifier array
                } else if (objectActionScript.for_objects() != null) {
                    boolean isTriggerable = false;

                    if (objectActionScript.for_modifiers() != null) {
                        for (InteractionModifier modifier : objectActionScript.for_modifiers()) {
                            if (modifier.equals(InteractionModifier.NORMAL))
                                isTriggerable = true;
                        }
                    } else if (objectActionScript.for_modifier() != null)
                        isTriggerable = true;

                    //Ensures that the modifier is not within the array
                    if (!isTriggerable)
                        return;

                }

                try {
                    ObjectActionScript objectActionInstance = objectActionScript.getClass().newInstance();
                    objectActionInstance.setPlayer(new PlayerAPI(player));
                    objectActionInstance.setObj(new ObjectAPI((WorldObject) actor));
                    player.component(ActorComponents.ACTION).startAction(objectActionInstance);

                } catch (Exception e) {

                    //Since this failed, we will attempt to process the normal object script anyways.
                    if (objectScripts.containsKey(actor.getName().toLowerCase().replace("_", " ")))
                        objectScripts.get(actor.getName().toLowerCase()).on_normal_interact(new PlayerAPI(player), new ObjectAPI((WorldObject) actor));

                    SystemLogger.sendSystemErrMessage("ScriptManager.onNormalInteract() -> Failed to initiate Object Action Script for " + actor.getName() + "! -> " + e.getMessage());
                }
                //create new instance and set params

            } else  if (objectScripts.containsKey(actor.getName().toLowerCase().replace("_", " "))) {
                objectScripts.get(actor.getName().toLowerCase().replace("_", " ")).on_normal_interact(new PlayerAPI(player), new ObjectAPI((WorldObject) actor));
            } else {
                player.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("[Object Unhandled] Object Id=" + actor.getId() + ", Name=" + actor.getName() + ", Modifier=NORMAL, Location=" + actor.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().toString() + ".");
            }
        }
    }

    /**
     * Calls the On Shift Interact Event
     * @param player
     * @param actor
     */
    public static void onShiftInteract(Player player, Actor actor) {
        if (actor instanceof NPC) {
            NPC npc = (NPC) actor;
            if (npc.component(ActorComponents.INTERACTION).isFlagged(InteractionFlags.TRADEABLE) && npc.container(Containers.VENDOR).getDataTable() != null) {
                player.component(PlayerComponents.WIDGET).showWidget("vendor");
                player.component(PlayerComponents.WIDGET).showWidget("inventory");
                npc.container(Containers.VENDOR).addViewer(player);

            } else if (npcScripts.containsKey(actor.getName().toLowerCase())) {
                npcScripts.get(actor.getName().toLowerCase()).on_shift_interact(new PlayerAPI(player), new NpcAPI((NPC) actor));
            } else {
                player.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("[NPC Unhandled] NPC Id=" + actor.getId() + ", Modifier=SHIFT, Location=" + actor.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().toString() + ".");
            }
        }

        if (actor instanceof WorldObject) {
            //Ensures the object has an action script attached to it. - Otherwise it will check for it's normal script
            if (!objectActionScripts.containsKey(actor.getName().toLowerCase())) {

                //Pulls the Object Action Script Instance
                ObjectActionScript objectActionScript = objectActionScripts.get(actor.getName().toLowerCase());

                //Checks to ensure the primary modifier check is of this interaction type (Normal)
                if (objectActionScript.for_modifier() != null) {
                    //Ensures the object
                    if (!objectActionScript.for_modifier().equals(InteractionModifier.SHIFT))
                        return;

                    //Checks if it's inside of the modifier array
                } else if (objectActionScript.for_objects() != null) {
                    boolean isTriggerable = false;

                    for (InteractionModifier modifier : objectActionScript.for_modifiers()) {
                        if (modifier.equals(InteractionModifier.SHIFT))
                            isTriggerable = true;
                    }

                    //Ensures that the modifier is not within the array
                    if (!isTriggerable)
                        return;

                }

                try {
                    ObjectActionScript objectActionInstance = objectActionScript.getClass().newInstance();
                    objectActionInstance.setPlayer(new PlayerAPI(player));
                    objectActionInstance.setObj(new ObjectAPI((WorldObject) actor));
                    player.component(ActorComponents.ACTION).startAction(objectActionInstance);

                } catch (Exception e) {

                    //Since this failed, we will attempt to process the normal object script anyways.
                    if (objectScripts.containsKey(actor.getName().toLowerCase()))
                        objectScripts.get(actor.getName().toLowerCase()).on_normal_interact(new PlayerAPI(player), new ObjectAPI((WorldObject) actor));

                    SystemLogger.sendSystemErrMessage("ScriptManager.onNormalInteract() -> Failed to initiate Object Action Script for " + actor.getName() + "! -> " + e.getMessage());
                }
                //create new instance and set params


            } else if (objectScripts.containsKey(actor.getName().toLowerCase())) {
                objectScripts.get(actor.getName().toLowerCase()).on_shift_interact(new PlayerAPI(player), new ObjectAPI((WorldObject) actor));
            } else {
                player.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("[Object Unhandled] Object Id=" + actor.getId() + ", Name=" + actor.getName() + ", Modifier=SHIFT, Location=" + actor.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().toString() + ".");
            }
        }
    }

    /**
     * Calls the Cntrl Interact Event
     * @param player
     * @param actor
     */
    public static void onCntrlInteract(Player player, Actor actor) {
        if (actor instanceof NPC) {
            if (npcScripts.containsKey(actor.getName().toLowerCase())) {
                npcScripts.get(actor.getName().toLowerCase()).on_cntrl_interact(new PlayerAPI(player), new NpcAPI((NPC) actor));
            } else {
                player.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("[NPC Unhandled] NPC Id=" + actor.getId() + ", Modifier=CNTRL, Location=" + actor.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().toString() + ".");
            }
        }

        if (actor instanceof WorldObject) {
            //Ensures the object has an action script attached to it. - Otherwise it will check for it's normal script
            if (!objectActionScripts.containsKey(actor.getName().toLowerCase())) {

                //Pulls the Object Action Script Instance
                ObjectActionScript objectActionScript = objectActionScripts.get(actor.getName().toLowerCase());

                //Checks to ensure the primary modifier check is of this interaction type (Normal)
                if (objectActionScript.for_modifier() != null) {
                    //Ensures the object
                    if (!objectActionScript.for_modifier().equals(InteractionModifier.SHIFT_CNTRL))
                        return;

                    //Checks if it's inside of the modifier array
                } else if (objectActionScript.for_objects() != null) {
                    boolean isTriggerable = false;

                    for (InteractionModifier modifier : objectActionScript.for_modifiers()) {
                        if (modifier.equals(InteractionModifier.SHIFT_CNTRL))
                            isTriggerable = true;
                    }

                    //Ensures that the modifier is not within the array
                    if (!isTriggerable)
                        return;

                }

                if (player.component(ActorComponents.ACTION).isDoingAction())
                    return;

                try {

                    ObjectActionScript objectActionInstance = objectActionScript.getClass().newInstance();
                    objectActionInstance.setPlayer(new PlayerAPI(player));
                    objectActionInstance.setObj(new ObjectAPI((WorldObject) actor));
                    player.component(ActorComponents.ACTION).startAction(objectActionInstance);

                } catch (Exception e) {

                    //Since this failed, we will attempt to process the normal object script anyways.
                    if (objectScripts.containsKey(actor.getName().toLowerCase()))
                        objectScripts.get(actor.getName().toLowerCase()).on_normal_interact(new PlayerAPI(player), new ObjectAPI((WorldObject) actor));

                    SystemLogger.sendSystemErrMessage("ScriptManager.onNormalInteract() -> Failed to initiate Object Action Script for " + actor.getName() + "! -> " + e.getMessage());
                }
                //create new instance and set params


            } else  if (objectScripts.containsKey(actor.getName().toLowerCase())) {
                objectScripts.get(actor.getName().toLowerCase()).on_cntrl_interact(new PlayerAPI(player), new ObjectAPI((WorldObject) actor));
            } else {
                player.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("[Object Unhandled] Object Id=" + actor.getId() + ", Name=" + actor.getName() + ", Modifier=CNTRL, Location=" + actor.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().toString() + ".");
            }
        }
    }

    /**
     * Starts doing the specified action for the player
     * @param player
     * @param actionName
     * @param params
     */
    public static void startAction(Player player, String actionName, Object... params) {
        if (player.component(ActorComponents.ACTION).isDoingAction())
            return;
        try {
            if (actionScripts.containsKey(actionName)) {
                ActionScript action = actionScripts.get(actionName).getClass().getDeclaredConstructor(Object[].class).newInstance(params);
                action.setPlayer(new PlayerAPI(player));
                player.component(ActorComponents.ACTION).startAction(action);
            }
        } catch (Exception e) {
            SystemLogger.sendSystemErrMessage("ScriptManager.startAction() -> Failed to initiate Action Script for " + player.getName() + "! -> " + e.getMessage());
        }
    }

    /**
     * Method called upon clicking on a registered widget
     * @param widgetName
     * @param player
     * @param buttonId
     */
    public static void onClickedWidget(String widgetName, Player player, int buttonId) {
        if (widgetScripts.containsKey(widgetName)) {
            WidgetScript script = widgetScripts.get(widgetName);
            script.on_clicked_widget(new PlayerAPI(player), null, buttonId);
        }
    }

    /**
     * Method called upon clicking on a registered widget
     * @param widgetName
     * @param player
     * @param parameters
     * @param buttonId
     */
    public static void onClickedWidget(String widgetName, Player player, List<Integer> parameters, int buttonId) {
        if (WidgetEventRepository.isHandled(widgetName)) {
            WidgetEventRepository.onClickedWidget(widgetName, player, parameters, buttonId);
            return;
        }

        if (widgetScripts.containsKey(widgetName)) {
            WidgetScript script = widgetScripts.get(widgetName);
            script.on_clicked_widget(new PlayerAPI(player), parameters, buttonId);
        }
    }

    /**
     * Method called upon clicking of a 'use' item
     * @param player
     * @param item
     * @param slotId
     * @return handled
     */
    public static boolean useItem(Player player, Item item, int slotId) {
        String itemName = item.getName().toLowerCase();
        if (itemUseScripts.containsKey(itemName)) {
            ItemUseScript itemUseScript = itemUseScripts.get(item.getName().toLowerCase());
            itemUseScript.on_use(new PlayerAPI(player), item, slotId);
            return true;
        }
        return false;
    }

    /**
     * Method used for triggering an ability with no coordinate
     * @param character
     * @param name
     * @return
     */
    public static boolean useAbility(PlayerAPI character, String name) {
        return useAbility(character, name, null, 0);
    }

    /**
     * Methoed used for triggering an ability
     * @param character
     * @param name
     */
    public static boolean useAbility(PlayerAPI character, String name, GridCoordinate coordinate, float pitch) {
        try {
            if (abilityScripts.containsKey(name.toLowerCase())) {
                AbilityScript script = abilityScripts.get(name);
                AbilityUDataTable table = AbilityUDataTable.forName(name);
                if (table == null)  {
                    SystemLogger.sendSystemErrMessage("Invalid ability data table for the ability " + name + ".");
                    return false;
                }

                //TODO passive check for stuns.

                CharacterCombatComponent combat = character.getPlayer().component(CharacterComponents.COMBAT);
                if (combat.isStunned() && !table.isCanUseWhileStunned())
                    return false;
                if (combat.isSnared() && !table.isCanUseWhleSnared())
                    return false;
                if (combat.isDisarmed() && !table.isCanUseWhileDisarmed())
                    return false;
                if (combat.isMagicLocked() && !table.isCanUseWhileMagicLocked())
                    return false;
                if (combat.isDisoriented() && !table.isCanUseWhileDisoriented())
                    return false;

                if (character.getPlayer().component(CharacterComponents.COMBAT).isCasting()) return false;

                if (script.can_use(character)) {
                    script.setSelectedCoordinate(coordinate);
                    script.setPitch(pitch);

                    int castTime = table.getCastTime();

                    //TODO we must add the calculation of cast speed as this will differ
                    //TODO from each entity to alter the duration

                    if (castTime <= 0) //Instant cast
                        script.on_use(character);

                    else {//Casting
                        //Prevents double trying to cast. Can only cast one at a time
                        script.on_cast(character);
                        character.getPlayer().component(CharacterComponents.COMBAT).setCastingAbility(character.zone.getLocation(), script, (System.currentTimeMillis() + (castTime * 10)));
                        character.getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_DISPLAY_CAST_TIME, World.UpdateCastTime.newBuilder().setAbilityId(table.getId() << 1 | (script.is_channel() ? 1 : 0)).setDuration(castTime).build());
                    }
                    return true;
                }
            } else {
                SystemLogger.sendSystemErrMessage("Unable to find the ability script by the name of " + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gets the buff script for the appopriate name
     * @param name
     * @return the buff script
     */
    public static BuffScript getBuffScript(String name) {
        if (buffScripts.containsKey(name.toLowerCase().replace("_", " ")))
            return buffScripts.get(name.toLowerCase().replace("_", " "));
        return null;
    }

    /**
     * Gets the quest script for the appopriate name
     * @param name
     * @return the quest script
     */
    public static QuestScript getQuestScript(String name) {
        if (questScripts.containsKey(name.toLowerCase().replace("_", " ")))
            return questScripts.get(name.toLowerCase().replace("_", " "));
        return null;
    }

    /**
     * Gets the NPC Combat Script for NPC Name
     * @param name
     * @return the npc combat script
     */
    public static NPCCombatScript getNPCCombatScript(String name) {
        if (npcCombatScripts.containsKey(name.toLowerCase().replace("_", " ")))
            return npcCombatScripts.get(name.toLowerCase().replace("_", " "));
        return null;
    }

}
