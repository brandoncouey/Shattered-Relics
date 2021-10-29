package com.shattered.game.actor

import com.shattered.game.GameObject
import com.shattered.game.actor.action.ActorActionComponent
import com.shattered.game.actor.components.ActorComponents
import com.shattered.game.actor.components.animation.ActorAnimSequenceComponent
import com.shattered.game.actor.components.flags.ActorFlagUpdateComponent
import com.shattered.game.actor.components.interaction.ActorInteractionFlaggerComponent
import com.shattered.game.actor.components.movement.ActorMovementComponent
import com.shattered.game.actor.components.variable.ActorTransVariableComponent
import com.shattered.game.actor.components.variable.ActorVariableComponent
import com.shattered.script.ScriptManager

abstract class Actor(id: Int) : GameObject(id) {

    /**
     * Represents the Index of the Actor
     */
     var clientIndex : Int = 0

    /**
     * Represents the Current Actor State
     */
     var state : ActorState = ActorState.CONSTRUCT


    /**
     * Initialization of the Actor
     */
    override fun onAwake() {
        ScriptManager.onAwake(this)
    }

    override fun addComponents() {
        super.addComponents()
        componentManager.attatch(ActorComponents.FLAG_UPDATE, ActorFlagUpdateComponent(this))
        componentManager.attatch(ActorComponents.ANIMATION, ActorAnimSequenceComponent(this))
        componentManager.attatch(ActorComponents.MOVEMENT, ActorMovementComponent(this))
        componentManager.attatch(ActorComponents.INTERACTION, ActorInteractionFlaggerComponent(this))
        componentManager.attatch(ActorComponents.VAR, ActorVariableComponent(this))
        componentManager.attatch(ActorComponents.TRANS_VAR, ActorTransVariableComponent(this))
        componentManager.attatch(ActorComponents.ACTION, ActorActionComponent(this));
    }

    /**
     * Represents the Updating Method
     * This method is called once per frame.
     */
    override fun onTick(deltaTime: Long) {
        super.onTick(deltaTime)
        ScriptManager.onTick(this, deltaTime)
    }

    /**
     * Represents when the 'Game Object' is dead, but not completely finished.
     */
    open fun onDeath(source: Actor) {
        state = ActorState.DEAD
        component(ActorComponents.MOVEMENT).lock();
        ScriptManager.onDeath(this, source)
    }

    /**
     * Finalization of the Actor
     */
    override fun onFinish() {
        state = ActorState.FINISHED
        ScriptManager.onFinished(this)
    }

}