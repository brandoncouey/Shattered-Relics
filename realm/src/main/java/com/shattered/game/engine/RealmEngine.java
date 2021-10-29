package com.shattered.game.engine;

import com.shattered.engine.Engine;
import com.shattered.game.engine.threads.ChannelTaskThread;
import com.shattered.threads.RealmThread;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost 9/7/2019 : 7:49 PM
 */
public class RealmEngine extends Engine {

    /**
     * Represents the GameRealm Thread
     */
    @Getter
    @Setter
    private RealmThread realmThread;

    @Getter
    @Setter
    private ChannelTaskThread channelTaskThread;

    /**
     * Initializes the GameRealm Engine
     */
    @Override
    public void run() {
        super.run();
        setRealmThread(new RealmThread());
        getRealmThread().start();

        setChannelTaskThread(new ChannelTaskThread());
        getChannelTaskThread().start();
    }
}
