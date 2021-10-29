package com.shattered.game.actor.character.player.component.container;

import com.shattered.datatable.tables.AbilityUDataTable;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.actionbar.PlayerActionBarComponent;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.actionbar.Shortcut;
import com.shattered.game.actor.character.player.component.actionbar.ShortcutType;
import com.shattered.game.actor.character.player.component.container.equipment.EquipmentContainer;
import com.shattered.game.actor.character.player.component.container.storage.BankContainer;
import com.shattered.game.actor.character.player.component.container.storage.InventoryContainer;
import com.shattered.game.actor.container.Container;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.actor.object.item.Item;
import com.shattered.game.actor.vendor.VendorContainer;
import com.shattered.game.component.WorldComponent;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.WorldProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.system.SystemLogger;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.ComponentManager;
import com.shattered.utilities.ecs.ProcessComponent;
import com.shattered.utilities.ecs.ProcessInterval;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost 11/1/2019 : 6:48 PM
 */
@ProcessComponent(interval = 1.f)
public class PlayerContainerManager extends ComponentManager {


    /**
     * Represents the current Container opened. This is used for npc vendors, loot containers, etc.
     */
    @Getter
    @Setter
    private Container current;

    /**
     * @param object
     */
    public PlayerContainerManager(Object object) {
        super(object);
    }


    @Override
    public void onStart() {
        super.onStart();

        /**
         * Used Inventory Slot
         */
        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_USE_ITEM_CONTAINER_SLOT, new WorldProtoListener<World.UseContainerSlot>() {

            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(World.UseContainerSlot message, Player player) {
                try {
                    Containers.Type type = Containers.Type.forId(message.getContainerId());
                    if (type == null) return;
                    switch (Containers.Type.forId(message.getContainerId())) {
                        case INVENTORY:
                            player.container(Containers.INVENTORY).onSlot(message.getSlotId(), message.getItemId());
                            break;
                        case ACTION_BAR:
                            player.component(PlayerComponents.ACTION_BAR).onSlot(message.getSlotId(), message.getItemId());
                            break;
                        case LOOT:
                            if (player.getContainerManager().getCurrent() != null)
                                player.getContainerManager().getCurrent().onSlot(message.getSlotId(), message.getItemId());
                            break;
                        case EQUIPMENT:
                            player.container(Containers.EQUIPMENT).onSlot(message.getSlotId(), message.getItemId());
                            break;
                        /*case GRIMOIRE_BOOK:
                        case GRIMOIRE_BOOK_2:
                            player.container(Containers.ABILITY_BOOK).onSlot(message.getSlotId(), message.getItemId());
                            break;*/
                        default:
                            SystemLogger.sendSystemMessage("Unhandled containerId: " + message.getContainerId());
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, World.UseContainerSlot.getDefaultInstance());

        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_USE_ABILITY_CONTAINER_SLOT, new WorldProtoListener<World.UseAbilitySlot>() {

            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(World.UseAbilitySlot message, Player player) {
                Containers.Type type = Containers.Type.forId(message.getContainerId());
                if (type == null) return;
                switch (Containers.Type.forId(message.getContainerId())) {
                    case ACTION_BAR:
                        player.component(PlayerComponents.ACTION_BAR).onSlot(message.getSlotId(), message.getAbilityId(), message.getPitch());
                        break;
                    case GRIMOIRE_BOOK:
                    case GRIMOIRE_BOOK_2:
                        player.container(Containers.ABILITY_BOOK).onSlot(message.getSlotId(), message.getAbilityId());
                        break;
                }

            }
        }, World.UseAbilitySlot.getDefaultInstance());

        /**
         * Handler for switching from slot x to slot y.
         */
        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_SHIFT_CONTAINER_SLOT, new WorldProtoListener<World.ShiftContainerSlot>() {

            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(World.ShiftContainerSlot message, Player player) {
                try {


                    Container container = null;
                    Containers.Type type = Containers.Type.forId(message.getContainerId());

                    if (type == null) return;

                    switch (type) {
                        case INVENTORY:
                            container = player.container(Containers.INVENTORY);
                            break;
                        case BANK:
                            container = player.container(Containers.BANK);
                            break;
                        case EQUIPMENT:
                            container = player.container(Containers.EQUIPMENT);
                            break;
                        case ACTION_BAR:
                            player.component(PlayerComponents.ACTION_BAR).switchSlots(message.getFromSlotId(), message.getToSlotId(), message.getItemId());
                            return;
                    }

                    if (container == null) return;

                    if (container.getItems() == null) return;

                    if (message.getFromSlotId() < 0 || message.getToSlotId() > container.getItems().length)
                        return;

                    if (message.getToSlotId() < 0 || message.getFromSlotId() > container.getItems().length)
                        return;

                    //TODO this is not being sent properly from the client. Disabled cus its not rlly even needed.
                    // if (message.getItemId() < 1) return; //TODO make definition list size and check > than itemId

                    if (container.switchSlots(message.getFromSlotId(), message.getToSlotId(), message.getItemId())) {
                        container.sendContainerPartialUpdate(type, message.getFromSlotId());
                        container.sendContainerPartialUpdate(type, message.getToSlotId());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, World.ShiftContainerSlot.getDefaultInstance());

        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_SHIFT_CONTAINER_SLOT_TO_WIDGET, new WorldProtoListener<World.ShiftContainerSlotToWidget>() {


            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(World.ShiftContainerSlotToWidget message, Player player) {
                int fromContainerId = message.getFromContainerId();
                int toContainerId = message.getToContainerId();
                Containers.Type fromContainer = Containers.Type.forId(fromContainerId);
                Containers.Type toContainer = Containers.Type.forId(toContainerId);
                int fromSlot = message.getFromSlotId();
                int toSlot = message.getToSlotId();
                int id = message.getId();

                if (fromContainer == null) return;
                if (toContainer == null) return;

                switch (fromContainer) {

                    //Actiionbar -> to Widget
                    case ACTION_BAR: {
                        switch (toContainer) {
                            case SCREEN:
                                PlayerActionBarComponent actionbar = player.component(PlayerComponents.ACTION_BAR);
                                actionbar.setShortcut(fromSlot, null);
                                actionbar.sendUpdatePartial(fromSlot);
                                break;
                        }
                    }

                    case GRIMOIRE_BOOK:
                    case GRIMOIRE_BOOK_2: {
                        switch (toContainer) {
                            case ACTION_BAR:
                                PlayerActionBarComponent actionbar = player.component(PlayerComponents.ACTION_BAR);
                                if (AbilityUDataTable.forId(id) != null)//Checks to ensure its a legit ability
                                    player.component(PlayerComponents.ACTION_BAR).setShortcut(toSlot, new Shortcut(id, ShortcutType.ABILITY));
                                break;
                        }
                    }

                    case EQUIPMENT: {
                        EquipmentContainer equipment = player.container(Containers.EQUIPMENT);
                        Item fromItem = equipment.get(fromSlot);
                        if (fromItem == null) return;
                        if (fromItem.getId() != id) return;
                        switch (toContainer) {
                            case BANK:
                            case INVENTORY: {
                                equipment.unequipToWidget(message.getFromSlotId(), fromItem.getId(), message.getToContainerId(), message.getToSlotId());
                                break;
                            }
                        }
                    }

                    //Inventory -> to Widget
                    case INVENTORY: {
                        InventoryContainer inventory = player.container(Containers.INVENTORY);
                        Item fromItem = inventory.get(fromSlot);
                        if (fromItem == null) return;
                        if (fromItem.getId() != id) return;

                        //this is what happens with lack of documentation :L
                        switch (toContainer) {

                            case ACTION_BAR: {
                                player.component(PlayerComponents.ACTION_BAR).setShortcut(toSlot, new Shortcut(fromItem.getId(), ShortcutType.ITEM));
                                break;
                            }

                            case BANK: {
                                player.container(Containers.BANK).addItem(fromItem);
                                inventory.deleteItemFromSlotCompletely(fromSlot);
                                break;
                            }

                            case EQUIPMENT: {
                                player.container(Containers.EQUIPMENT).equip(fromSlot, toSlot, fromItem);
                                break;
                            }

                            case SCREEN: {
                                //TODO delete item window
                                break;
                            }
                        }

                        break;
                    }

                    //Bank -> to Widget
                    case BANK: {
                        BankContainer bank = player.container(Containers.BANK);
                        InventoryContainer inventory = player.container(Containers.INVENTORY);
                        Item fromItem = bank.get(fromSlot);
                        if (fromItem == null) return;
                        if (fromItem.getId() != id) return;
                        switch (toContainer) {

                            case INVENTORY: {
                                if (inventory.isValidEmptySlot(toSlot) && bank.deleteItemFromSlotCompletely(fromSlot)) {
                                    inventory.setNullOnlySlot(toSlot, fromItem);
                                    return;
                                } else {
                                    if (inventory.canAdd(fromItem) && bank.deleteItemFromSlotCompletely(fromSlot)) {
                                        inventory.addItem(fromItem);
                                        return;
                                    }
                                }
                                break;
                            }
                        }
                    }

                }


            }
        }, World.ShiftContainerSlotToWidget.getDefaultInstance());

    }


    /**
     * Called every update
     * @param deltaTime
     */
    @Override
    public void onTick(long deltaTime) {
        super.onTick(deltaTime);
        if (getCurrent() != null) {
            if (!getCharacter().component(GameObjectComponents.TRANSFORM_COMPONENT).getLocation().isWithinUnits(getCurrent().getGameObject(), 350)) {
                close();
            }
        }
    }

    /**
     * Method Called upon the {@link Character} is dying
     */
    public void onDeath(Actor source) {
        for (Component components : getComponents().values()) {
            if (components == null) continue;
            if (components instanceof WorldComponent) {
                WorldComponent worldComponent = (WorldComponent) components;
                worldComponent.onDeath(source);
            }
        }
    }

    /**
     * Closes the Vendor
     * TODO don't destruct vendor anymore
     * HIDE Vendor.
     */
    public void close() {
        if (getCurrent() != null) {
            if (getCurrent() instanceof VendorContainer) {
                VendorContainer vendor = (VendorContainer) getCurrent();
                vendor.removeViewer(getCharacter());
            } else
                getCurrent().hide();
            setCurrent(null);
        }
    }

    /**
     * Gets the Character Game Object
     * @return
     */
    public Player getCharacter() {
        return (Player) object;
    }
}
