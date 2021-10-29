package com.shattered.game.actor.character.player.component.container.mailbox;

import com.shattered.database.mysql.MySQLCommand;
import com.shattered.game.actor.container.Container;
import com.shattered.game.actor.object.item.Item;
import lombok.Getter;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MailboxContainer extends Container {


    /**
     * Represents the Default Mailbox Container Size
     */
    public static final int DEFAULT_MAILBOX_SIZE = 60;

    /**
     * Represents the List of Mail
     */
    @Getter
    private List<Mail> mail = new ArrayList<>();

    /**
     * Creates a new Mailbox Container instance (Used for Identification)
     */
    public MailboxContainer() {
        super(null, DEFAULT_MAILBOX_SIZE);//Used for Identifier
    }

    /**
     * Creates a Constructor initializing a new instance of a mailbox
     * @param object
     */
    public MailboxContainer(Object object) {
        super(object, DEFAULT_MAILBOX_SIZE);
    }

    @Override
    public void onStart() {



    }


    /**
     * Sends mail to the player
     * @param mail
     * @return sent
     */
    public boolean sendMail(Mail mail) {
        if (getMail().size() >= DEFAULT_MAILBOX_SIZE) {
            return false;
        }
        getMail().add(mail);
        return true;
    }

    /**
     *
     * @param uuid
     */
    public void deleteMail(int uuid) {
        entry(getDatabaseName(), getTableName(), null, MySQLCommand.DELETE_FROM, new WhereConditionOption("uuid", uuid));
    }

    @Override
    public void onWorldAwake() {
        for (Mail mail : getMail()) {
            if (mail == null) continue;
            //TODO send
        }
    }

    @Override
    public String getDatabaseName() {
        return "shatteredrelics";
    }

    @Override
    public String getTableName() {
        return "mailbox";
    }

    @Override
    public WhereConditionOption[] getFetchConditions() {
        return new WhereConditionOption[] { new WhereConditionOption("characterId", getPlayer().getId()) };
    }

    @Override
    public WhereConditionOption[] getUpdateConditions() {
        return new WhereConditionOption[] { new WhereConditionOption("characterId", getPlayer().getId()) };
    }

    @Override
    public boolean insert() {
        return false;
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public void delete() {

    }

    @Override
    public boolean fetch() {
        ResultSet resultSet = getResults();
        try {
            if (resultSet.next()) {
                int uuid = resultSet.getInt("uuid");
                int fromCharacterId = resultSet.getInt("from_character_id");
                String from = resultSet.getString("from");
                String subject = resultSet.getString("subject");
                String message = resultSet.getString("message");
                int coins = resultSet.getInt("coins");
                String column = resultSet.getString("items");
                String[] item = column.split(",");
                List<Item> items = new ArrayList<>();
                for (String x : item) {
                    String[] result = x.split(":");
                    int itemId = Integer.parseInt(result[0]);
                    int amount = Integer.parseInt(result[1]);
                    if (itemId > 0 && amount > 0)
                        items.add(new Item(itemId, amount));
                }
                Mail mail = new Mail(uuid, fromCharacterId, from, subject, message, coins, items);
                getMail().add(mail);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Method called when a 'Slot' is used.
     *
     * @param slotId
     * @param itemId
     */
    @Override
    public void onSlot(int slotId, int itemId) {

    }
}
