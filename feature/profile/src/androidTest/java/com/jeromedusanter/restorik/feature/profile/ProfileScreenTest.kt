package com.jeromedusanter.restorik.feature.profile

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import java.time.YearMonth
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val testCurrentMonth = YearMonth.of(2024, 6)
    private val testMinMonth = YearMonth.of(2024, 1)

    private fun baseState(
        selectedMonth: YearMonth = testCurrentMonth,
        monthlySpending: Double = 0.0,
        previousMonthSpending: Double = 0.0,
        numberOfMeals: Int = 0,
        averageRating: Double = 0.0,
        numberOfRestaurants: Int = 0,
        newRestaurantsTried: Int = 0,
        topRestaurantsBySpending: List<RestaurantSpending> = emptyList(),
        averageMealSpending: Double = 0.0,
        isLoading: Boolean = false
    ) = ProfileUiState(
        currentMonth = testCurrentMonth,
        selectedMonth = selectedMonth,
        monthlySpending = monthlySpending,
        previousMonthSpending = previousMonthSpending,
        numberOfMeals = numberOfMeals,
        averageRating = averageRating,
        numberOfRestaurants = numberOfRestaurants,
        newRestaurantsTried = newRestaurantsTried,
        topRestaurantsBySpending = topRestaurantsBySpending,
        averageMealSpending = averageMealSpending,
        isLoading = isLoading,
        minMonth = testMinMonth
    )

    @Test
    fun emptyState_showsNoDataMessage() {
        // GIVEN
        val emptyState = baseState()

        // WHEN
        composeTestRule.setContent {
            ProfileScreen(uiState = emptyState)
        }

        // THEN
        composeTestRule
            .onNodeWithTag(testTag = "no_data_message")
            .assertIsDisplayed()
    }

    @Test
    fun monthlySpending_whenHasData_showsSpendingAmount() {
        // GIVEN
        val stateWithData = baseState(
            monthlySpending = 450.50,
            numberOfMeals = 5
        )

        // WHEN
        composeTestRule.setContent {
            ProfileScreen(uiState = stateWithData)
        }

        // THEN
        composeTestRule
            .onNodeWithTag(testTag = "monthly_spending_amount")
            .assertIsDisplayed()
    }

    @Test
    fun monthlySpending_whenHasPreviousMonthData_showsPercentageChange() {
        // GIVEN
        val stateWithComparison = baseState(
            monthlySpending = 500.0,
            previousMonthSpending = 400.0,
            numberOfMeals = 5
        )

        // WHEN
        composeTestRule.setContent {
            ProfileScreen(uiState = stateWithComparison)
        }

        // THEN
        composeTestRule
            .onNodeWithTag(testTag = "spending_comparison")
            .assertIsDisplayed()
    }

    @Test
    fun monthSelector_displaysCurrentMonth() {
        // GIVEN
        val januaryState = baseState(
            selectedMonth = YearMonth.of(2024, 1)
        )

        // WHEN
        composeTestRule.setContent {
            ProfileScreen(uiState = januaryState)
        }

        // THEN
        composeTestRule
            .onNodeWithTag(testTag = "month_chip")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun monthSelector_whenClicked_triggersCallback() {
        // GIVEN
        var monthChipClicked = false
        val state = baseState()

        // WHEN
        composeTestRule.setContent {
            ProfileScreen(
                uiState = state,
                onMonthChipClick = { monthChipClicked = true }
            )
        }

        composeTestRule
            .onNodeWithTag(testTag = "month_chip")
            .performClick()

        // THEN
        assertTrue(monthChipClicked)
    }

    @Test
    fun navigationArrows_previousMonth_whenAtMinMonth_isDisabled() {
        // GIVEN
        val state = baseState(
            selectedMonth = testMinMonth
        )

        // WHEN
        composeTestRule.setContent {
            ProfileScreen(uiState = state)
        }

        // THEN
        composeTestRule
            .onNodeWithTag(testTag = "previous_month_button")
            .assertIsNotEnabled()
    }

    @Test
    fun navigationArrows_previousMonth_whenNotAtMinMonth_isEnabled() {
        // GIVEN
        val state = baseState(
            selectedMonth = YearMonth.of(2024, 3)
        )

        // WHEN
        composeTestRule.setContent {
            ProfileScreen(uiState = state)
        }

        // THEN
        composeTestRule
            .onNodeWithTag(testTag = "previous_month_button")
            .assertIsEnabled()
    }

    @Test
    fun navigationArrows_nextMonth_whenAtCurrentMonth_isDisabled() {
        // GIVEN
        val state = baseState()

        // WHEN
        composeTestRule.setContent {
            ProfileScreen(uiState = state)
        }

        // THEN
        composeTestRule
            .onNodeWithTag(testTag = "next_month_button")
            .assertIsNotEnabled()
    }

    @Test
    fun navigationArrows_whenClickedPrevious_triggersMonthChangeCallback() {
        // GIVEN
        var changedToMonth: YearMonth? = null
        val currentMonth = YearMonth.of(2024, 3)
        val state = baseState(
            selectedMonth = currentMonth
        )

        // WHEN
        composeTestRule.setContent {
            ProfileScreen(
                uiState = state,
                onMonthChange = { changedToMonth = it }
            )
        }

        composeTestRule
            .onNodeWithTag(testTag = "previous_month_button")
            .performClick()

        // THEN
        assertEquals(YearMonth.of(2024, 2), changedToMonth)
    }

    @Test
    fun navigationArrows_whenClickedNext_triggersMonthChangeCallback() {
        // GIVEN
        var changedToMonth: YearMonth? = null
        val pastMonth = YearMonth.of(2024, 1)
        val state = baseState(
            selectedMonth = pastMonth
        )

        // WHEN
        composeTestRule.setContent {
            ProfileScreen(
                uiState = state,
                onMonthChange = { changedToMonth = it }
            )
        }

        composeTestRule
            .onNodeWithTag(testTag = "next_month_button")
            .performClick()

        // THEN
        assertEquals(YearMonth.of(2024, 2), changedToMonth)
    }

    @Test
    fun statistics_displayAllCards() {
        // GIVEN
        val state = baseState(
            numberOfMeals = 10,
            averageRating = 4.5,
            averageMealSpending = 25.50,
            numberOfRestaurants = 5,
            newRestaurantsTried = 2
        )

        // WHEN
        composeTestRule.setContent {
            ProfileScreen(uiState = state)
        }

        // THEN - Verify all stat cards are displayed using test tags
        composeTestRule
            .onNodeWithTag(testTag = "stat_meals_value")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(testTag = "stat_rating_value")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(testTag = "stat_avg_spending_value")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(testTag = "stat_restaurants_value")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(testTag = "stat_new_restaurants_value")
            .assertIsDisplayed()
    }

    @Test
    fun statistics_whenNoData_showsDashes() {
        // GIVEN
        val emptyState = baseState()

        // WHEN
        composeTestRule.setContent {
            ProfileScreen(uiState = emptyState)
        }

        // THEN - Verify dash is shown for average rating
        composeTestRule
            .onNodeWithTag(testTag = "stat_rating_value")
            .assertIsDisplayed()

        // Verify dash is shown for average meal spending
        composeTestRule
            .onNodeWithTag(testTag = "stat_avg_spending_value")
            .assertIsDisplayed()
    }

    @Test
    fun topRestaurants_whenEmpty_showsDash() {
        // GIVEN
        val state = baseState()

        // WHEN
        composeTestRule.setContent {
            ProfileScreen(uiState = state)
        }

        // THEN
        composeTestRule
            .onNodeWithTag(testTag = "stat_top_restaurants_title")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(testTag = "stat_top_restaurants_value")
            .assertIsDisplayed()
    }

    @Test
    fun topRestaurants_whenHasData_displaysRestaurantsWithSpending() {
        // GIVEN
        val topRestaurantList = listOf(
            RestaurantSpending(restaurantName = "Pizza Palace", totalSpending = 200.0),
            RestaurantSpending(restaurantName = "Burger Barn", totalSpending = 150.0),
            RestaurantSpending(restaurantName = "Sushi Spot", totalSpending = 100.0)
        )
        val state = baseState(
            topRestaurantsBySpending = topRestaurantList
        )

        // WHEN
        composeTestRule.setContent {
            ProfileScreen(uiState = state)
        }

        // THEN - Verify each restaurant is displayed using test tags
        composeTestRule
            .onNodeWithTag(testTag = "stat_top_restaurants_item_0_name")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(testTag = "stat_top_restaurants_item_0_spending")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(testTag = "stat_top_restaurants_item_1_name")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(testTag = "stat_top_restaurants_item_1_spending")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(testTag = "stat_top_restaurants_item_2_name")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(testTag = "stat_top_restaurants_item_2_spending")
            .assertIsDisplayed()
    }
}
