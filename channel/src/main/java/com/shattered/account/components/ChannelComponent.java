package com.shattered.account.components;

import com.shattered.account.Account;
import com.shattered.account.ChannelAccount;
import com.shattered.utilities.ecs.Component;

/**
 * @author JTlr Frost 2/1/2020 : 4:39 PM
 */
public abstract class ChannelComponent extends Component {

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public ChannelComponent(Object gameObject) {
        super(gameObject);
    }


    /**
     * Gets the Account Object from the Object Instance.
     * @return
     */
    public ChannelAccount getAccount() {
        return (ChannelAccount) gameObject;
    }


}
