package com.shattered.script.api.impl

import com.shattered.datatable.UDataTableRepository
import com.shattered.datatable.tables.SoundUDataTable
import com.shattered.engine.tasks.DelayedTaskTicker
import com.shattered.game.actor.ActorState
import com.shattered.game.actor.`object`.component.GameObjectComponents
import com.shattered.game.actor.character.components.CharacterComponents
import com.shattered.game.actor.character.player.Player
import com.shattered.game.actor.character.player.component.PlayerComponents
import com.shattered.game.actor.character.player.component.channel.ChannelComponents
import com.shattered.game.actor.components.ActorComponents
import com.shattered.game.actor.components.interaction.InteractionFlags
import com.shattered.game.actor.container.Containers
import com.shattered.game.grid.GridCoordinate
import com.shattered.networking.proto.PacketOuterClass
import com.shattered.networking.proto.World
import com.shattered.script.ScriptManager
import com.shattered.script.api.*
import com.shattered.utilities.VariableUtility
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiConsumer

class PlayerAPI(player: Player) : RelicPlayerAPI(player) {


    /**
     * Used for sending the Required Notification
     * i.e the Box over the reticle with a red warning symbol, and red text. Symbolizing a 'Required' for action.
     */
    override fun notify_required(message: String?) {
        player.component(PlayerComponents.WIDGET)?.sendWarningMessage(message!!)
    }

    /**
     * Used for displaying the  Action Timer with Cancel Option
     */
    override fun display_cancel_timer(action: String?, duration: Float) {
        player.component(PlayerComponents.WIDGET)?.sendActionTimeProgressBar(action, duration)
    }

    /**
     * Begins the action of the specified {@link rActionScript}
     */
    override fun start_action(action: String) {
        ScriptManager.startAction(player, action)
    }

    /**
     * Begins the action of the specified {@link ActionScript} with parameters
     */
    override fun start_action(action: String?, vararg parameters: Any?) {
        ScriptManager.startAction(player, action, parameters)
    }

    /**
     * Stops the current action for the player
     */
    override fun stop_action() {
       player.component(ActorComponents.ACTION).stopAction()
    }

    /**
     * Method used for increasing a player stat,
     * this method also triggers the notification
     *
     * To increase a stat without a notification, {@see CharacterVarAPI#var_increment}
     */
    override fun stat_increase(stat: String?, amount: Int) {
        player.component(PlayerComponents.WIDGET)?.sendStatIncreasedNotification(-1, amount)//TODO convert string to id
    }

    /**
     * Method used for increasing a trade skill stat,
     * this method also triggers the notification
     *
     * To increase a trade skill stat without a notification, {@see CharacterVarAPI#var_increment}
     */
    override fun trade_increase(trade: String?, amount: Int) {
        vars.increment_int(trade, amount)
        player.component(ActorComponents.VAR).incrementVarInt(trade, amount)
        player.component(PlayerComponents.WIDGET)?.sendTradeXPNotification(trade, amount)
    }

    /**
     * Method used for displaying a warning message on top of the screen.
     */
    override fun top_warning_message(message: String?) {
        player.component(PlayerComponents.WIDGET)?.showWidget("TopErrorNotification")
        set_widget_param("TopErrorNotification", "", message)
    }

    /**
     * Method used for displaying a warning message above the player's head.
     */
    override fun overhead_warning_message(message: String?) {
        player.component(PlayerComponents.WIDGET)?.sendWarningMessage(message)
    }

    /**
     * Method used for displaying a warning message above an actors position
     */
    override fun overhead_warning_message(actor: RelicActorAPI?, message: String?) {
        player.component(PlayerComponents.WIDGET)?.sendWarningMessage(actor?.actor, message);
    }

    /**
     * Makes the Player play an Animation with the provided Id.
     * @param animationId
     */
    override fun play_animation(animationId: Int) {
        player.component(ActorComponents.ANIMATION)?.playAnimSequence(animationId, true)
    }

    /**
     * Makes the Player play an Animation with the provided Animation Name
     * @param animationName
     */
    override fun play_animation(animationName: String?) {
        player.component(ActorComponents.ANIMATION).playAnimSequence(animationName, true)
    }

    /**
     * Makes the Player stop playing their current animation
     */
    override fun stop_animation() {
        player.component(ActorComponents.ANIMATION).stopAnimation()
    }


    /**
     * Sends a projectile from the the specified characater socket
     */
    override fun send_projectile(name: String?, socket: String?) {
        TODO("Not yet implemented")
    }

    /**
     * Sends a projectile from the specified socket, at the desired speed
     */
    override fun send_projectile(name: String?, speed: Int, socket: String?) {
        TODO("Not yet implemented")
    }



    /**
     * Makes the player play a Sound Track with the provided Track Name.
     * @param trackName
     */
    override fun play_track(trackName: String) {
        player.playSoundTrack(trackName)
    }

    /**
     * Makes the player play a sfx at the player's location
     */
    override fun play_sfx(sfxName: String, loc: GridCoordinate) {
        player.playSoundEffect(sfxName, loc)
    }

    /**
     * Locks the player, making them unable to move or rotate.
     */
    override fun lock() {
        player.component(ActorComponents.MOVEMENT).lock()
    }

    /**
     * Locks the player for the length of the specified delay in seconds and then unlocks them once the
     * duration has ended.
     * @param delay
     */
    override fun lock(delay: Int) {
        player.component(ActorComponents.MOVEMENT).lock(delay)
    }

    /**
     * Unlocks the player and gives them the ability to move and rotate.
     */
    override fun unlock() {
        player.component(ActorComponents.MOVEMENT).unlock()
    }

    /**
     * Adds the specified buff with the desired duration to the character
     * @param name
     * @param duration
     */
    override fun add_buff(name: String?, duration: Float) {
        add_buff(name, 1, duration)
    }

    /**
     * Adds teh specified buff with the desired stacks and time duration.
     * @param name
     * @param stacks
     * @param duration
     */
    override fun add_buff(name: String?, stacks: Int, duration: Float) {
        add_buff(name, this, stacks, duration)
    }

    /**
     * Adds the specified buff with the desired duration to the character
     * @param name
     * @param source
     * @param duration
     */
    override fun add_buff(name: String?, source: RelicCharacterAPI?, duration: Float) {
        add_buff(name, source, 1, duration)
    }

    /**
     * Adds teh specified buff with the desired stacks and time duration.
     * @param name
     * @param source
     * @param stacks
     * @param duration
     */
    override fun add_buff(name: String?, source: RelicCharacterAPI?, stacks: Int, duration: Float) {
        player.component(CharacterComponents.BUFF).addBuffAPI(name, source, stacks, duration)
    }

    /**
     * Removes the specified buff from the character.
     * @param name
     */
    override fun remove_buff(name: String?) {
        player.component(CharacterComponents.BUFF).removeBuff(name)
    }

    /**
     * Checks if the sepcified buff is currently active
     * @param name
     */
    override fun has_buff(name: String?): Boolean {
        return player.component(CharacterComponents.BUFF).hasBuff(name)
    }

    /**
     * Creates the Widget with the given referenced name.
     * @param widget
     */
    override fun create_widget(widget: String?) {
        player.component(PlayerComponents.WIDGET).createWidget(widget)
    }

    /**
     * Destructs the widget with the given referenced name.
     * @param widget
     */
    override fun destruct_widget(widget: String?) {
        player.component(PlayerComponents.WIDGET).destructWidget(widget)
    }

    /**
     * Shows the Widget with the given referenced name.
     * @param widget
     */
    override fun show_widget(widget: String?) {
        player.component(PlayerComponents.WIDGET).showWidget(widget)
    }

    /**
     * Hides the widget with the given referenced name.
     * @param widget
     */
    override fun hide_widget(widget: String?) {
        player.component(PlayerComponents.WIDGET).hideWidget(widget)
    }

    /**
     * Sets the widget param with the specified key and value
     */
    override fun set_widget_param(widget: String?, key: String?, value: String?) {
       player.component(PlayerComponents.WIDGET).setWidgetParam(widget, key, value);
    }

    /**
     * Sets the widget param with the specified key and value
     */
    override fun set_widget_param(widget: String?, key: String?, value: Int) {
        player.component(PlayerComponents.WIDGET).setWidgetParam(widget, key, value);
    }

    /**
     * Sets the widget param with the specified key and value
     */
    override fun set_widget_param(widget: String?, key: String?, value: Boolean) {
        player.component(PlayerComponents.WIDGET).setWidgetParam(widget, key, value);
    }

    /**
     * Adds an amount of experience to a specific stat
     */
    override fun add_stat_xp(stat: String?, amount: Int) {
        //TODO check for levevlups?
        player.component(ActorComponents.VAR).incrementVarInt(stat, amount)
        player.component(PlayerComponents.WIDGET).sendStatXPNotification(stat, amount)
    }

    /**
     * Gets the amount of xp for a specific stat
     */
    override fun get_stat_xp(stat: String?): Int {
        TODO("Not yet implemented")
    }

    /**
     * Adds reputation for a specific key
     */
    override fun add_reputation(key: String?, amount: Int) {
        TODO("Not yet implemented")
    }

    /**
     * Returns the amount of reputation for a specific key
     */
    override fun get_reputation(key: String?): Int {
        TODO("Not yet implemented")
    }

    /**
     * Checks if the player is in a party
     */
    override fun in_party(): Boolean {
        return player.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).isInParty
    }

    /**
     * Checks if the player is in a party
     */
    override fun in_party(other: RelicPlayerAPI?): Boolean {
        return player.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).containsPlayer(other?.player)
    }

    /**
     * Gets a list of all of the party members
     */
    override fun get_party_members(): MutableMap<Int, RelicPlayerAPI?> {
        val members: MutableMap<Int, RelicPlayerAPI?> = ConcurrentHashMap()
        player.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).members.forEach(BiConsumer { integer: Int, player: Player ->
            members[integer] = PlayerAPI(player)
        })
        return members
    }

    /**
     * Adds the ability to your ability book with notification
     */
    override fun learn_ability(abilityName: String?) {
        learn_ability(abilityName, true)
    }

    /**
     * Adds the ability to your ability book and toggles notification
     */
    override fun learn_ability(abilityName: String?, notify: Boolean) {
        player.container(Containers.ABILITY_BOOK).add(abilityName, notify)
    }

    /**
     * Adds the recipe to your recipe book with notification
     */
    override fun learn_recipe(recipeName: String?) {
        learn_recipe(recipeName, true)
    }

    /**
     * Adds the recipe to your ability book and toggles notification
     */
    override fun learn_recipe(recipeName: String?, notify: Boolean) {
        //TODO add to learned recipes
        if (notify)
            player.component(PlayerComponents.WIDGET).sendLearnedNotification(recipeName, true)
    }

    /**
     * Gets the Name of the NPC
     * @return the npc's name
     */
    override fun getName(): String {
        return player.name
    }

    /**
     * Gets the Client Index (the UUID) of the npc
     * @return the npc's uuid
     */
    override fun getIndex(): Int {
        return player.clientIndex
    }

    /**
     * Gets the Actor's current state
     * @return the actor state
     */
    override fun getState(): ActorState {
        return player.state
    }

    /**
     * Applys flags to the npc for availability for interaction
     * @param flags
     */
    override fun flag(vararg flags: InteractionFlags?) {
        flags.forEach { n -> player.component(ActorComponents.INTERACTION).flag(n) }
    }

    /**
     * Moves the Character to a specific character
     * @param character
     */
    override fun move_to(character: RelicCharacterAPI?) {
        player.component(ActorComponents.MOVEMENT)?.moveToActor(character?.actor)
    }

    /**
     * Moves the character to a specific coordinate
     * @param location
     */
    override fun move_to(location: GridCoordinate?) {
       player.component(ActorComponents.MOVEMENT)?.moveToLocation(location)
    }

    /**
     * Checks if the character is currently moving
     * @return is moving
     */
    override fun getMoving(): Boolean {
        return player.component(ActorComponents.MOVEMENT)?.moving!!
    }

    /**
     * Decreases the characters movement speed by the specified percent
     * @param percent
     */
    override fun decrease_speed_by_percent(percent: Int) : Int {
        return player.component(ActorComponents.MOVEMENT).removeMovementModifierByPercent(percent)
    }

    /**
     * Decreases the characters movement speed by the specified percent
     * @param amount
     */
    override fun decrease_speed(amount: Int) {
        player.component(ActorComponents.MOVEMENT).removeMovementModifier(amount)
    }

    /**
     * Decreases the characters movement speed by the specified percent
     * for the given amount of seconds
     * @param percent
     * @param duration
     */
    override fun decrease_speed(percent: Int, duration: Float) : Int {
        var amount = player.component(ActorComponents.MOVEMENT).removeMovementModifierByPercent(percent)
        DelayedTaskTicker.delayTask({
            player.component(ActorComponents.MOVEMENT).addMovementModifier(amount)
        }, duration)
        return amount
    }

    /**
     * Increases the character movement speed by
     * @param percent
     *
     * @return the amount of percent
     */
    override fun increase_speed_by_percent(percent: Int): Int {
        return player.component(ActorComponents.MOVEMENT).addMovementModifierByPercent(percent)
    }

    /**
     * Increases the character movement speed by
     * @param amount
     */
    override fun increase_speed(amount: Int) {
        player.component(ActorComponents.MOVEMENT).addMovementModifier(amount)
    }

    /**
     * Increases the character movement speed by the specified percent for
     * the given amount of seconds
     * @param percent
     * @param seconds
     */
    override fun increase_speed(percent: Int, duration: Float) : Int {
        var amount = player.component(ActorComponents.MOVEMENT).addMovementModifierByPercent(percent)
        DelayedTaskTicker.delayTask({
            player.component(ActorComponents.MOVEMENT).removeMovementModifier(amount)
        }, duration)
        return amount
    }

    /**
     * Makes the Character face a specific Actor
     * @param actor
     */
    override fun face_actor(actor: RelicActorAPI?) {
        player.component(ActorComponents.MOVEMENT)?.faceActor(actor!!.actor)
    }

    /**
     * Makes the Character face a specific coordinate
     * @param coordinate
     */
    override fun face_location(coordinate: GridCoordinate?) {
        player.component(ActorComponents.MOVEMENT)?.faceCoordinate(coordinate)
    }

    /**
     * PLays the specified sfx at the actor's location
     *
     * @param id
     */
    override fun play_rsfx(id: Int) {
       // if (!UDataTableRepository.getSoundEffectDataTable().containsKey(id)) return
        for (p in player.component(GameObjectComponents.ZONE_COMPONENT).node.allPlayers) {
            if (!p.component(GameObjectComponents.TRANSFORM_COMPONENT).isWithinDistance(player, 5000)) continue
            p.sendMessage(PacketOuterClass.Opcode.SMSG_PLAY_SOUND_EFFECT_AT_LOC, World.PlaySoundEffectAtLocation.newBuilder().setId(id).setTransform(p.component(GameObjectComponents.TRANSFORM_COMPONENT).toWorldTransform()).build())
        }
    }

    /**
     * PLays the specified sfx at the actor's location
     *
     * @param name
     */
    override fun play_rsfx(name: String?) {
        val table = SoundUDataTable.forSFX(name) ?: return
        for (p in player.component(GameObjectComponents.ZONE_COMPONENT).node.allPlayers) {
            if (!p.component(GameObjectComponents.TRANSFORM_COMPONENT).isWithinDistance(player, 5000)) continue
            p.sendMessage(PacketOuterClass.Opcode.SMSG_PLAY_SOUND_EFFECT_AT_LOC, World.PlaySoundEffectAtLocation.newBuilder().setId(table.id).setTransform(p.component(GameObjectComponents.TRANSFORM_COMPONENT).toWorldTransform()).build())
        }
    }
}