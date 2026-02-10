package com.jeromedusanter.restorik.core.testing.repository

import com.jeromedusanter.restorik.core.data.UserPreferencesRepository
import com.jeromedusanter.restorik.core.model.SortMode
import com.jeromedusanter.restorik.core.model.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TestUserPreferencesRepository : UserPreferencesRepository {

    private val _sortMode = MutableStateFlow(SortMode.DATE)
    private val _sortOrder = MutableStateFlow(SortOrder.DESCENDING)

    override val sortMode: Flow<SortMode> = _sortMode

    override suspend fun setSortMode(sortMode: SortMode) {
        _sortMode.value = sortMode
    }

    override val sortOrder: Flow<SortOrder> = _sortOrder

    override suspend fun setSortOrder(sortOrder: SortOrder) {
        _sortOrder.value = sortOrder
    }
}
