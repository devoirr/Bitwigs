package dev.devoirr.bitwigs.core.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

class ComponentUtility {

    companion object {

        fun listOfStringToComponent(list: List<String>): Component {
            if (list.isEmpty())
                return Component.empty()

            if (list.size == 1)
                return LegacyComponentSerializer.legacyAmpersand().deserialize(list[0])

            var component: Component = Component.empty()
            list.forEachIndexed { index, string ->
                if (index != 0) {
                    component = component.appendNewline()
                }

                component.append(LegacyComponentSerializer.legacyAmpersand().deserialize(string))
            }

            return component
        }

    }

}