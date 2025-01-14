package dev.devoirr.bitwigs.core.blocks.dropping.model.task

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.ReplacedBlockData
import dev.devoirr.bitwigs.core.blocks.ReplacedBlocks
import dev.devoirr.bitwigs.core.blocks.dropping.model.DroppingBlockType
import dev.devoirr.bitwigs.core.centralize
import dev.devoirr.bitwigs.core.randomizer.Randomizer
import org.bukkit.Material
import org.bukkit.Note
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class LootingTask(private val block: Block, private val type: DroppingBlockType, private val steps: Int) :
    BukkitRunnable(), HologramProgressTask {

    init {
        block.setMetadata("looting", FixedMetadataValue(BitwigsPlugin.instance, "true"))
    }

    private var defaultMaterial: Material = block.type
    private var defaultBlockData: BlockData = block.blockData

    private var step: Int = 0
    private val uuid = UUID.randomUUID().toString()
    private val loots = block.getMetadata("dropping_block_loots")[0].asInt()

    override fun run() {

        if (createFunctionIfOnStart()) {

            if (getType().lootFiller != null) {
                val filler = getType().lootFiller!!

                defaultMaterial = block.type
                defaultBlockData = block.blockData

                block.type = filler.material
                if (block.type == Material.NOTE_BLOCK && filler.note != null && filler.isPowered != null && filler.instrument != null) {
                    val data = block.blockData as NoteBlock

                    data.note = Note(filler.note)
                    data.instrument = filler.instrument
                    data.isPowered = filler.isPowered

                    ReplacedBlocks.add(ReplacedBlockData(block, defaultMaterial, defaultBlockData))
                }
            }

            step++
            return
        }

        if (step == steps + 1) {
            cancel()

            if (getType().lootFiller != null) {
                block.type = defaultMaterial
                block.blockData = defaultBlockData

                ReplacedBlocks.removeBlock(block)
            }

            if (getType().items.isNotEmpty()) {
                val drop = Randomizer(getType().items).chooseRandom()
                getBlock().world.dropItemNaturally(
                    getBlock().location.clone().centralize().add(0.0, 0.7, 0.0),
                    drop.itemStack
                )
            }

            block.removeMetadata("looting", BitwigsPlugin.instance)
            block.setMetadata("dropping_block_loots", FixedMetadataValue(BitwigsPlugin.instance, loots + 1))

            if (type.refillAfterLoots <= loots + 1) {
                RefillingTask(block, type, type.refillTime)
                    .runTaskTimer(BitwigsPlugin.instance, 0L, 20L)
            }

        } else {
            type.lootEffect?.playAt(block.location.clone().centralize())
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