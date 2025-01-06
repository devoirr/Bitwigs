package dev.devoirr.bitwigs.core.block.furniture.listener.packet

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData
import dev.devoirr.bitwigs.core.block.furniture.FurnitureManager
import org.bukkit.entity.Player

class FurniturePacketListener(private val manager: FurnitureManager) : PacketListener {

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