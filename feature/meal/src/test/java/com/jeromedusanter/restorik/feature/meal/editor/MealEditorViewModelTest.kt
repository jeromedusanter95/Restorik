package com.jeromedusanter.restorik.feature.meal.editor

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.jeromedusanter.restorik.core.model.Dish
import com.jeromedusanter.restorik.core.model.DishType
import com.jeromedusanter.restorik.core.testing.MainDispatcherRule
import com.jeromedusanter.restorik.core.testing.data.FakeMealData
import com.jeromedusanter.restorik.core.testing.repository.TestCityRepository
import com.jeromedusanter.restorik.core.testing.repository.TestMealRepository
import com.jeromedusanter.restorik.core.testing.repository.TestRestaurantRepository
import com.jeromedusanter.restorik.core.testing.resources.TestResourceProvider
import com.jeromedusanter.restorik.feature.meal.navigation.MealDestinations
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MealEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var testMealRepository: TestMealRepository
    private lateinit var testRestaurantRepository: TestRestaurantRepository
    private lateinit var testCityRepository: TestCityRepository
    private lateinit var mapper: MealEditorMapper
    private lateinit var resourceProvider: TestResourceProvider

    @Before
    fun setup() {
        resourceProvider = TestResourceProvider()
        testMealRepository = TestMealRepository()
        testRestaurantRepository = TestRestaurantRepository()
        testCityRepository = TestCityRepository()
        mapper = MealEditorMapper()
    }

    private fun givenFullMealData() {
        testMealRepository.sendMeals(meals = FakeMealData.meals)
        testRestaurantRepository.sendRestaurants(restaurants = FakeMealData.restaurants)
        testCityRepository.sendCities(cities = FakeMealData.cities)
    }

    private fun createViewModelInAddMode(): MealEditorViewModel {
        val savedStateHandle = SavedStateHandle()

        return MealEditorViewModel(
            savedStateHandle = savedStateHandle,
            mealRepository = testMealRepository,
            restaurantRepository = testRestaurantRepository,
            cityRepository = testCityRepository,
            mealEditorMapper = mapper,
            resourceProvider = resourceProvider
        )
    }

    private fun createViewModelInEditMode(mealId: Int): MealEditorViewModel {
        val savedStateHandle = SavedStateHandle().apply {
            set(MealDestinations.MealEditor.mealIdArg, mealId)
        }

        return MealEditorViewModel(
            savedStateHandle = savedStateHandle,
            mealRepository = testMealRepository,
            restaurantRepository = testRestaurantRepository,
            cityRepository = testCityRepository,
            mealEditorMapper = mapper,
            resourceProvider = resourceProvider
        )
    }

    private fun createTestDish(
        id: Int = 1,
        name: String = "Test Dish",
        rating: Float = 4.5f,
        description: String = "",
        price: Double = 10.0,
        dishType: DishType = DishType.MAIN_COURSE
    ) = Dish(
        id = id,
        name = name,
        rating = rating,
        description = description,
        price = price,
        dishType = dishType
    )

    @Test
    fun `initial state is empty in add mode`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        assertThat(viewModel.isEditMode).isFalse()
        assertThat(state.restaurantName).isEmpty()
        assertThat(state.cityName).isEmpty()
        assertThat(state.name).isEmpty()
        assertThat(state.dishList).isEmpty()
        assertThat(state.photoUriList).isEmpty()
        assertThat(state.isLoading).isFalse()
        assertThat(state.showAddButtonPhoto).isTrue()
    }

    @Test
    fun `edit mode loads existing meal`() = runTest {
        // GIVEN
        givenFullMealData()

        // WHEN
        val viewModel = createViewModelInEditMode(mealId = ITALIAN_DINNER_MEAL_ID)
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.isEditMode).isTrue()
        val state = viewModel.uiState.value
        assertThat(state.name).isEqualTo("Italian Dinner")
        assertThat(state.restaurantName).isEqualTo("Sapore Italiano")
        assertThat(state.cityName).isEqualTo("Paris")
        assertThat(state.dishList).hasSize(3)
    }

    @Test
    fun `updateRestaurantName updates state`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()

        // WHEN
        viewModel.updateRestaurantName(restaurantName = "New Restaurant")

        // THEN
        assertThat(viewModel.uiState.value.restaurantName).isEqualTo("New Restaurant")
    }

    @Test
    fun `updateRestaurantName triggers restaurant search with valid query`() = runTest {
        // GIVEN
        givenFullMealData()
        val viewModel = createViewModelInAddMode()

        // WHEN - Query with 2+ characters
        viewModel.updateRestaurantName(restaurantName = "Sa")
        advanceUntilIdle()

        // THEN - Should show suggestions starting with "Sa"
        val state = viewModel.uiState.value
        assertThat(state.restaurantSuggestionList).isNotEmpty()
        assertThat(state.restaurantSuggestionList[0].name).isEqualTo("Sapore Italiano")
    }

    @Test
    fun `updateRestaurantName with short query does not trigger search`() = runTest {
        // GIVEN
        givenFullMealData()
        val viewModel = createViewModelInAddMode()

        // WHEN - Query with less than 2 characters
        viewModel.updateRestaurantName(restaurantName = "S")
        advanceUntilIdle()

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.restaurantSuggestionList).isEmpty()
    }

    @Test
    fun `selectRestaurantSuggestion updates restaurant name`() = runTest {
        // GIVEN
        givenFullMealData()
        val viewModel = createViewModelInAddMode()
        viewModel.updateRestaurantName(restaurantName = "Sa")
        advanceUntilIdle()

        // WHEN
        val suggestion = viewModel.uiState.value.restaurantSuggestionList.first()
        viewModel.selectRestaurantSuggestion(suggestion = suggestion)

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.restaurantName).isEqualTo("Sapore Italiano")
        assertThat(state.restaurantSuggestionList).isEmpty()
    }

    @Test
    fun `updateCityName updates state`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()

        // WHEN
        viewModel.updateCityName(cityName = "Tokyo")

        // THEN
        assertThat(viewModel.uiState.value.cityName).isEqualTo("Tokyo")
    }

    @Test
    fun `updateCityName triggers city search with valid query`() = runTest {
        // GIVEN
        givenFullMealData()
        val viewModel = createViewModelInAddMode()

        // WHEN - Query with 2+ characters
        viewModel.updateCityName(cityName = "Pa")
        advanceUntilIdle()

        // THEN - Should show suggestions starting with "Pa"
        val state = viewModel.uiState.value
        assertThat(state.citySuggestionList).isNotEmpty()
        assertThat(state.citySuggestionList[0].name).isEqualTo("Paris")
    }

    @Test
    fun `selectCitySuggestion updates city name`() = runTest {
        // GIVEN
        givenFullMealData()
        val viewModel = createViewModelInAddMode()
        viewModel.updateCityName(cityName = "Pa")
        advanceUntilIdle()

        // WHEN
        val suggestion = viewModel.uiState.value.citySuggestionList.first()
        viewModel.selectCitySuggestion(suggestion = suggestion)

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.cityName).isEqualTo("Paris")
        assertThat(state.citySuggestionList).isEmpty()
    }

    @Test
    fun `updateMealName updates state`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()

        // WHEN
        viewModel.updateMealName(name = "Lunch at Restaurant")

        // THEN
        assertThat(viewModel.uiState.value.name).isEqualTo("Lunch at Restaurant")
    }

    @Test
    fun `setIsSomeoneElsePaying updates state`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()

        // WHEN
        viewModel.setIsSomeoneElsePaying(isSomeoneElsePaying = true)

        // THEN
        assertThat(viewModel.uiState.value.isSomeoneElsePaying).isTrue()
    }

    @Test
    fun `addDish adds dish to list`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        val dish = createTestDish(description = "Description")

        // WHEN
        viewModel.addDish(dish = dish)

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.dishList).hasSize(1)
        assertThat(state.dishList[0]).isEqualTo(dish)
    }

    @Test
    fun `updateDish updates existing dish`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        val dish = createTestDish(name = "Original", rating = 4.0f)
        viewModel.addDish(dish = dish)

        // WHEN
        val updatedDish = dish.copy(name = "Updated")
        viewModel.updateDish(dishId = 1, updatedDish = updatedDish)

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.dishList[0].name).isEqualTo("Updated")
    }

    @Test
    fun `deleteDish removes dish from list`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        val dish = createTestDish()
        viewModel.addDish(dish = dish)

        // WHEN
        viewModel.deleteDish(dishId = 1)

        // THEN
        assertThat(viewModel.uiState.value.dishList).isEmpty()
    }

    @Test
    fun `addPhoto adds photo to list and updates UI state`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        val photoUri = mockk<Uri>(relaxed = true)

        // WHEN
        viewModel.addPhoto(uri = photoUri)

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.photoUriList).hasSize(1)
        assertThat(state.photoUriList[0]).isEqualTo(photoUri)
        assertThat(state.showAddButtonPhoto).isFalse()
        assertThat(state.showAddButtonPhotoItem).isTrue()
        assertThat(state.photoTitleSuffix).isEqualTo("(1/5)")
    }

    @Test
    fun `addPhoto at max count updates UI correctly`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()

        // Add 5 photos (max count)
        repeat(5) { index ->
            viewModel.addPhoto(uri = mockk(relaxed = true))
        }

        // WHEN
        val state = viewModel.uiState.value

        // THEN
        assertThat(state.photoUriList).hasSize(5)
        assertThat(state.showAddButtonPhotoItem).isFalse()
        assertThat(state.photoTitleSuffix).isEqualTo("(5/5)")
    }

    @Test
    fun `deletePhoto removes photo from list and updates UI state`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        val photoUri = mockk<Uri>(relaxed = true)
        viewModel.addPhoto(uri = photoUri)

        // WHEN
        viewModel.deletePhoto(uri = photoUri)

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.photoUriList).isEmpty()
        assertThat(state.showAddButtonPhoto).isTrue()
        assertThat(state.photoTitleSuffix).isEqualTo("(0/5)")
    }

    @Test
    fun `saveMeal fails with validation errors when fields are blank`() = runTest {
        // GIVEN
        val viewModel = createViewModelInAddMode()

        // WHEN - Try to save without filling required fields
        var saveSuccessCalled = false
        viewModel.saveMeal(onSaveMealSuccess = { saveSuccessCalled = true })
        advanceUntilIdle()

        // THEN
        assertThat(saveSuccessCalled).isFalse()
        val state = viewModel.uiState.value
        assertThat(state.fieldErrors.hasErrors()).isTrue()
        assertThat(state.fieldErrors.restaurantNameError).isNotNull()
        assertThat(state.fieldErrors.cityNameError).isNotNull()
        assertThat(state.fieldErrors.mealNameError).isNotNull()
        assertThat(state.fieldErrors.dishListError).isNotNull()
    }

    // Note: saveMeal tests with actual database operations are integration tests
    // and are complex to test due to Dispatchers.IO usage.
    // They would be better tested in instrumented/integration tests.

    @Test
    fun `clearFieldError clears specific field error`() = runTest {
        // GIVEN
        val viewModel = createViewModelInAddMode()

        // Trigger validation errors
        var saveSuccessCalled = false
        viewModel.saveMeal(onSaveMealSuccess = { saveSuccessCalled = true })
        advanceUntilIdle()

        // WHEN
        viewModel.clearFieldError(field = MealEditorField.RESTAURANT_NAME)

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.fieldErrors.restaurantNameError).isNull()
        assertThat(state.fieldErrors.cityNameError).isNotNull() // Others still have errors
    }

    @Test
    fun `showPhotoSelectionBottomSheet updates state`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()

        // WHEN
        viewModel.showPhotoSelectionBottomSheet()

        // THEN
        assertThat(viewModel.uiState.value.showPhotoSelectionBottomSheet).isTrue()
    }

    @Test
    fun `hidePhotoSelectionBottomSheet updates state`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        viewModel.showPhotoSelectionBottomSheet()

        // WHEN
        viewModel.hidePhotoSelectionBottomSheet()

        // THEN
        assertThat(viewModel.uiState.value.showPhotoSelectionBottomSheet).isFalse()
    }

    @Test
    fun `selectPhotoForView updates selected photo`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        val photoUri = mockk<Uri>(relaxed = true)

        // WHEN
        viewModel.selectPhotoForView(uri = photoUri)

        // THEN
        assertThat(viewModel.uiState.value.selectedPhotoUri).isEqualTo(photoUri)
    }

    @Test
    fun `clearSelectedPhoto clears selected photo`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        val photoUri = mockk<Uri>(relaxed = true)
        viewModel.selectPhotoForView(uri = photoUri)

        // WHEN
        viewModel.clearSelectedPhoto()

        // THEN
        assertThat(viewModel.uiState.value.selectedPhotoUri).isNull()
    }

    @Test
    fun `showDishDialog for new dish shows empty editor`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()

        // WHEN
        viewModel.showDishDialog(dish = null)

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.showDishDialog).isTrue()
        assertThat(state.dishEditorState.dishId).isEqualTo(0)
        assertThat(state.dishEditorState.name).isEmpty()
    }

    @Test
    fun `showDishDialog for existing dish shows dish data`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        val dish = createTestDish(description = "Description")

        // WHEN
        viewModel.showDishDialog(dish = dish)

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.showDishDialog).isTrue()
        assertThat(state.dishEditorState.dishId).isEqualTo(1)
        assertThat(state.dishEditorState.name).isEqualTo("Test Dish")
        assertThat(state.dishEditorState.priceString).isEqualTo("10.0")
        assertThat(state.dishEditorState.rating).isEqualTo(4.5f)
    }

    @Test
    fun `dismissDishDialog closes dialog and resets state`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        viewModel.showDishDialog(dish = null)

        // WHEN
        viewModel.dismissDishDialog()

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.showDishDialog).isFalse()
        assertThat(state.dishEditorState.name).isEmpty()
    }

    @Test
    fun `updateDishName updates dish editor state`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        viewModel.showDishDialog(dish = null)

        // WHEN
        viewModel.updateDishName(name = "New Dish")

        // THEN
        assertThat(viewModel.uiState.value.dishEditorState.name).isEqualTo("New Dish")
    }

    @Test
    fun `updateDishPrice updates dish editor state`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        viewModel.showDishDialog(dish = null)

        // WHEN
        viewModel.updateDishPrice(priceString = "15.50")

        // THEN
        assertThat(viewModel.uiState.value.dishEditorState.priceString).isEqualTo("15.50")
    }

    @Test
    fun `updateDishRating updates dish editor state`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        viewModel.showDishDialog(dish = null)

        // WHEN
        viewModel.updateDishRating(rating = 4.5f)

        // THEN
        assertThat(viewModel.uiState.value.dishEditorState.rating).isEqualTo(4.5f)
    }

    @Test
    fun `saveDishFromEditor fails with validation errors`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        viewModel.showDishDialog(dish = null)

        // WHEN - Try to save without filling required fields
        viewModel.saveDishFromEditor()

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.showDishDialog).isTrue() // Dialog should stay open
        assertThat(state.dishEditorState.nameError).isNotNull()
        assertThat(state.dishEditorState.ratingError).isNotNull()
    }

    @Test
    fun `saveDishFromEditor succeeds with valid data`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        viewModel.showDishDialog(dish = null)
        viewModel.updateDishName(name = "New Dish")
        viewModel.updateDishPrice(priceString = "15.50")
        viewModel.updateDishRating(rating = 4.5f)

        // WHEN
        viewModel.saveDishFromEditor()

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.showDishDialog).isFalse() // Dialog should close
        assertThat(state.dishList).hasSize(1)
        assertThat(state.dishList[0].name).isEqualTo("New Dish")
    }

    @Test
    fun `saveDishFromEditor updates existing dish`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        val dish = createTestDish(name = "Original", rating = 4.0f)
        viewModel.addDish(dish = dish)
        viewModel.showDishDialog(dish = dish)
        viewModel.updateDishName(name = "Updated")

        // WHEN
        viewModel.saveDishFromEditor()

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.dishList).hasSize(1)
        assertThat(state.dishList[0].name).isEqualTo("Updated")
    }

    @Test
    fun `saveDishFromEditor with isSomeoneElsePaying true does not require price`() {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        viewModel.setIsSomeoneElsePaying(isSomeoneElsePaying = true)
        viewModel.showDishDialog(dish = null)
        viewModel.updateDishName(name = "Free Dish")
        viewModel.updateDishRating(rating = 4.5f)

        // WHEN
        viewModel.saveDishFromEditor()

        // THEN
        val state = viewModel.uiState.value
        assertThat(state.showDishDialog).isFalse()
        assertThat(state.dishList).hasSize(1)
        assertThat(state.dishList[0].price).isEqualTo(0.0)
    }

    @Test
    fun `clearError clears error message`() = runTest {
        // GIVEN
        val viewModel = createViewModelInAddMode()
        viewModel.saveMeal(onSaveMealSuccess = {})
        advanceUntilIdle()

        // WHEN
        viewModel.clearError()

        // THEN
        assertThat(viewModel.uiState.value.errorMessage).isNull()
    }

    private companion object {
        const val ITALIAN_DINNER_MEAL_ID = 1
    }
}
