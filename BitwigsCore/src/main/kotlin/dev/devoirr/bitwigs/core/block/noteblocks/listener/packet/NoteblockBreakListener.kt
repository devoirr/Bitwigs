package dev.devoirr.bitwigs.core.block.noteblocks.listener.packet

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging
import dev.devoirr.bitwigs.core.block.noteblocks.NoteblocksManager
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player

class NoteblockBreakListener(private val manager: NoteblocksManager) : PacketListener {

    override fun onPacketReceive(event: PacketReceiveEvent?) {
        event!!

        if (event.packetType != PacketType.Play.Client.PLAYER_DIGGING)
            return

        val packet = WrapperPlayClientPlayerDigging(event)
        val player: Player = event.getPlayer()
        val item = player.inventory.itemInMainHand

        if (player.gameMode == GameMode.CREATIVE)
            return

        val blockLocation = packet.blockPosition
        val block = player.world.getBlockAt(blockLocation.x, blockLocation.y, blockLocation.z)
        val blockFace = BlockFace.valueOf(packet.blockFace.name)

        if (block.type != Material.NOTE_BLOCK)
            return

        val type = manager.getTypeByBlock(block) ?: return
    }

}