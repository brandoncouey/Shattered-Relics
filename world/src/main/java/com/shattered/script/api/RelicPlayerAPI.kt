package com.shattered.script.api

import com.shattered.game.actor.character.player.Player
import com.shattered.game.grid.GridCoordinate
import com.shattered.script.api.impl.*

abstract class RelicPlayerAPI(player: Player) : RelicCharacterAPI(player) {

    /**
     * Represents the Character referenced for API
     */
    val player: Player = player

    /**
     * Represents the Channel API
     * This API is used for social related functions and utilities.
     */
    val channel: PlayerChannelAPI = PlayerChannelAPI(player)

    /**
     * Represents the Quest API
     * This API is used for any questing related functions and utilities.
     */
    val quest: PlayerQuestAPI = PlayerQuestAPI(player)

    /**
     * Represents the Container API
     * This API is used for any containers related functions and utilities.
     * i.e Inventory, Banks, Equipment, etc.
     */
    val containers: PlayerContainerAPI = PlayerContainerAPI(player)

    /**
     * Represents the Trades API
     * This API is used for any relative functions and utilities for Tradesmen
     */
    val trades: PlayerTradesAPI = PlayerTradesAPI(player)

    /**
     * Used for sending the Required Notification
     * i.e the Box over the reticle with a red warning symbol, and red text. Symbolizing a 'Required' for action.
     */
    abstract fun notify_required(message: String?)

    /**
     * Used for displaying the  Action Timer with Cancel Option
     */
    abstract fun display_cancel_timer(action: String?, duration: Float)

    /**
     * Begins the action of the specified {@link ActionScript}
     */
    abstract fun start_action(action: String)

    /**
     * Begins the action of the specified {@link ActionScript} with parameters
     */
    abstract fun start_action(actionName: String?, vararg parameters: Any?)

    /**
     * Stops the current action for the player
     */
    abstract fun stop_action()

    /**
     * Method used for increasing a player stat,
     * this method also triggers the notification
     *
     * To increase a stat without a notification, {@see CharacterVarAPI#var_increment}
     */
    abstract fun stat_increase(stat: String?, amount: Int)

    /**
     * Method used for increasing a trade skill stat,
     * this method also triggers the notification
     *
     * To increase a trade skill stat without a notification, {@see CharacterVarAPI#var_increment}
     */
    abstract fun trade_increase(trade: String?, amount: Int)

    /**
     * Method used for displaying a warning message on top of the screen.
     */
    abstract fun top_warning_message(message: String?)

    /**
     * Method used for displaying a warning message above the player's head.
     */
    abstract fun overhead_warning_message(message: String?)

    /**
     * Method used for displaying a warning message above an actors position
     */
    abstract fun overhead_warning_message(actor: RelicActorAPI?, message: String?)

    /**
     * Makes the Player play an Animation with the provided Id.
     * @param animationId
     */
    abstract fun play_animation(animationId: Int)

    /**
     * Makes the Player stop playing their current animation
     */
    abstract fun stop_animation()

    /**
     * Makes the Player play an Animation with the provided Animation Name
     * @param animationName
     */
    abstract fun play_animation(animationName: String?)

    /**
     * Sends a projectile from the the specified characater socket
     */
    abstract fun send_projectile(name: String?, socket: String?)

    /**
     * Sends a projectile from the specified socket, at the desired speed
     */
    abstract fun send_projectile(name: String?, speed: Int = 1000, socket: String?)

    /**
     * Makes the player play a Sound Track with the provided Track Name.
     * @param trackName
     */
    abstract fun play_track(trackName: String)

    /**
     * Makes the player play a sfx at the player's location
     */
    abstract fun play_sfx(sfxName: String, loc: GridCoordinate)

    /**
     * Creates the Widget with the given referenced name.
     * @param widget
     */
    abstract fun create_widget(widget: String?)

    /**
     * Destructs the widget with the given referenced name.
     * @param widget
     */
    abstract fun destruct_widget(widget: String?)

    /**
     * Shows the Widget with the given referenced name.
     * @param widget
     */
    abstract fun show_widget(widget: String?)

    /**
     * Hides the widget with the given referenced name.
     * @param widget
     */
    abstract fun hide_widget(widget: String?)

    /**
     * Sets the widget param with the specified key and value
     */
    abstract fun set_widget_param(widget: String?, key: String?, value: String?)

    /**
     * Sets the widget param with the specified key and value
     */
    abstract fun set_widget_param(widget: String?, key: String?, value: Int)

    /**
     * Sets the widget param with the specified key and value
     */
    abstract fun set_widget_param(widget: String?, key: String?, value: Boolean)

    /**
     * Adds an amount of experience to a specific stat
     */
    abstract fun add_stat_xp(stat: String?, amount: Int)

    /**
     * Gets the amount of xp for a specific stat
     */
    abstract fun get_stat_xp(stat: String?) : Int

    /**
     * Adds reputation for a specific key
     */
    abstract fun add_reputation(key: String?, amount: Int)

    /**
     * Returns the amount of reputation for a specific key
     */
    abstract fun get_reputation(key: String?) : Int

    /**
     * Checks if the player is in a party
     */
    abstract fun in_party() : Boolean

    /**
     * Checks if the player is in a party
     */
    abstract fun in_party(player: RelicPlayerAPI?) : Boolean

    /**
     * Gets a list of all of the party members
     */
    abstract fun get_party_members() : MutableMap<Int, RelicPlayerAPI?>

    /**
     * Adds the ability to your ability book with notification
     */
    abstract fun learn_ability(abilityName: String?)

    /**
     * Adds the ability to your ability book and toggles notification
     */
    abstract fun learn_ability(abilityName: String?, notify: Boolean)

    /**
     * Adds the recipe to your recipe book with notification
     */
    abstract fun learn_recipe(recipeName: String?)

    /**
     * Adds the recipe to your ability book and toggles notification
     */
    abstract fun learn_recipe(recipeName: String?, notify: Boolean)

}