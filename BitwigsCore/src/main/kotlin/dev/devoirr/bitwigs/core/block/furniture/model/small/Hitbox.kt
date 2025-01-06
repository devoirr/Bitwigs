package dev.devoirr.bitwigs.core.block.furniture.model.small

import dev.devoirr.bitwigs.core.getRight
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import kotlin.math.roundToInt

data class Hitbox(val width: Double, val height: Double, val depth: Double) {

    fun getBlocks(center: Block, facing: BlockFace, onlyAboveY: Boolean = false): List<Block> {
        val intWidth = width.roundToInt() - 1
        val intHeigh = height.roundToInt() - 1
        val intDepth = depth.roundToInt() - 1

        val widthPlus = intWidth - (intWidth / 2)
        val widthMinus = intWidth - widthPlus

        val heightPlus = intHeigh - (intHeigh / 2)
        val heightMinus = intHeigh - heightPlus

        val depthPlus = intDepth - (intDepth / 2)
        val depthMinus = intDepth - depthPlus

        val list = mutableListOf(center)

        list.addAll(getBlocksInRow(center, widthPlus, widthMinus, facing.getRight()))

        val temp = mutableListOf<Block>()
        if (onlyAboveY) {
            list.forEach {
                temp.addAll(getBlocksInRow(it, height.toInt() - 1, 0, BlockFace.UP))
            }
        } else {
            list.forEach {
                temp.addAll(getBlocksInRow(it, heightPlus, heightMinus, BlockFace.UP))
            }
        }

        list.addAll(temp)
        temp.clear()

        list.forEach {
            temp.addAll(getBlocksInRow(it, depthPlus, depthMinus, facing))
        }

        list.addAll(temp)

        return list
    }

    private fun getBlocksInRow(center: Block, positive: Int, negative: Int, face: BlockFace): List<Block> {
        val list = mutableListOf<Block>()

        var block = center
        var direction = face

        for (i in 1..positive) {
            block = block.getRelative(direction)
            list.add(block)
        }

        block = center
        direction = face.oppositeFace

        for (i in 1..negative) {
            block = block.getRelative(direction)
            list.add(block)
        }

        return list
    }

}
