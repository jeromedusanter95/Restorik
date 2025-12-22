package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.model.RecentSearch
import com.jeromedusanter.restorik.core.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun search(query: String): Flow<List<SearchResult>>
    fun observeRecentSearches(limit: Int): Flow<List<RecentSearch>>
    suspend fun saveRecentSearch(query: String)
    suspend fun deleteRecentSearch(query: String)
    suspend fun clearRecentSearches()
}
