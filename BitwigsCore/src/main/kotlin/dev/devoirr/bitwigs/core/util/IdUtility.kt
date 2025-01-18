package dev.devoirr.bitwigs.core.util

import java.util.*

class IdUtility {

    companion object {
        fun getUID(digit: Int): Long {
            var currentMilliSeconds: String = "" + Calendar.getInstance().timeInMillis
            var genDigit: Int = digit
            if (genDigit < 8)
                genDigit = 8

            if (genDigit > 12)
                genDigit = 12

            val cut = currentMilliSeconds.length - genDigit
            currentMilliSeconds = currentMilliSeconds.substring(cut);
            return currentMilliSeconds.toLong()
        }
    }
}