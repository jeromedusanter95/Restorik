package com.jeromedusanter.restorik.feature.search

data class SearchUiState(
    val query: String = "",
    val searchResultList: List<SearchResultUiModel> = emptyList(),
    val recentSearchList: List<String> = emptyList(),
    val isSearching: Boolean = false,
) {
    companion object {
        val EMPTY = SearchUiState()
    }
}
