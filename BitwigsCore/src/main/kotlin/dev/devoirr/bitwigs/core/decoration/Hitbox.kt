package dev.devoirr.bitwigs.core.decoration

import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.configuration.ConfigurationSection
import kotlin.math.roundToInt

data class Hitbox(val width: Double, val height: Double, val depth: Double) {

    companion object {
        fun parse(section: ConfigurationSection): Hitbox {
            val width = if (section.getKeys(false).contains("width")) section.getDouble("width") else 1.0
            val height = if (section.getKeys(false).contains("height")) section.getDouble("height") else 1.0
            val depth = if (section.getKeys(false).contains("depth")) section.getDouble("depth") else 1.0
            return Hitbox(width, height, depth)
        }
    }

    fun getBlocks(center: Block, blockFace: BlockFace): List<Block> {

        val height = height.roundToInt()
        val width = width.roundToInt()
        val depth = depth.roundToInt()

        val list = mutableListOf<Block>()

        var block = center
        for (i in 1..height) {
            list.add(block)
            block = block.getRelative(BlockFace.UP)
        }

        val tempList = mutableListOf<Block>()
        val depthPos = depth / 2 + 1
        val depthNeg = depth - depthPos

        for (tempBlock in list) {
            block = tempBlock

            for (i in 1..depthPos) {
                tempList.add(block)
                block = block.getRelative(blockFace)
            }

            block = tempBlock
            for (i in 1..depthNeg) {
                tempList.add(block)
                block = block.getRelative(blockFace.oppositeFace)
            }
        }

        list.addAll(tempList)
        tempList.clear()

        val widthPos = width / 2 + 1
        val widthNeg = width - widthPos

        val positiveDirection = blockFace.getRight().oppositeFace
        val negativeDirection = positiveDirection.oppositeFace

        for (tempBlock in list) {
            block = tempBlock

            for (i in 1..widthPos) {
                tempList.add(block)
                block = block.getRelative(positiveDirection)
            }

            block = tempBlock
            for (i in 1..widthNeg) {
                tempList.add(block)
                block = block.getRelative(negativeDirection)
            }
        }

        list.addAll(tempList)

        return list
    }
}

fun BlockFace.getRight(): BlockFace {
    return when (this) {
        BlockFace.NORTH -> BlockFace.EAST
        BlockFace.EAST -> BlockFace.SOUTH
        BlockFace.SOUTH -> BlockFace.WEST
        BlockFace.WEST -> BlockFace.NORTH
        else -> BlockFace.NORTH
    }
}
