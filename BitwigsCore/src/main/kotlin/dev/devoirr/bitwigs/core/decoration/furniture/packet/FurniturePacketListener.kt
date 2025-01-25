package dev.devoirr.bitwigs.core.decoration.furniture.packet

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.player.DiggingAction
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.centralize
import dev.devoirr.bitwigs.core.decoration.InteractionType
import dev.devoirr.bitwigs.core.decoration.furniture.FurnitureManager
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class FurniturePacketListener(private val manager: FurnitureManager) : PacketListener {

    private val tasks = mutableMapOf<Location, BukkitTask>()

    override fun onPacketReceive(event: PacketReceiveEvent?) {

        event!!

        if (event.packetType != PacketType.Play.Client.PLAYER_DIGGING)
            return

        val packet = WrapperPlayClientPlayerDigging(event)
        val player: Player = event.getPlayer()
        if (player.gameMode == GameMode.CREATIVE)
            return

        val block = player.world.getBlockAt(packet.blockPosition.x, packet.blockPosition.y, packet.blockPosition.z)

        if (!block.hasMetadata("furniture"))
            return

        if (block.type != Material.BARRIER) {
            block.removeMetadata("furniture", BitwigsPlugin.instance)
            return
        }

        event.isCancelled = true

        if (packet.action == DiggingAction.START_DIGGING) {
            handleStart(event.getPlayer(), block)
        } else {
            tasks.remove(block.location)
        }

    }

    private fun handleStart(player: Player, block: Block) {
        tasks.remove(block.location)?.cancel()

        val period = manager.getPeriodForBlock(block, player.inventory.itemInMainHand)
        val placedFurniture = manager.getPlacedFurniture(block.getMetadata("furniture")[0].asString()) ?: return
        val type = manager.getFurnitureType(placedFurniture.type) ?: return
        val item = player.inventory.itemInMainHand

        object : BukkitRunnable() {
            override fun run() {
                player.gameMode = GameMode.ADVENTURE
            }
        }.runTask(BitwigsPlugin.instance)

        val task = object : BukkitRunnable() {
            var value = 0
            override fun run() {

                if (!tasks.containsKey(block.location)) {
                    cancel()
                    return
                }

                type.effects[InteractionType.DIG]?.playAt(block.location.centralize())

                if (value++ < 10)
                    return

                manager.breakBlock(block, player, item)
                cancel()

                tasks.remove(block.location)
                player.gameMode = GameMode.SURVIVAL
            }

            override fun cancel() {
                super.cancel()
                player.gameMode = GameMode.SURVIVAL
            }
        }.runTaskTimer(BitwigsPlugin.instance, period, period)

        tasks[block.location] = task
    }


}