package dev.devoirr.bitwigs.core.database

import dev.devoirr.bitwigs.core.util.factory.Factory
import org.bukkit.configuration.ConfigurationSection

class DatabaseInfoFactory : Factory<DatabaseInfo> {

    override fun parse(section: ConfigurationSection): DatabaseInfo {
        val typeName = section.getString("type", "sqlite")!!.uppercase()
        val type = DatabaseType.entries.firstOrNull { it.name == typeName } ?: DatabaseType.SQLITE

        if (type == DatabaseType.SQLITE) {
            val fileName = section.getString("file-name", "database")!!
            return DatabaseInfo(type, "jdbc:sqlite:plugins/Bitwigs/$fileName.db")
        }

        val host = section.getString("host", "localhost")!!
        val database = section.getString("database", "minecraft")!!
        val user = section.getString("user", "root")!!
        val password = section.getString("password", "password")!!
        val port = section.getInt("port", 3306)

        return DatabaseInfo(type, "jdbc:mysql://$host:$port/$database?user=$user&password=$password")
    }

    override fun write(section: ConfigurationSection) {
        TODO("Not yet implemented")
    }
}