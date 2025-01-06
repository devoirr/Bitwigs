package dev.devoirr.bitwigs.core.warps.model

import org.bukkit.Location
import java.util.*

data class Warp(val name: String, val location: Location, val creator: UUID)
