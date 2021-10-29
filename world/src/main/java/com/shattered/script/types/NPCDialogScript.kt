package com.shattered.script.types

import com.shattered.game.actor.character.player.component.PlayerComponents
import com.shattered.game.actor.character.player.component.interaction.dialogue.DialogOption
import com.shattered.game.actor.character.player.component.interaction.dialogue.DialogueOption
import com.shattered.networking.proto.PacketOuterClass
import com.shattered.networking.proto.World
import com.shattered.networking.proto.World.DialogOptions
import com.shattered.script.api.RelicEngineAPI
import com.shattered.script.api.RelicMathAPI
import com.shattered.script.api.RelicUtilityAPI
import com.shattered.script.api.RelicWorldAPI
import com.shattered.script.api.impl.*

abstract class NPCDialogScript {

    /**
     * Represents the Game World API
     */
    val world: RelicWorldAPI = WorldAPI()

    /**
     * Represents the Game World API
     */
    val engine: RelicEngineAPI = EngineAPI()

    /**
     * Represents the Math API
     */
    val math: RelicMathAPI = MathAPI()

    /**
     * Represents the Utility API
     */
    val utility: RelicUtilityAPI = UtilityAPI()

    /**
     * Represents the Data Table API
     * This API is used for grabbing selected information throughout all our data tables
     * i.e item, npc, object information
     */
    val tables = DataTableAPI()

    /**
     * Represents the Character API
     */
    var player: PlayerAPI? = null

    /**
     * Represents the NPC API
     */
    var npc: NpcAPI? = null

    /**
     * Represents the Current Stage of the Dialog.
     */
    var stage: Short = 0

    /**
     * Sets all npcs that are relative to this dialog script
     * @return the array of npc names
     */
    open fun for_npcs(): Array<String?>? {
        return null
    }

    /**
     * The single npc that is relative to this dialog script
     * @return the npc name
     */
    open fun fornpc(): String? {
        return null
    }

    /**
     * Method used when the dialog is started
     */
    abstract fun on_start(player: PlayerAPI, npc: NpcAPI)

    /**
     * Called upon every button request
     * @param id
     */
    abstract fun on_option(id: Int)

    /**
     * Called upon exiting the dialog
     */
    open fun on_finished() {

    }

    /**
     * Sends a Single Dialog Message with NPC ID and Continue Button Text
     * @param npcId
     * @param message
     */
    fun single(npcId: Int, message: String?) {
        single(npcId, message, "Continue")
    }

    /**
     * Sends a Single Dialog Message with NPC ID and desired Button Text
     * @param npcId
     * @param message
     * @param buttonText
     */
    fun single(npcId: Int, message: String?, buttonText: String?) {
        val singleDialog = World.Dialog.newBuilder()
        singleDialog.npcId = npcId
        singleDialog.message = message
        singleDialog.buttonText = buttonText
        player!!.player!!.sendMessage(PacketOuterClass.Opcode.SMSG_DIALOG, singleDialog.build())
    }

    /**
     * Sends a Single Dialog Packet
     * @param message
     */
    fun single(message: String?) {
        single(message, "Continue")
    }

    /**
     * Sends the Single Dialog Packet
     *
     * @param message
     */
    fun single(message: String?, buttonText: String?) {
        val singleDialog = World.Dialog.newBuilder()
        singleDialog.npcId = npc!!.id
        singleDialog.message = message
        singleDialog.buttonText = buttonText
        player!!.player!!.sendMessage(PacketOuterClass.Opcode.SMSG_DIALOG, singleDialog.build())
    }

    /**
     * Creates a Multi Dialog Option with Set Sprites
     * @param options
     */
    fun options(vararg options: DialogOption?) {
        val optionDialog = DialogOptions.newBuilder()
        for (option in options) {
            if (option == null) continue
            if (option.text.isEmpty()) continue
            optionDialog.addOption(World.DialogOption.newBuilder().setSpriteId(option.spriteId).setButtonText(option.text).build())
        }
        player!!.player!!.sendMessage(PacketOuterClass.Opcode.SMSG_OPTION_DIALOG, optionDialog.build())
    }

    /**
     * Sends a Multi option Dialog with sprites for each given option
     * Do not use if the options do not require sprites.
     * @param options
     */
    fun options(vararg options: DialogueOption?) {
        val optionDialog = DialogOptions.newBuilder()
        for (opt in options) {
            if (opt == null) continue
            if (opt.optionText.isEmpty()) continue
            optionDialog.addOption(World.DialogOption.newBuilder().setSpriteId(opt.spriteId).setButtonText(opt.optionText).build())
        }
    }

    /**
     * Sends a Multi Option Dialog Packet
     * Seting the default sprite to 0 (Will appear transparent inside the option dialog)
     *
     * @param options
     */

    fun options(vararg options: String?) {
        val optionDialog = DialogOptions.newBuilder()
        for (option in options) {
            if (option == null) continue
            if (option.isEmpty()) continue
            optionDialog.addOption(World.DialogOption.newBuilder().setButtonText(option).build())
        }
        player!!.player!!.sendMessage(PacketOuterClass.Opcode.SMSG_OPTION_DIALOG, optionDialog.build())
    }


    /**
     * Exits the Dialog
     */
    fun exit() {
        player!!.player!!.component(PlayerComponents.WIDGET).hideWidget("dialog")
        player!!.player!!.component(PlayerComponents.DIALOG).currentDialogue!!.on_finished()
        player!!.player!!.component(PlayerComponents.DIALOG).currentDialogue = null
        player!!.player!!.component(PlayerComponents.DIALOG).currentNPC = null
    }
}