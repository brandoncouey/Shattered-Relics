package com.shattered.game.engine

import com.shattered.engine.Engine
import com.shattered.game.engine.threads.*

class WorldEngine : Engine() {


    /**
     * Represents the World Thread
     */
    private val worldLogicThread: WorldLogicThread = WorldLogicThread()

    /**
     * Represents the Central Task Thread
     */
    private val centralTaskThread: CentralTaskThread = CentralTaskThread()

    /**
     * Represents the System Command Threadline.
     */
    private val systemThread: SystemThread = SystemThread()


    override fun run() {
        super.run()
        worldLogicThread.start()
        centralTaskThread.start()
        systemThread.start()
    }
}