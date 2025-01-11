package dev.devoirr.bitwigs.core.blocks.dropping.database

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "dropping_blocks")
class PlacedDroppingBlockRow {

    @DatabaseField(canBeNull = false)
    lateinit var location: String

    @DatabaseField(canBeNull = false)
    lateinit var type: String

}