package dev.devoirr.bitwigs.core.test

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import org.bukkit.Instrument
import org.bukkit.Material
import org.bukkit.Note
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.entity.Player

class TestCommand : BaseCommand() {

    @CommandAlias("test")
    fun send(player: Player) {
        val block = player.location.block

        block.type = Material.NOTE_BLOCK
        val data = block.blockData as NoteBlock

        data.note = Note(23)
        data.instrument = Instrument.BIT
        data.isPowered = false

        block.setBlockData(data, true)

    }


}