package dev.devoirr.bitwigs.core.frames

import dev.devoirr.bitwigs.core.database.DatabaseInfo
import dev.devoirr.bitwigs.core.module.Loadable

class FramesManager : Loadable {

    override fun getName(): String {
        return "frames"
    }

    lateinit var databaseInfo: DatabaseInfo

    override fun onEnable() {

    }

    override fun onDisable() {

    }
}