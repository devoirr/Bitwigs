package dev.devoirr.bitwigs.core.blocks.dropping.model.task

import de.oliver.fancyholograms.api.FancyHologramsPlugin
import de.oliver.fancyholograms.api.data.TextHologramData
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.dropping.model.DroppingBlockType
import dev.devoirr.bitwigs.core.centralize
import dev.devoirr.bitwigs.core.util.TextUtility
import org.bukkit.ChatColor
import org.bukkit.block.Block
import org.bukkit.entity.TextDisplay
import org.bukkit.scheduler.BukkitRunnable

interface HologramProgressTask {

    fun getId(): String
    fun getBlock(): Block
    fun getSteps(): Int
    fun getStep(): Int
    fun getType(): DroppingBlockType

    fun createHologram() {

        val manager = FancyHologramsPlugin.get().hologramManager
        val hologramData =
            TextHologramData(getId(), getBlock().location.clone().centralize().add(0.0, getType().hologramYOffset, 0.0))

        hologramData.text =
            listOf(
                TextUtility.createStringProgressBar(
                    0,
                    getSteps(),
                    getSteps() * 2,
                    '|',
                    ChatColor.GREEN,
                    ChatColor.GRAY
                )
            )
        hologramData.textAlignment = TextDisplay.TextAlignment.CENTER
        hologramData.isPersistent = false

        val hologram = manager.create(hologramData)
        manager.addHologram(hologram)

    }

    fun deleteHologram() {
        val manager = FancyHologramsPlugin.get().hologramManager

        val hologram = manager.getHologram(getId())
        if (hologram.isEmpty)
            return

        manager.removeHologram(hologram.get())
    }

    fun updateHologram() {
        val hologramOptional = FancyHologramsPlugin.get().hologramManager.getHologram(getId())
        if (hologramOptional.isPresent) {
            val hologram = hologramOptional.get()
            val data = hologram.data as TextHologramData

            data.text = mutableListOf(
                TextUtility.createStringProgressBar(
                    getStep(),
                    getSteps(),
                    getSteps() * 2,
                    '|',
                    ChatColor.GREEN,
                    ChatColor.GRAY
                )
            )

            hologram.refreshForViewersInWorld()
        }
    }

    fun createFunctionIfOnStart(): Boolean {

        if (getStep() != 0)
            return false

        createHologram()

        object : BukkitRunnable() {
            override fun run() {
                deleteHologram()
            }
        }.runTaskLater(BitwigsPlugin.instance, 20L * getSteps() + 10L)

        return true

    }

}