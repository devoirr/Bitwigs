package dev.devoirr.bitwigs.core.block.furniture.model.database

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "furniture")
class FurnitureDataRow {

    @DatabaseField(canBeNull = false, dataType = DataType.INTEGER, id = true)
    var display: Int = 0

    @DatabaseField(canBeNull = false)
    lateinit var type: String

    @DatabaseField(canBeNull = false)
    lateinit var center: String

    @DatabaseField(canBeNull = false)
    lateinit var facing: String

    @DatabaseField(canBeNull = false, dataType = DataType.DOUBLE)
    var yaw: Double = 0.0

    @DatabaseField(canBeNull = false)
    lateinit var itemStack: String
}