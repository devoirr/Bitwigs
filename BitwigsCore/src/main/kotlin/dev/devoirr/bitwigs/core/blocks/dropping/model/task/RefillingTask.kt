package dev.devoirr.bitwigs.core.blocks.dropping.model.task

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.dropping.model.DroppingBlockType
import org.bukkit.block.Block
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class RefillingTask(private val block: Block, private val type: DroppingBlockType, private val steps: Int) :
    BukkitRunnable(), HologramProgressTask {

    private var step = 0
    private val uuid = UUID.randomUUID().toString()

    init {
        block.setMetadata("refilling", FixedMetadataValue(BitwigsPlugin.instance, "true"))
    }

    override fun run() {

        if (createFunctionIfOnStart()) {
            step++
            return
        }

        if (step == steps + 1) {
            cancel()

            block.removeMetadata("refilling", BitwigsPlugin.instance)
            block.setMetadata("dropping_block_loots", FixedMetadataValue(BitwigsPlugin.instance, 0))
        } else {
            type.refillEffect?.playAt(block.location)
        }

        updateHologram()

        step++

    }

    override fun getId(): String {
        return uuid
    }

    override fun getBlock(): Block {
        return block
    }

    override fun getSteps(): Int {
        return steps
    }

    override fun getStep(): Int {
        return step
    }

    override fun getType(): DroppingBlockType {
        return type
    }
}