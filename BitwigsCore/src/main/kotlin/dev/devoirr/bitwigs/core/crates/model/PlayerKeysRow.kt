package dev.devoirr.bitwigs.core.crates.model

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "keys")
class PlayerKeysRow {

    @DatabaseField(canBeNull = false)
    lateinit var uuid: String

    @DatabaseField(canBeNull = false)
    lateinit var crate: String

    @DatabaseField(canBeNull = false, dataType = DataType.INTEGER)
    var amount: Int = 0

}