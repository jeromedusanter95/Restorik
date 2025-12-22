package com.jeromedusanter.restorik.debug

import com.jeromedusanter.restorik.core.data.MealRepository
import com.jeromedusanter.restorik.core.data.RestaurantRepository
import com.jeromedusanter.restorik.core.model.Meal
import com.jeromedusanter.restorik.core.model.Restaurant
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class MockDataPopulator @Inject constructor(
    private val mealRepository: MealRepository,
    private val restaurantRepository: RestaurantRepository
) {
    suspend fun populateWithMockData() {
        // Create mock restaurants first
        val restaurantNames = listOf(
            "Le Petit Bistro",
            "Sushi Master",
            "Pizza Italia",
            "Le Gourmet",
            "Burger House",
            "Thai Paradise",
            "La Brasserie",
            "Ramen King"
        )

        val restaurants = mutableListOf<Restaurant>()
        restaurantNames.forEach { name ->
            val restaurant = restaurantRepository.saveByNameAndGetLocal(restaurantName = name)
            restaurants.add(restaurant)
        }

        // Create mock meals
        val meals = createMockMeals(restaurantList = restaurants)
        meals.forEach { meal ->
            mealRepository.saveMealInLocalDb(meal = meal)
        }
    }

    private fun createMockMeals(restaurantList: List<Restaurant>): List<Meal> {
        val now = LocalDateTime.now()
        val mealNames = listOf(
            "Steak Frites",
            "Salmon Sushi Platter",
            "Margherita Pizza",
            "Caesar Salad",
            "Beef Burger",
            "Pad Thai",
            "Duck Confit",
            "Tonkotsu Ramen",
            "Tiramisu",
            "Chocolate Fondant",
            "Crème Brûlée",
            "Tuna Tartare",
            "Carbonara Pasta",
            "Green Curry",
            "Ribeye Steak",
            "Mushroom Risotto",
            "Tom Yum Soup",
            "Chicken Teriyaki",
            "Vegetable Tempura",
            "Beef Pho"
        )

        val comments = listOf(
            "Absolutely delicious!",
            "Great value for money",
            "Nice atmosphere",
            "Would recommend",
            "Portion was generous",
            "Perfectly cooked",
            "Fresh ingredients",
            "Will come back again",
            "Good service",
            "Authentic taste",
            "Highly recommended",
            "Very tasty",
            "Excellent presentation",
            "Friendly staff",
            "Cozy restaurant"
        )

        return (1..20).map { index ->
            // Generate random date within past 6 months
            val daysAgo = Random.nextInt(from = 0, until = 180)
            val dateTime = now.minusDays(daysAgo.toLong())
                .withHour(Random.nextInt(from = 11, until = 22))
                .withMinute(Random.nextInt(from = 0, until = 60))

            Meal(
                id = index,
                restaurantId = restaurantList[Random.nextInt(until = restaurantList.size)].id,
                name = mealNames[index - 1],
                comment = comments[Random.nextInt(until = comments.size)],
                price = Random.nextDouble(from = 8.0, until = 45.0).let {
                    // Round to 2 decimal places
                    (it * 100).toInt() / 100.0
                },
                dateTime = dateTime,
                ratingOnFive = Random.nextInt(from = 3, until = 6),
                photoList = emptyList()
            )
        }
    }
}
