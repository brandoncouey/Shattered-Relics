package com.shattered.game.actor.character.player.component.quest

import com.shattered.game.actor.`object`.item.Item
import com.shattered.game.actor.ability.Ability
import com.shattered.script.types.QuestScript

class QuestScriptTest : QuestScript() {

    /**
     * Constructs the quest
     */
    override fun construct() {
        rewards.items.add(Item(1, 1))
        rewards.abilitys.add(Ability(1))
    }

    /**
     * Registers the Quest for the Quest Name
     * @return the quest name
     */
    override fun name(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}