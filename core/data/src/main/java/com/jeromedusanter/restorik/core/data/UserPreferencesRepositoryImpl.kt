package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.datastore.UserPreferencesDataSource
import com.jeromedusanter.restorik.core.model.SortMode
import com.jeromedusanter.restorik.core.model.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class UserPreferencesRepositoryImpl @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
) : UserPreferencesRepository {

    override val sortMode: Flow<SortMode> = userPreferencesDataSource.sortMode.map { savedValue ->
        savedValue?.let { value ->
            try {
                SortMode.valueOf(value = value)
            } catch (e: IllegalArgumentException) {
                DEFAULT_SORT_MODE
            }
        } ?: DEFAULT_SORT_MODE
    }.flowOn(Dispatchers.IO)

    override suspend fun setSortMode(sortMode: SortMode) = withContext(Dispatchers.IO) {
        userPreferencesDataSource.setSortMode(sortMode = sortMode.name)
    }

    override val sortOrder: Flow<SortOrder> = userPreferencesDataSource.sortOrder.map { savedValue ->
        savedValue?.let { value ->
            try {
                SortOrder.valueOf(value = value)
            } catch (e: IllegalArgumentException) {
                DEFAULT_SORT_ORDER
            }
        } ?: DEFAULT_SORT_ORDER
    }.flowOn(Dispatchers.IO)

    override suspend fun setSortOrder(sortOrder: SortOrder) = withContext(Dispatchers.IO) {
        userPreferencesDataSource.setSortOrder(sortOrder = sortOrder.name)
    }

    private companion object {
        val DEFAULT_SORT_MODE = SortMode.DATE
        val DEFAULT_SORT_ORDER = SortOrder.DESCENDING
    }
}
