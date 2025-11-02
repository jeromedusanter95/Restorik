# Claude Code Rules for Restorik

## File Organization

- **Always create separate files** for:
  - Enums
  - Data classes
  - Composables

  Each of these should have its own dedicated file rather than being nested inside other files.

## Kotlin Code Standards

### Class Organization
- **Companion objects** should always be placed at the end of the class
- **Private functions** should always be placed at the end of the class but **above the companion object**

Order within a class:
1. Properties (public, then private)
2. Init blocks
3. Public functions
4. Private functions
5. Companion object (if present)

Example:
```kotlin
class MyViewModel @Inject constructor() : ViewModel() {
    // Properties
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    // Public functions
    fun updateData(data: String) {
        validateAndUpdate(data = data)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // Private functions (at the end, before companion object)
    private fun validateAndUpdate(data: String) {
        // Implementation
    }

    private fun processData(input: String): String {
        // Implementation
        return input
    }

    // Companion object (at the very end)
    companion object {
        const val MAX_LENGTH = 100
    }
}
```

### Null Safety
- **Never use `!!` (double-bang operator)** in Kotlin code
- Always find a safer alternative:
  - Use `?.let { }` for safe calls
  - Use `?:` (Elvis operator) for default values
  - Use `if (x != null) { }` checks
  - Use `requireNotNull()` or `checkNotNull()` with meaningful error messages when appropriate

### Function Calls
- **Always use named arguments** when calling functions
- This improves code readability and makes the intent clear

Example:
```kotlin
// Good
viewModel.updateMealName(newValue = "Pizza")

// Bad
viewModel.updateMealName("Pizza")
```

### Naming Conventions

#### Lists
- **Always use the suffix `List`** for ALL list variable names
- Format: `<itemType>List` (singular item type + "List")
- This applies to any collection of items

Examples:
```kotlin
// Good
val photoList: List<Photo>
val mealList: List<Meal>
val restaurantList: List<Restaurant>
val carList: List<Car>
val userList: List<User>
val itemList: List<Item>

// Bad
val photos: List<Photo>
val meals: List<Meal>
val restaurants: List<Restaurant>
val cars: List<Car>
val users: List<User>
val items: List<Item>
```

## Dependency Injection (Hilt)

### Context Injection
- **Only inject `Context`** when only the Context is needed (e.g., for accessing string resources)
- Use `@param:ApplicationContext` annotation with explicit use-site target
- Do not inject `Application` when `Context` is sufficient

Example:
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ViewModel()
```
