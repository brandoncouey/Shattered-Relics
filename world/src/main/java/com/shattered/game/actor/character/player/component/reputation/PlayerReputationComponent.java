package com.shattered.game.actor.character.player.component.reputation;

import com.shattered.account.Account;
import com.shattered.game.component.WorldComponent;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

public class PlayerReputationComponent extends WorldComponent {

    /**
     * Represents the Reputation Levels
     */
    public static final int BANISHED = -3, HATED = -2, CONTEMPT = -1, UNKNOWN = 0, NEUTRAL = 1, FRIENDLY = 2, RESPECTED = 3, HONORED = 4, GRAND = 5;


    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public PlayerReputationComponent(Object gameObject) {
        super(gameObject);
    }


    //add vars...
    public void add(String key, int amount) {

    }

}
