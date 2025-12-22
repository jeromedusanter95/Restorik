package com.jeromedusanter.restorik.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeromedusanter.restorik.core.data.SearchRepository
import com.jeromedusanter.restorik.core.model.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _isSearching = MutableStateFlow(false)

    @OptIn(FlowPreview::class)
    private val debouncedQuery = _query.debounce(timeoutMillis = 300)

    private val searchResults = debouncedQuery.flatMapLatest { query ->
        if (query.length >= 2) {
            searchRepository.search(query = query)
                .onStart { _isSearching.update { true } }
                .onEach { _isSearching.update { false } }
        } else {
            _isSearching.update { false }
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = emptyList()
    )

    val uiState: StateFlow<SearchUiState> = combine(
        _query,
        searchResults,
        searchRepository.observeRecentSearches(limit = 20),
        _isSearching
    ) { query, searchResultList, recentSearchList, isSearching ->
        SearchUiState(
            query = query,
            searchResultList = searchResultList.map { searchResult ->
                when (searchResult) {
                    is SearchResult.MealResult -> SearchResultUiModel.MealItem(
                        id = searchResult.meal.id,
                        name = searchResult.meal.name,
                        restaurantName = searchResult.restaurantName,
                        rating = searchResult.meal.ratingOnFive,
                        photoUri = searchResult.meal.photoList.firstOrNull(),
                    )
                    is SearchResult.RestaurantResult -> SearchResultUiModel.RestaurantItem(
                        id = searchResult.restaurant.id,
                        name = searchResult.restaurant.name,
                    )
                }
            },
            recentSearchList = recentSearchList.map { it.query },
            isSearching = isSearching,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SearchUiState.EMPTY
    )

    fun updateQuery(newQuery: String) {
        _query.update { newQuery }
    }

    fun onSearchSubmitted(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            searchRepository.saveRecentSearch(query = query)
        }
    }

    fun onRecentSearchClicked(query: String) {
        _query.update { query }
        onSearchSubmitted(query = query)
    }

    fun deleteRecentSearch(query: String) {
        viewModelScope.launch {
            searchRepository.deleteRecentSearch(query = query)
        }
    }

    fun clearQuery() {
        _query.update { "" }
    }
}
