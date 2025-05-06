package com.example.bakerytaskmanagementapp.data.database

import androidx.room.TypeConverter
import com.example.bakerytaskmanagementapp.data.database.model.MeasurementUnit
import java.util.Date

class Converters {
    @TypeConverter
    fun toLong(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date {
        return Date(millisSinceEpoch)
    }

    @TypeConverter
    fun fromMeasurementUnit(unit: MeasurementUnit): String {
        return unit.unit
    }

    @TypeConverter
    fun toMeasurementUnit(name: String): MeasurementUnit {
        return MeasurementUnit.entries
            .find { it.unit == name } ?: MeasurementUnit.PIECE
    }
}