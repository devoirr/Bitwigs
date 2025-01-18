package dev.devoirr.bitwigs.core.util

import org.bukkit.block.BlockFace
import org.bukkit.entity.Player

class PlayerUtility {

    companion object {
        private val axis = listOf(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)

        fun yawToBlockFace(player: Player): BlockFace {
            return axis[(Math.round(player.yaw / 90f) and 0x3)]
        }
    }

}