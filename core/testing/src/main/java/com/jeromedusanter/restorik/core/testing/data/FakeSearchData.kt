package com.jeromedusanter.restorik.core.testing.data

import com.jeromedusanter.restorik.core.model.Dish
import com.jeromedusanter.restorik.core.model.DishType
import com.jeromedusanter.restorik.core.model.Meal
import com.jeromedusanter.restorik.core.model.Restaurant
import java.time.LocalDateTime

/**
 * Fake data for testing search functionality
 * Use this data in tests by calling the send methods on TestSearchRepository
 */
object FakeSearchData {

    val cities = mapOf(
        1 to "Paris",
        2 to "Lyon"
    )

    val restaurants = listOf(
        Restaurant(
            id = 1,
            name = "Sapore Italiano",
            cityId = 1
        ),
        Restaurant(
            id = 2,
            name = "Pizza Palace",
            cityId = 1
        ),
        Restaurant(
            id = 3,
            name = "Sushi Spot",
            cityId = 1
        ),
        Restaurant(
            id = 4,
            name = "Burger Barn",
            cityId = 1
        ),
        Restaurant(
            id = 5,
            name = "Le Bistrot Français",
            cityId = 2
        ),
        Restaurant(
            id = 6,
            name = "Taco Fiesta",
            cityId = 2
        ),
        Restaurant(
            id = 7,
            name = "Golden Dragon",
            cityId = 1
        ),
        Restaurant(
            id = 8,
            name = "The Steakhouse",
            cityId = 2
        )
    )

    val dishes = listOf(
        // Italian dishes
        Dish(
            id = 1,
            name = "Spaghetti Carbonara",
            rating = 4.5f,
            description = "Classic Roman pasta with eggs, pecorino, and guanciale",
            price = 18.50,
            dishType = DishType.MAIN_COURSE
        ),
        Dish(
            id = 2,
            name = "Margherita Pizza",
            rating = 4.8f,
            description = "Traditional pizza with tomato, mozzarella, and basil",
            price = 14.00,
            dishType = DishType.MAIN_COURSE
        ),
        Dish(
            id = 3,
            name = "Tiramisu",
            rating = 5.0f,
            description = "Coffee-flavored Italian dessert",
            price = 8.50,
            dishType = DishType.DESSERT
        ),
        Dish(
            id = 4,
            name = "Bruschetta",
            rating = 4.2f,
            description = "Grilled bread with tomatoes and olive oil",
            price = 7.00,
            dishType = DishType.STARTER
        ),
        // Sushi dishes
        Dish(
            id = 5,
            name = "Salmon Nigiri",
            rating = 4.7f,
            description = "Fresh salmon on pressed rice",
            price = 6.50,
            dishType = DishType.MAIN_COURSE
        ),
        Dish(
            id = 6,
            name = "California Roll",
            rating = 4.3f,
            description = "Crab, avocado, and cucumber roll",
            price = 9.00,
            dishType = DishType.MAIN_COURSE
        ),
        Dish(
            id = 7,
            name = "Miso Soup",
            rating = 4.0f,
            description = "Traditional Japanese soup",
            price = 4.50,
            dishType = DishType.STARTER
        ),
        Dish(
            id = 8,
            name = "Green Tea Ice Cream",
            rating = 4.4f,
            description = "Japanese-style matcha ice cream",
            price = 5.50,
            dishType = DishType.DESSERT
        ),
        // Burger dishes
        Dish(
            id = 9,
            name = "Classic Cheeseburger",
            rating = 4.6f,
            description = "Beef patty with cheddar, lettuce, tomato",
            price = 12.00,
            dishType = DishType.MAIN_COURSE
        ),
        Dish(
            id = 10,
            name = "Bacon Burger",
            rating = 4.8f,
            description = "Double beef with crispy bacon",
            price = 15.00,
            dishType = DishType.MAIN_COURSE
        ),
        Dish(
            id = 11,
            name = "French Fries",
            rating = 4.5f,
            description = "Crispy golden fries",
            price = 5.00,
            dishType = DishType.SIDE_DISH
        ),
        Dish(
            id = 12,
            name = "Milkshake",
            rating = 4.7f,
            description = "Vanilla or chocolate milkshake",
            price = 6.00,
            dishType = DishType.DRINK
        ),
        // French dishes
        Dish(
            id = 13,
            name = "Coq au Vin",
            rating = 4.9f,
            description = "Chicken braised in red wine",
            price = 24.00,
            dishType = DishType.MAIN_COURSE
        ),
        Dish(
            id = 14,
            name = "French Onion Soup",
            rating = 4.6f,
            description = "Caramelized onions with gruyere",
            price = 9.50,
            dishType = DishType.STARTER
        ),
        Dish(
            id = 15,
            name = "Crème Brûlée",
            rating = 5.0f,
            description = "Vanilla custard with caramelized sugar",
            price = 8.00,
            dishType = DishType.DESSERT
        ),
        // Mexican dishes
        Dish(
            id = 16,
            name = "Tacos al Pastor",
            rating = 4.7f,
            description = "Marinated pork with pineapple",
            price = 11.00,
            dishType = DishType.MAIN_COURSE
        ),
        Dish(
            id = 17,
            name = "Guacamole",
            rating = 4.4f,
            description = "Fresh avocado dip",
            price = 6.50,
            dishType = DishType.STARTER
        ),
        Dish(
            id = 18,
            name = "Churros",
            rating = 4.5f,
            description = "Fried dough with cinnamon sugar",
            price = 7.00,
            dishType = DishType.DESSERT
        ),
        // Chinese dishes
        Dish(
            id = 19,
            name = "Kung Pao Chicken",
            rating = 4.6f,
            description = "Spicy stir-fried chicken with peanuts",
            price = 16.00,
            dishType = DishType.MAIN_COURSE
        ),
        Dish(
            id = 20,
            name = "Spring Rolls",
            rating = 4.3f,
            description = "Crispy vegetable rolls",
            price = 6.00,
            dishType = DishType.STARTER
        ),
        // Steakhouse dishes
        Dish(
            id = 21,
            name = "Ribeye Steak",
            rating = 4.9f,
            description = "Premium aged beef, 300g",
            price = 32.00,
            dishType = DishType.MAIN_COURSE
        ),
        Dish(
            id = 22,
            name = "Caesar Salad",
            rating = 4.4f,
            description = "Romaine lettuce with Caesar dressing",
            price = 9.00,
            dishType = DishType.STARTER
        ),
        Dish(
            id = 23,
            name = "Mashed Potatoes",
            rating = 4.5f,
            description = "Creamy garlic mashed potatoes",
            price = 6.50,
            dishType = DishType.SIDE_DISH
        ),
        Dish(
            id = 24,
            name = "Red Wine",
            rating = 4.7f,
            description = "House red wine, glass",
            price = 8.00,
            dishType = DishType.DRINK
        )
    )

    val meals = listOf(
        // Meal at Sapore Italiano
        Meal(
            id = 1,
            name = "Italian Dinner",
            restaurantId = 1,
            dateTime = LocalDateTime.now().minusDays(1),
            photoList = emptyList(),
            dishList = listOf(
                dishes[3], // Bruschetta
                dishes[0], // Carbonara
                dishes[2]  // Tiramisu
            ),
            isSomeoneElsePaying = false
        ),
        // Meal at Pizza Palace
        Meal(
            id = 2,
            name = "Pizza Night",
            restaurantId = 2,
            dateTime = LocalDateTime.now().minusDays(3),
            photoList = emptyList(),
            dishList = listOf(dishes[1]), // Margherita Pizza
            isSomeoneElsePaying = false
        ),
        // Meal at Sushi Spot
        Meal(
            id = 3,
            name = "Sushi Feast",
            restaurantId = 3,
            dateTime = LocalDateTime.now().minusDays(5),
            photoList = emptyList(),
            dishList = listOf(
                dishes[6], // Miso Soup
                dishes[4], // Salmon Nigiri
                dishes[5], // California Roll
                dishes[7]  // Green Tea Ice Cream
            ),
            isSomeoneElsePaying = true
        ),
        // Meal at Burger Barn
        Meal(
            id = 4,
            name = "Burger Lunch",
            restaurantId = 4,
            dateTime = LocalDateTime.now().minusDays(7),
            photoList = emptyList(),
            dishList = listOf(
                dishes[9],  // Bacon Burger
                dishes[10], // French Fries
                dishes[11]  // Milkshake
            ),
            isSomeoneElsePaying = false
        ),
        // Meal at Le Bistrot Français
        Meal(
            id = 5,
            name = "French Experience",
            restaurantId = 5,
            dateTime = LocalDateTime.now().minusDays(10),
            photoList = emptyList(),
            dishList = listOf(
                dishes[13], // French Onion Soup
                dishes[12], // Coq au Vin
                dishes[14]  // Crème Brûlée
            ),
            isSomeoneElsePaying = true
        ),
        // Meal at Taco Fiesta
        Meal(
            id = 6,
            name = "Taco Tuesday",
            restaurantId = 6,
            dateTime = LocalDateTime.now().minusDays(14),
            photoList = emptyList(),
            dishList = listOf(
                dishes[16], // Guacamole
                dishes[15], // Tacos al Pastor
                dishes[17]  // Churros
            ),
            isSomeoneElsePaying = false
        ),
        // Meal at Golden Dragon
        Meal(
            id = 7,
            name = "Chinese Dinner",
            restaurantId = 7,
            dateTime = LocalDateTime.now().minusDays(20),
            photoList = emptyList(),
            dishList = listOf(
                dishes[19], // Spring Rolls
                dishes[18]  // Kung Pao Chicken
            ),
            isSomeoneElsePaying = false
        ),
        // Meal at The Steakhouse
        Meal(
            id = 8,
            name = "Steak Dinner",
            restaurantId = 8,
            dateTime = LocalDateTime.now().minusDays(25),
            photoList = emptyList(),
            dishList = listOf(
                dishes[21], // Caesar Salad
                dishes[20], // Ribeye Steak
                dishes[22], // Mashed Potatoes
                dishes[23]  // Red Wine
            ),
            isSomeoneElsePaying = true
        )
    )
}
