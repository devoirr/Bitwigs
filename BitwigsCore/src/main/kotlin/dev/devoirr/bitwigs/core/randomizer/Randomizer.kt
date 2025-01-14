package dev.devoirr.bitwigs.core.randomizer

import kotlin.random.Random

class Randomizer<T : ItemWithChance>(private val list: List<T>) {

    fun chooseRandom(): T {
        val sortByChance = list.shuffled().sortedBy { it.getChance() }
        val maxChance = sortByChance.last().getChance()

        val chance = Random.nextDouble(0.0, maxChance + 1)
        for (t in sortByChance) {
            if (chance < t.getChance())
                return t
        }

        return sortByChance.last()
    }

}