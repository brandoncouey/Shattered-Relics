package com.shattered.game.component;

import com.google.protobuf.GeneratedMessageV3;
import com.shattered.account.Account;
import com.shattered.game.GameObject;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.character.Character;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.components.variable.ActorTransVariableComponent;
import com.shattered.game.actor.container.Container;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.variable.ActorVariableComponent;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.actor.object.component.transform.Vector3;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.Components;
import lombok.NonNull;

import java.util.List;

/**
 * @author JTlr Frost 11/6/2019 : 9:39 PM
 */
public abstract class WorldComponent extends Component {
    
    
    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public WorldComponent(Object gameObject) {
        super(gameObject);
    }

    /**
     * Gets the Vars
     * @return
     */
    public ActorVariableComponent getVars() {
        return getActor().component(ActorComponents.VAR);
    }

    /**
     * Gets the Transiet Vars
     * @return
     */
    public ActorTransVariableComponent getTVars() {
        return getActor().component(ActorComponents.TRANS_VAR);
    }

    /**
     * Method called upon flags being cleared.
     */
    public void onClearedFlags() {

    }

    /**
     * Method called upon the actor dying.
     */
    public void onDeath(Actor source) {

    }

    /**
     * If the component is handling the certain widget
     *      overriding this and setting the widget name will prevent the API from being able to modify it.
     *              This should only be used if widgets are CORE online i.e inventory
     * @param buttonId
     * @return the widget you're handling
     */
    public String onWidgetHandle(int buttonId, @NonNull List<Integer> parameters) {
        return null;
    }

    /**
     * Gets a piece of Game Object Component
     * @param components
     */
    public <T extends Component> T component(Components<T> components) {
        return getActor().getComponentManager().get(components);
    }

    /**
     * Gets a Container Object.
     * @param components
     * @param <T>
     * @return
     */
    public <T extends Container> T container(Containers<T> components) {
        return (T) getPlayer().getContainerManager().get(components);
    }

    /**
     * Sends a Packet for the {@link Player} with EmptyPayload for the message
     * @param opcode
     */
    public void sendMessage(PacketOuterClass.Opcode opcode) {
        if (isPlayer())
            getPlayer().sendMessage(opcode);
    }

    /**
     * Sends a Packet for the {@link Player}
     * @param opcode
     * @param message
     */
    public void sendMessage(PacketOuterClass.Opcode opcode, GeneratedMessageV3 message) {
        if (isPlayer())
            getPlayer().sendMessage(opcode, message);
    }

    /**
     * Sends a Default Message
     * @param message
     */
    public void sendDefaultMessage(String message) {
        component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage(message);
    }

    /**
     * Checks if this game object and the specified game object is within a certain distance.
     * @param object
     * @param units
     * @return is within the distance units
     */
    public boolean isWithinDistance(GameObject object, int units) {
        if (object == null)
            return false;
        return getGameObject().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().distanceTo(object.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation()) <= units;
    }

    /**
     * The distance between this game object and the specified game object
     * @param object
     * @return the distance in ue4 units
     */
    public int distanceTo(GameObject object) {
        if (object == null)
            return Integer.MAX_VALUE;//Just set it far away.
        return getGameObject().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().distanceTo(object.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation());
    }

    /**
     * The distance between this game object and the specified Vector
     * @param target
     * @return the distance in ue4 units
     */
    public int distanceTo(Vector3 target) {
        return getGameObject().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().distanceTo(target);
    }

    /**
     * Gets the {@link GameObject} from the Object
     * @return
     */
    public GameObject getGameObject() {
        return (GameObject) gameObject;
    }


    /**
     * Gets the {@link Actor} from the {@link GameObject}
     * @return
     */
    public Actor getActor() { return (Actor) gameObject; }

    /**
     * Gets the {@link Character} from the {@link Character}
     * @return
     */
    public Character getCharacter() { return (Character) gameObject; }

    /**
     * Gets the {@link NPC} from the {@link GameObject}
     * @return
     */
    public NPC getNPC() {
        return (NPC) gameObject;
    }

    /**
     * Gets the {@link Player} from the {@link GameObject}
     * @return
     */
    public Player getPlayer() {
        return (Player) gameObject;
    }

    /**
     * Checks if the game object is a Character
     * @return
     */
    public boolean isPlayer() {
        return gameObject instanceof Player;
    }

    /**
     * Checks if the game object is a NPC
     * @return
     */
    public boolean isNPC() {
        return gameObject instanceof NPC;
    }

}
