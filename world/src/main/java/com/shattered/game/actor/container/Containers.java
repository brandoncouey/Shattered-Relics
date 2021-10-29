package com.shattered.game.actor.container;

import com.shattered.game.actor.character.player.component.container.abilitybook.AbilityBookContainer;
import com.shattered.game.actor.character.player.component.container.equipment.EquipmentContainer;
import com.shattered.game.actor.character.player.component.container.mailbox.MailboxContainer;
import com.shattered.game.actor.character.player.component.container.storage.BankContainer;
import com.shattered.game.actor.character.player.component.container.storage.InventoryContainer;
import com.shattered.game.actor.character.player.component.container.trade.TradeContainer;
import com.shattered.game.actor.container.loot.LootContainer;
import com.shattered.game.actor.vendor.VendorContainer;
import com.shattered.utilities.ecs.Components;

import java.util.function.Supplier;

/**
 * @author JTlr Frost 11/1/2019 : 6:48 PM
 */
public class Containers <T extends Container> extends Components {



    /**
     * Represents Types of Containers
     */
    public static final Containers<InventoryContainer> INVENTORY = new Containers<>(() -> new InventoryContainer(null));

    /**
     * Represents the Equipment Container Component
     */
    public static final Containers<EquipmentContainer> EQUIPMENT = new Containers<>(() -> new EquipmentContainer(null));

    /**
     * Represents the Actors' Vendor Container
     */
    public static final Containers<VendorContainer> VENDOR = new Containers<>(VendorContainer::new);

    /**
     * Represents the Player's Trade Container
     */
    public static final Containers<TradeContainer> TRADE = new Containers<>(TradeContainer::new);

    /**
     * Represents the Player's Mailbox
     */
    public static final Containers<MailboxContainer> MAILBOX = new Containers<>(MailboxContainer::new);

    /**
     * Represents the Player's Loot Window
     */
    public static final Containers<LootContainer> LOOT = new Containers<>(LootContainer::new);

    /**
     * Represents the Player's Bank
     */
    public static final Containers<BankContainer> BANK = new Containers<>(() -> new BankContainer(null));

    /**
     * Represents the Player's Ability Book
     */
    public static final Containers<AbilityBookContainer> ABILITY_BOOK = new Containers<>(() -> new AbilityBookContainer(null));

    /**
     * @param supplier
     */
    public Containers(Supplier<T> supplier) {
        super(supplier);
    }


    /**
     * Represents the Types of Containers
     * Index starts from 0 - length
     */
    public enum Type {

        SCREEN,

        INVENTORY,

        EQUIPMENT,

        ACTION_BAR,

        VENDOR,

        TRADE,

        TRADE_OFFER,

        MAILBOX,

        LOOT,

        BANK,

        GRIMOIRE_BOOK,

        GRIMOIRE_BOOK_2,

        ;

        /**
         * Gets The type For Id
         * @param typeId
         * @return
         */
        public static Type forId(int typeId) {
            for (Type types : Type.values()) {
                if (types.ordinal() == typeId)
                    return types;
            }
            return null;
        }

    }
}
