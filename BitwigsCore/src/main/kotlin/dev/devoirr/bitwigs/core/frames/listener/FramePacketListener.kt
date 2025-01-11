package dev.devoirr.bitwigs.core.frames.listener

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.player.InteractionHand
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity
import dev.devoirr.bitwigs.core.frames.FramesManager
import org.bukkit.entity.Player

class FramePacketListener(private val manager: FramesManager) : PacketListener {

    override fun onPacketReceive(event: PacketReceiveEvent?) {

        event!!
        if (event.packetType != PacketType.Play.Client.INTERACT_ENTITY)
            return

        val packet = WrapperPlayClientInteractEntity(event)
        val player: Player = event.getPlayer()

        if (packet.hand != InteractionHand.MAIN_HAND)
            return

        if (packet.action == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {

            if (manager.dropItem(packet.entityId))
                return

            manager.destroy(packet.entityId)

        } else {

            val itemStack = player.inventory.itemInMainHand
            
            val id = packet.entityId
            manager.putItem(id, itemStack)
        }

    }

}