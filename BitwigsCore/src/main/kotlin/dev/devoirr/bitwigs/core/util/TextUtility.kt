package dev.devoirr.bitwigs.core.util

import com.google.common.base.Strings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor

class TextUtility {

    companion object {

        fun listOfStringToComponent(list: List<String>): Component {
            if (list.isEmpty())
                return Component.empty()

            if (list.size == 1)
                return LegacyComponentSerializer.legacyAmpersand().deserialize(list[0])
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.NOT_SET)

            var component: Component = Component.empty()
            list.forEachIndexed { index, string ->
                if (index != 0) {
                    component = component.appendNewline()
                }

                component.append(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(string)
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.NOT_SET)
                )
            }

            return component
        }

        fun createProgressBar(
            current: Int,
            max: Int,
            bars: Int,
            symbol: Char,
            completedColor: TextColor,
            defaultColor: TextColor
        ): Component {

            val percent = (current.toDouble() / max.toDouble())
            val filled = (bars.toDouble() * percent).toInt()

            var component = Component.empty()

            var color: TextColor
            for (i in 1..bars) {
                color = if (i < filled) completedColor else defaultColor
                component = component.append(Component.text(symbol).color(color))
            }

            return component

        }

        fun createStringProgressBar(
            current: Int,
            max: Int,
            bars: Int,
            symbol: Char,
            completedColor: ChatColor,
            defaultColor: ChatColor
        ): String {
            val percent = (current.toDouble() / max.toDouble())
            val filled = (bars.toDouble() * percent).toInt()

            return Strings.repeat("" + completedColor + symbol, filled) + Strings.repeat(
                "" + defaultColor + symbol,
                bars - filled
            )
        }

        fun millisToTime(millis: Long): String {
            val seconds = millis / 1000
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val secs = seconds % 60

            // Manually build the string for optimal performance
            return formatTimePiece(hours.toInt(), true) +
                    formatTimePiece(minutes.toInt(), true) +
                    formatTimePiece(seconds.toInt(), false)
        }

        private fun formatTimePiece(time: Int, needColon: Boolean): String {
            if (time == 0)
                return ""

            return "${if (time < 10) "0" else ""}$time${if (needColon) ":" else ""}"
        }

    }

}