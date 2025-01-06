package dev.devoirr.bitwigs.core.economy.model.database

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "accounts")
class AccountRow {

    @DatabaseField(canBeNull = false)
    lateinit var uuid: String

    @DatabaseField(canBeNull = false)
    lateinit var currency: String

    @DatabaseField(canBeNull = false, dataType = DataType.DOUBLE)
    var balance: Double = 0.0

    @DatabaseField(canBeNull = false)
    lateinit var server: String

}