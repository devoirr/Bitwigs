package dev.devoirr.bitwigs.updater

import org.apache.commons.io.IOUtils
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.InputStream
import java.net.URI

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

        Bukkit.getLogger().info("Current version: $currentVersion")
        Bukkit.getLogger().info("Latest version: $latest")

    }

}