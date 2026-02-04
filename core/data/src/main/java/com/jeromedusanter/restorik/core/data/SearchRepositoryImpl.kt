package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.database.dao.MealDao
import com.jeromedusanter.restorik.core.database.dao.RecentSearchDao
import com.jeromedusanter.restorik.core.database.dao.RestaurantDao
import com.jeromedusanter.restorik.core.database.model.RecentSearchEntity
import com.jeromedusanter.restorik.core.database.model.RestaurantEntity
import com.jeromedusanter.restorik.core.model.RecentSearch
import com.jeromedusanter.restorik.core.model.Restaurant
import com.jeromedusanter.restorik.core.model.SearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val restaurantDao: RestaurantDao,
    private val recentSearchDao: RecentSearchDao,
    private val mealMapper: MealMapper,
) : SearchRepository {

    override fun search(query: String): Flow<List<SearchResult>> = flow {
        if (query.isBlank()) {
            emit(emptyList())
            return@flow
        }

        val resultList = mutableListOf<SearchResult>()

        // Search restaurants
        val restaurantEntityList = restaurantDao.searchRestaurants(query = query)
        resultList.addAll(
            restaurantEntityList.map { restaurantEntity ->
                SearchResult.RestaurantResult(restaurant = restaurantEntity.toModel())
            }
        )

        // Search meals
        val mealWithDishesList = mealDao.searchMealsWithDishes(query = query)

        // Get all restaurant entities at once for efficiency
        val allRestaurantList = restaurantDao.observeAll()
        val restaurantMap = mutableMapOf<Int, String>()

        // Wait for the first emission to build restaurant map
        allRestaurantList.collect { restaurantEntityList ->
            restaurantEntityList.forEach { restaurantEntity ->
                restaurantMap[restaurantEntity.id] = restaurantEntity.name
            }

            // Add meal results
            mealWithDishesList.forEach { mealWithDishes ->
                val meal = mealMapper.mapEntityModelToDomainModel(mealWithDishes = mealWithDishes)
                resultList.add(
                    SearchResult.MealResult(
                        meal = meal,
                        restaurantName = restaurantMap[mealWithDishes.meal.restaurantId] ?: ""
                    )
                )
            }

            emit(resultList)
        }
    }

    override fun observeRecentSearches(limit: Int): Flow<List<RecentSearch>> {
        return recentSearchDao.observeRecentSearches(limit = limit).map { entityList ->
            entityList.map { entity ->
                RecentSearch(
                    id = entity.id,
                    query = entity.query,
                    timestamp = entity.timestamp,
                )
            }
        }
    }

    override suspend fun saveRecentSearch(query: String) {
        if (query.isBlank()) return

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

    override suspend fun deleteRecentSearch(query: String) {
        recentSearchDao.deleteByQuery(query = query)
    }

    override suspend fun clearRecentSearches() {
        recentSearchDao.deleteAll()
    }

    private fun RestaurantEntity?.toModel(): Restaurant {
        return Restaurant(id = this?.id ?: -1, name = this?.name ?: "")
    }
}
