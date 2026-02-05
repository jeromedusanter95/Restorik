package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.model.SortMode
import com.jeromedusanter.restorik.core.model.SortOrder
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val sortMode: Flow<SortMode>
    suspend fun setSortMode(sortMode: SortMode)

    val sortOrder: Flow<SortOrder>
    suspend fun setSortOrder(sortOrder: SortOrder)
}
