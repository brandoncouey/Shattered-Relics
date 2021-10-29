package com.shattered.game.actor.components.movement;

import com.shattered.account.Account;
import com.shattered.datatable.UDataTableRepository;
import com.shattered.datatable.tables.AnimSequenceUDataTable;
import com.shattered.datatable.tables.MountUDataTable;
import com.shattered.engine.tasks.DelayedTaskTicker;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.ActorType;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.components.combat.CharacterCombatComponent;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.flags.FlagType;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.actor.object.component.transform.TransformComponent;
import com.shattered.game.actor.object.component.transform.Vector3;
import com.shattered.game.actor.object.component.transform.Rotation;
import com.shattered.game.component.WorldComponent;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.WorldProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.script.ScriptManager;
import com.shattered.script.api.impl.PlayerAPI;
import com.shattered.utilities.VariableUtility;
import com.shattered.utilities.VectorUtilities;
import com.shattered.utilities.ecs.PriorityComponent;
import com.shattered.utilities.ecs.ProcessComponent;
import jotunheim.vmap.NavRequestPath;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author JTlr Frost 11/30/2019 : 7:57 AM
 */
@PriorityComponent
@ProcessComponent(interval = 0.f)
public class ActorMovementComponent extends WorldComponent {


    /**
     * Represents the Timestamp of when the last time you moved was.
     */
    @Getter
    @Setter
    private long lastTimeMoved;

    /**
     * Represents the Moving to Target Tile
     */
    @Getter @Setter
    private LinkedList<Vector3> currentPath = new LinkedList<>();

    @Getter
    private Vector3 currentDestination;

    private int currentPointIndex;

    private boolean update;


    /**
     * Represents the Int to Direction
     */
    public static final int FORWARD_LEFT = 1, FORWARD_RIGHT = 2, BACKWARD_LEFT = 3, BACKWARD_RIGHT = 4, FORWARD = 5, BACKWARD = 6, LEFT = 7, RIGHT = 8;

    /**
     * Represents Movement Increase Modifiers
     */
    @Getter
    private int movementModifiers;

    /**
     * Represents the duration since the last roll
     */
    @Getter
    @Setter
    private long rollDelay;

    /**
     * Represents the Roll Invulnerability
     */
    @Getter
    @Setter
    private long rollInvulnerability;

    /**
     * Represents the direction of the last roll
     */
    @Getter
    @Setter
    public int lastRollDirection;

    /**
     * Represents the current speed of the actor.
     */
    @Setter
    private int speed = 400;

    /**
     * Represents their current direction (need to ensure this is the same as the yaw rotation)
     */
    @Getter @Setter
    private float direction;

    /**
     * Represents if the current actor is moving
     */
    @Setter
    public boolean moving;

    /**
     * Represents if the Actor is Walking
     */
    @Getter
    @Setter
    private boolean running = true;//Mostly for npcs

    /**
     * Represents the previous movement update (Players)
     */
    private long lastMovementUpdate;

    /**
     * Represents the Players Last Movement Input
     */
    @Getter
    @Setter
    private World.CharacterMovementInput lastMovementInput;

    /**
     * Represents the Movement Flags
     */
    @Getter @Setter
    private int flags;

    /**
     * Represents a List of Movement Flag Types to Update
     */
    private List<MovementFlags> flagTypes = new CopyOnWriteArrayList<>();


    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public ActorMovementComponent(Object gameObject) {
        super(gameObject);
    }

    /**
     * Locks the Actor from Moving
     */
    public void lock() {
        flag(MovementFlags.LOCKED);
    }

    /**
     * Unlocks the {@link Actor}'s ability to move.
     */
    public void unlock() {
        unflag(MovementFlags.LOCKED);
    }

    /**
     * Checks if the Actor is locked
     * @return locked
     */
    public boolean isLocked() { return isFlagged(MovementFlags.LOCKED); }


    /**
     * Locks the {@link Actor}'s ability to move for the duration in 500ms * delay.
     * @param delay
     */
    public void lock(int delay) {
        lock();
        DelayedTaskTicker.delayTask(this::unlock, delay);
    }

    /**
     *
     * @param actorType
     * @param location
     * @param rotation
     * @param flags
     */
    public void sendMovementUpdate(ActorType actorType, Vector3 location, Rotation rotation, int... flags) {
        switch (actorType) {
            case NPC:
                break;
            case CHARACTER:
                break;
        }
    }

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
        if (isPlayer()) {
            ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_REQUEST_ROLL, new WorldProtoListener<World.RequestRollDirection>() {

                /**
                 * @param message
                 * @param player
                 */
                @Override
                public void handle(World.RequestRollDirection message, Player player) {
                    player.component(ActorComponents.MOVEMENT).onRoll(message.getDirection());
                }
            }, World.RequestRollDirection.getDefaultInstance());
        }
        if (isNPC()) {
            if (getNPC().getDataTable() != null) {
                setSpeed(getNPC().getDataTable().getSpeed());
                setRunning(false);
            }
        }
    }

    /**
     * Sends the 'Roll' Movement for Ability
     * @param direction
     */
    public void onRoll(int direction) {
        if (!isRolling()) {
            if (isPlayer()) {
                PlayerAPI api = new PlayerAPI(getPlayer());
                setRollDelay(System.currentTimeMillis() + 450);
                setRollInvulnerability(System.currentTimeMillis() + 1000);
                switch (direction) {
                    case FORWARD:
                        ScriptManager.useAbility(api, "roll forward");
                        break;
                    case FORWARD_LEFT:
                        ScriptManager.useAbility(api, "roll forward left");
                        break;
                    case FORWARD_RIGHT:
                        ScriptManager.useAbility(api, "roll forward right");
                        break;
                    case LEFT:
                        ScriptManager.useAbility(api, "roll left");
                        break;
                    case BACKWARD:
                        ScriptManager.useAbility(api, "roll backward");
                        break;
                    case BACKWARD_LEFT:
                        ScriptManager.useAbility(api, "roll backward left");
                        break;
                    case BACKWARD_RIGHT:
                        ScriptManager.useAbility(api, "roll backward right");
                        break;
                    case RIGHT:
                        ScriptManager.useAbility(api, "roll right");
                        break;
                }
            }
        }
    }



    /**
     * Checks if the player is currently rolling
     * @return rolling
     */
    public boolean isRolling() {
        return System.currentTimeMillis() < getRollDelay();
    }

    /**
     * Handles the Input Movement for the Player
     * @param message
     */
    public void handleTransform(World.CharacterMovementInput message) {
        /*if ((!component(PlayerComponents.WORLD_LEVEL_MANAGER).isWorldLevelLoaded() && !component(PlayerComponents.WORLD_LEVEL_MANAGER).isWorldLoaded()) || !canMove()) {
            //Send Forced Movement
            component(ActorComponents.FLAG_UPDATE).flag(FlagType.TRANSFORM);
            return;
        }*/
        //TODO on moved..
        long previousUpdate = lastMovementUpdate;

        if (previousUpdate != 0L) {

            long timeElapsed = message.getTime() - previousUpdate;

            if (timeElapsed < 0) {//Discarding out of date packet
                System.out.println("Dis packet out of date, fuck u my guy");
                return;
            }

            float elapsedSeconds = (float) timeElapsed / 1000.f;
            Vector3 displacement = VectorUtilities.subtract(component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation(), new Vector3(message.getPosition().getX(), message.getPosition().getY(), message.getPosition().getZ()));
            double distanceXY = displacement.flatten().length();
            double distanceZ = Math.abs(displacement.getZ());
            double averageXYVelocity = distanceXY / elapsedSeconds;

            if (averageXYVelocity > 800d) {
                //Send Forced movement
                //This mf hacking speed
                System.out.println("Get your hackin ass outa here: " + averageXYVelocity);
                return;
            } else {
                System.out.println("Velocity is " + averageXYVelocity);
            }
        }
        lastMovementUpdate = message.getTime();
        ScriptManager.onMoved(getActor(), component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation());
        component(ActorComponents.MOVEMENT).setLastTimeMoved(System.currentTimeMillis());
        component(GameObjectComponents.TRANSFORM_COMPONENT).setTransform(new GridCoordinate(message.getPosition().getX(), message.getPosition().getY(), message.getPosition().getZ()), new Rotation((int) message.getDirection()));
        component(ActorComponents.FLAG_UPDATE).flag(FlagType.TRANSFORM);
    }

    /**
     * Adds a path to the actors movement queue
     * @param target
     */
    public boolean addPathToMovementQueue(Vector3 target) {
        if (!canMove()) {
            stop();
            return false;
        }
        try {
            List<Vector3> path = NavRequestPath.findPath("Systems", component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation(), target);
            if (path == null) return false;
            currentDestination = null;
            for (Vector3 point : path) {
                getCurrentPath().add(point);
            }
            currentPointIndex = -1;
            update = true;
        } catch (Exception e) {
        }
        return true;
    }


    @Override
    public void onTick(long deltaTime) {
        super.onTick(deltaTime);

        //Player doesn't have to deal with this bs
        if (isPlayer()) return;

        if (!canMove() && isFlagged(MovementFlags.FORWARD)) {
            stop();
            return;
        }

        double seconds = (double) deltaTime / 1000.d;

        Vector3 currentDest = currentDestination;
        Vector3 currentPosition = component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation();

        if (((currentDest == null || (VectorUtilities.subtract(currentPosition, currentDest)).flatten().length() < 10) || currentPointIndex == -1) && !currentPath.isEmpty()) {
            nextPoint();
        }

        Vector3 dest = currentDestination;

        if (dest != null) {
            // Move towards destination
            Vector3 difference = VectorUtilities.subtract(dest, currentPosition);
            double maxLength = difference.length();
            Vector3 direction = difference.normalize();
            setDirection(direction.rotation());
            component(GameObjectComponents.TRANSFORM_COMPONENT).setLocation(VectorUtilities.add(currentPosition, VectorUtilities.multiply(direction, Math.min(getSpeed() * seconds, maxLength))));

            if (update) {
                update = false;
                flag(MovementFlags.FORWARD);
                component(ActorComponents.FLAG_UPDATE).flag(FlagType.TRANSFORM);
            }
        }
    }

    /**
     * Grabs the next point in the defined path
     */
    public void nextPoint() {
        LinkedList<Vector3> path = currentPath;
        if (path != null && path.size() > 0) {
            currentPointIndex += 1;
            if (path.size() > currentPointIndex) {
                currentDestination = path.get(currentPointIndex);
                update = true;
            } else {
                stop();
            }
        }
    }

    public void stop() {
        currentPointIndex = -1;
        currentDestination = null;
        update = true;
        getCurrentPath().clear();
        clearFlags();
    }

    public boolean canMove() {
        CharacterCombatComponent combat = getCharacter().component(CharacterComponents.COMBAT);
        return !isLocked() && !combat.isSnared() && !combat.isStunned() &&
                getActor().getState().equals(ActorState.ALIVE);
    }

    /**
     * Moves the Character to a specific location
     * @param location
     */
    public void moveToLocation(GridCoordinate location) {
        if (component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().distanceTo(location) <= 25) {
            getCurrentPath().clear();
            currentPointIndex = -1;
        } else if ((currentPath.isEmpty()  || !getCurrentPath().peek().equals(location)) && location.distanceTo(location) <= 25) {
            getCurrentPath().clear();
            currentPointIndex = -1;
        }
    }

    /**
     * Moves the Actor to a specific actor
     * @param actor
     */
    public void moveToActor(Actor actor) {
        if (actor == null) return;
        if (actor.component(PlayerComponents.WORLD_LEVEL_MANAGER).isWorldLoaded()) {
            if (isWithinDistance(actor, 50)) {
                getCurrentPath().clear();
                currentPointIndex = -1;
            } else if ((currentPath.isEmpty() || !getCurrentPath().peek().equals(actor.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation())) && actor.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().distanceTo(actor.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation()) < 50) {
                getCurrentPath().clear();
                currentPointIndex = -1;
                // getCharacter().component(ActorComponents.MOVEMENT).addPathToMovementQueue(actor.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation());
            }

        }
    }

    /**
     * Has the character face a specific coordinate
     * @param coordinate
     */
    public void faceCoordinate(GridCoordinate coordinate) {
        setDirection(getCharacter().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().directionTo(coordinate));
        getCharacter().component(GameObjectComponents.TRANSFORM_COMPONENT).getRotation().setYaw(getCharacter().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().directionTo(coordinate));
    }

    /**
     * Has the character face a specific actor
     * @param actor
     */
    public void faceActor(Actor actor) {
        if (actor == null) return;
        setDirection(getCharacter().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().directionTo(actor.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation()));
        getCharacter().component(GameObjectComponents.TRANSFORM_COMPONENT).getRotation().setYaw(getCharacter().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().directionTo(actor.component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation()));
        component(ActorComponents.FLAG_UPDATE).flag(FlagType.TRANSFORM);
    }


    public int getSpeed() {//TODO we need to add exceptions, water, etc.
        int currentSpeed = speed;
        if (!isRunning())
            currentSpeed = (speed / 2);
        currentSpeed += movementModifiers;
        if (isPlayer() && isMounted())
            currentSpeed += UDataTableRepository.getMountsDataTable().get(getActor().component(ActorComponents.TRANS_VAR).getVarInt("mount_id")).getSpeedBonus();

        return currentSpeed;
    }


    /**
     * Checks if a {@link FlagType} has been flagged.
     * @param flagType
     * @return contains the flag
     */
    public boolean isFlagged(MovementFlags flagType) {
        return flagTypes.contains(flagType);
    }


    /**
     * Flags a specific {@link MovementFlags} to be updated
     * @param flagType
     */
    public void flag(MovementFlags flagType) {
        if (flagTypes.contains(flagType)) return;
        flagTypes.add(flagType);
        flags = flags | flagType.getFlag();
        getActor().component(ActorComponents.FLAG_UPDATE).flag(FlagType.TRANSFORM);
    }

    /**
     * Unflags a specific {@link FlagType}
     * @param flagType
     */
    public void unflag(MovementFlags flagType) {
        if (!flagTypes.contains(flagType)) return;
        flagTypes.remove(flagType);
        flags = flags & ~flagType.getFlag();
        getActor().component(ActorComponents.FLAG_UPDATE).flag(FlagType.TRANSFORM);
    }

    /**
     * Clears all the Flags that are not manual reset
     */
    public void clearFlags() {
        clearFlags(false);
    }

    /**
     * Clears all the Flags
     *
     * @param force
     */
    public void clearFlags(boolean force) {
        for (MovementFlags flag : flagTypes) {
            if (!force && flag.isClearManually()) continue;
            flagTypes.remove(flag);
        }
        flags = 0x0;
    }

    public int addMovementModifierByPercent(int percent) {
        int amount = VariableUtility.getPercent(percent, getSpeed());
        movementModifiers += amount;
        getActor().component(ActorComponents.FLAG_UPDATE).flag(FlagType.TRANSFORM);
        return amount;
    }

    public int removeMovementModifierByPercent(int percent) {
        int amount = VariableUtility.getPercent(percent, getSpeed());
        movementModifiers -= amount;
        getActor().component(ActorComponents.FLAG_UPDATE).flag(FlagType.TRANSFORM);
        return amount;
    }

    public void addMovementModifier(int amount) {
        movementModifiers += amount;
        getActor().component(ActorComponents.FLAG_UPDATE).flag(FlagType.TRANSFORM);
    }

    public void removeMovementModifier(int amount) {
        movementModifiers -= amount;
        getActor().component(ActorComponents.FLAG_UPDATE).flag(FlagType.TRANSFORM);
    }

    public void mount(String mount) {
        MountUDataTable mountTable = MountUDataTable.forName(mount);
        if (mountTable == null)
            return;
        getActor().component(ActorComponents.TRANS_VAR).setVarInt("mount_id", mountTable.getId());
        getActor().component(PlayerComponents.MODEL_BLOCK).getModel().setBlendId(AnimSequenceUDataTable.BlendPoses.MOUNTED.ordinal());
        getActor().component(ActorComponents.FLAG_UPDATE).flag(FlagType.TRANSFORM);
        getActor().component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
    }

    public void demount() {
        getActor().component(ActorComponents.TRANS_VAR).setVarInt("mount_id", 0);
        getActor().component(ActorComponents.FLAG_UPDATE).flag(FlagType.TRANSFORM);
        getActor().component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
        // getActor().component(PlayerComponents.MODEL_BLOCK).getModel().setBlendId(ItemUDataTable.);
    }

    public boolean isMounted() {
        return getPlayer().component(ActorComponents.TRANS_VAR).getVarInt("mount_id") > 0;
    }


    /**
     * Grabs the transform component
     * @return the transform component
     */
    public TransformComponent transform() {
        return getActor().component(GameObjectComponents.TRANSFORM_COMPONENT);
    }

    /**
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {
        super.onWorldAwake();
    }

    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {

    }

    @Override
    public void onClearedFlags() {
        super.onClearedFlags();
        clearFlags();
    }
}
