package dev.devoirr.bitwigs.core.blocks.dropping.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.dropping.DroppingBlocksManager
import dev.devoirr.bitwigs.core.blocks.dropping.model.DroppingBlockItem
import dev.devoirr.bitwigs.core.blocks.dropping.model.database.PlacedDroppingBlockRow
import dev.devoirr.bitwigs.core.gui.Menu
import dev.devoirr.bitwigs.core.locale.Locale
import dev.devoirr.bitwigs.core.toComponent
import dev.devoirr.bitwigs.core.toString
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue

@CommandAlias("droppingblocks|db")
@CommandPermission("bitwigs.droppingblocks")
class DroppingBlocksCommand(private val manager: DroppingBlocksManager) : BaseCommand() {

    @Subcommand("additem")
    @Syntax("<ID> <Default Chance>")
    @Description("Добавляет предмет, выпадающий из дроп-блоков")
    fun addItem(player: Player, id: String, chance: Double) {
        var section = manager.config.get().getConfigurationSection("items")
        if (section == null) {
            section = manager.config.get().createSection("items")
        }

        if (section.getKeys(false).contains(id)) {
            Locale.droppingBlockItemAlreadyExists.send(player)
            return
        }

        val itemStack = player.inventory.itemInMainHand
        if (itemStack.type == Material.AIR) {
            Locale.needToHoldAnItem.send(player)
            return
        }

        val item = DroppingBlockItem(itemStack, chance)
        val itemSection = section.createSection(id)

        DroppingBlockItem.write(item, itemSection)

        manager.config.save()
        Locale.droppingBlockItemSaved.send(player)
    }

    @Subcommand("addblock")
    @Syntax("<Type>")
    @Description("Добавляет дроп-блок определённого вида")
    @CommandCompletion("@droppingblocktypes")
    fun addBlock(player: Player, typeName: String) {

        val targetBlock = player.getTargetBlockExact(5)
        if (targetBlock == null) {
            Locale.mustLookAtBlock.send(player)
            return
        }

        if (targetBlock.hasMetadata("dropping_block")) {
            Locale.alreadyDroppingBlock.send(player)
            return
        }

        val type = manager.getType(typeName)
        if (type == null) {
            Locale.droppingBlockTypeNotFound.send(player)
            return
        }

        val placedDroppingBlockRow = PlacedDroppingBlockRow()
        placedDroppingBlockRow.type = typeName
        placedDroppingBlockRow.location = targetBlock.location.toString(block = true)

        manager.database.write(placedDroppingBlockRow)

        targetBlock.setMetadata("dropping_block", FixedMetadataValue(BitwigsPlugin.instance, typeName))
        targetBlock.setMetadata("dropping_block_loots", FixedMetadataValue(BitwigsPlugin.instance, 0))

        Locale.droppingBlockCreated.send(player)

    }

    @Subcommand("removeblock")
    @Description("Удаляет дроп-блок")
    fun removeBlock(player: Player, args: Array<String>) {
        val targetBlock = player.getTargetBlockExact(5)
        if (targetBlock == null) {
            Locale.mustLookAtBlock.send(player)
            return
        }

        if (!targetBlock.hasMetadata("dropping_block")) {
            Locale.notDroppingBlock.send(player)
            return
        }

        val type = targetBlock.getMetadata("dropping_block")[0].asString()

        targetBlock.removeMetadata("dropping_block", BitwigsPlugin.instance)
        targetBlock.removeMetadata("dropping_block_loots", BitwigsPlugin.instance)

        val row = PlacedDroppingBlockRow()
        row.type = type
        row.location = targetBlock.location.toString(block = true)

        manager.database.delete(row)
        Locale.droppingBlockRemoved.send(player)
    }

    @Subcommand("info")
    fun info(player: Player, args: Array<String>) {

        val targetBlock = player.getTargetBlockExact(5)
        if (targetBlock == null) {
            Locale.mustLookAtBlock.send(player)
            return
        }

        if (!targetBlock.hasMetadata("dropping_block")) {
            Locale.notDroppingBlock.send(player)
            return
        }

        val id = targetBlock.getMetadata("dropping_block")[0].asString()
        val type = manager.getType(id) ?: return

        val infoItem = buildItem(
            Component.text(id).color(NamedTextColor.WHITE),
            listOf(
                "".toComponent(),
                " &fВремя рефилла: &7${type.refillTime} &fсек.".toComponent(),
                " &fВремя лута: &7${type.lootTime} &fсек.".toComponent(),
                "".toComponent(),
                " &fРефилл каждые &7${type.refillAfterLoots} &fлутов".toComponent(),
                "".toComponent()
            )
        )

        var menu = Menu().size(5 * 9).title("&0Информация о блоке".toComponent())
            .item({ _: Player -> infoItem }, 4)

        type.items.forEach {
            menu = menu.addItem({ _: Player -> setLore(it.itemStack.clone(), it.defaultChance) }, { _, _ -> }, 18)
        }

        val glass = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
        (9..17).forEach { menu = menu.item({ _ -> glass }, { _, _ -> }, it) }

        menu.openFor(player)
    }

    private fun buildItem(name: Component, lore: List<Component>): ItemStack {
        val itemStack = ItemStack(Material.ITEM_FRAME)
        val itemMeta = itemStack.itemMeta

        itemMeta.displayName(name.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
        itemMeta.lore(lore.map { it.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE) })

        itemStack.itemMeta = itemMeta
        return itemStack
    }

    private fun setLore(itemStack: ItemStack, chance: Double): ItemStack {
        val meta = itemStack.itemMeta
        meta.lore(listOf("".toComponent(), " &fШанс выпадения: &7$chance".toComponent(), "".toComponent()))
        itemStack.itemMeta = meta
        return itemStack
    }

}