package dev.devoirr.bitwigs.core.blocks.action.model.database

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "clickable_blocks")
class PlacedActionBlockRow {

    @DatabaseField(canBeNull = false)
    lateinit var location: String

    @DatabaseField(canBeNull = false)
    lateinit var type: String

}