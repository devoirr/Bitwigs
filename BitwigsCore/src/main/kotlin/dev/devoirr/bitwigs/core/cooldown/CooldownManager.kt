package dev.devoirr.bitwigs.core.cooldown

import dev.devoirr.bitwigs.core.getGroup
import dev.devoirr.bitwigs.core.hasCooldownBypass
import dev.devoirr.bitwigs.core.util.TextUtility
import org.bukkit.entity.Player

object CooldownManager {

    private val cooldowns = mutableMapOf<String, Long>()

    fun addToPlayer(player: Player, cooldown: Cooldown) {
        if (player.hasCooldownBypass())
            return

        val group = player.getGroup()
        val cooldownTime = cooldown.getFor(group)

        if (cooldownTime <= 0)
            return

        val key = "${player.name}_${cooldown.key}"
        cooldowns[key] = System.currentTimeMillis() + cooldownTime * 1000L
    }

    fun getLeft(player: Player, cooldown: Cooldown): String? {
        if (player.hasCooldownBypass())
            return null

        val key = "${player.name}_${cooldown.key}"
        if (key !in cooldowns.keys)
            return null

        val left = cooldowns[key]!! - System.currentTimeMillis()
        if (left <= 0) {
            cooldowns.remove(key)
            return null
        } else {
            return TextUtility.millisToTime(left)
        }
    }

}