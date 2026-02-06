package com.jeromedusanter.restorik.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

@Fts4(
    contentEntity = CityEntity::class,
    tokenizer = FtsOptions.TOKENIZER_UNICODE61,
    tokenizerArgs = ["remove_diacritics=2"]
)
@Entity(tableName = "cities_fts")
data class CityFts(
    @ColumnInfo(name = "name") val name: String
)
