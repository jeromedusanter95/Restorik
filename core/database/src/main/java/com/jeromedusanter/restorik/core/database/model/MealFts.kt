package com.jeromedusanter.restorik.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

@Fts4(
    contentEntity = MealEntity::class,
    tokenizer = FtsOptions.TOKENIZER_UNICODE61, // Handle all the international characters
    tokenizerArgs = ["remove_diacritics=2"] // Remove accent when indexing the words
)
@Entity(tableName = "meals_fts")
data class MealFts(
    @ColumnInfo(name = "name") val name: String
)
