package dev.devoirr.bitwigs.core.block.noteblocks

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.util.Vector3i
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockBreakAnimation
import dev.devoirr.bitwigs.core.block.noteblocks.model.type.NoteblockSubType
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

class NoteblockBreakingTask(
    private val player: Player,
    private val entityId: Int,
    private val type: NoteblockSubType,
    private val key: String,
    private val manager: NoteblocksManager,
    private val block: Block,
    private val timePerStage: Int,
    private val finish: Runnable? = null,
    private val stage: Int = 0
) : BukkitRunnable() {

    companion object {
        private val playerData = mutableListOf<String>()

        fun registerKey(key: String) {
            playerData.add(key)
        }

        fun removeKey(player: Player) {
            val size = playerData.size
            playerData.removeAll { it.startsWith("${player.name}-") }
            if (size != playerData.size) {
                player.removePotionEffect(PotionEffectType.MINING_FATIGUE)
            }
        }

        fun removeKey(key: String) {
            playerData.remove(key)
        }

        fun hasKey(player: Player) = playerData.any { it.startsWith("${player.name}-") }
    }

    private val blockVector = Vector3i(block.x, block.y, block.z)

    private val resetPacket =
        WrapperPlayServerBlockBreakAnimation(player.entityId, blockVector, -1)

    override fun run() {

        if (key !in playerData) {
            sendResetPacketToWorld()
            return
        }

        if (stage == 10) {
            finish()
            return
        }

        val packet = WrapperPlayServerBlockBreakAnimation(entityId, blockVector, stage.toByte())
        block.world.players.forEach {
            PacketEvents.getAPI().playerManager.sendPacket(it, packet)
        }

        val task = NoteblockBreakingTask(
            player,
            entityId,
            type,
            key,
            manager,
            block,
            timePerStage,
            finish,
            stage + 1,
        )

        manager.getTaskManager().runTaskLater(task, timePerStage.toLong())

    }

    private fun finish() {
        block.type = Material.AIR
        removeKey(key)

        val itemStack = type.createItemStack()
        block.location.world.dropItemNaturally(block.location.clone().add(0.5, 0.5, 0.5), itemStack)
    }

    private fun sendResetPacketToWorld() {
        block.world.players.forEach {
            PacketEvents.getAPI().playerManager.sendPacket(it, resetPacket)
        }
    }
}