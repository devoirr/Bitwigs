package dev.devoirr.bitwigs.core.block.furniture.model

import dev.devoirr.bitwigs.core.block.furniture.FurnitureManager
import dev.devoirr.bitwigs.core.block.furniture.model.placed.PlacedFurniture
import dev.devoirr.bitwigs.core.isTool
import dev.devoirr.bitwigs.core.withModelData
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.scheduler.BukkitRunnable

class FurnitureBreakingTask(
    private val player: Player,
    private val key: String,
    private val placedFurniture: PlacedFurniture,
    private val furnitureType: FurnitureType,
    private val manager: FurnitureManager,
    private val timePerStage: Int,
    private val finish: Runnable? = null,
    private val stage: Int = 1,
    private val displays: List<ItemDisplay> = listOf()
) : BukkitRunnable() {

    companion object {
        private val BREAKING_MATERIAL = Material.SUGAR

        private val playerData = mutableListOf<String>()

        fun registerKey(key: String) {
            playerData.add(key)
        }

        fun removeKey(player: Player) {
            playerData.removeAll { it.startsWith("${player.name}-") }
        }

        fun removeKey(key: String) {
            playerData.remove(key)
        }
    }

    override fun run() {

        if (key !in playerData) {
            displays.forEach { it.remove() }
            return
        }

        if (stage == 11) {
            finish()
            return
        }

        if (stage == 1) {
            create()
            return
        }

        displays.forEach {
            it.setItemStack(it.itemStack.withModelData(stage))
        }

        val task = FurnitureBreakingTask(
            player,
            key,
            placedFurniture,
            furnitureType,
            manager,
            timePerStage,
            finish,
            stage + 1,
            displays
        )

        manager.getTaskManager().runTaskLater(task, timePerStage.toLong())

    }

    private fun finish() {
        displays.forEach { it.remove() }
        removeKey(key)
        manager.placedFurnitureHolder.destroy(placedFurniture)

        playEffect(furnitureType, player, placedFurniture.center)

        if (!player.inventory.itemInMainHand.isTool())
            return

        val damage = furnitureType.breakDamage?.get(player.inventory.itemInMainHand.type)?.toInt() ?: 0
        if (damage == 0)
            return

        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta as Damageable

        meta.damage += damage
        item.setItemMeta(meta)

        if (meta.damage > item.type.maxDurability) {
            item.amount--
            player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
        }
    }

    private fun create() {
        val displaysList = mutableListOf<ItemDisplay>()
        val display = placedFurniture.center.world.spawn(
            placedFurniture.center.location.clone().add(0.5, 0.5, 0.5),
            ItemDisplay::class.java
        )

        val itemStack = ItemStack(BREAKING_MATERIAL)

        val itemMeta = itemStack.itemMeta

        itemMeta.setCustomModelData(stage)
        itemStack.setItemMeta(itemMeta)

        display.setItemStack(itemStack)
        displaysList.add(display)

        val task = FurnitureBreakingTask(
            player,
            key,
            placedFurniture,
            furnitureType,
            manager,
            timePerStage,
            finish,
            stage + 1,
            displaysList
        )
        manager.getTaskManager().runTaskLater(task, timePerStage.toLong())
    }

    private fun playEffect(
        furnitureType: FurnitureType,
        player: Player,
        center: Block
    ) {
        furnitureType.breakEffect?.let { effect ->
            effect.sound?.let { player.playSound(player, it, 1f, 1f) }
            effect.particle?.let {
                center.world.spawnParticle(it, center.location.clone().add(0.5, 0.5, 0.5), 10)
            }
        }
    }
}