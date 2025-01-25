package dev.devoirr.bitwigs.core.decoration.noteblocks.packet

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.player.DiggingAction
import com.github.retrooper.packetevents.util.Vector3i
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockBreakAnimation
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.centralize
import dev.devoirr.bitwigs.core.decoration.InteractionType
import dev.devoirr.bitwigs.core.decoration.noteblocks.NoteblocksManager
import dev.devoirr.bitwigs.core.decoration.noteblocks.model.NoteblockType
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*

class NoteblockBreakListener(private val manager: NoteblocksManager) : PacketListener {

    private val tasks = mutableMapOf<Location, BukkitTask>()
    private val blocks = mutableMapOf<UUID, Block>()

    override fun onPacketReceive(event: PacketReceiveEvent?) {
        event!!

        if (event.packetType != PacketType.Play.Client.PLAYER_DIGGING)
            return

        val packet = WrapperPlayClientPlayerDigging(event)
        val player: Player = event.getPlayer()
        if (player.gameMode == GameMode.CREATIVE)
            return

        val block = player.world.getBlockAt(packet.blockPosition.x, packet.blockPosition.y, packet.blockPosition.z)

        blocks[player.uniqueId] = block

        val typePair = manager.getTypeAndSubtype(block) ?: return

        event.isCancelled = true

        if (packet.action == DiggingAction.START_DIGGING) {
            handleStart(event.getPlayer(), block, typePair.first)
        } else {
            tasks.remove(block.location)
        }

    }

    private fun handleStart(player: Player, block: Block, type: NoteblockType) {
        tasks.remove(block.location)?.cancel()
        blocks[player.uniqueId] = block

        val period = manager.getPeriodForBlock(block, player.inventory.itemInMainHand)
        val item = player.inventory.itemInMainHand

        object : BukkitRunnable() {
            override fun run() {
                player.gameMode = GameMode.ADVENTURE
                player.addPotionEffect(
                    PotionEffect(
                        PotionEffectType.MINING_FATIGUE,
                        (period * 11).toInt(),
                        Int.MAX_VALUE,
                        false,
                        false,
                        false
                    )
                )
            }
        }.runTask(BitwigsPlugin.instance)

        val task = object : BukkitRunnable() {
            var value = 0
            override fun run() {

                if (!tasks.containsKey(block.location)) {
                    cancel()
                    return
                }

                if (player.getTargetBlockExact(5) != block) {
                    cancel()
                    return
                }

                type.effects[InteractionType.DIG]?.playAt(block.location.centralize())
                sendBlockDamage(block, value)

                if (value++ < 10)
                    return

                manager.breakBlock(block, player, item)
                cancel()

                tasks.remove(block.location)
                player.gameMode = GameMode.SURVIVAL

            }

            override fun cancel() {
                super.cancel()

                object : BukkitRunnable() {
                    override fun run() {
                        player.gameMode = GameMode.SURVIVAL
                        player.removePotionEffect(PotionEffectType.MINING_FATIGUE)
                    }
                }.runTask(BitwigsPlugin.instance)

                blocks.remove(player.uniqueId)
                sendBlockDamage(block, 11)
            }
        }.runTaskTimer(BitwigsPlugin.instance, period, period)

        tasks[block.location] = task
    }

    fun sendBlockDamage(block: Block, step: Int) {
        val packet = WrapperPlayServerBlockBreakAnimation(0, Vector3i(block.x, block.y, block.z), step.toByte())
        for (nearbyPlayer in block.location.getNearbyPlayers(16.0)) {
            PacketEvents.getAPI().playerManager.sendPacket(nearbyPlayer, packet)
        }
    }

}