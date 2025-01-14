package dev.devoirr.bitwigs.core.blocks.clickable.model.database

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "clickable_blocks")
class PlacedClickableBlockRow {

    @DatabaseField(canBeNull = false)
    lateinit var location: String

    @DatabaseField(canBeNull = false)
    lateinit var type: String

}