package dev.devoirr.bitwigs.core.decoration.furniture.listener

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.decoration.furniture.FurnitureManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent

class FurniturePlayerListener(private val manager: FurnitureManager) : Listener, PacketListener {

    @EventHandler
    fun onWorldJoin(event: PlayerChangedWorldEvent) {
        manager.placedFurnitureHolder.sendAllForPlayer(event.player)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        manager.placedFurnitureHolder.sendAllForPlayer(event.player)

        event.player.scheduler.runAtFixedRate(BitwigsPlugin.instance, {
            manager.placedFurnitureHolder.sendChunkForPlayer(event.player, event.player.chunk)
        }, {}, 0L, 20L * 5)
    }

    override fun onPacketSend(event: PacketSendEvent?) {
        event!!
        
        if (event.packetType != PacketType.Play.Server.CHUNK_DATA)
            return

        val packet = WrapperPlayServerChunkData(event)
        val player: Player = event.getPlayer()
        val column = packet.column

        val chunkX = column.x
        val chunkZ = column.z
        val chunk = player.world.getChunkAt(chunkX, chunkZ)

        manager.placedFurnitureHolder.sendChunkForPlayer(player, chunk)
    }


}