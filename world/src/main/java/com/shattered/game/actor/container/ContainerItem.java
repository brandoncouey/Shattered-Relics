package com.shattered.game.actor.container;

import com.shattered.game.GameObject;

public class ContainerItem extends GameObject {


    /**
     * Initializes the 'Containers' of the 'Game Object'
     *
     * @param id
     */
    public ContainerItem(int id) {
        super(id);
    }

    /**
     * Represents the Name of the Game Object
     *
     * @return
     */
    @Override
    public String getName() {
        return null;
    }

    /**
     * Represents when the 'Game Object' has Started
     */
    @Override
    public void onAwake() {

    }

    /**
     * Represents When the 'Game Object' is finished
     */
    @Override
    public void onFinish() {

    }
}
