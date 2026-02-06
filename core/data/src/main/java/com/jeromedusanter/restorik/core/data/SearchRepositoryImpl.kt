package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.database.dao.CityDao
import com.jeromedusanter.restorik.core.database.dao.MealDao
import com.jeromedusanter.restorik.core.database.dao.RecentSearchDao
import com.jeromedusanter.restorik.core.database.dao.RestaurantDao
import com.jeromedusanter.restorik.core.database.model.RecentSearchEntity
import com.jeromedusanter.restorik.core.database.model.RestaurantEntity
import com.jeromedusanter.restorik.core.database.util.removeStopWords
import com.jeromedusanter.restorik.core.model.RecentSearch
import com.jeromedusanter.restorik.core.model.Restaurant
import com.jeromedusanter.restorik.core.model.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val restaurantDao: RestaurantDao,
    private val cityDao: CityDao,
    private val recentSearchDao: RecentSearchDao,
    private val mealMapper: MealMapper,
) : SearchRepository {

    override fun search(query: String): Flow<List<SearchResult>> = flow {
        if (query.isBlank()) {
            emit(emptyList())
            return@flow
        }

        // Remove stop words from query for more relevant results
        val filteredQuery = removeStopWords(query = query)

        // If all words were stop words, use original query
        val searchQuery = filteredQuery.ifBlank { query }

        val resultList = mutableListOf<SearchResult>()

        // Get all restaurants and cities at once for efficiency
        val allRestaurantList = restaurantDao.observeAll().first()
        val allCityList = cityDao.observeAll().first()

        // Build maps for lookup
        val restaurantMap = allRestaurantList.associateBy({ it.id }, { it })
        val cityMap = allCityList.associateBy({ it.id }, { it.name })

        // Search restaurants
        val restaurantEntityList = restaurantDao.searchRestaurants(query = searchQuery)
        resultList.addAll(
            restaurantEntityList.map { restaurantEntity ->
                SearchResult.RestaurantResult(
                    restaurant = restaurantEntity.toModel(),
                    cityName = cityMap[restaurantEntity.cityId] ?: ""
                )
            }
        )

        // Search meals
        val mealWithDishesList = mealDao.searchMealsWithDishes(query = searchQuery)

        // Add meal results
        mealWithDishesList.forEach { mealWithDishes ->
            val meal = mealMapper.mapEntityModelToDomainModel(mealWithDishes = mealWithDishes)
            val restaurant = restaurantMap[mealWithDishes.meal.restaurantId]
            resultList.add(
                SearchResult.MealResult(
                    meal = meal,
                    restaurantName = restaurant?.name ?: "",
                    cityName = restaurant?.let { cityMap[it.cityId] } ?: ""
                )
            )
        }

        emit(resultList)
    }.flowOn(Dispatchers.IO)

    override fun observeRecentSearches(limit: Int): Flow<List<RecentSearch>> {
        return recentSearchDao.observeRecentSearches(limit = limit).map { entityList ->
            entityList.map { entity ->
                RecentSearch(
                    id = entity.id,
                    query = entity.query,
                    timestamp = entity.timestamp,
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun saveRecentSearch(query: String) = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext

        // Delete existing entry with the same query to avoid duplicates
        recentSearchDao.deleteByQuery(query = query)

        // Insert new search
        recentSearchDao.insert(
            recentSearch = RecentSearchEntity(
                query = query,
                timestamp = System.currentTimeMillis(),
            )
        )

        // Keep only the latest 20 searches
        recentSearchDao.deleteOldSearches(limit = 20)
    }

    override suspend fun deleteRecentSearch(query: String) = withContext(Dispatchers.IO) {
        recentSearchDao.deleteByQuery(query = query)
    }

    override suspend fun clearRecentSearches() = withContext(Dispatchers.IO) {
        recentSearchDao.deleteAll()
    }

    private fun RestaurantEntity?.toModel(): Restaurant {
        return Restaurant(
            id = this?.id ?: -1,
            name = this?.name ?: "",
            cityId = this?.cityId ?: -1
        )
    }
}
