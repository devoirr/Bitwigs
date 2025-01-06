package dev.devoirr.bitwigs.core.util

import org.bukkit.block.BlockFace

class BlockFaceUtility {

    companion object {

        private val axis = arrayOf(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)
        private val radial =
            arrayOf(
                BlockFace.NORTH,
                BlockFace.NORTH_EAST,
                BlockFace.EAST,
                BlockFace.SOUTH_EAST,
                BlockFace.SOUTH,
                BlockFace.SOUTH_WEST,
                BlockFace.WEST,
                BlockFace.NORTH_WEST
            )

        fun getClosestBlockface(yaw: Float, useSubCardinalDirections: Boolean = false): BlockFace {
            if (useSubCardinalDirections)
                return radial[Math.round(yaw / 45f) and 0x7].oppositeFace

            return axis[Math.round(yaw / 90f) and 0x3].oppositeFace;
        }

    }
}