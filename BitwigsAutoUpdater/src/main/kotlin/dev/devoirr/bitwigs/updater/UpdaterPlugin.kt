package dev.devoirr.bitwigs.updater

import org.apache.commons.io.IOUtils
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.InputStream
import java.net.URI
import kotlin.math.min

class UpdaterPlugin: JavaPlugin() {

    override fun onEnable() {

        var input: InputStream? = null
        var latest = "Unknown"
        try {
            input = URI("https://raw.githubusercontent.com/devoirr/Bitwigs/refs/heads/main/version.txt").toURL().openStream()
        } catch (e: Exception) {
            Bukkit.getLogger().info("Failed to check for latest Bitwigs version.")
            e.printStackTrace()
        }

        try {
            latest = IOUtils.readLines(input, "UTF-8")[0]
        } catch (e: Exception) {
            Bukkit.getLogger().info("Failed to check for latest Bitwigs version.")
            e.printStackTrace()
        } finally {
            IOUtils.closeQuietly(input)
        }


        val corePlugin = Bukkit.getPluginManager().getPlugin("Bitwigs") ?: return
        val currentVersion = corePlugin.pluginMeta.version

        logger.info("Current version: $currentVersion")
        logger.info("Latest Bitwigs version: $latest")

        val outdated = isOutdated(currentVersion, latest)

        if (!outdated)
            return



    }

    private fun isOutdated(current: String, latest: String): Boolean {

        val currentArgs = current.split(".")
        val latestArgs = latest.split(".")

        val size = min(latestArgs.size, currentArgs.size)

        for (i in 0 until size) {
            if (latestArgs[i] > currentArgs[i]) {
                return true
            }
        }

        return false

    }

    private fun downloadUpdate(): Boolean {
        val localFile = File("plugins/Bitwigs.jar")
        return true
    }

}