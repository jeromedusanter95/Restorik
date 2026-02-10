package com.jeromedusanter.restorik.core.testing.data

import com.jeromedusanter.restorik.core.model.City
import com.jeromedusanter.restorik.core.model.Dish
import com.jeromedusanter.restorik.core.model.DishType
import com.jeromedusanter.restorik.core.model.Meal
import com.jeromedusanter.restorik.core.model.Restaurant
import java.time.LocalDateTime

/**
 * Fake data for testing meal functionality
 * Use this data in tests by calling the send methods on Test repositories
 */
object FakeMealData {

    val cities = listOf(
        City(id = 1, name = "Paris"),
        City(id = 2, name = "Lyon"),
        City(id = 3, name = "Tokyo")
    )

    val restaurants = listOf(
        Restaurant(id = 1, name = "Sapore Italiano", cityId = 1),
        Restaurant(id = 2, name = "Pizza Palace", cityId = 1),
        Restaurant(id = 3, name = "Sushi Spot", cityId = 3),
        Restaurant(id = 4, name = "Burger Barn", cityId = 1),
        Restaurant(id = 5, name = "Le Bistrot Français", cityId = 2)
    )

    val dishes = listOf(
        // Italian dishes
        Dish(
            id = 1,
            name = "Spaghetti Carbonara",
            rating = 4.5f,
            description = "Classic Roman pasta",
            price = 18.50,
            dishType = DishType.MAIN_COURSE
        ),
        Dish(
            id = 2,
            name = "Tiramisu",
            rating = 5.0f,
            description = "Coffee dessert",
            price = 8.50,
            dishType = DishType.DESSERT
        ),
        Dish(
            id = 3,
            name = "Bruschetta",
            rating = 4.2f,
            description = "Grilled bread",
            price = 7.00,
            dishType = DishType.STARTER
        ),
        // Sushi dishes
        Dish(
            id = 4,
            name = "Salmon Nigiri",
            rating = 4.7f,
            description = "Fresh salmon",
            price = 6.50,
            dishType = DishType.MAIN_COURSE
        ),
        Dish(
            id = 5,
            name = "California Roll",
            rating = 4.3f,
            description = "Crab and avocado",
            price = 9.00,
            dishType = DishType.MAIN_COURSE
        ),
        // Burger dishes
        Dish(
            id = 6,
            name = "Classic Cheeseburger",
            rating = 4.6f,
            description = "Beef patty",
            price = 12.00,
            dishType = DishType.MAIN_COURSE
        ),
        Dish(
            id = 7,
            name = "French Fries",
            rating = 4.5f,
            description = "Crispy fries",
            price = 5.00,
            dishType = DishType.SIDE_DISH
        ),
        // French dishes
        Dish(
            id = 8,
            name = "Coq au Vin",
            rating = 4.9f,
            description = "Chicken in wine",
            price = 24.00,
            dishType = DishType.MAIN_COURSE
        ),
        Dish(
            id = 9,
            name = "Crème Brûlée",
            rating = 5.0f,
            description = "Vanilla custard",
            price = 8.00,
            dishType = DishType.DESSERT
        ),
        // Pizza
        Dish(
            id = 10,
            name = "Margherita Pizza",
            rating = 4.8f,
            description = "Classic pizza",
            price = 14.00,
            dishType = DishType.MAIN_COURSE
        )
    )

    val meals = listOf(
        // Today
        Meal(
            id = 1,
            name = "Italian Dinner",
            restaurantId = 1,
            dateTime = LocalDateTime.now().minusHours(2),
            photoList = emptyList(),
            dishList = listOf(dishes[2], dishes[0], dishes[1]), // Bruschetta, Carbonara, Tiramisu
            isSomeoneElsePaying = false
        ),
        // Yesterday
        Meal(
            id = 2,
            name = "Sushi Feast",
            restaurantId = 3,
            dateTime = LocalDateTime.now().minusDays(1).minusHours(5),
            photoList = emptyList(),
            dishList = listOf(dishes[3], dishes[4]), // Salmon Nigiri, California Roll
            isSomeoneElsePaying = true
        ),
        // This week (3 days ago)
        Meal(
            id = 3,
            name = "Burger Lunch",
            restaurantId = 4,
            dateTime = LocalDateTime.now().minusDays(3),
            photoList = emptyList(),
            dishList = listOf(dishes[5], dishes[6]), // Cheeseburger, Fries
            isSomeoneElsePaying = false
        ),
        // This week (5 days ago)
        Meal(
            id = 4,
            name = "Pizza Night",
            restaurantId = 2,
            dateTime = LocalDateTime.now().minusDays(5),
            photoList = emptyList(),
            dishList = listOf(dishes[9]), // Margherita Pizza
            isSomeoneElsePaying = false
        ),
        // This month (15 days ago)
        Meal(
            id = 5,
            name = "French Experience",
            restaurantId = 5,
            dateTime = LocalDateTime.now().minusDays(15),
            photoList = emptyList(),
            dishList = listOf(dishes[7], dishes[8]), // Coq au Vin, Crème Brûlée
            isSomeoneElsePaying = true
        ),
        // Older (40 days ago)
        Meal(
            id = 6,
            name = "Quick Burger",
            restaurantId = 4,
            dateTime = LocalDateTime.now().minusDays(40),
            photoList = emptyList(),
            dishList = listOf(dishes[5]), // Cheeseburger
            isSomeoneElsePaying = false
        ),
        // Another at same restaurant (for filter testing)
        Meal(
            id = 7,
            name = "Italian Lunch",
            restaurantId = 1,
            dateTime = LocalDateTime.now().minusDays(7),
            photoList = emptyList(),
            dishList = listOf(dishes[0], dishes[1]), // Carbonara, Tiramisu
            isSomeoneElsePaying = false
        )
    )
}
