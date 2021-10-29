package com.shattered.game.grid;

import com.shattered.game.actor.Actor;

public abstract class ReplicationNode {

    public abstract void onAdd(Actor actor);

    public abstract void onRemoved(Actor actor);


}
