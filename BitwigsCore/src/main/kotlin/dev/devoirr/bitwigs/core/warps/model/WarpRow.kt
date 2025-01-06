package dev.devoirr.bitwigs.core.warps.model

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "warps")
class WarpRow {

    @DatabaseField(id = true, canBeNull = false)
    lateinit var name: String

    @DatabaseField(canBeNull = false)
    lateinit var location: String

    @DatabaseField(canBeNull = false)
    lateinit var creator: String

}
