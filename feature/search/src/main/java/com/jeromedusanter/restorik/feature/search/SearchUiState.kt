package com.jeromedusanter.restorik.feature.search

data class SearchUiState(
    val query: String,
    val searchResultList: List<SearchResultUiModel>,
    val recentSearchList: List<String>,
    val isSearching: Boolean,
) {
    companion object {
        val EMPTY = SearchUiState(
            query = "",
            searchResultList = emptyList(),
            recentSearchList = emptyList(),
            isSearching = false
        )
    }
}
