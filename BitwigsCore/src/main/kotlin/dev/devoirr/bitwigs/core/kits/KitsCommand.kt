package dev.devoirr.bitwigs.core.kits

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import dev.devoirr.bitwigs.core.locale.Locale
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot

@CommandAlias("kits")
@CommandPermission("bitwigs.command.kits")
class KitsCommand(private val manager: KitsManager) : BaseCommand() {

    @Subcommand("create")
    fun createKit(player: Player, name: String) {

        val items = (0..35).mapNotNull { player.inventory.getItem(it) }

        val kit = Kit(name, 0, items, mutableMapOf())
        if (player.inventory.getItem(EquipmentSlot.HEAD).type != Material.AIR) {
            kit.equipment[EquipmentSlot.HEAD] = player.inventory.getItem(EquipmentSlot.HEAD)
        }

        if (player.inventory.getItem(EquipmentSlot.CHEST).type != Material.AIR) {
            kit.equipment[EquipmentSlot.CHEST] = player.inventory.getItem(EquipmentSlot.CHEST)
        }

        if (player.inventory.getItem(EquipmentSlot.LEGS).type != Material.AIR) {
            kit.equipment[EquipmentSlot.LEGS] = player.inventory.getItem(EquipmentSlot.LEGS)
        }

        if (player.inventory.getItem(EquipmentSlot.FEET).type != Material.AIR) {
            kit.equipment[EquipmentSlot.FEET] = player.inventory.getItem(EquipmentSlot.FEET)
        }

        if (manager.getKit(name) != null) {
            Locale.kitAlreadyExists.send(player, "{name}" to name)
            return
        }

        manager.saveKit(kit)
        Locale.kitCreated.send(player)

    }

    @Subcommand("get")
    fun get(player: Player, name: String) {

        val kit = manager.getKit(name)
        if (kit == null) {
            Locale.kitNotFound.send(player, "{name}" to name)
            return
        }

        kit.give(player)

    }

    @Subcommand("preview")
    fun preview(player: Player, name: String) {

        val kit = manager.getKit(name)
        if (kit == null) {
            Locale.kitNotFound.send(player, "{name}" to name)
            return
        }

        kit.getPreview().openFor(player)

    }
}