package com.shattered.game.actor.character.player.component.container.mailbox;

import com.shattered.game.actor.object.item.Item;
import com.shattered.utilities.VariableUtility;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Mail {

    /**
     * Represents the UUID of the Mail
     */
    private int uuid = VariableUtility.random(1, Integer.MAX_VALUE);

    /**
     * Represents the player Id of the sender
     */
    private int fromCharacterId;

    /**
     * Represents who the mail is from
     */
    private String from;

    /**
     * Represents the Subject of the Mail
     */
    private String subject;

    /**
     * Represents the Body Message of the mail
     */
    private String message;

    /**
     * Represents the amount of coins
     */
    private int coins;

    /**
     * Represents the List of Items
     */
    private List<Item> items = new ArrayList<>();

    /**
     *
     * @param from
     * @param subject
     * @param message
     */
    public Mail(String from, String subject, String message) {
        this.from = from;
        this.subject = subject;
        this.message = message;
    }

    /**
     * Creates a Default Mail Constructor
     * @param fromCharacterId
     * @param from
     * @param subject
     * @param message
     */
    public Mail(int fromCharacterId, String from, String subject, String message) {
        this.fromCharacterId = fromCharacterId;
        this.from = from;
        this.subject = subject;
        this.message = message;
    }

    /**
     *
     * @param from
     * @param subject
     * @param message
     * @param coins
     */
    public Mail(String from, String subject, String message, int coins) {
        this.from = from;
        this.subject = subject;
        this.message = message;
        this.coins = coins;
    }

    /**
     *
     * @param fromCharacterId
     * @param from
     * @param subject
     * @param message
     * @param coins
     */
    public Mail(int fromCharacterId, String from, String subject, String message, int coins) {
        this.fromCharacterId = fromCharacterId;
        this.from = from;
        this.subject = subject;
        this.message = message;
        this.coins = coins;
    }

    /**
     *
     * @param fromCharacterId
     * @param from
     * @param subject
     * @param message
     * @param coins
     * @param items
     */
    public Mail(int fromCharacterId, String from, String subject, String message, int coins, List<Item> items) {
        this.fromCharacterId = fromCharacterId;
        this.from = from;
        this.subject = subject;
        this.message = message;
        this.coins = coins;
        this.items = items;
    }

    /**
     *
     * @param from
     * @param subject
     * @param message
     * @param coins
     * @param items
     */
    public Mail(String from, String subject, String message, int coins, List<Item> items) {
        this.from = from;
        this.subject = subject;
        this.message = message;
        this.coins = coins;
        this.items = items;
    }

    /**
     *
     * @param uuid
     * @param fromCharacterId
     * @param from
     * @param subject
     * @param message
     * @param coins
     * @param items
     */
    public Mail(int uuid, int fromCharacterId, String from, String subject, String message, int coins, List<Item> items) {
        this.uuid = uuid;
        this.fromCharacterId = fromCharacterId;
        this.from = from;
        this.subject = subject;
        this.message = message;
        this.coins = coins;
        this.items = items;
    }


}
