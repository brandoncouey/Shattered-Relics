package com.shattered.script.impl

import com.shattered.engine.tasks.DelayedTaskTicker
import com.shattered.game.actor.character.components.combat.Hit
import com.shattered.script.api.RelicCharacterAPI
import com.shattered.script.types.NPCCombatScript

class FrilledLizzardCombatScript : NPCCombatScript() {

    var attackDelay: Long = 0

    var specialDelay: Long = 0

    override fun fornpc(): String {
        return "frilled lizzard"
    }

    override fun on_attacked(target: RelicCharacterAPI?, hit: Hit?) {
        super.on_attacked(target, hit)
    }

    override fun on_hit(target: RelicCharacterAPI?, hit: Hit?) {
        super.on_hit(target, hit)
    }

    override fun on_tick(deltaTime: Long) {
        super.on_tick(deltaTime)
        val target = npc.combat.target ?: return
        if (npc.zone.is_within_distance(target, distance)) {

            if (System.currentTimeMillis() >= attackDelay) {
                main_attack(target)
            }

            if (System.currentTimeMillis() >= specialDelay) {
                special_attack(target)
            }
        }
    }

    override fun on_death(source: RelicCharacterAPI?) {
        super.on_death(source)
    }


    fun main_attack(target: RelicCharacterAPI?) {
        npc.face_actor(target)
        npc.lock(3)
        npc.play_animation("lizzard_hit_01")
        attackDelay = System.currentTimeMillis() + 2000
        npc.combat.hit(target, 10, 500.0f)
    }

    fun special_attack(target: RelicCharacterAPI?) {
       // npc.combat.send_projectile("lizzard_spit", target,  10, 3)
    }




}