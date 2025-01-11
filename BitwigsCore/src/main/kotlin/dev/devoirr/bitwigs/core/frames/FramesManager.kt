package dev.devoirr.bitwigs.core.frames

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.centralize
import dev.devoirr.bitwigs.core.frames.listener.FrameListener
import dev.devoirr.bitwigs.core.frames.listener.FramePacketListener
import dev.devoirr.bitwigs.core.frames.listener.toFrameData
import dev.devoirr.bitwigs.core.module.Loadable
import dev.devoirr.bitwigs.core.withAmount
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.ThreadLocalRandom

class FramesManager : Loadable {

    private val plugin = BitwigsPlugin.instance

    private val placedFrames = mutableListOf<FrameData>()
    private val random = ThreadLocalRandom.current()
    override fun getName(): String {
        return "frames"
    }

    override fun onEnable() {
        plugin.server.pluginManager.registerEvents(FrameListener(this), plugin)
        PacketEvents.getAPI().eventManager.registerListener(FramePacketListener(this), PacketListenerPriority.HIGH)
    }

    override fun onDisable() {

    }

    fun create(block: Block, blockFace: BlockFace) {
        var id = random.nextInt(432423235)
        while (placedFrames.any { it.id == id })
            id = random.nextInt(432423235)

        val direction = blockFace.oppositeFace.toFrameData()

        val entitySpawnPacket = WrapperPlayServerSpawnEntity(
            id,
            null,
            EntityTypes.ITEM_FRAME,
            SpigotConversionUtil.fromBukkitLocation(block.getRelative(blockFace).location),
            0f,
            direction,
            null
        )

        val entityMetadataPacket = WrapperPlayServerEntityMetadata(
            id,
            listOf(
                EntityData(
                    8,
                    EntityDataTypes.ITEMSTACK,
                    SpigotConversionUtil.fromBukkitItemStack(ItemStack(Material.AIR))
                ),
                EntityData(
                    9,
                    EntityDataTypes.INT,
                    2
                )
            )
        )

        for (player in block.location.getNearbyPlayers(16.0)) {
            PacketEvents.getAPI().playerManager.sendPacket(player, entitySpawnPacket)
            PacketEvents.getAPI().playerManager.sendPacket(player, entityMetadataPacket)
        }

        placedFrames.add(FrameData(id, block.location, direction, 0, null))
    }

    fun putItem(id: Int, itemStack: ItemStack) {
        val frame = placedFrames.firstOrNull { it.id == id } ?: return

        if (frame.itemStack != null) {

            val entityMetadataPacket = WrapperPlayServerEntityMetadata(
                id,
                listOf(
                    EntityData(
                        9,
                        EntityDataTypes.INT,
                        frame.rotation - 1
                    )
                )
            )

            frame.rotation--

            object : BukkitRunnable() {
                override fun run() {
                    for (player in frame.location.getNearbyPlayers(16.0)) {
                        PacketEvents.getAPI().playerManager.sendPacket(player, entityMetadataPacket)
                    }
                }
            }.runTask(plugin)

            return
        }

        if (itemStack.type == Material.AIR)
            return

        frame.itemStack = itemStack
        val entityMetadataPacket = WrapperPlayServerEntityMetadata(
            id,
            listOf(
                EntityData(
                    8,
                    EntityDataTypes.ITEMSTACK,
                    SpigotConversionUtil.fromBukkitItemStack(itemStack.clone().withAmount(1))
                )
            )
        )

        itemStack.amount--

        object : BukkitRunnable() {
            override fun run() {
                for (player in frame.location.getNearbyPlayers(16.0)) {
                    PacketEvents.getAPI().playerManager.sendPacket(player, entityMetadataPacket)
                }
            }
        }.runTask(plugin)
    }

    fun dropItem(id: Int): Boolean {
        val frame = placedFrames.firstOrNull { it.id == id } ?: return false

        val item = frame.itemStack ?: return false

        val entityMetadataPacket = WrapperPlayServerEntityMetadata(
            id,
            listOf(
                EntityData(
                    8,
                    EntityDataTypes.ITEMSTACK,
                    SpigotConversionUtil.fromBukkitItemStack(ItemStack(Material.AIR))
                )
            )
        )

        frame.itemStack = null

        object : BukkitRunnable() {
            override fun run() {
                frame.location.world.dropItemNaturally(frame.location.centralize().add(0.0, 0.5, 0.0), item)

                for (player in frame.location.getNearbyPlayers(16.0)) {
                    PacketEvents.getAPI().playerManager.sendPacket(player, entityMetadataPacket)
                }
            }
        }.runTask(plugin)
        return true
    }

    fun destroy(id: Int) {
        val frame = placedFrames.firstOrNull { it.id == id } ?: return

        placedFrames.remove(frame)

        val packet = WrapperPlayServerDestroyEntities(id)
        object : BukkitRunnable() {
            override fun run() {
                for (player in frame.location.getNearbyPlayers(16.0)) {
                    PacketEvents.getAPI().playerManager.sendPacket(player, packet)
                }

                frame.location.world.dropItemNaturally(
                    frame.location.centralize().clone().add(0.0, 0.5, 0.5), ItemStack(Material.ITEM_FRAME)
                )
            }
        }.runTask(plugin)
    }

}