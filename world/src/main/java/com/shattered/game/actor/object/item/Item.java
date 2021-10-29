package com.shattered.game.actor.object.item;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.datatable.tables.ItemUDataTable;
import com.shattered.game.GameObject;
import com.shattered.game.actor.container.ContainerItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @author JTlr Frost - 8/29/2018 - 1:48 PM
 */
public class Item extends ContainerItem {


    /**
     * Represents the Amount of the Item
     */
    @Getter
    @Setter
    private int amount;

    /**
     * Represents the Item Quality
     */
    @Getter
    @Setter
    private ItemQuality quality = ItemQuality.NONE;

    /**
     * Creates a new Item with the provided id with a set amount of 1.
     * @param id
     */
    public Item(int id) {
        super (id);
        setAmount(1);
    }

    /**
     * Creates a new Item with the provided id with a set amount of 1 with the specified Quality
     * @param id
     * @param quality
     */
    public Item(int id, @NonNull ItemQuality quality) {
        super(id);
        setAmount(1);
        setQuality(quality);

    }

    /**
     * Creates a new Item with the provided id and the amount of the item.
     * @param id
     * @param amount
     */
    public Item(int id, int amount) {
        super(id);
        setAmount(amount);
    }

    /**
     * Creates a new Item instance with the provided Item Quality of the item.
     * @param id
     * @param amount
     * @param quality
     */
    public Item(int id, int amount, @NonNull ItemQuality quality) {
        super(id);
        setAmount(amount);
        setQuality(quality);
    }

    /**
     * Creates an item with the specified name and default amount of 1
     * @param name
     */
    public Item(String name) {
        this(name, 1);
    }

    /**
     * Creates an item with the specified name and amount
     * @param name
     * @param amount
     */
    public Item(String name, int amount) {
        super(ItemUDataTable.forId(name));
        setAmount(amount);
    }


    /**
     * Gets the name of the item, if null it returns unavailable
     * @return the name
     */
    @Override
    public String getName() {
        if (getTable() != null)
            return getTable().getName();
        return "Unavailable";
    }

    /**
     * Method called for initializing / loading of the item.
     */
    @Override
    public void onAwake() {

    }


    /**
     * Adds the Components
     */
    @Override
    public void addComponents() {
        super.addComponents();
    }

    /**
     * Method called when the item has been destroyed (Finished)
     */
    @Override
    public void onFinish() {

    }

    /**
     * Gets the DataTable for the {@link Item}
     * @return
     */
    public ItemUDataTable getTable() {
        return UDataTableRepository.getItemDataTable().get(getId());
    }


}
