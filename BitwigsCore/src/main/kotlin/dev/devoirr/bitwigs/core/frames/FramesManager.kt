package dev.devoirr.bitwigs.core.frames

import dev.devoirr.bitwigs.core.database.DatabaseInfo
import dev.devoirr.bitwigs.core.frames.listener.FramesListener
import dev.devoirr.bitwigs.core.module.Loadable

class FramesManager : Loadable {

    override fun getName(): String {
        return "frames"
    }

    lateinit var databaseInfo: DatabaseInfo

    private val listener = FramesListener(this)

    override fun onEnable() {
        listener.register()
    }

    override fun onDisable() {

    }
}